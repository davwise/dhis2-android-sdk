/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.domain.aggregated.data.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.factories.internal.QueryCall
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.dataapproval.DataApproval
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalQuery
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationQuery
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.internal.DataValueQuery
import org.hisp.dhis.android.core.domain.aggregated.data.AggregatedD2Progress
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import javax.inject.Inject

@Reusable
internal class AggregatedDataCall @Inject constructor(
    private val systemInfoModuleDownloader: SystemInfoModuleDownloader,
    private val dataValueCall: QueryCall<DataValue, DataValueQuery>,
    private val dataSetCompleteRegistrationCall: QueryCall<DataSetCompleteRegistration, DataSetCompleteRegistrationQuery>,
    private val dataApprovalCall: QueryCall<DataApproval, DataApprovalQuery>,
    private val categoryOptionComboStore: CategoryOptionComboStore,
    private val rxCallExecutor: RxAPICallExecutor,
    private val aggregatedDataSyncStore: ObjectWithoutUidStore<AggregatedDataSync>,
    private val aggregatedDataCallBundleFactory: AggregatedDataCallBundleFactory,
    private val resourceHandler: ResourceHandler,
    private val hashHelper: AggregatedDataSyncHashHelper
) {
    fun download(): Observable<AggregatedD2Progress> {
        val progressManager = AggregatedD2ProgressManager(null)
        val observable = systemInfoModuleDownloader.downloadWithProgressManager(progressManager)
            .switchMap { selectDataSetsAndDownload(progressManager) }
        return rxCallExecutor.wrapObservableTransactionally(observable, true)
    }

    private fun selectDataSetsAndDownload(
        progressManager: AggregatedD2ProgressManager
    ): Observable<AggregatedD2Progress> {
        val bundles = aggregatedDataCallBundleFactory.bundles
        val dataSets = bundles.flatMap { it.dataSets() }.mapNotNull { it.uid() }

        return Observable.merge {
            listOf(
                Observable.just(progressManager.setDataSets(dataSets)),
                Observable.fromIterable(bundles).flatMap { downloadInternal(it, progressManager) },
                Observable.just(progressManager.increaseProgress(DataValue::class.java, isComplete = true))
            )
        }
    }

    private fun downloadInternal(
        bundle: AggregatedDataCallBundle,
        progressManager: AggregatedD2ProgressManager
    ): Observable<AggregatedD2Progress> {
        val dataValueQuery = DataValueQuery.create(bundle)
        val dataValueSingle = dataValueCall.download(dataValueQuery)

        val completeRegistrationQuery = DataSetCompleteRegistrationQuery.create(
            getUids(bundle.dataSets()),
            bundle.periodIds(),
            bundle.rootOrganisationUnitUids(),
            bundle.key().lastUpdatedStr()
        )

        val dataSetCompleteRegistrationSingle = dataSetCompleteRegistrationCall.download(completeRegistrationQuery)

        return dataValueSingle
            .flatMap { dataSetCompleteRegistrationSingle }
            .flatMap { getApprovalSingle(bundle, progressManager) }
            .flatMap { updateAggregatedDataSync(bundle, progressManager) }
            .map { progressManager.updateDataSets(bundle.dataSets().mapNotNull { it.uid() }, isComplete = true) }
            .toObservable()
    }

    private fun updateAggregatedDataSync(
        bundle: AggregatedDataCallBundle,
        progressManager: AggregatedD2ProgressManager
    ): Single<AggregatedD2Progress> {
        return Single.fromCallable {
            for (dataSet in bundle.dataSets()) {
                aggregatedDataSyncStore.updateOrInsertWhere(
                    AggregatedDataSync.builder()
                        .dataSet(dataSet.uid())
                        .periodType(dataSet.periodType())
                        .pastPeriods(bundle.key().pastPeriods())
                        .futurePeriods(dataSet.openFuturePeriods())
                        .dataElementsHash(hashHelper.getDataSetDataElementsHash(dataSet))
                        .organisationUnitsHash(bundle.allOrganisationUnitUidsSet().hashCode())
                        .lastUpdated(resourceHandler.serverDate)
                        .build()
                )
            }
            progressManager.increaseProgress(AggregatedDataSync::class.java, false)
        }
    }

    private fun getApprovalSingle(
        bundle: AggregatedDataCallBundle,
        progressManager: AggregatedD2ProgressManager
    ): Single<AggregatedD2Progress> {
        val dataSetsWithWorkflow = bundle.dataSets().filter { it.workflow() != null }
        val workflowUids = dataSetsWithWorkflow.map { it.workflow()!!.uid() }

        return if (workflowUids.isEmpty()) {
            Single.just(progressManager.increaseProgress(DataApproval::class.java, false))
        } else {
            val attributeOptionComboUids = getAttributeOptionCombosUidsFrom(dataSetsWithWorkflow)
            val dataApprovalQuery = DataApprovalQuery.create(
                workflowUids,
                bundle.allOrganisationUnitUidsSet(), bundle.periodIds(), attributeOptionComboUids,
                bundle.key().lastUpdatedStr()
            )
            dataApprovalCall.download(dataApprovalQuery)
                .map { progressManager.increaseProgress(DataApproval::class.java, false) }
        }
    }

    private fun getAttributeOptionCombosUidsFrom(dataSetsWithWorkflow: Collection<DataSet>): Set<String> {
        val categoryComboUids = dataSetsWithWorkflow.map { it.categoryCombo()!!.uid() }.toSet()

        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO,
                categoryComboUids
            ).build()

        return categoryOptionComboStore.selectWhere(whereClause).map { it.uid() }.toSet()
    }

    fun blockingDownload() {
        download().blockingSubscribe()
    }
}
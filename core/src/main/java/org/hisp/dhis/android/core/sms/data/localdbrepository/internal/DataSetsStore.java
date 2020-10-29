package org.hisp.dhis.android.core.sms.data.localdbrepository.internal;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCollectionRepository;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository;
import org.hisp.dhis.android.core.datavalue.DataValueModule;
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

class DataSetsStore {
    private final DataValueModule dataValueModule;
    private final DataValueStore dataValueStore;
    private final DataSetCompleteRegistrationStore dataSetStore;
    private final DataSetCompleteRegistrationCollectionRepository completeRegistrationRepository;

    @Inject
    DataSetsStore(DataValueModule dataValueModule,
                  DataValueStore dataValueStore,
                  DataSetCompleteRegistrationStore dataSetStore,
                  DataSetCompleteRegistrationCollectionRepository completeRegistrationRepository) {
        this.dataValueModule = dataValueModule;
        this.dataValueStore = dataValueStore;
        this.dataSetStore = dataSetStore;
        this.completeRegistrationRepository = completeRegistrationRepository;
    }

    Single<List<DataValue>> getDataValues(String dataSetUid, String orgUnit,
                                          String period, String attributeOptionComboUid) {
        return Single.fromCallable(() -> {
            DataValueCollectionRepository baseDataValuesRepo = dataValueModule.dataValues()
                    .byDataSetUid(dataSetUid)
                    .byOrganisationUnitUid().eq(orgUnit)
                    .byPeriod().eq(period)
                    .byAttributeOptionComboUid().eq(attributeOptionComboUid);

            List<DataValue> dataValues = baseDataValuesRepo
                    .byState().in(Arrays.asList(State.uploadableStates()))
                    .blockingGet();

            // TODO Workaround to prevent empty lists. Not supported in compression library
            if (dataValues.isEmpty()) {
                List<DataValue> allDataValues = baseDataValuesRepo.blockingGet();

                if (!allDataValues.isEmpty()) {
                    dataValues = allDataValues.subList(0, 1);
                }
            }

            return dataValues;
        });
    }

    Completable updateDataSetValuesState(String dataSet,
                                         String orgUnit,
                                         String period,
                                         String attributeOptionComboUid,
                                         State state) {
        return getDataValues(dataSet, orgUnit, period, attributeOptionComboUid)
                .flattenAsObservable(items -> items)
                .flatMapCompletable(item -> Completable.fromAction(() ->
                        dataValueStore.setState(item, state))
                );
    }

    Completable updateDataSetCompleteRegistrationState(String dataSetId,
                                                       String orgUnit,
                                                       String period,
                                                       String attributeOptionComboUid,
                                                       State state) {
        return Completable.fromAction(() -> {
            DataSetCompleteRegistration dataSet = completeRegistrationRepository
                    .byDataSetUid().eq(dataSetId)
                    .byOrganisationUnitUid().eq(orgUnit)
                    .byPeriod().eq(period)
                    .byAttributeOptionComboUid().eq(attributeOptionComboUid)
                    .one().blockingGet();
            if (dataSet != null) {
                dataSetStore.setState(dataSet, state);
            }
        });
    }
}

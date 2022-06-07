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

import org.hisp.dhis.android.core.arch.call.D2ProgressStatus
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.domain.aggregated.data.AggregatedD2Progress

class AggregatedD2ProgressManager(totalCalls: Int?) : D2ProgressManager(totalCalls) {

    private var progress: AggregatedD2Progress

    override fun getProgress(): AggregatedD2Progress {
        return progress
    }

    override fun <T> increaseProgress(resourceClass: Class<T>, isComplete: Boolean): AggregatedD2Progress {
        return progress.toBuilder()
            .doneCalls(progress.doneCalls() + resourceClass.simpleName)
            .isComplete(isComplete)
            .build()
            .also {
                progress = it
            }
    }

    fun setDataSets(dataSets: Collection<String>): AggregatedD2Progress {
        return progress.toBuilder()
            .dataSets(dataSets.associateWith { D2ProgressStatus(isComplete = false) })
            .build()
            .also { progress = it }
    }

    fun updateDataSets(dataSets: Collection<String>, isComplete: Boolean): AggregatedD2Progress {
        return progress.toBuilder()
            .dataSets(progress.dataSets() + dataSets.associateWith { D2ProgressStatus(isComplete) })
            .build()
            .also { progress = it }
    }

    fun complete(): AggregatedD2Progress {
        return progress.toBuilder()
            .dataSets(progress.dataSets().mapValues { D2ProgressStatus(isComplete = true) })
            .isComplete(true)
            .build()
            .also { progress = it }
    }

    init {
        this.progress = AggregatedD2Progress.empty(totalCalls)
    }
}

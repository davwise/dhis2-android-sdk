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

package org.hisp.dhis.android.testapp.trackedentity;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(D2JunitRunner.class)
public class TrackedEntityInstanceFilterCollectionRepositoryMockIntegrationShould
        extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<TrackedEntityInstanceFilter> trackedEntityInstanceFilters =
                d2.trackedEntityModule().trackedEntityInstanceFilters()
                        .blockingGet();

        assertThat(trackedEntityInstanceFilters.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_program() {
        List<TrackedEntityInstanceFilter> trackedEntityInstanceFilters =
                d2.trackedEntityModule().trackedEntityInstanceFilters()
                        .byProgram().eq("lxAQ7Zs9VYR")
                        .blockingGet();

        assertThat(trackedEntityInstanceFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_description() {
        List<TrackedEntityInstanceFilter> trackedEntityInstanceFilters =
                d2.trackedEntityModule().trackedEntityInstanceFilters()
                        .byDescription().eq("Foci response assigned to someone, and the enrollment is still active")
                        .blockingGet();

        assertThat(trackedEntityInstanceFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_sort_order() {
        List<TrackedEntityInstanceFilter> trackedEntityInstanceFilters =
                d2.trackedEntityModule().trackedEntityInstanceFilters()
                        .bySortOrder().eq(2)
                        .blockingGet();

        assertThat(trackedEntityInstanceFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_enrollment_status() {
        List<TrackedEntityInstanceFilter> trackedEntityInstanceFilters =
                d2.trackedEntityModule().trackedEntityInstanceFilters()
                        .byEnrollmentStatus().eq(EnrollmentStatus.COMPLETED)
                        .blockingGet();

        assertThat(trackedEntityInstanceFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_follow_up() {
        List<TrackedEntityInstanceFilter> trackedEntityInstanceFilters =
                d2.trackedEntityModule().trackedEntityInstanceFilters()
                        .byFollowUp().eq(Boolean.TRUE)
                        .blockingGet();

        assertThat(trackedEntityInstanceFilters.size()).isEqualTo(0);
    }

    @Test
    public void filter_by_organization_unit() {
        List<TrackedEntityInstanceFilter> trackedEntityInstanceFilters =
                d2.trackedEntityModule().trackedEntityInstanceFilters()
                        .byOrganisationUnit().eq("orgUnitUid")
                        .blockingGet();

        assertThat(trackedEntityInstanceFilters.size()).isEqualTo(1);
    }

    @Test
    public void include_event_filters_as_children() {
        TrackedEntityInstanceFilter trackedEntityInstanceFilter =
                d2.trackedEntityModule().trackedEntityInstanceFilters()
                .withTrackedEntityInstanceEventFilters().one().blockingGet();

        assertThat(trackedEntityInstanceFilter.eventFilters().size()).isEqualTo(1);
        assertThat(trackedEntityInstanceFilter.eventFilters().get(0).eventStatus()).isEqualTo(EventStatus.OVERDUE);
    }
}
/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.testapp.program;

import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class ProgramStageSectionCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<ProgramStageSection> stageSections =
                d2.programModule().programStageSections()
                        .blockingGet();

        assertThat(stageSections.size()).isEqualTo(2);
    }

    @Test
    public void include_program_indicators_as_children() {
        ProgramStageSection stageSections =
                d2.programModule().programStageSections()
                        .withProgramIndicators()
                        .one().blockingGet();

        assertThat(stageSections.programIndicators().size()).isEqualTo(1);
    }

    @Test
    public void include_data_elements_as_children() {
        ProgramStageSection stageSections =
                d2.programModule().programStageSections()
                        .withDataElements()
                        .one().blockingGet();

        assertThat(stageSections.dataElements().size()).isEqualTo(1);
        assertThat(stageSections.dataElements().get(0).name()).isEqualTo("MCH ANC Visit");
    }

    @Test
    public void filter_by_sort_order() {
        List<ProgramStageSection> stageSections =
                d2.programModule().programStageSections()
                        .bySortOrder()
                        .eq(1)
                        .blockingGet();

        assertThat(stageSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_program_stage() {
        List<ProgramStageSection> stageSections =
                d2.programModule().programStageSections()
                        .byProgramStageUid()
                        .eq("dBwrot7S421")
                        .blockingGet();

        assertThat(stageSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_desktop_render_type() {
        List<ProgramStageSection> stageSections =
                d2.programModule().programStageSections()
                        .byDesktopRenderType()
                        .eq("LISTING")
                        .blockingGet();

        assertThat(stageSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_mobile_render_type() {
        List<ProgramStageSection> stageSections =
                d2.programModule().programStageSections()
                        .byMobileRenderType()
                        .eq("LISTING")
                        .blockingGet();

        assertThat(stageSections.size()).isEqualTo(1);
    }

}
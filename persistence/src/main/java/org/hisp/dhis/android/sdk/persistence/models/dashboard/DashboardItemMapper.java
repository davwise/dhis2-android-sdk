/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.persistence.models.dashboard;

import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.persistence.models.common.base.AbsMapper;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardItem$Flow;

public class DashboardItemMapper extends AbsMapper<DashboardItem, DashboardItem$Flow> {
    // public static final DashboardItemMapper INSTANCE = new DashboardItemMapper();
    private final DashboardMapper dashboardMapper;

    public DashboardItemMapper(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public DashboardItem$Flow mapToDatabaseEntity(DashboardItem dashboardItem) {
        if (dashboardItem == null) {
            return null;
        }

        DashboardItem$Flow dashboardItemFlow = new DashboardItem$Flow();
        dashboardItemFlow.setId(dashboardItem.getId());
        dashboardItemFlow.setUId(dashboardItem.getUId());
        dashboardItemFlow.setCreated(dashboardItem.getCreated());
        dashboardItemFlow.setLastUpdated(dashboardItem.getLastUpdated());
        dashboardItemFlow.setAccess(dashboardItem.getAccess());
        dashboardItemFlow.setName(dashboardItem.getName());
        dashboardItemFlow.setDisplayName(dashboardItem.getDisplayName());
        dashboardItemFlow.setDashboard(dashboardMapper
                .mapToDatabaseEntity(dashboardItem.getDashboard()));
        dashboardItemFlow.setType(dashboardItem.getType());
        dashboardItemFlow.setShape(dashboardItem.getShape());
        return dashboardItemFlow;
    }

    @Override
    public DashboardItem mapToModel(DashboardItem$Flow dashboardItemFlow) {
        if (dashboardItemFlow == null) {
            return null;
        }

        DashboardItem dashboardItem = new DashboardItem();
        dashboardItem.setId(dashboardItemFlow.getId());
        dashboardItem.setUId(dashboardItemFlow.getUId());
        dashboardItem.setCreated(dashboardItemFlow.getCreated());
        dashboardItem.setLastUpdated(dashboardItemFlow.getLastUpdated());
        dashboardItem.setAccess(dashboardItemFlow.getAccess());
        dashboardItem.setName(dashboardItemFlow.getName());
        dashboardItem.setDisplayName(dashboardItemFlow.getDisplayName());
        dashboardItem.setDashboard(dashboardMapper
                .mapToModel(dashboardItemFlow.getDashboard()));
        dashboardItem.setType(dashboardItemFlow.getType());
        dashboardItem.setShape(dashboardItemFlow.getShape());
        return dashboardItem;
    }

    @Override
    public Class<DashboardItem> getModelTypeClass() {
        return DashboardItem.class;
    }

    @Override
    public Class<DashboardItem$Flow> getDatabaseEntityTypeClass() {
        return DashboardItem$Flow.class;
    }
}

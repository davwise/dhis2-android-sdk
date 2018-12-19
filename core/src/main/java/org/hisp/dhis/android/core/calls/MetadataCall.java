/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.modules.Downloader;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboEndpointCallFactory;
import org.hisp.dhis.android.core.category.CategoryComboUidsSeeker;
import org.hisp.dhis.android.core.category.CategoryEndpointCallFactory;
import org.hisp.dhis.android.core.category.CategoryParentUidsHelper;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.organisationunit.SearchOrganisationUnitCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserDownloadModule;

import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
public class MetadataCall extends SyncCall<Unit> {

    private final GenericCallData genericCallData;

    private final Downloader<SystemInfo> systemInfoDownloader;
    private final Downloader<SystemSetting> systemSettingDownloader;
    private final UserDownloadModule userDownloadModule;
    private final UidsCallFactory<Category> categoryCallFactory;
    private final UidsCallFactory<CategoryCombo> categoryComboCallFactory;
    private final CategoryComboUidsSeeker categoryComboUidsSeeker;
    private final Downloader<List<Program>> programDownloader;
    private final OrganisationUnitCall.Factory organisationUnitCallFactory;
    private final SearchOrganisationUnitCall.Factory searchOrganisationUnitCallFactory;
    private final Downloader<List<DataSet>> dataSetDownloader;
    private final ForeignKeyCleaner foreignKeyCleaner;

    public MetadataCall(@NonNull GenericCallData genericCallData,
                        @NonNull Downloader<SystemInfo> systemInfoDownloader,
                        @NonNull Downloader<SystemSetting> systemSettingDownloader,
                        @NonNull UserDownloadModule userDownloadModule,
                        @NonNull UidsCallFactory<Category> categoryCallFactory,
                        @NonNull UidsCallFactory<CategoryCombo> categoryComboCallFactory,
                        @NonNull CategoryComboUidsSeeker categoryComboUidsSeeker,
                        @NonNull Downloader<List<Program>> programDownloader,
                        @NonNull OrganisationUnitCall.Factory organisationUnitCallFactory,
                        @NonNull SearchOrganisationUnitCall.Factory searchOrganisationUnitCallFactory,
                        @NonNull Downloader<List<DataSet>> dataSetDownloader,
                        @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.genericCallData = genericCallData;
        this.systemInfoDownloader = systemInfoDownloader;
        this.systemSettingDownloader = systemSettingDownloader;
        this.userDownloadModule = userDownloadModule;
        this.categoryCallFactory = categoryCallFactory;
        this.categoryComboCallFactory = categoryComboCallFactory;
        this.categoryComboUidsSeeker = categoryComboUidsSeeker;
        this.programDownloader = programDownloader;
        this.organisationUnitCallFactory = organisationUnitCallFactory;
        this.searchOrganisationUnitCallFactory = searchOrganisationUnitCallFactory;
        this.dataSetDownloader = dataSetDownloader;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public Unit call() throws Exception {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor(genericCallData.databaseAdapter());

        return executor.executeD2CallTransactionally(new Callable<Unit>() {
            @Override
            public Unit call() throws Exception {
                systemInfoDownloader.download().call();

                systemSettingDownloader.download().call();

                User user = userDownloadModule.downloadUser().call();

                userDownloadModule.downloadAuthority().call();

                List<Program> programs = programDownloader.download().call();

                List<DataSet> dataSets = dataSetDownloader.download().call();

                List<CategoryCombo> categoryCombos = categoryComboCallFactory.create(
                        categoryComboUidsSeeker.seekUids()).call();

                categoryCallFactory.create(CategoryParentUidsHelper.getCategoryUids(categoryCombos)).call();

                organisationUnitCallFactory.create(
                        genericCallData, user, UidsHelper.getUids(programs), UidsHelper.getUids(dataSets)).call();

                searchOrganisationUnitCallFactory.create(genericCallData, user).call();

                foreignKeyCleaner.cleanForeignKeyErrors();

                return new Unit();
            }
        });
    }

    public static MetadataCall create(GenericCallData genericCallData,
                                      D2InternalModules internalModules) {
        APICallExecutor apiCallExecutor = APICallExecutorImpl.create(genericCallData.databaseAdapter());

        return new MetadataCall(
                genericCallData,
                internalModules.systemInfo,
                internalModules.systemSetting,
                internalModules.user,
                new CategoryEndpointCallFactory(genericCallData, apiCallExecutor),
                new CategoryComboEndpointCallFactory(genericCallData, apiCallExecutor),
                new CategoryComboUidsSeeker(genericCallData.databaseAdapter()),
                internalModules.program,
                OrganisationUnitCall.FACTORY,
                SearchOrganisationUnitCall.FACTORY,
                internalModules.dataSet,
                ForeignKeyCleanerImpl.create(genericCallData.databaseAdapter())
        );
    }
}
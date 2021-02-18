package org.hisp.dhis.android.core.settings;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.internal.ReadOnlyAnyObjectWithDownloadRepositoryImpl;
import org.hisp.dhis.android.core.settings.internal.AppearanceSettingCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class AppearanceSettingsObjectRepository
        extends ReadOnlyAnyObjectWithDownloadRepositoryImpl<AppearanceSettings>
        implements ReadOnlyWithDownloadObjectRepository<AppearanceSettings> {

    private final ObjectWithoutUidStore<FilterSetting> filterSettingStore;
    private final ObjectWithoutUidStore<CompletionSpinner> completionSpinnerStore;

    @Inject
    AppearanceSettingsObjectRepository(ObjectWithoutUidStore<FilterSetting> filterSettingStore,
                                       ObjectWithoutUidStore<CompletionSpinner> completionSpinnerStore,
                                       AppearanceSettingCall appearanceSettingCall) {
        super(appearanceSettingCall);
        this.filterSettingStore = filterSettingStore;
        this.completionSpinnerStore = completionSpinnerStore;
    }

    @Override
    public AppearanceSettings blockingGet() {
        List<FilterSetting> filters = filterSettingStore.selectAll();

        //FilterSorting
        FilterSorting.Builder filterSortingBuilder = FilterSorting.builder();
        filterSortingBuilder.home(getHomeFilters(filters));
        filterSortingBuilder.dataSetSettings(getDataSetFilters(filters));
        filterSortingBuilder.programSettings(getProgramFilters(filters));
        FilterSorting filterSorting = filterSortingBuilder.build();

        //CompletionSpinner
        List<CompletionSpinner> completionSpinnerList = completionSpinnerStore.selectAll();
        CompletionSpinnerSetting.Builder completionSpinnerSettingBuilder = CompletionSpinnerSetting.builder();
        completionSpinnerSettingBuilder.globalSettings(getGlobalCompletionSpinner(completionSpinnerList));
        completionSpinnerSettingBuilder.specificSettings(getSpecificCompletionsSpinners(completionSpinnerList));
        CompletionSpinnerSetting completionSpinnerSetting = completionSpinnerSettingBuilder.build();

        //Appearance
        AppearanceSettings.Builder appearanceSettingsBuilder = AppearanceSettings.builder();
        appearanceSettingsBuilder.filterSorting(filterSorting);
        appearanceSettingsBuilder.completionSpinner(completionSpinnerSetting);

        return appearanceSettingsBuilder.build();
    }

    public Map<HomeFilter, FilterSetting> getHomeFilters() {
        return blockingGet().filterSorting().home();
    }

    public Map<DataSetFilter, FilterSetting> getDataSetFiltersByUid(String uid) {
        Map<DataSetFilter, FilterSetting> filters = blockingGet().filterSorting().dataSetSettings().specificSettings().get(uid);
        if (filters == null) {
            filters = blockingGet().filterSorting().dataSetSettings().globalSettings();
        }
        return filters;
    }

    public Map<ProgramFilter, FilterSetting> getTrackedEntityTypeFilters() {
        return blockingGet().filterSorting().programSettings().globalSettings();
    }

    public Map<ProgramFilter, FilterSetting> getProgramFiltersByUid(String uid) {
        Map<ProgramFilter, FilterSetting> filters = blockingGet().filterSorting().programSettings().specificSettings().get(uid);
        if (filters == null) {
            filters = blockingGet().filterSorting().programSettings().globalSettings();
        }
        return filters;
    }

    private Map<String, CompletionSpinner> getSpecificCompletionsSpinners(List<CompletionSpinner> completionSpinnerList) {
        Map<String, CompletionSpinner> result = new HashMap<>();
        for (CompletionSpinner completionSpinner : completionSpinnerList) {
            if (completionSpinner.uid() != null) {
                result.put(completionSpinner.uid(), completionSpinner);
            }
        }

        return result;
    }

    private CompletionSpinner getGlobalCompletionSpinner(List<CompletionSpinner> completionSpinnerList) {
        for (CompletionSpinner completionSpinner : completionSpinnerList) {
            if (completionSpinner.uid() == null) {
                return completionSpinner;
            }
        }
        return null;
    }

    private Map<HomeFilter, FilterSetting> getHomeFilters(List<FilterSetting> filters) {
        Map<HomeFilter, FilterSetting> homeFilters = new HashMap<>();
        for (FilterSetting filter : filters) {
            if (Objects.equals(filter.scope(), HomeFilter.class.getSimpleName())) {
                homeFilters.put(HomeFilter.valueOf(filter.filterType()), filter);
            }
        }
        return homeFilters;
    }

    private DataSetFilters getDataSetFilters(List<FilterSetting> filters) {
        Map<DataSetFilter, FilterSetting> globalDataSetFilters = new HashMap<>();
        Map<String, Map<DataSetFilter, FilterSetting>> specificDataSetFilters = new HashMap<>();
        for (FilterSetting filter : filters) {
            if (Objects.equals(filter.scope(), DataSetFilter.class.getSimpleName())) {
                if (filter.uid() == null) {
                    globalDataSetFilters.put(DataSetFilter.valueOf(filter.filterType()), filter);
                } else {
                    Map<DataSetFilter, FilterSetting> uidFilters = specificDataSetFilters.get(filter.uid());
                    if (uidFilters != null) {
                        uidFilters.put(DataSetFilter.valueOf(filter.filterType()), filter);
                    } else {
                        Map<DataSetFilter, FilterSetting> dataSetFilters = new HashMap<>();
                        dataSetFilters.put(DataSetFilter.valueOf(filter.filterType()), filter);
                        specificDataSetFilters.put(filter.uid(), dataSetFilters);
                    }
                }
            }
        }

        DataSetFilters.Builder dataSetScopeBuilder = DataSetFilters.builder();
        dataSetScopeBuilder.globalSettings(globalDataSetFilters);
        dataSetScopeBuilder.specificSettings(specificDataSetFilters);
        return dataSetScopeBuilder.build();
    }

    private ProgramFilters getProgramFilters(List<FilterSetting> filters) {
        Map<ProgramFilter, FilterSetting> globalDataSetFilters = new HashMap<>();
        Map<String, Map<ProgramFilter, FilterSetting>> specificDataSetFilters = new HashMap<>();
        for (FilterSetting filter : filters) {
            if (Objects.equals(filter.scope(), ProgramFilter.class.getSimpleName())) {
                if (filter.uid() == null) {
                    globalDataSetFilters.put(ProgramFilter.valueOf(filter.filterType()), filter);
                } else {
                    Map<ProgramFilter, FilterSetting> uidFilters = specificDataSetFilters.get(filter.uid());
                    if (uidFilters != null) {
                        uidFilters.put(ProgramFilter.valueOf(filter.filterType()), filter);
                    } else {
                        Map<ProgramFilter, FilterSetting> dataSetFilters = new HashMap<>();
                        dataSetFilters.put(ProgramFilter.valueOf(filter.filterType()), filter);
                        specificDataSetFilters.put(filter.uid(), dataSetFilters);
                    }
                }
            }
        }

        ProgramFilters.Builder dataSetScopeBuilder = ProgramFilters.builder();
        dataSetScopeBuilder.globalSettings(globalDataSetFilters);
        dataSetScopeBuilder.specificSettings(specificDataSetFilters);
        return dataSetScopeBuilder.build();
    }

    public CompletionSpinner getGlobalCompletionSpinner() {
        List<CompletionSpinner> completionSpinnerList = completionSpinnerStore.selectAll();
        return getGlobalCompletionSpinner(completionSpinnerList);
    }

    public CompletionSpinner getCompletionSpinnerById(String uid) {
        List<CompletionSpinner> completionSpinnerList = completionSpinnerStore.selectAll();
        CompletionSpinner result = getSpecificCompletionsSpinners(completionSpinnerList).get(uid);
        if (result == null) {
            result = getGlobalCompletionSpinner();
        }
        return result;
    }
}

package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.ui.Container;

//import com.codename1.ui.*;
//import com.codename1.ui.events.ActionEvent;
//import com.codename1.ui.layouts.BoxLayout;
//import com.codename1.ui.table.TableLayout;
//import com.codename1.ui.util.Resources;
//import com.parse4cn1.ParseException;
//import java.io.IOException;
//import java.util.Map;
/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenSettingsStatistics extends ScreenSettingsCommon {

    ScreenSettingsStatistics(MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
        super(mainScreen, doneAction);
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container cont) {
//        cont.setScrollableY(true);

//        addSettingStringValues(cont, parseIdMap2, MyPrefs.statisticsGroupByDateInterval, ScreenStatistics.ShowGroupedBy.values(),
//                                () -> MyPrefs.statisticsGroupByDateInterval.getString()? "" : item.getImportance().getDescription(),
//                (s) -> item.setImportance(Item.HighMediumLow.getValue(s)));
//
//                false);
        
//        addSettingStringValues(cont, parseIdMap2, MyPrefs.statisticsSortBy, ScreenStatistics.SortStatsOn.values(), false);
        addSettingEnumAsCompGroup(cont, parseIdMap2, MyPrefs.statisticsSortBy, 
//                ScreenStatistics.SortStatsOn.values(),ScreenStatistics.SortStatsOn.getDisplayNames(), false, true);
                ScreenStatistics.SortStatsOn.getNames(),ScreenStatistics.SortStatsOn.getDisplayNames(), false, true);
//        addSettingStringValues(cont, parseIdMap2, MyPrefs.statisticsGroupBy, ScreenStatistics.ShowGroupedBy.values(), false);
        addSettingEnumAsCompGroup(cont, parseIdMap2, MyPrefs.statisticsGroupBy, 
//                ScreenStatistics.ShowGroupedBy.values(),ScreenStatistics.ShowGroupedBy.getDisplayNames(), false, true);
                ScreenStatistics.ShowGroupedBy.getNames(),ScreenStatistics.ShowGroupedBy.getDisplayNames(), false, true);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsGroupTasksUnderTheirProject);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsShowDetailsForAllLists);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsShowMostRecentFirst);
        
//        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsGroupByCategoryInsteadOfList);
        addSettingInt(cont, parseIdMap2, MyPrefs.statisticsScreenNumberPastDaysToShow, 1,365,1);

        if (false) {
            //Examples:
            addSettingBoolean(cont, parseIdMap2, MyPrefs.timerShowEffortEstimateDetails);
            addSettingInt(cont, parseIdMap2, MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds, 0, 30, 1);
            addSettingTimeInMinutes(cont, parseIdMap2, MyPrefs.timerBuzzerInterval); //disable until buzzer can run in background
        }

    }
}

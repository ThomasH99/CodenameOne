package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.events.ActionListener;

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

    ScreenSettingsStatistics(MyForm previousScreen, Runnable doneAction) { // throws ParseException, IOException {
        super(previousScreen, doneAction);
    }

    MyComponentGroup secondGroupBy;
    MyComponentGroup firstGroupBy;
    Container secondGroupByParent;

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
        if (true) {
            ActionListener setupsecondGroupByAL = e -> {
//                Container parent = secondGroupBy != null ? secondGroupBy.getParent() : null;
//                Container parent = firstGroupBy.getParent();
                if (secondGroupByParent != null) {
                    boolean noneSelected = firstGroupBy.getSelectedIndex() == 0; //0==none
                    secondGroupByParent.getParent().setHidden(noneSelected); //hide/show container with second field
                    if (noneSelected) {
                        MyPrefs.setString(MyPrefs.statisticsSecondGroupBy, ScreenStatistics2.GroupOn2.none.name()); //set to 'none'
                        //seems below is needed to add update to parseIdMap
//                        MyComponentGroup newSecondGroupBy = addSettingEnumAsCompGroup(null, parseIdMap2, MyPrefs.statisticsSecondGroupBy,
//                                ScreenStatistics2.GroupOn2.getSecondLevelGroupingNames(firstGroupBy.getSelectedIndex()),
//                                ScreenStatistics2.GroupOn2.getSecondLevelGroupingDisplayNames(firstGroupBy.getSelectedIndex()), false, true);
                    } else{
                        
                        MyPrefs.setString(MyPrefs.statisticsSecondGroupBy, (String)secondGroupBy.getSelectedValue()); //keep previously selected value if possible (if not in new option list, None is selected
                        //create/update second grouping setting based on firstGroup setting, cont=null to not add setting automatically
                        MyComponentGroup newSecondGroupBy = addSettingEnumAsCompGroup(null, parseIdMap2, MyPrefs.statisticsSecondGroupBy,
                                ScreenStatistics2.GroupOn2.getSecondLevelGroupingNames(firstGroupBy.getSelectedIndex()),
                                ScreenStatistics2.GroupOn2.getSecondLevelGroupingDisplayNames(firstGroupBy.getSelectedIndex()), false, true);
//                        newSecondGroupBy.selectValue(secondGroupBy.getSelectedValue()); //reselect the same field as before
                        if (newSecondGroupBy.getSelectedIndex() == -1) { //if previously selected field no longer available
                            newSecondGroupBy.selectIndex(0); //then simply select first value
                        }
                        secondGroupByParent.replace(secondGroupBy, newSecondGroupBy, null);
                        secondGroupBy = newSecondGroupBy;
                    }
//                    secondGroupByParent.revalidate();
                    secondGroupByParent.getParent().getParent().animateLayout(300);
                } else ASSERT.that("secondGroupByParent should never be null");
//                else { //if secondGroup wasn't shown on init (since firstGroup was None
//                    secondGroupBy = addSettingEnumAsCompGroup(cont, parseIdMap2, MyPrefs.statisticsSecondGroupBy,
//                            ScreenStatistics2.GroupOn2.getSecondLevelGroupingNames(firstGroupBy.getSelectedIndex()),
//                            ScreenStatistics2.GroupOn2.getSecondLevelGroupingDisplayNames(firstGroupBy.getSelectedIndex()), false, true);
//                    Container mainCont = firstGroupBy.getParent().getParent();
//                    int firstGrpIdx = mainCont.getComponentIndex(firstGroupBy.getParent());
//                    mainCont.addComponent(firstGrpIdx, cont);
//                    secondGroupByParent = secondGroupBy.getParent();
//                }
            };

            firstGroupBy = addSettingEnumAsCompGroup(cont, parseIdMap2, MyPrefs.statisticsFirstGroupBy,
                    ScreenStatistics2.GroupOn2.getNames(), ScreenStatistics2.GroupOn2.getDisplayNames(), false, true, setupsecondGroupByAL);

            boolean noneSelected = firstGroupBy.getSelectedIndex() == 0; //'none' selected
            if (noneSelected) { //even if none selected we create a second group setting to know where to insert a later secondGroup setting
                secondGroupBy = addSettingEnumAsCompGroup(cont, parseIdMap2, MyPrefs.statisticsSecondGroupBy,
                        ScreenStatistics2.GroupOn2.getSecondLevelGroupingNames( 0 ),
                        ScreenStatistics2.GroupOn2.getSecondLevelGroupingDisplayNames( 0), false, true);
                secondGroupByParent = secondGroupBy.getParent();
                secondGroupByParent.getParent().setHidden(noneSelected); //hide/show container with second field
            } else {
                secondGroupBy = addSettingEnumAsCompGroup(cont, parseIdMap2, MyPrefs.statisticsSecondGroupBy,
                        ScreenStatistics2.GroupOn2.getSecondLevelGroupingNames(firstGroupBy.getSelectedIndex()),
                        ScreenStatistics2.GroupOn2.getSecondLevelGroupingDisplayNames(firstGroupBy.getSelectedIndex()), false, true);
                secondGroupByParent = secondGroupBy.getParent();
            }

        } else {
////        addSettingStringValues(cont, parseIdMap2, MyPrefs.statisticsSortBy, ScreenStatistics.SortStatsOn.values(), false);
//            addSettingEnumAsCompGroup(cont, parseIdMap2, MyPrefs.statisticsSortBy,
//                    //                ScreenStatistics.SortStatsOn.values(),ScreenStatistics.SortStatsOn.getDisplayNames(), false, true);
//                    ScreenStatistics2.SortStatsOnXXX.getNames(), ScreenStatistics2.SortStatsOnXXX.getDisplayNames(), false, true);
////        addSettingStringValues(cont, parseIdMap2, MyPrefs.statisticsGroupBy, ScreenStatistics.ShowGroupedBy.values(), false);
//            addSettingEnumAsCompGroup(cont, parseIdMap2, MyPrefs.statisticsGroupBy,
//                    //                ScreenStatistics.ShowGroupedBy.values(),ScreenStatistics.ShowGroupedBy.getDisplayNames(), false, true);
//                    ScreenStatistics2.ShowGroupedByXXX.getNames(), ScreenStatistics2.ShowGroupedByXXX.getDisplayNames(), false, true);
        }
        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsGroupTasksUnderTheirProject);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsShowDetailsForAllLists);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsShowDetailsForAllTasks);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsShowMostRecentFirst);

//        addSettingBoolean(cont, parseIdMap2, MyPrefs.statisticsGroupByCategoryInsteadOfList);
        addSettingInt(cont, parseIdMap2, MyPrefs.statisticsScreenNumberPastDaysToShow, 1, 365, 1);

    }
}

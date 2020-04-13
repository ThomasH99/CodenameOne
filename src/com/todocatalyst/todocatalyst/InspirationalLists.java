/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import static com.todocatalyst.todocatalyst.FilterSortDef.compareDate;
import static com.todocatalyst.todocatalyst.FilterSortDef.compareDouble;
import static com.todocatalyst.todocatalyst.FilterSortDef.compareLong;
import java.util.Comparator;

/**
 *
 * @author thomashjelm
 */
public class InspirationalLists {

    /**
     * _X means that it cannot (currently) be created as a user-defined filter,
     * e.g. because it involves formulaes between fields or similar
     */
    enum PredefinedFilters {
        ROIoverRemaining_X, ProgressNoActuals, WarmUp_X, LastLittleEffort_X, zz, WaitingForTooLong_X, xx, yy, tt, vv, ImportantNeverGetsDone;
    }
    final static FilterSortDef ROIoverRemaining_X = new FilterSortDef("InspirROI/Rem", "Highest earned ROI relative to remaining time",
            "the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)") {
        @Override
        public boolean test(Item item) {
            return item.getEarnedValue()>0&&item.getRemaining() > 0; //only keep if remaining is not zero (otherwise divide by zero)!
        }

        Comparator<Item> getSortingComparator() {
            return (i1, i2) -> compareDouble(i1.getEarnedValue() / i1.getRemaining(), i2.getEarnedValue() / i2.getRemaining());
        }
    };

    final static FilterSortDef UrgentByCreated = new FilterSortDef("OldestUrgent", "Urgent tasks with oldest creation date first",
            "the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)") {
        @Override
        public boolean test(Item item) {
            return item.getUrgencyN()!=null && (item.getUrgencyN()==HighMediumLow.HIGH); //only keep if remaining is not zero (otherwise divide by zero)!
        }

        Comparator<Item> getSortingComparator() {
            return (i1, i2) -> compareDate(i1.getCreatedDateD(), i2.getCreatedAt());
        }
    };

    public static FilterSortDef makeFilter(PredefinedFilters predefinedFilter, String description, String helpTxt) {
        FilterSortDef filter = null;
        switch (predefinedFilter) {
            case ROIoverRemaining_X:
                filter = new FilterSortDef() {
                    public boolean test(Item item) {
                        return true;
                    }

                    Comparator<Item> getSortingComparator() {
                        return (i1, i2) -> compareDouble(i1.getEarnedValue() / i1.getRemaining(), i2.getEarnedValue() / i2.getRemaining());
                    }
                };
                filter.setFilterName(PredefinedFilters.ROIoverRemaining_X.toString());
                filter.setDescription("Highest earned ROI relative to remaining time");
                filter.setHelp("the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)");
                break;

            case ProgressNoActuals:
                filter = new FilterSortDef() {
                    public boolean test(Item item) {
                        return item.getStatus() == ItemStatus.ONGOING && item.getActual() == 0;
                    }

                    Comparator<Item> getSortingComparator() {
                        return (i1, i2) -> compareDate(i1.getUpdatedAt(), i2.getUpdatedAt());
                    }
                };
                filter.setFilterName(PredefinedFilters.ROIoverRemaining_X.toString());
                filter.setDescription("Tasks in progress but without any actual time recorded");
                filter.setHelp("the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)");
                filter.setDefinition("Status is ONGOING, Actual is zero, sorted with most recent last");
                break;
            case WarmUp_X:
                filter = new FilterSortDef() {
                    public boolean test(Item item) {
                        return (item.getStatus() == ItemStatus.CREATED || item.getStatus() == ItemStatus.ONGOING)
                                && item.getRemaining() <= 10 * MyDate.MINUTE_IN_MILLISECONDS
                                && (item.getChallengeN() != null && (item.getChallengeN() == Challenge.VERY_EASY || item.getChallengeN() == Challenge.EASY));
                    }

                    Comparator<Item> getSortingComparator() {
                        return (i1, i2) -> compareLong(i1.getRemaining(), i2.getRemaining());
                    }
                };
                filter.setFilterName(PredefinedFilters.ROIoverRemaining_X.toString());
                filter.setDescription("quick easy tasks to get that dopamine flowing");
                filter.setHelp("**the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)");
                filter.setDefinition("**Status is ONGOING, Actual is zero, sorted with most recent last");
                break;
            case LastLittleEffort_X:
                filter = new FilterSortDef() {
                    public boolean test(Item item) {
                        return (item.getStatus() == ItemStatus.CREATED || item.getStatus() == ItemStatus.ONGOING);
                    }

                    Comparator<Item> getSortingComparator() {
                        return (i1, i2) -> compareLong(i1.getRemaining() / i1.getActual(), i2.getRemaining() / i2.getActual());
                    }
                };
                filter.setFilterName(PredefinedFilters.ROIoverRemaining_X.toString());
                filter.setDescription("Just a last little effort to finish");
                filter.setHelp("**the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)");
                filter.setDefinition("**Sort tasks/projects on ratio of remaining effort over total time already worked");
                break;
            case WaitingForTooLong_X:
                filter = new FilterSortDef() {
                    public boolean test(Item item) {
                        return (item.getStatus() == ItemStatus.WAITING
                                && ((item.getWaitingTillDate().getTime() < MyDate.currentTimeMillis())
                                || item.getDateWhenSetWaiting() == null && item.getDateWhenSetWaiting().getTime() < MyDate.currentTimeMillis()));
                    }

                    Comparator<Item> getSortingComparator() {
                        return (i1, i2) -> compareDate(i1.getWaitingTillDate() != null ? i1.getWaitingTillDate() : i1.getDateWhenSetWaiting(),
                                i2.getWaitingTillDate() != null ? i2.getWaitingTillDate() : i2.getDateWhenSetWaiting()); //waiting the longest first
                    }
                };
                filter.setFilterName(PredefinedFilters.ROIoverRemaining_X.toString());
                filter.setDescription("Waiting for too long");
                filter.setHelp("**the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)");
                filter.setDefinition("Sort Waiting tasks/projects where either Waiting Till date is passed or which were set Waiting a long time ago");
                break;
            case ImportantNeverGetsDone:
                filter = new FilterSortDef() {
                    public boolean test(Item item) {
                        return (item.getStatus() == ItemStatus.CREATED || item.getStatus() == ItemStatus.ONGOING || item.getStatus() == ItemStatus.WAITING)
                                && ((item.getImportanceN() == HighMediumLow.HIGH)
                                && (item.getUrgencyN() == null || item.getUrgencyN() == HighMediumLow.LOW || item.getUrgencyN() == HighMediumLow.MEDIUM))
                                && (item.getChallengeN() == null || item.getChallengeN() == Challenge.VERY_HARD || item.getChallengeN() == Challenge.HARD)
                                && (item.getActual() == 0 || item.getActual() > 40 * MyDate.HOUR_IN_MILISECONDS)
                                && (item.getDueDateD() != null && item.getDueDateD().getTime() < MyDate.currentTimeMillis() //overdue
                                || (item.getUpdatedAt() != null && item.getUpdatedAt().getTime() < MyDate.currentTimeMillis() - 90 * MyDate.DAY_IN_MILLISECONDS)); //or not touched since 90 days
                    }

                    Comparator<Item> getSortingComparator() {
                        return (i1, i2) -> compareDate(i1.getWaitingTillDate() != null ? i1.getWaitingTillDate() : i1.getDateWhenSetWaiting(),
                                i2.getWaitingTillDate() != null ? i2.getWaitingTillDate() : i2.getDateWhenSetWaiting()); //waiting the longest first
                    }
                };
                filter.setFilterName(PredefinedFilters.ROIoverRemaining_X.toString());
                filter.setDescription("Important but never gets done");
                filter.setHelp("**the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)");
                filter.setDefinition("What never gets done: Important, not Urgent and Challenging or time consuming");
                break;
        }
        return filter;
    }

    public static FilterSortDef makeFilterSort(String predefinedFilter) {
        switch (predefinedFilter) {
            case "":
//                return new FilterSortDef(Item.PARSE_COMPLETED_DATE, MyPrefs.statisticsShowMostRecentFirst.getBoolean(), 
//                        "Highest earned ROI relative to remaining time");
                break;
            case "x":
//                return FilterSortDef.getMultipleComparator(new String[]{Item.PARSE_COMPLETED_DATE, Item.PARSE_OWNER_LIST}, new boolean[]{false, false});
            default:

        }
        return null;
    }

}

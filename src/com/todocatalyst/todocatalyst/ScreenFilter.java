package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.Resources;
//import com.codename1.ui.*;
import com.codename1.ui.table.TableLayout;
import com.parse4cn1.ParseQuery;

//import com.codename1.ui.*;
//import com.codename1.ui.events.ActionEvent;
//import com.codename1.ui.layouts.BoxLayout;
//import com.codename1.ui.table.TableLayout;
//import com.codename1.ui.util.Resources;
//import com.parse4cn1.ParseException;
//import java.io.IOException;
//import java.util.Map;
/**
 * Select filters on Todo Items
 *
 * @author Thomas
 */
public class ScreenFilter extends MyForm {
    //TODO add option to save filter as 'shared' (can be used by other screens/) or named (
    //TODO add option to set a filter as default
    //DONE save filter from session to session (in each screen using filter/sort)
    //DONE: add option to sort on combination of Importance/Urgency "By Importance/Urgency"
    //TODO: add support to sorting based on ROI "Value per hour"

//    private static String SCREEN_TITLE = "Which tasks to show";
    private static String SCREEN_TITLE = "Sort and Filter";
    FilterSortDef filterSortDef;
//<editor-fold defaultstate="collapsed" desc="comment">
//    public interface Predicate<T> {
//
//        public boolean apply(T type);
//    }
//
//    class FilteredArrayList<T> extends ArrayList {
//
//        public /*static*/ Collection filter(Collection target,
//                        Predicate predicate) {
//            Collection filteredCollection = new ArrayList();
//
//            /* contains */
//            String field = null;
//            Item item = null;
//            Object value = null;
//            if (item.has(field) || item.get(field).equals(value) || ((Date) item.get(field).equals(value))
//
//                for (T t : target) {
//                    if (predicate.apply(t)) {
//                        filteredCollection.add(t);
//                    }
//                }
//            return filteredCollection;
//        }
//    }
//</editor-fold>

    ScreenFilter(FilterSortDef filterSortDef, MyForm previousForm, UpdateField updateActionOnDone) {
        super(SCREEN_TITLE, previousForm, updateActionOnDone);
        this.filterSortDef = filterSortDef;
//        FilterPredicate filterPredicate, MyHashMap filterHashMap, 
//        this.filterPredicate = filterPredicate;
//        this.filterSortDef.filterMap = filterMap;
//        this.updateActionOnDone = updateActionOnDone; //() -> {}; //no action (updating the filters are done in parseIdMap
//        setScrollableY(true); //set by default
        setLayout(BoxLayout.y());
        setScrollableY(true);
        addCommandsToToolbar(getToolbar());
//        buildContentPane(getContentPane());
        refreshAfterEdit();
//        getContentPane().setScrollableY(true);
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        restoreKeepPos();
          super.refreshAfterEdit();
  }

    public void addCommandsToToolbar(Toolbar toolbar) {

        //DONE/BACK
//        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand());
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }

        toolbar.addCommandToOverflowMenu(new Command("Save**")); //save with name for reuse elsewhere (how useful is it with the current few options??)
        toolbar.addCommandToOverflowMenu(new Command("Copy existing filter**")); //save with name for reuse elsewhere (how useful is it with the current few options??)
        toolbar.addCommandToOverflowMenu(new Command("Reset to default**")); //reset to default values
    }

    private Container sortContainer;
    private Container sortSelectorContainer;
    private Label hideSortSelectorContainerLabel = new Label("");

    /**
     * This method shows the main user interface of the app
     *
     * @param back indicates if we are currently going back to the main form
     * which will display it with a back transition
     * @param errorMessage an error message in case we are returning from a
     * search error
     * @param listings the listing of alternate spellings in case there was an
     * error on the server that wants us to prompt the user for different
     * spellings
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    private Container buildContentPane(Container content) {
        content.setScrollableY(true);
//        Container content = new Container();
//TableLayout tl = new TableLayout(2, 3); 
//Form hi = new Form("Table Layout Cons", tl);
        if (false) {
            TableLayout tl;
//        int spanButton = 2;
            int nbFields = 30;
            if (Display.getInstance().isTablet() || true) {
                tl = new TableLayout(nbFields, 2);
            } else {
                tl = new TableLayout(nbFields * 2, 1);
//            spanButton = 1;
            }
            tl.setGrowHorizontally(true);
//        setScrollableY(true);
            content.setLayout(tl);
//        content.setLayout(BoxLayout.y());
            TableLayout.Constraint rightAdj = tl.createConstraint().horizontalAlign(Component.RIGHT).widthPercentage(20);
        }
//        ParseQuery query = null;
        //SORT
//        boolean ascending = true;
        MyOnOffSwitch inverseOrder = new MyOnOffSwitch(parseIdMap2, () -> {
            return filterSortDef.isSortDescending();
        }, (b) -> {/*button value used directly*/
            filterSortDef.setSortDescending(b);
        });

//        if (filterSortDef.sortOptions.length != filterSortDef.sortField.length) {
//            throw new RuntimeException(); //check that length is the same
//        }
        assert filterSortDef.getSortOptions().length == filterSortDef.getSortField().length;
//        content.add(new Label("Manual sorting"));
        MyOnOffSwitch sortOnSwitch = new MyOnOffSwitch(parseIdMap2,
                () -> {
                    return !filterSortDef.isSortOn();
                },
                (b) -> {
                    filterSortDef.setSortOn(!b);
                });
//        sortOnSwitch.setOn("Sort");
//        sortOnSwitch.setOff("Manual");
        sortOnSwitch.addActionListener((e) -> {
            if (sortOnSwitch.isValue()) {
                //TODO!!! do the animation at the level above (like in Timer)
//                sortContainer.replaceAndWait(sortSelectorContainer, hideSortSelectorContainerLabel, CommonTransitions.createFade(300));
                sortContainer.replaceAndWait(sortSelectorContainer, hideSortSelectorContainerLabel, CommonTransitions.createFade(300));
            } else {
                sortContainer.replaceAndWait(hideSortSelectorContainerLabel, sortSelectorContainer, CommonTransitions.createFade(300));
            }
        });
//        content.add(sortOnSwitch);
//        sortContainer = Container.encloseIn(BoxLayout.y(), new Label("Manual sorting"), sortOnSwitch);
        sortContainer = BorderLayout.west(new SpanLabel("Manual sorting")).add(BorderLayout.EAST, sortOnSwitch);
        sortSelectorContainer
                = Container.encloseIn(BoxLayout.y(),
                        BorderLayout.west(new SpanLabel("Sort on")).add(BorderLayout.EAST,
                                new MyStringPicker(filterSortDef.getSortOptions(), parseIdMap2, () -> {
                                    String sortId = filterSortDef.getSortFieldId();
                                    for (int i = 0, size = filterSortDef.getSortField().length; i < size; i++) {
                                        if (filterSortDef.getSortField()[i].equals(sortId)) {
                                            return i;
                                        }
                                    }
                                    return 0; //default is first sort field selected
                                }, (i) -> {
//                    switch (filterSortDef.sortOptions[i]) {
//                        default: //for all other, simply add to query
//                            if (inverseOrder.isValue()) {
//                                if (query!=null) query.addDescendingOrder(filterSortDef.sortField[i]);
//                            } else {
//                                if (query!=null) query.addAscendingOrder(filterSortDef.sortField[i]);
//                            }
//                    };
                                    filterSortDef.setSortFieldId(filterSortDef.getSortField()[i]);
                                })), BorderLayout.west(new SpanLabel("Reverse sort order")).add(BorderLayout.EAST, inverseOrder));
        if (filterSortDef.isSortOn()) {
            sortContainer.add(BorderLayout.SOUTH, sortSelectorContainer);
        } else {
            sortContainer.add(BorderLayout.SOUTH, hideSortSelectorContainerLabel);
        }
//        content.add(tl.createConstraint().horizontalSpan(2), sortContainer);
        content.add(sortContainer);

        //FILTER
        //Select Done or not
//        new Container().add(MyOnOffSwitch doneTasks = new MyOnOffSwitch(parseIdMap2, () -> {return false;}, 
//        content.add(tl.createConstraint().horizontalSpan(2), new SpanLabel("Show tasks with status:"));
        content.add(new SpanLabel("Show tasks with status:"));
//        content.add(new SpanLabel(ItemStatus.CREATED.getName())).add(rightAdj, new MyOnOffSwitch(parseIdMap2,
        content.add(layout(ItemStatus.CREATED.getName(), new MyOnOffSwitch(parseIdMap2,
                () -> {
                    return filterSortDef.isShowNewTasks();
                },
                (b) -> {
                    filterSortDef.setShowNewTasks(b);
                }), "**"));
//                content.add(new SpanLabel(ItemStatus.ONGOING.getName())).add(rightAdj, new MyOnOffSwitch(parseIdMap2, () -> {
        content.add(layout(ItemStatus.ONGOING.getName(), new MyOnOffSwitch(parseIdMap2, () -> {
            return filterSortDef.isShowOngoingTasks();
        },
                (b) -> {
                    filterSortDef.setShowOngoingTasks(b);
                }), "**"));
//                content.add(new SpanLabel(ItemStatus.WAITING.getName())).add(rightAdj, new MyOnOffSwitch(parseIdMap2, () -> {
        content.add(layout(ItemStatus.WAITING.getName(), new MyOnOffSwitch(parseIdMap2, () -> {
            return filterSortDef.isShowWaitingTasks();
        },
                (b) -> {
                    filterSortDef.setShowWaitingTasks(b);
                }), "**"));
//                content.add(new SpanLabel(ItemStatus.DONE.getName())).add(rightAdj, new MyOnOffSwitch(parseIdMap2, () -> {
        content.add(layout(ItemStatus.DONE.getName(), new MyOnOffSwitch(parseIdMap2, () -> {
            return filterSortDef.isShowDoneTasks();
        },
                (b) -> {
                    filterSortDef.setShowDoneTasks(b);
                }), "**"));
//                content.add(new SpanLabel(ItemStatus.CANCELLED.getName())).add(rightAdj, new MyOnOffSwitch(parseIdMap2, () -> {
        content.add(layout(ItemStatus.CANCELLED.getName(), new MyOnOffSwitch(parseIdMap2, () -> {
            return filterSortDef.isShowCancelledTasks();
        },
                (b) -> {
                    filterSortDef.setShowCancelledTasks(b);
                }), "**"));

        //show even before Hide until date
//        content.add(tl.createConstraint().horizontalSpan(2), new SpanLabel("Show hidden tasks"));
        content.add(new SpanLabel("Show hidden tasks"));
//        content.add(new SpanLabel("Before Hide until date")).add(rightAdj, new MyOnOffSwitch(parseIdMap2,
        content.add(layout("Before Hide until date", new MyOnOffSwitch(parseIdMap2,
                () -> {
                    return filterSortDef.isShowBeforeHideUntilDate();
                },
                //only show if hideUntil date is passed (is larger than now)
                (b) -> {
                    filterSortDef.setShowBeforeHideUntilDate(b);
                }
        ), "**")); //if show hidden tasks, then no add'l query constraint

        //show even if expiredDate is passed
//        content.add(new SpanLabel("Expired tasks")).add(rightAdj, new MyOnOffSwitch(parseIdMap2,
        content.add(layout("Expired tasks", new MyOnOffSwitch(parseIdMap2,
                () -> {
                    return filterSortDef.isShowExpiresOnDate();
                },
                //only show if expire is not passed (is smaller than now)
                (b) -> {
                    filterSortDef.setShowExpiresOnDate(b);
                }), "**")); //if show hidden tasks, then no add'l query constraint

//        content.add(tl.createConstraint().horizontalSpan(2), new SpanLabel("Show only certain types of tasks"));
        content.add(new SpanLabel("Show only certain types of tasks"));
        //Projects
//        content.add(new SpanLabel("Projects")).add(rightAdj, new MyOnOffSwitch(parseIdMap2, () -> {
        content.add(layout("Projects", new MyOnOffSwitch(parseIdMap2, () -> {
            return filterSortDef.isShowProjectsOnly();
        }, (b) -> {
            filterSortDef.setShowProjectsOnly(b);
        }), "**"));

//                .add(new Label("Leaf tasks"))
//                .add(new MyOnOffSwitch(parseIdMap2, () -> {
//                    return  filterSortDef.showProjectsOnly;
//                }, (b) -> {
//                        filterSortDef.showProjectsOnly = b;
//                }));
        //Interrupt tasks
//       content.add(new SpanLabel("Interrupt tasks")).add(rightAdj, new MyOnOffSwitch(parseIdMap2,
        content.add(layout("Interrupt tasks", new MyOnOffSwitch(parseIdMap2,
                () -> {
                    return filterSortDef.isShowInterruptTasksOnly();
                }, (b) -> {
                    filterSortDef.setShowInterruptTasksOnly(b);
                }), "**"));

        //Estimates all/with/without
//        content.add(new SpanLabel("With estimates")).add(rightAdj, new MyOnOffSwitch(parseIdMap2, () -> {
        content.add(layout("With estimates", new MyOnOffSwitch(parseIdMap2, () -> {
            return filterSortDef.isShowWithoutEstimatesOnly();
        }, (b) -> {
            filterSortDef.setShowWithoutEstimatesOnly(b);
        }), "**"));

        //Actual all/with/withoutno/all
//        content.add(new SpanLabel("With work time")).add(rightAdj, new MyOnOffSwitch(parseIdMap2, () -> {
        content.add(layout("With work time", new MyOnOffSwitch(parseIdMap2, () -> {
            return filterSortDef.isShowWithActualsOnly();
        }, (b) -> {
            filterSortDef.setShowWithActualsOnly(b);
        }), "**"));

        //Depending on tasks
//        content.add(new SpanLabel("Tasks depending on other tasks")).add(rightAdj, new MyOnOffSwitch(parseIdMap2,
        content.add(layout("Tasks depending on other tasks", new MyOnOffSwitch(parseIdMap2,
                () -> {
                    return filterSortDef.isShowDependingOnUndoneTasks();
                },
                //only show if hideUntil date is passed (is larger than now)
                (b) -> {
                    filterSortDef.setShowDependingOnUndoneTasks(b);
                }
        ), "**")); //if show hidden tasks, then no add'l query constraint

//        ButtonGroup taskStatusSwitches = new ButtonGroup(); //TODO mutually exclusive choices (necessary??)
        //SAVE filter
        //TODO change UI to a [SAVE] button + pop-up to enter name+purpose + [SAVE] and [CANCEL] buttons
//        content
//                .add(new Label("Enter name and purpose to save this view"))
//                .add(new Label("Name"))
//                .add(new MyTextField(screenTitle, parseIdMap2, getValue, setValue))
//                .add(new Label("Purpose"))
//                .add(new MyTextField(screenTitle, parseIdMap2, getValue, setValue))
//                .add(new Button("Save", parseIdMap2, getValue, setValue))
//                .add(new Button("Delete", parseIdMap2, getValue, setValue))
//        content.add(new Label(""));
        return content;
    }
}

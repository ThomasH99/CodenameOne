/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.io.Log;
import com.codename1.ui.Container;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;

/**
 *
 * @author thomashjelm
 */
//public class ScreenRepeatRuleHistory {

/**
 *
 * @author Thomas
 */
public class ScreenRepeatRuleInstancesOverview extends MyForm {

    private static String screenTitle = RepeatRuleParseObject.REPEAT_RULE+" history"; //TODO!!! replace history by for example: "overview of tasks(workSlots
    private RepeatRuleParseObject repeatRule;

    ScreenRepeatRuleInstancesOverview(RepeatRuleParseObject repeatRule, MyForm previousForm) { //throws ParseException, IOException {
        this(repeatRule, previousForm, null);
    }

    ScreenRepeatRuleInstancesOverview(RepeatRuleParseObject repeatRule, MyForm previousForm, Runnable doneAction) { //throws ParseException, IOException {
        super("", previousForm, doneAction);
        setTitle(screenTitle);
        this.repeatRule=repeatRule;
        setUniqueFormId("ScreenRepeatRuleInstancesOverview");
        setLayout(BoxLayout.y());
        setScrollableY(true); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        addCommandsToToolbar(getToolbar());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        super.refreshAfterEdit();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {

        super.addCommandsToToolbar(toolbar);
        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand());

        if (false&&MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) { //currently no editing in this screen, so no reason to Cancel
            toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> {
                Log.p("Clicked");
                showPreviousScreen(true); //false);
            });
        }
    }

    private Container buildContentPane(Container content) {
        
//                if (repeatRule != null && repeatRule.getListOfUndoneInstances().size() > 0) {
                if (repeatRule != null ) {
                    
                    long now = MyDate.currentTimeMillis();
                    
                    Container dones = new Container(BoxLayout.y()); //should automatically be sorted by completion date
                    Container undones = new Container(BoxLayout.y()); //should automatically be sorted by due date
                    
                    ExpandableContainer showDones = new ExpandableContainer("Past instances", dones, ()->true, null); //true: default collapse past repeat
                    ExpandableContainer showUndones = new ExpandableContainer("Current instances", undones, ()->false, null);
                    
                    for (Object e: repeatRule.getListOfDoneInstances()) {
                        if (e instanceof Item)
                            dones.add(ScreenListOfItems.buildItemContainer(ScreenRepeatRuleInstancesOverview.this, (Item) e, null, null));
                        else if (e instanceof WorkSlot)
                            dones.add(ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) e, ScreenRepeatRuleInstancesOverview.this, null, false, false, now));
                    }
                    for (Object e: repeatRule.getListOfUndoneInstances()) {
                        if (e instanceof Item)
                            undones.add(ScreenListOfItems.buildItemContainer(ScreenRepeatRuleInstancesOverview.this, (Item) e, null, null));
                        else if (e instanceof WorkSlot)
                            undones.add(ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) e, ScreenRepeatRuleInstancesOverview.this, null, false, false, now));
                    }
                    
                    add(showDones);
                    add(showUndones);
//<editor-fold defaultstate="collapsed" desc="comment">
//            Container repeatRuleDetailsContainer = content;
//            Container repeatRuleHideableDetailsContainer = content;
//
//            Button buttonRepeatRuleHistory = new Button();
//            repeatRuleHideableDetailsContainer.setHidden(MyPrefs.repeatHidePreviousTasksDetails.getBoolean()); //UI: default hidden
//            buttonRepeatRuleHistory.setMaterialIcon(repeatRuleHideableDetailsContainer.isHidden() ? Icons.iconShowMore : Icons.iconShowLess); //switch icon
//            buttonRepeatRuleHistory.addActionListener((e) -> {
//                if (repeatRuleHideableDetailsContainer.getComponentCount() == 0) { //lazy evaluation
//                    List<RepeatRuleObjectInterface> list = repeatRule.getListOfUndoneInstances();
////                    long now = MyDate.currentTimeMillis(); //use a single value of now
//                    for (int i = 0, size = list.size(); i < size; i++) {
//                        RepeatRuleObjectInterface item = list.get(i);
//                        if (item instanceof Item) {
////                            repeatRuleHideableDetailsContainer.add(ScreenListOfItems.buildItemContainer((Item) item, null, () -> false, null, false, null));
//                            repeatRuleHideableDetailsContainer.add(ScreenListOfItems.buildItemContainer(ScreenRepeatRuleInstancesOverview.this, (Item) item, null, null));
//                        } else if (item instanceof WorkSlot) {
//                            repeatRuleHideableDetailsContainer.add(ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) item, ScreenRepeatRuleInstancesOverview.this, null, false, false, now));
//                        }
//                    }
//
//                    if (repeatRule.getLatestDateCompletedOrCancelled().getTime() != 0) {
//                        repeatRuleHideableDetailsContainer.add(Format.f("Total number of repeats {0}", "" + repeatRule.getTotalNumberOfInstancesGeneratedSoFar()));
//                    }
//
////                    repeatRuleHideableDetailsContainer.add(Format.f("Due date of last completed task {0}", MyDate.formatDateNew(myRepeatRule.getLatestDateCompletedOrCancelled())));
//                    repeatRuleHideableDetailsContainer.add(Format.f(
//                            "Last Due date of all completed tasks {0}", MyDate.formatDateNew(repeatRule.getLatestDateCompletedOrCancelled())));
//
//                    repeatRuleHideableDetailsContainer.add(new Button(MyReplayCommand.create("Show all tasks", Format.f("Show all {0 getTotalNumberOfInstancesGeneratedSoFar} tasks", "" + repeatRule.getTotalNumberOfInstancesGeneratedSoFar()), null, (ev) -> {
//                        new ScreenListOfItems("", new ItemList(DAO.getInstance().getAllItemsForRepeatRule(repeatRule), true), (MyForm) motherContainer.getComponentForm(), (i) -> {
//                            refreshAfterEdit();
//                        }, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP | ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
//                                | ScreenListOfItems.OPTION_NO_INTERRUPT | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME).show();
//
//                    }))); //TODO!!!!
//                }
//
//                MyPrefs.repeatHidePreviousTasksDetails.flipBoolean();
//                repeatRuleHideableDetailsContainer.setHidden(MyPrefs.repeatHidePreviousTasksDetails.getBoolean());
//                buttonRepeatRuleHistory.setMaterialIcon(repeatRuleHideableDetailsContainer.isHidden() ? Icons.iconShowMore : Icons.iconShowLess); //switch icon
//                animateLayout(ANIMATION_TIME_DEFAULT);
//            });
//
//
//            repeatRuleDetailsContainer = MyBorderLayout.center(FlowLayout.encloseCenter(new Label(Format.f("{0 total number repeats generated so far} tasks, {1 tasksCreatedNotDoneYet} active",
//                    "" + repeatRule.getTotalNumberOfInstancesGeneratedSoFar(),
//                    "" + repeatRule.getListOfUndoneInstances().size()))))
//                    .add(My
//                    BorderLayout.EAST, buttonRepeatRuleHistory).add(MyBorderLayout.SOUTH, repeatRuleHideableDetailsContainer);
//</editor-fold>
        }
        return content;
    }
}

//}

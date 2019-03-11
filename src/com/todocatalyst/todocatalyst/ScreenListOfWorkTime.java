/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.ui.*;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.parse4cn1.ParseObject;
import java.util.Date;
import java.util.List;
//import com.java4less.rchart.*;
//import javax.microedition.io.ConnectionNotFoundException;
//import javax.microedition.io.PushRegistry;
//import javax.microedition.m3g.Background;
//import org.joda.time.base.BaseInterval;

/**
 * shows the details of allocated workTime
 *
 * @author Thomas
 */
public class ScreenListOfWorkTime extends MyForm {
    //TODO!! make the workSlots editable directly inline in the list (more natural to edit in an overview of all workslots

    static String SCREEN_TITLE = "Work time details: ";
// protected static String FORM_UNIQUE_ID = "ScreenListOfWorkSlTime"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    private ItemAndListCommonInterface owner;
    private WorkTimeSlices workTime;

//    ScreenListOfWorkTime(String nameOfOwner, WorkTimeSlices workTime, MyForm previousForm) {
    ScreenListOfWorkTime(ItemAndListCommonInterface owner, WorkTimeSlices workTime, MyForm previousForm) {
//        super("Work time for " + nameOfOwner, previousForm, () -> updateItemListOnDone.update(workSlotList));
//        super(SCREEN_TITLE + ((nameOfOwner != null && nameOfOwner.length() > 0) ? " for " + nameOfOwner : ""), previousForm, () -> updateItemListOnDone.update(workTime));
        super(SCREEN_TITLE + owner.getText(), previousForm, () -> {
        });
        this.workTime = workTime;
        setUniqueFormId("ScreenListOfWorkTime");
//        setUpdateItemListOnDone(updateItemListOnDone);
//        this.workSlotList = workSLotList;
        this.owner = owner;
        addCommandsToToolbar(getToolbar());
        setLayout(BoxLayout.y());
        getContentPane().setScrollableY(true);
//        String expandedObjectsFileName = getUniqueFormId() + owner.getObjectIdP();
//        expandedObjects = new ExpandedObjects(expandedObjectsFileName); //no persistance if filename and is empty (e.g. like with list of project subtasks)
        expandedObjects = new ExpandedObjects(getUniqueFormId(), (ParseObject) owner); //no persistance if filename and is empty (e.g. like with list of project subtasks)

//        getContentPane().add(buildContentPaneForWorkSlotList(workTime));
//        buildContentPaneForWorkSlotList(workTime);
        refreshAfterEdit();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        //BACK
//        toolbar.addCommandToLeftBar(makeDoneCommand("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle())));
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(true));

        //CANCEL - not relevant, all edits are done immediately so not possible to cancel
    }

    protected void buildContentPaneForWorkSlotList(WorkTimeSlices workTime) {
//        Container cont =  new Container(BoxLayout.y());
        Container cont = getContentPane();
        cont.removeAll();
        if (workTime != null) {
            for (WorkSlotSlice workSlice : workTime.getWorkSlotSlices()) {
                if (false && workSlice.getDuration() == 0) { //don't filter these since they are used for 
                    continue;
                }
                Container sliceCont = new Container(new FlowLayout());
                sliceCont
                        .add(new Label((workSlice.workSlot.getOwner().getText()) + " "
                                + MyDate.formatDateTimeNew(new Date(workSlice.getStartTime()))
//                                + "-" + MyDate.formatTimeNew(new Date(workSlice.getEndTime()))));
                                + " " + MyDate.formatDurationShort(workSlice.getDuration())));
                if (Config.TEST)
                    sliceCont.add(new Label("\"" + workSlice.workSlot.getText() + "\" [" + workSlice.workSlot.getObjectIdP() + "]"));
//                    .add(new Label(MyDate.formatDateNew(workSlice.getStartTime())))
//                    .add(new Label(MyDate.formatTimeDuration(workSlice.getDurationInMillis())));
                cont.add(sliceCont);
            }
        } else {
            cont.add("No workslots**");
        }
//        return cont;
    }

    @Override
    public void refreshAfterEdit() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        ASSERT.that("Not supported yet.");
        buildContentPaneForWorkSlotList(workTime);
        super.refreshAfterEdit();

    }

}

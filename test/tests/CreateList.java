package tests;

import com.codename1.testing.AbstractTest;

import com.codename1.ui.Display;

public class CreateList extends AbstractTest {
    public boolean runTest() throws Exception {
        waitForFormTitle("TodoCatalyst");
        clickButtonByPath(new int[]{0, 5, 0});
        waitForFormTitle("Lists");
        Display.getInstance().getCurrent().setName("Form_1");
        assertEqual(getToolbarCommands().length, 6);
        executeToolbarCommandAtOffset(4);
        waitForFormName("Form_1");
        clickButtonByPath(new int[]{0, 0, 0, 2, 0, 1, 2});
        waitForFormName("Form_1");
        waitForFormTitle("List");
        Display.getInstance().getCurrent().setName("Form_2");
        setText(new int[]{0, 1}, "ListAuto");
        setText(new int[]{1, 1}, "DescriptionText");
        assertEqual(getToolbarCommands().length, 5);
        executeToolbarCommandAtOffset(0);
        waitForFormName("Form_1");
        clickButtonByPath(new int[]{0, 0, 0, 2, 0, 1, 1});
        waitForFormTitle("ListAuto");
        Display.getInstance().getCurrent().setName("Form_3");
        setText("InlineInsert text field", "");
        assertEqual(getToolbarCommands().length, 17);
        executeToolbarCommandAtOffset(1);
        waitForFormName("Form_1");
        setText(new int[]{1, 0, 2, 0, 0}, "");
        clickButtonByPath(new int[]{0, 0, 0, 2, 0, 1, 1});
        waitForFormTitle("ListAuto");
        Display.getInstance().getCurrent().setName("Form_4");
        setText("InlineInsert text field", "");
        assertEqual(getToolbarCommands().length, 17);
        executeToolbarCommandAtOffset(6);
        waitForFormName("Form_4");
        pointerPress(0.796f, 0.103418805f, "InlineInsertNewItemContainer2");
        waitFor(82);
        pointerRelease(0.796f, 0.103418805f, "InlineInsertNewItemContainer2");
        waitForFormName("Form_4");
        waitForFormTitle("List");
        Display.getInstance().getCurrent().setName("Form_5");
        setText(new int[]{0, 1}, "ListAuto");
        assertTextArea("List name");
        assertTextArea("ListAuto");
        assertTextArea("Description");
        assertTextArea("DescriptionText");
        assertTextArea("Created");
        assertTextArea("Modified");
        assertTextArea("Id");
        assertLabel("");
        assertLabel("");
        assertLabel("");
        assertLabel("14/04/2019");
        assertLabel("");
        assertLabel("14/04/2019");
        assertLabel("");
        assertLabel("3rdDefst6D");
        assertLabel("3rdDefst6D");
        assertLabel("");
        assertEqual(getToolbarCommands().length, 5);
        executeToolbarCommandAtOffset(0);
        waitForFormTitle("Error");
        Display.getInstance().getCurrent().setName("Form_6");
        goBack();
        waitForFormName("Form_5");
        setText(new int[]{0, 1}, "ListAuto");
        return true;
    }
}

package tests;

import com.codename1.testing.AbstractTest;

import com.codename1.ui.Display;

public class CreateListAndTask extends AbstractTest {
    public boolean runTest() throws Exception {
        setText("InlineInsert text field", "");
        waitForFormTitle("TodoCatalyst");
        clickButtonByLabel("Lists");
        waitForFormTitle("Lists");
        Display.getInstance().getCurrent().setName("Form_1");
        assertEqual(getToolbarCommands().length, 8);
        executeToolbarCommandAtOffset(5);
        waitForFormName("Form_1");
        waitForFormName("Form_1");
        waitForFormTitle("List");
        Display.getInstance().getCurrent().setName("Form_2");
        setText(new int[]{0, 1, 0}, "");
        pointerPress(0.31733334f, 0.21808511f, new int[]{0});
        waitFor(128);
        pointerRelease(0.31733334f, 0.21808511f, new int[]{0});
        pointerPress(0.13157895f, 0.5980392f, new int[]{0, 1});
        waitFor(96);
        pointerRelease(0.13157895f, 0.5980392f, new int[]{0, 1});
        setText(new int[]{0, 1, 0}, "L10");
        assertEqual(getToolbarCommands().length, 5);
        executeToolbarCommandAtOffset(0);
        waitForFormName("Form_1");
        clickButtonByPath(new int[]{1, 0, 0, 2, 0, 1, 2});
        waitForFormTitle("L10");
        Display.getInstance().getCurrent().setName("Form_3");
        setText("InlineInsert text field", "");
        assertEqual(getToolbarCommands().length, 20);
        executeToolbarCommandAtOffset(6);
        waitForFormName("Form_3");
        waitForFormName("Form_3");
        waitForUnnamedForm();
        Display.getInstance().getCurrent().setName("Form_4");
        setText(new int[]{0, 0, 0, 0}, "task1");
        setText(new int[]{0, 0, 0, 1, 0}, "notes task1");
        waitForUnnamedForm();
        Display.getInstance().getCurrent().setName("Form_5");
        ensureVisible(new int[]{0, 4});
        clickButtonByLabel("Waiting");
        waitForFormName("Form_4");
        setText(new int[]{0, 0, 0, 0}, "task1");
        return true;
    }
}

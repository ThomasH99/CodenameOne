package tests;

import com.codename1.testing.AbstractTest;

import com.codename1.ui.Display;

public class test1 extends AbstractTest {
    public boolean runTest() throws Exception {
        waitForFormTitle("TodoCatalyst 1222 [1423]");
        clickButtonByPath(new int[]{3, 0});
        waitForFormTitle("Next");
        Display.getInstance().getCurrent().setName("Form_1");
        assertEqual(getToolbarCommands().length, 11);
        executeToolbarCommandAtOffset(0);
        waitForUnnamedForm();
        Display.getInstance().getCurrent().setName("Form_2");
        setText(new int[]{0, 0, 0, 1, 0, 0}, "");
        setText(new int[]{0, 0, 0, 0, 0, 1}, "testtask");
        clickButtonByPath(new int[]{0, 0, 0, 0, 0, 2});
        clickButtonByPath(new int[]{0, 0, 0, 0, 1, 1, 0});
        setText(new int[]{0, 0, 0, 0, 1, 0}, "14/10/2017: oiuoiuoiuoiu");
        clickButtonByPath(new int[]{0, 0, 0, 0, 1, 1, 0});
        setText(new int[]{0, 0, 0, 0, 1, 0}, "14/10/2017: oiuoiuoiuoiu
14/10/2017: 65656565");
        pointerPress(0.75714284f, 0.50714284f, new int[]{0, 0, 0, 0, 2, 1, 2, 0});
        waitFor(10);
        pointerRelease(0.75714284f, 0.50714284f, new int[]{0, 0, 0, 0, 2, 1, 2, 0});
        waitForUnnamedForm();
        Display.getInstance().getCurrent().setName("Form_3");
        selectInList(new int[]{0, 0}, 17454);
        clickButtonByLabel("OK");
        waitForFormName("Form_2");
        setText(new int[]{0, 0, 0, 1, 0, 0}, "");
        setText(new int[]{0, 0, 0, 1, 0, 0}, "new subtask");
        waitFor(5433);
        gameKeyPress(Display.GAME_FIRE);
        clickButtonByPath(new int[]{0, 0, 0, 1, 1, 0, 0, 2, 0, 3, 0});
        clickButtonByPath(new int[]{0, 0, 0, 1, 1, 0, 0, 2, 0, 3, 2});
        waitForFormTitle("new subtask");
        Display.getInstance().getCurrent().setName("Form_4");
        setText(new int[]{0, 0, 0, 1, 0, 0}, "");
        clickButtonByPath(new int[]{0, 0, 0, 0, 0, 2});
        assertEqual(getToolbarCommands().length, 7);
        executeToolbarCommandAtOffset(0);
        waitForFormName("Form_2");
        setText(new int[]{0, 0, 0, 1, 0, 0}, "");
        assertEqual(getToolbarCommands().length, 7);
        executeToolbarCommandAtOffset(0);
        waitForFormName("Form_1");
        assertEqual(getToolbarCommands().length, 11);
        executeToolbarCommandAtOffset(1);
        waitForFormTitle("TodoCatalyst 1222 [1423]");
        Display.getInstance().getCurrent().setName("Form_5");
        return true;
    }
}

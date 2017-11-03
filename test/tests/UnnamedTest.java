package tests;

import com.codename1.testing.AbstractTest;

import com.codename1.ui.Display;

public class UnnamedTest extends AbstractTest {
    public boolean runTest() throws Exception {
        waitForFormTitle("Error");
        goBack();
        waitForFormTitle("Todo Catalyst");
        clickButtonByPath((String)null);
        waitForFormTitle("");
        clickButtonByLabel("Timer");
        waitForFormTitle("Todo Catalyst");
        waitForFormTitle("Timer");
        clickButtonByName("OverflowButton");
        waitForFormTitle("Timer");
        pointerPress(0.84810126f, 0.58064514f, new int[]{0});
        waitFor(112);
        pointerRelease(0.84810126f, 0.58064514f, new int[]{0});
        waitForFormTitle("Timer");
        waitForFormTitle("Timer");
        clickButtonByPath((String)null);
        clickButtonByPath(new int[]{6, 1});
        setText(new int[]{1, 0}, "i");
        clickButtonByLabel("Completed + Next");
        clickButtonByPath((String)null);
        waitForFormTitle("Todo Catalyst");
        clickButtonByPath((String)null);
        waitForFormTitle("");
        clickButtonByLabel("Filter");
        waitForFormTitle("Todo Catalyst");
        waitForFormTitle("Which tasks to show");
        pointerPress(0.11392405f, 0.42857143f, new int[]{3});
        waitFor(137);
        pointerRelease(0.11392405f, 0.42857143f, new int[]{3});
        pointerPress(0.7358491f, 0.31428573f, new int[]{5, 1});
        waitFor(112);
        pointerRelease(0.7358491f, 0.31428573f, new int[]{5, 1});
        clickButtonByPath((String)null);
        waitForFormTitle("Error");
        goBack();
        waitForFormTitle("Which tasks to show");
        clickButtonByName("OverflowButton");
        waitForFormTitle("Which tasks to show");
        pointerPress(0.73734176f, 0.5483871f, new int[]{0});
        waitFor(102);
        pointerRelease(0.73734176f, 0.5483871f, new int[]{0});
        waitForFormTitle("Which tasks to show");
        waitForFormTitle("Todo Catalyst");
        clickButtonByPath((String)null);
        waitForFormTitle("");
        clickButtonByLabel("Lists");
        waitForFormTitle("Todo Catalyst");
        waitForFormTitle("Lists");
        clickButtonByLabel("List1");
        waitForFormTitle("Error");
        goBack();
        waitForFormTitle("Lists");
        assertLabel("List1");
        assertLabel(" (list description)");
        assertLabel("[3]");
        assertLabel("");
        assertLabel("List2");
        assertLabel("");
        assertLabel("List3");
        assertLabel("");
        assertLabel("New List");
        assertLabel(" (list description)");
        assertLabel("");
        assertLabel("t");
        assertLabel("[1]");
        assertLabel("");
        return true;
    }
}

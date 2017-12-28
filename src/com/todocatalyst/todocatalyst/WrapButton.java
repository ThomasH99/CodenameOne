package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.TextArea;
import com.codename1.ui.layouts.BorderLayout;

/**
 *
 * @author thomashjelm
 */
/**
 * <p>
 * A complex button similar to MultiButton that breaks lines automatically and
 * looks like a regular button (more or less). Unlike the multi button the span
 * button has the UIID style of a button.</p>
 * <script src="https://gist.github.com/codenameone/7bc6baa3a0229ec9d6f6.js"></script>
 * <img src="https://www.codenameone.com/img/developer-guide/components-spanbutton.png" alt="SpanButton Sample" />
 *
 * @author Shai Almog
 */
public class WrapButton extends Container {

    Button actualButton;
    TextArea text;
    private boolean shouldLocalize = true;

    /**
     * Default constructor will be useful when adding this to the GUI builder
     */
    public WrapButton() {
        this("");
    }

    /**
     * Constructor accepting default text and uiid for the text
     *
     * @param txt the text
     * @param textUiid the new text UIID
     */
    public WrapButton(String txt, String textUiid) {
        this(txt);
        text.setUIID(textUiid);
    }

    /**
     * Constructor accepting default text
     */
    public WrapButton(String txt) {
        setUIID("Button");
        setLayout(new BorderLayout());
        text = new TextArea(getUIManager().localize(txt, txt));
        text.setColumns(100);
        text.setUIID("Button");
        text.setEditable(false);
        text.setFocusable(false);
        text.setActAsLabel(true);
        actualButton = new Button();
        actualButton.setUIID("IconInList");
        addComponent(BorderLayout.WEST, actualButton);
        addComponent(BorderLayout.CENTER, text);
//        setLeadComponent(actualButton);
    }

    public WrapButton(Command cmd) {
        this(cmd.getCommandName());
    }

    public Button getActualButton() {
        return actualButton;
    }

    /**
     * Set the text of the button
     *
     * @param t text of the button
     */
    public void setText(String t) {
        if (shouldLocalize) {
            text.setText(getUIManager().localize(t, t));
        } else {
            text.setText(t);
        }
    }

    /**
     * Sets the command for the component
     *
     * @param cmd the command
     */
    public void setCommand(Command cmd) {
        actualButton.setCommand(cmd);
        actualButton.setText(""); //remove any text set by cmd for the actualButton since text from Command should only be shown for the TextArea
        setText(cmd.getCommandName()); //use the text of the Command (since Button is hidden)
    }

    /**
     * Sets the UIID for the actual text
     *
     * @param uiid the uiid
     */
    public void setTextUIID(String uiid) {
        text.setUIID(uiid);
    }

}

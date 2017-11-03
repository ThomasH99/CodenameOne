/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Image;

/**
 *
 * @author Thomas
 */
public class MyButton extends Button {

//    interface MyButtonActiveInterface {
//
//        /** returns the text to display for the command in the given context. Returns null if none defined */
//        public String getText(MyButton myButton);
//        /** returns the image to display for the command in the given context. Returns null if none defined */
//        public Image getIcon(MyButton myButton);
//    }
//    private MyActiveInterface buttonGenerator;
//    Object buttonTextSource;
    /**
     * Constructs a button with an empty string for its text.
     */
    public MyButton() {
        super("");
    }

    /**
     * Constructs a button with the specified text.
     *
     * @param text label appearing on the button
     */
    public MyButton(String text) {
        super(text);
    }

    /**
     * Allows binding a command to a button for ease of use
     *
     * @param cmd command whose text would be used for the button and would
     * recive action events from the button
     */
    public MyButton(Command cmd) {
        super(cmd);
    }

//    public MyButton(Command cmd, MyActiveInterface buttonGenerator) { //- use overriding of methods in new instead of using the MyActiveInterface
//        super(cmd);
//        this.buttonGenerator = buttonGenerator;
//    }
    /**
     * Constructs a button with the specified image.
     *
     * @param icon appearing on the button
     */
    public MyButton(Image icon) {
        super("", icon);
    }

    /**
     * Constructor a button with text and image
     *
     * @param text label appearing on the button
     * @param icon image appearing on the button
     */
    public MyButton(String text, Image icon) {
        super(text, icon);
    }

//    public MyButton(MyActiveInterface buttonGenerator, Image icon) {
//        super("", icon);
//        setActiveGenerator(buttonGenerator);
////        setText(buttonGenerator.getText(this));
//    }
//    public MyButton(MyActiveInterface buttonGenerator) {
//        super();
//        setActiveGenerator(buttonGenerator);
////         this.buttonGenerator = buttonGenerator;
////        setText(buttonText.getText(this));
//    }
//    final void setActiveGenerator(MyActiveInterface buttonGenerator) {
//        this.buttonGenerator = buttonGenerator;
//        setText(buttonGenerator.getText(this));
//    }
//    void setButtonTextSourceObject(Object buttonTextSource) {
//        this.buttonText = buttonText;
//    }
    /**
     * Returns the label text
     *
     * @return the label text
     */
    public String getText() {
//        if (buttonText == null) {
//        if (buttonGenerator != null) {
//            setText(buttonGenerator.getText(this));
//        } else
        if (getCommand() instanceof MyCommand) {
            setText(((MyCommand) getCommand()).getCommandName());
        }
        return super.getText();
    }

    public Image getIcon() {
//        if (buttonText == null) {
//        if (buttonGenerator == null) {
        return super.getIcon();
//        } else {
//            return buttonGenerator.getIcon(this);
//        }
    }

    public void longPointerPress(int x, int y) {
        super.longPointerPress(x, y);
        Log.p("longPointerPress(int x, int y) = (" + x + ", " + y + ")");
    }
}

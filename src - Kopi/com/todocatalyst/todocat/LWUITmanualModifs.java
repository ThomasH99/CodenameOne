/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.todocatalyst.todocat;

/**
 *
 * @author Thomas
 */
public class LWUITmanualModifs {

    // in RadioButton.java:
    
//    /*THHJ*/
//    /**
//     * returns the ButtonGroup for the RadioButton. Used to find out what index a RadioButton triggering an ActionListener is.
//     * @return
//     */
//    public ButtonGroup myGetGroup() {
//        return group;
//    }


// in     Display.java (Dialog.java?):

    // made playDialogSound public (THHJ)
//     public /*THHJ*/ void playDialogSound(final int type) {
//        impl.playDialogSound(type);
//    }


    // in Dialog.java:
//        /* THHJ */
//    public static Dialog buildDialog(String title, Component body, /*Command defaultCommand,*/ Command[] cmds, int type, Image icon) { //, long timeout, Transition transition) {
//        Command defaultCommand = null;
//        long timeout = 0; //THHJ
//        Transition transition = null; //THHJ
//        Dialog dialog = new Dialog(title);
//        dialog.dialogType = type;
//        dialog.setTransitionInAnimator(transition);
//        dialog.setTransitionOutAnimator(transition);
//        dialog.lastCommandPressed = null;
//        dialog.setLayout(new BorderLayout());
//        if(cmds != null) {
//            if(commandsAsButtons) {
//                Container buttonArea = new Container();
//                dialog.addComponent(BorderLayout.SOUTH, buttonArea);
//                if(cmds.length > 0) {
//                    for(int iter = 0 ; iter < cmds.length ; iter++) {
//                        buttonArea.addComponent(new Button(cmds[iter]));
//                    }
//                    buttonArea.getComponentAt(0).requestFocus();
//                }
//            } else {
//                for(int iter = 0 ; iter < cmds.length ; iter++) {
//                    dialog.addCommand(cmds[iter]);
//                }
//            }
//
//            // for the special case of one command map it to the back button
//            if(cmds.length == 1) {
//                dialog.setBackCommand(cmds[0]);
//            } else {
//                // map two commands to fire and back
//                if(cmds.length == 2 && defaultCommand == null) {
//                    defaultCommand = cmds[0];
//                    dialog.setBackCommand(cmds[1]);
//                }
//            }
//        }
//        dialog.addComponent(BorderLayout.CENTER, body);
//        if (icon != null) {
//            dialog.addComponent(BorderLayout.EAST, new Label(icon));
//        }
//        if (timeout != 0) {
//            dialog.setTimeout(timeout);
//        }
//        if(body.isScrollable() || disableStaticDialogScrolling){
//            dialog.setScrollable(false);
//        }
////        dialog.show(); //THHJ
////        return dialog.lastCommandPressed; //THHJ
//        return dialog; //THHJ
//    }

//Form.java: add:

//    /*private*/ public /*THHJ*/ Hashtable keyListeners;

//    /*private*/ public /*THHJ*/ Hashtable gameKeyListeners;
/*
     public void clearGameKeyListener() { //THHJ added
        gameKeyListeners = null;
    }

    public void clearKeyKeyListener() { //THHJ added
        keyListeners = null;
    }
*/
     

//Command.java

/* in LWUIT package com.sun.lwuit, file Command.java:

   public String toString() {
//        return command;
        return getCommandName(); //THHJ
    }

    public void setCommand(String command) { //not needed, use setCommandName() instead!!
        this.command = command;
    }


 */

//plaf - UIManager.java

    /* in LWUIT package com.sun.lwuit.plaf, file UIManager.java:

 public Hashtable getMyThemeProps() {return themeProps;} //THHJ

public Style myCreateStyle(Style style, String id, String prefix, boolean selected) {
//        Style style; //THHJ
String originalId = id;
//        if(selected) {  //THHJ
//            style = new Style(defaultSelectedStyle);
//        } else {
//            style = new Style(defaultStyle);
//        }
if (prefix != null && prefix.length() > 0) {
id += prefix;
}
if(themeProps != null){
String bgColor;
String fgColor;
Object border;

bgColor = (String)themeProps.get(id + Style.BG_COLOR);
fgColor = (String)themeProps.get(id + Style.FG_COLOR);
border = themeProps.get(id + Style.BORDER);
Object bgImage = themeProps.get(id + Style.BG_IMAGE);
String transperency = (String)themeProps.get(id + Style.TRANSPARENCY);
String margin = (String)themeProps.get(id + Style.MARGIN);
String padding = (String)themeProps.get(id + Style.PADDING);
Object font = themeProps.get(id + Style.FONT);

Byte backgroundType = (Byte)themeProps.get(id + Style.BACKGROUND_TYPE);
Byte backgroundAlignment = (Byte)themeProps.get(id + Style.BACKGROUND_ALIGNMENT);
Object[] backgroundGradient = (Object[])themeProps.get(id + Style.BACKGROUND_GRADIENT);
if(bgColor != null){
style.setBgColor(Integer.valueOf(bgColor, 16).intValue());
}
if(fgColor != null){
style.setFgColor(Integer.valueOf(fgColor, 16).intValue());
}
if(transperency != null){
style.setBgTransparency(Integer.valueOf(transperency).intValue());
} else {
if(selected) {
transperency = (String)themeProps.get(originalId + Style.TRANSPARENCY);
if(transperency != null){
style.setBgTransparency(Integer.valueOf(transperency).intValue());
}
}
}
if(margin != null){
int [] marginArr = toIntArray(margin.trim());
style.setMargin(marginArr[0], marginArr[1], marginArr[2], marginArr[3]);
}
if(padding != null){
int [] paddingArr = toIntArray(padding.trim());
style.setPadding(paddingArr[0], paddingArr[1], paddingArr[2], paddingArr[3]);
}
if(backgroundType != null) {
style.setBackgroundType(backgroundType.byteValue());
}
if(backgroundAlignment != null) {
style.setBackgroundAlignment(backgroundAlignment.byteValue());
}
if(backgroundGradient != null) {
if(backgroundGradient.length < 5) {
Object[] a = new Object[5];
System.arraycopy(backgroundGradient, 0, a, 0, backgroundGradient.length);
backgroundGradient = a;
backgroundGradient[4] = new Float(1);
}
style.setBackgroundGradient(backgroundGradient); //PATCH: make "void setBackgroundGradient(Object[] backgroundGradient)" in Style line 954
}
if(bgImage != null){
Image im = null;
if(bgImage instanceof String){
try {
String bgImageStr = (String)bgImage;
if(imageCache.contains(bgImageStr)) {
im = (Image)imageCache.get(bgImageStr);
} else {
if(bgImageStr.startsWith("/")) {
im = Image.createImage(bgImageStr);
} else {
im = parseImage((String)bgImage);
}
imageCache.put(bgImageStr, im);
}
themeProps.put(id + Style.BG_IMAGE, im);
} catch (IOException ex) {
System.out.println("failed to parse image for id = "+id + Style.BG_IMAGE);
}
}else{
im = (Image)bgImage;
}
// this code should not excute in the resource editor!
if(id.indexOf("Form") > -1){
if((im.getWidth() != Display.getInstance().getDisplayWidth() ||
im.getHeight() != Display.getInstance().getDisplayHeight())
&& style.getBackgroundType() == Style.BACKGROUND_IMAGE_SCALED && accessible) {
im.scale(Display.getInstance().getDisplayWidth(),
Display.getInstance().getDisplayHeight());
}
}
style.setBgImage(im);
}
if(font != null){
if(font instanceof String){
style.setFont(parseFont((String)font));
}else{
style.setFont((com.sun.lwuit.Font)font);
}
}

style.setBorder((Border)border);
style.resetModifiedFlag();
}

return style;
}

     */

}

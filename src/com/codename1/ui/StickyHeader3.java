/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package com.codename1.ui;

import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.todocatalyst.todocatalyst.Config;
import com.todocatalyst.todocatalyst.Icons;
import com.todocatalyst.todocatalyst.MyForm;
import java.util.ArrayList;

/**
 * Requirements / ideas: Wrap the parent holding the StickyHeader in a
 * BorderLayout where North will hold the sticky note. In this way, no conflict
 * with contentPane's use fo North (e.g. Search field). Scrolling when one SH
 * replaces another: scroll up: as soon as top of SH2 enters North/SH1, add it
 * to North and start scrolling North so SH1 exits towards to the top.
 * North size = SH1.height + SH2.h - scrollY-delta of North. Remove SH1 when scrolled up so that SH1.bottom is outside North. 
 *
 * @author Chen.
 */
public class StickyHeader3 extends Container implements ScrollListener {

    private int previousPosition;

    private boolean needToCheck;
    private Component comp;

    private boolean hidden;
//    private Button hideShowButton = new Button();

    private static String KEY_STICKY = "sticky";
    boolean longPress;
    Button button;

    //NB: caching the preferred size won't work if the content may dynaically resize
    private Dimension preferredSizeCache; //cache size so that removing the StickyHeader content to North won't change its size

    public StickyHeader3() {
//        super();
        setFocusable(false); //as done in init of Container
        setLayout(BoxLayout.y());

        button = new Button();
        add(button);
        button.setTextPosition(LEFT); //LEFT wrt icon //localize

        button.setCommand(Command.createMaterial("", Icons.iconCollapseListStickyHeader, (ev) -> {
            if (longPress) {
                longPress = false;
            } else {
                hidden = !hidden;
//            hideFollowingComponents(hidden);
                Container parent = getParent();
                int index = parent.getComponentIndex(this);
                int count = parent.getComponentCount();
                for (int i = index + 1; i < count; i++) {
                    Component comp = parent.getComponentAt(i);
                    if (comp instanceof StickyHeader3) {
                        break;
                    } else {
                        comp.setHidden(hidden);
                    }
                }
//            if (hidden)
//                hideShowButton.setMaterialIcon(hidden?FontImage.MATERIAL_EXPAND_LESS:FontImage.MATERIAL_EXPAND_MORE);
//            else
//                hideShowButton.setMaterialIcon(FontImage.MATERIAL_EXPAND_MORE);
//                hideShowButton.setMaterialIcon(hidden ? Icons.iconExpandListStickyHeader : Icons.iconCollapseListStickyHeader);
                button.setMaterialIcon(hidden ? Icons.iconExpandListStickyHeader : Icons.iconCollapseListStickyHeader);
                parent.animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT); //can't access MyForm
            }
        }));

        //if longpressing an expanded stickyHeader, collapse all; if longpressing a collapsed stickyHeader, expand all those already collapsed
//        hideShowButton.addLongPressListener((e) -> {
        button.addLongPressListener((e) -> {
            longPress = true;
            if (hidden) {
                for (Component comp : getParent().getChildrenAsList(true)) {
                    if (comp instanceof StickyHeader3) {
                        ((StickyHeader3) comp).hidden = false;
//                        ((StickyHeader) comp).hideShowButton.setMaterialIcon(Icons.iconCollapseListStickyHeader);
                        ((StickyHeader3) comp).button.setMaterialIcon(Icons.iconCollapseListStickyHeader);
                    } else {
                        comp.setHidden(false);
                    }
                }
            } else {
                for (Component comp : getParent().getChildrenAsList(true)) {
                    if (comp instanceof StickyHeader3) {
                        ((StickyHeader3) comp).hidden = true;
//                        ((StickyHeader) comp).hideShowButton.setMaterialIcon(Icons.iconExpandListStickyHeader);
                        ((StickyHeader3) comp).button.setMaterialIcon(Icons.iconExpandListStickyHeader);
                    } else {
                        comp.setHidden(true);
                    }
                }
            }
            getParent().animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
//            e.consume(); //prevent event to be send to component *below* stickyHeader (below glasspane)? Not working
        });

    }

    public StickyHeader3(String uiid) {
        this();
//        this.setUIID(uiid);
        button.setUIID(uiid);
    }

    public void setText(String headerText) {
        button.setText(headerText);
        if (Config.TEST) {
            this.setName(headerText);
        }
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        Container p = getParent();
        p.addScrollListener(this);
        previousPosition = getParent().getAbsoluteY() - getAbsoluteY();
    }

    @Override
    protected void laidOut() {
        if (getY() == 0) {
            needToCheck = true;
        }
    }

//    @Override
    public Dimension getPreferredSizeXXX() {
        if (preferredSizeCache == null) {
            preferredSizeCache = super.getPreferredSize();
        }
        return preferredSizeCache;
    }

    @Override
    public void scrollChanged(int scrollX, int scrollY, int oldscrollX, int oldscrollY) {
        int position = getParent().getAbsoluteY() - getHeight() + scrollY - getAbsoluteY();
        if (position >= 0) {
            if (previousPosition < 0) {
                needToCheck = true;
            }
        } else {
            if (previousPosition > 0) {
                needToCheck = true;
            }
        }
        if (scrollY - oldscrollY >= 0) {
            if (needToCheck) {
                pushToHeader();
            }
        } else {
            ArrayList stack = (ArrayList) getParent().getClientProperty("sticky");
            if (stack != null && !stack.isEmpty() && stack.get(0) == this && position < 0) {
                popFromHeader();
            }
        }
        previousPosition = position;
        needToCheck = false;
    }

//    @Override
//    void paintGlassImpl(Graphics g) {
//    }
    private void popFromHeader() {
        ArrayList stack = (ArrayList) getParent().getClientProperty("sticky");
        StickyHeader3 h1 = (StickyHeader3) stack.remove(0); //remove current stickyHeader
        h1.comp.remove();
        h1.addComponent(h1.comp); //add orginal content back into this stickyHeader

        if (!stack.isEmpty()) {
            StickyHeader3 h = (StickyHeader3) stack.get(0);
            h.placeSticky(); //if there is a preceding stickyHeader, install (show) that
        } else { //last stickyHeader
//            getComponentForm().getLayeredPane().removeComponent(comp); //remove stickyHeader content from North pane
            comp.remove();
            addComponent(comp); //and put it back into this stickyHeader
        }
    }

    private void pushToHeader() {
        ArrayList stack = (ArrayList) getParent().getClientProperty("sticky");
        if (stack == null) { //ceate a new stack if needed (first time use)
            stack = new ArrayList();
            getParent().putClientProperty("sticky", stack);
        }
        if (!stack.isEmpty()) {
            StickyHeader3 h = (StickyHeader3) stack.get(0); //get last pushed stickyHeader
            if (getY() < h.getY()) { //if last pushed is further down the screen, no need to do anything for this stickyHeader
                return;
            } else {
                h.comp.remove(); //remove previous stickyHeader's content from stickyHeader content holder
            }
        } //else
        stack.add(0, this); //push this one to the stack and 
        placeSticky(); //show this one
    }

    private void addToStickyHolder(Component comp) {
        Layout layout = getComponentForm().getContentPane().getLayout();
        if (layout instanceof BorderLayout) { //            if (Component north = ((BorderLayout) ).getNorth();
            if (false) {
                getComponentForm().getLayeredPane().addComponent(BorderLayout.NORTH, comp); //add it to the North pane
            } else {
                Container contentPane = getComponentForm().getContentPane();
                Component north = ((BorderLayout) contentPane.getLayout()).getNorth();
                if (north instanceof Container) {
                    comp.remove();
                    ((Container) north).add(comp); //add *after* any existing content (eg Search field)
                } else if (north != null) {
                    Container northCont = new Container(BoxLayout.y());
//keep existing north content
                    north.remove();
                    northCont.add(north);
                    comp.remove();
                    northCont.add(comp);
                    contentPane.add(BorderLayout.NORTH, northCont);
                } else { //north==null
                    comp.remove();
                    contentPane.add(BorderLayout.NORTH, comp);
                }
//                getComponentForm().getContentPane().animateLayout(300);
                revalidateWithAnimationSafety();
            }
        }
    }

    void placeSticky() {
        if (false && !(getComponentForm().getLayeredPane().getLayout() instanceof BorderLayout)) {
            getComponentForm().getLayeredPane().setLayout(new BorderLayout()); //only done once on first call
        }
//        else if (!(getComponentForm().getLayeredPane().getLayout() instanceof BorderLayout)) {
//            throw new Error("Layered pane already exists with other layout than BorderLayout");
//        } else if (((BorderLayout) getComponentForm().getLayeredPane().getLayout()).getNorth() != null) {
//            throw new Error("Layered pane already contains an element");
//        }
        if (comp != null) {
//            comp.remove();
//            getComponentForm().getLayeredPane().addComponent(BorderLayout.NORTH, comp); //add it to the North pane
            if (false) {
                getComponentForm().getContentPane().addComponent(BorderLayout.NORTH, comp); //add it to the North pane
            } else {
                addToStickyHolder(comp);
            }
        } else {
            if (getComponentCount() < 1) { //if stickyHeader is empty, do nothing (nothing to show)
                return;
            }
            //else 
            comp = getComponentAt(0); //get this stickyHeader's content (and store it in local variable comp
//        removeComponent(comp); //remove it from the stickyHeader
//            comp.remove();
            if (false) {
                getComponentForm().getLayeredPane().addComponent(BorderLayout.NORTH, comp); //add it to the North pane
            } else {
                addToStickyHolder(comp);
            }
        }
    }
}

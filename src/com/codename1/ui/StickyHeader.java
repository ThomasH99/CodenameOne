/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

 */
package com.codename1.ui;

import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.BorderLayout;
import com.todocatalyst.todocatalyst.Config;
import com.todocatalyst.todocatalyst.Icons;
import com.todocatalyst.todocatalyst.MyForm;
import java.util.ArrayList;

/* //example: simply add sticky headers where appropriate
Form hi = new Form("Sticky Header");
        hi.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        hi.setScrollableY(true);

        for (int j = 0; j < 10; j++) {
            StickyHeader header = new StickyHeader();
            header.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            header.setUIID("Header");
            Label headerLbl = new Label("header" + j);
            headerLbl.getAllStyles().setAlignment(Component.CENTER);
            header.add(headerLbl);
            hi.addComponent(header);                        
            for (int i = 0; i < 10; i++) {
                hi.addComponent(new Label("Label " + ((j*10) + i)));            
            }
        }
 */
/**
 * **** Important - make sure the class stays in the com.codename1.ui package
 * ****
 * -------------------------------------------------------------------------------
 * https://www.codenameone.com/blog/sticky-headers.html
 * https://github.com/chen-fishbein/stickyheaders-codenameone
 *
 * @author Chen
 */
public class StickyHeader extends Button implements ScrollListener {

    private int previousPosition;
    private boolean hidden = false;
    private boolean longPressShowsThisAndHidesOthers; //if true, longPress shows the current and hide the others

//    private Button hideShowButton = new Button();
    private boolean needToCheck = false;
    private static String KEY_STICKY = "sticky";
    boolean longPress = false;
    private boolean DEACTIVATE_STICKYNESS = true;

    public StickyHeader(String uiid) {
        this();
        setUIID(uiid);
    }

    public StickyHeader(String text, String uiid) {
        this();
        setText(text);
        setUIID(uiid);
    }

    public StickyHeader(String text, String uiid, boolean longPressShowsThisAndHidesOthers) {
        this(text, uiid);
        setLongPressShowsAndHidesOthers(longPressShowsThisAndHidesOthers);
    }

    public StickyHeader() {
        super();
        if (Config.TEST) {
            setName(getText());
        }
        setTextPosition(LEFT);
        if (false) {
            setGrabsPointerEvents(true);  //prevent event to be send to component *below* stickyHeader (below glasspane)? 
        }//        setLayout(BorderLayout.center());
//        hideShowButton.setCommand(Command.createMaterial("", FontImage.MATERIAL_EXPAND_LESS, (ev) -> {
//        hideShowButton.setCommand(Command.createMaterial("", Icons.iconCollapseListStickyHeader, (ev) -> {
        setCommand(Command.createMaterial("", Icons.iconCollapseListStickyHeader, (ev) -> {
            if (longPress) {
                longPress = false;
            } else {
//                if (longPressShowsThisAndHidesOthers) {
//                    hidden = false;
//                } else {
//                    hidden = !hidden;
//                }
                hidden = !hidden;
//            hideFollowingComponents(hidden);
                Container parent = getParent();
//                int index = parent.getComponentIndex(this);
//                int count = parent.getComponentCount();
                for (int i = parent.getComponentIndex(this) + 1, count = parent.getComponentCount(); i < count; i++) {
                    Component comp = parent.getComponentAt(i);
                    if (comp instanceof StickyHeader) {
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
                setMaterialIcon(hidden ? Icons.iconExpandListStickyHeader : Icons.iconCollapseListStickyHeader);
                parent.animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT); //can't access MyForm
            }
        }));

        //if longpressing an expanded stickyHeader, collapse all; if longpressing a collapsed stickyHeader, expand all those already collapsed
//        hideShowButton.addLongPressListener((e) -> {
        addLongPressListener((e) -> {
            longPress = true;
            boolean hideOthers = false;
            if (longPressShowsThisAndHidesOthers) {
                hidden = false; //show this
                hideOthers = true; //hide others
            } else {
                hidden = !hidden; //flip state of this
                hideOthers = hidden; //and of others
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                if (hidden) {
//                    for (Component comp : getParent().getChildrenAsList(true)) {
//                        if (comp instanceof StickyHeader) {
//                            ((StickyHeader) comp).hidden = false;
////                        ((StickyHeader) comp).hideShowButton.setMaterialIcon(Icons.iconCollapseListStickyHeader);
//                            ((StickyHeader) comp).setMaterialIcon(Icons.iconCollapseListStickyHeader);
//                        } else {
//                            comp.setHidden(false);
//                        }
//                    }
//                } else {
//                    for (Component comp : getParent().getChildrenAsList(true)) {
//                        if (comp instanceof StickyHeader) {
//                            ((StickyHeader) comp).hidden = true;
////                        ((StickyHeader) comp).hideShowButton.setMaterialIcon(Icons.iconExpandListStickyHeader);
//                            ((StickyHeader) comp).setMaterialIcon(Icons.iconExpandListStickyHeader);
//                        } else {
//                            comp.setHidden(true);
//                        }
//                    }
//                }
//            }
//</editor-fold>
            //flip state of this header
//            hidden = !hidden;
            setMaterialIcon(hidden ? Icons.iconExpandListStickyHeader : Icons.iconCollapseListStickyHeader);

            boolean insideThisHeader = false;
            for (Component comp : getParent().getChildrenAsList(true)) {
                if (longPressShowsThisAndHidesOthers) {
                    if (comp instanceof StickyHeader) {
                        if (comp == this) { //show this (no matter the previous state)
                            insideThisHeader = true;
                            ((StickyHeader) comp).setMaterialIcon(Icons.iconCollapseListStickyHeader);
                        } else {
                            insideThisHeader = false;
                            ((StickyHeader) comp).hidden = true;
//                        ((StickyHeader) comp).hideShowButton.setMaterialIcon(Icons.iconCollapseListStickyHeader);
                            ((StickyHeader) comp).setMaterialIcon(Icons.iconExpandListStickyHeader);
                        }
                    } else {
                        comp.setHidden(!insideThisHeader); //hide all other elements, except when 'inside' (after) this header
                    }
                } else { //normal longpress == flip hidden state of current and set the same state for all others
                    if (comp instanceof StickyHeader) {
                        if (comp != this) {
                            ((StickyHeader) comp).hidden =  hidden ;
                            ((StickyHeader) comp).setMaterialIcon(((StickyHeader) comp).hidden ? Icons.iconExpandListStickyHeader : Icons.iconCollapseListStickyHeader);
                        } 
                    } else {
                        comp.setHidden(hidden); //hide all other elements, except when 'inside' (after) this header
                    }
                }
            }
            getParent().animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
//            e.consume(); //prevent event to be send to component *below* stickyHeader (below glasspane)? Not working
        });

//        addPointerPressedListener((e) -> e.consume());  //prevent event to be send to component *below* stickyHeader (below glasspane)? Not working
//        super.add(BorderLayout.EAST, hideShowButton);
    }

    public void setLongPressShowsAndHidesOthers(boolean on) {
        longPressShowsThisAndHidesOthers = on;
    }

    public void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
    }

//    public StickyHeader(String uiid, String iconUiid) {
//        this();
//        this.setUIID(uiid);
//        hideShowButton.setUIID(iconUiid);
//    }
    private void hideFollowingComponents(boolean hide) {
        Container parent = getParent();
        int index = parent.getComponentIndex(this);
        int count = parent.getComponentCount();
        for (int i = index + 1; i < count; i++) {
            Component comp = parent.getComponentAt(i);
            if (comp instanceof StickyHeader) {
                break;
            } else {
                comp.setHidden(hide);
            }
        }
        parent.animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
    }

    public Container addXXX(Component comp) {
//        return super.add(BorderLayout.CENTER, comp);
        return null;
    }

//    public void setText(Component comp) {
//        return super.add(BorderLayout.CENTER, comp);
//    }
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

    @Override
    public void scrollChanged(int scrollX, int scrollY, int oldscrollX, int oldscrollY) {
        if (!DEACTIVATE_STICKYNESS) {
            int position = getParent().getAbsoluteY() + scrollY - getAbsoluteY();
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
                ArrayList stack = (ArrayList) getParent().getClientProperty(KEY_STICKY);
                if (stack != null && !stack.isEmpty() && stack.get(0) == this && position < 0) {
                    popFromHeader();
                }
            }
            previousPosition = position;
            needToCheck = false;
        }
    }

    @Override
    void paintGlassImpl(Graphics g) {
    }

    private void popFromHeader() {
        ArrayList stack = (ArrayList) getParent().getClientProperty(KEY_STICKY);
        stack.remove(0);

        if (!stack.isEmpty()) {
            StickyHeader h = (StickyHeader) stack.get(0);
            h.installSticky();
        } else {
            getComponentForm().setGlassPane(null);
        }
    }

    private void pushToHeader() {
        ArrayList stack = (ArrayList) getParent().getClientProperty(KEY_STICKY);
        if (stack == null) {
            stack = new ArrayList();
            getParent().putClientProperty(KEY_STICKY, stack);
        }
        if (!stack.isEmpty()) {
            StickyHeader h = (StickyHeader) stack.get(0);
            if (getY() < h.getY()) {
                return;
            }
        }
        stack.add(0, this);
        installSticky();
    }

    void installSticky() {
        Painter sticky = createPainter();
        getComponentForm().setGlassPane(sticky);
    }

    Painter createPainter() {
        Painter sticky = new Painter() {

            @Override
            public void paint(Graphics g, Rectangle rect) {
                int cx = g.getClipX();
                int cy = g.getClipY();
                int cw = g.getClipWidth();
                int ch = g.getClipHeight();

                if (false) {
                    int parAbsX = getParent().getAbsoluteX();
                    int parPadLeftX = getParent().getStyle().getPaddingLeft(isRTL());
                    int parMargLeftX = getParent().getStyle().getMarginLeft(isRTL()); //THJ)
                    int parX = getParent().getX();
                    int parScrollX = getParent().getScrollX();
                    int contPaneAbsX = getComponentForm().getContentPane().getAbsoluteX();
                    int contPanePadLeftX = getComponentForm().getContentPane().getStyle().getPaddingLeft(isRTL());
                    int contPaneMargLeftX = getComponentForm().getContentPane().getStyle().getMarginLeft(isRTL()); //THJ)
                    int pcontPaneX = getComponentForm().getContentPane().getX();
                    int contPaneScrollX = getComponentForm().getContentPane().getScrollX();
                    int absX = getAbsoluteX();
                    int outX = getOuterX();
                    int x = getX();
                    int parAbsY = getParent().getAbsoluteY();
                    int parPadLeftY = getParent().getStyle().getPaddingTop();
                    int parMargTopY = getParent().getStyle().getMarginTop(); //THJ)
                    int parY = getParent().getY();
                    int parScrollY = getParent().getScrollY();
                    int contPaneAbsY = getComponentForm().getContentPane().getAbsoluteY();
                    int contPanePadTopY = getComponentForm().getContentPane().getStyle().getPaddingTop();
                    int contPaneMargTopY = getComponentForm().getContentPane().getStyle().getMarginTop(); //THJ)
                    int pcontPaneY = getComponentForm().getContentPane().getY();
                    int contPaneScrollY = getComponentForm().getContentPane().getScrollY();
                    int absY = getAbsoluteY();
                    int outY = getOuterY();
                    int y = getY();
                    int stop = 1;
                }

//                int tx = getParent().getX();// - getX();
//                int ty = getParent().getY() - getY();
                Form form = getComponentForm();
                if (form != null) {
                    Container contPane = getComponentForm().getContentPane();
                    Component scrollableComp = getComponentForm().findScrollableChild(form.getContentPane());
//                    Component contPane = scrollableComp;

                    if (true || scrollableComp instanceof Container) {
//                        Container contPane = (Container) scrollableComp;
//                int tx = contPane.getAbsoluteX() + contPane.getStyle().getMarginLeft(isRTL()); //- getX(); //WORKS
                        int tx = contPane.getAbsoluteX() + contPane.getStyle().getMarginLeft(isRTL()) + getParent().getX(); //- getX(); //WORKS
//                int ty = getComponentForm().getContentPane().getY() - getY();
                        int ty = contPane.getAbsoluteY() - getY(); //WORKS

//                tx = getParent().getAbsoluteX() + getParent().getStyle().getPaddingLeft(isRTL()) + getParent().getScrollX() - getX(); //latest CN1 version, pbs: shifts sticky lable a few pixels left, and leaves a transparent space above the sticky label where the scrolled elements can be seen behind/through the label
//                ty = getParent().getAbsoluteY() + getParent().getStyle().getPaddingTop() + getParent().getScrollY() - getY();
                        g.setClip(0, 0, rect.getWidth(), rect.getHeight());

                        g.translate(tx, ty);
                        StickyHeader.this.paintComponentBackground(g);
                        StickyHeader.this.paint(g);
                        if (StickyHeader.this.getStyle().getBorder() != null) {
                            StickyHeader.this.paintBorder(g);
                        }
                        g.translate(-tx, -ty);
                        g.setClip(cx, cy, cw, ch);
                    }
                }
            }
        };
        return sticky;
    }

}

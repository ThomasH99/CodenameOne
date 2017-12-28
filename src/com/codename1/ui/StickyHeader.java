/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

 */
package com.codename1.ui;

import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Painter;
import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.geom.Rectangle;
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
public class StickyHeader extends Container implements ScrollListener {

    private int previousPosition;

    private boolean needToCheck = false;

    public StickyHeader() {
    }

    public StickyHeader(String uiid) {
        this();
        this.setUIID(uiid);
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

    @Override
    public void scrollChanged(int scrollX, int scrollY, int oldscrollX, int oldscrollY) {
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
            ArrayList stack = (ArrayList) getParent().getClientProperty("sticky");
            if (stack != null && !stack.isEmpty() && stack.get(0) == this && position < 0) {
                popFromHeader();
            }
        }
        previousPosition = position;
        needToCheck = false;
    }

    void paintGlassImpl(Graphics g) {
    }

    private void popFromHeader() {
        ArrayList stack = (ArrayList) getParent().getClientProperty("sticky");
        stack.remove(0);

        if (!stack.isEmpty()) {
            StickyHeader h = (StickyHeader) stack.get(0);
            h.installSticky();
        } else {
            getComponentForm().setGlassPane(null);
        }
    }

    private void pushToHeader() {
        ArrayList stack = (ArrayList) getParent().getClientProperty("sticky");
        if (stack == null) {
            stack = new ArrayList();
            getParent().putClientProperty("sticky", stack);
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

                if (true) {
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
                Container contPane = getComponentForm().getContentPane();
                int tx = contPane.getAbsoluteX() + contPane.getStyle().getMarginLeft(isRTL()); //- getX(); //WORKS
//                int ty = getComponentForm().getContentPane().getY() - getY();
                int ty = contPane.getAbsoluteY() - getY(); //WORKS

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
        };
        return sticky;
    }

}

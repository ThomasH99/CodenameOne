/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.Layout;

/**
 *
 * @author Thomas
 */
public class ContainerScrollY extends Container {
    
    
    public ContainerScrollY() {
        super();
//        setScrollableY(true);
    }
    public ContainerScrollY(Layout l) {
        super(l);
//        setScrollableY(true);
    }
    
    public void setScrollYPublic(int scrollY) {
        setScrollY(scrollY);
    }
    
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
    * Makes sure the component is visible in the scroll if this container is
    * scrollable
    *
    * @param c the component that will be scrolling for visibility
    //     */
//    public void scrollComponentToVisible(final Component c) {
//        if (isScrollable()) {
//            if (c != null) {
//                Rectangle r = c.getVisibleBounds();
//                if (c.getParent() != null) {
//                    // special case for the first component to allow the user to scroll all the
//                    // way to the top
//                    Form f = getComponentForm();
//                    if (f != null && f.getInvisibleAreaUnderVKB() == 0 &&
//                            f.findFirstFocusable() == c) {
//                        // support this use case only if the component doesn't explicitly declare visible bounds
//                        if (r == c.getBounds() && !Display.getInstance().isTouchScreenDevice()) {
//                            scrollRectToVisible(new Rectangle(0, 0,
//                                    c.getX() + Math.min(c.getWidth(), getWidth()),
//                                    c.getY() + Math.min(c.getHeight(), getHeight())), this);
//                            return;
//                        }
//                    }
//                }
//                boolean moveToVisible = true;
//                Dimension size = r.getSize();
//                boolean large = size.getHeight() > getHeight() ||
//                        size.getWidth() > getWidth();
//                if (large) {
//                    int x = getScrollX();
//                    int y = getScrollY();
//                    int w = getWidth();
//                    int h = getHeight();
//                    boolean visible = contains(c) && Rectangle.intersects(c.getAbsoluteX(),
//                            c.getAbsoluteY(),
//                            c.getWidth(),
//                            c.getHeight(),
//                            getAbsoluteX() + x,
//                            getAbsoluteY() + y,
//                            w,
//                            h);
//                    //if this is a big component no need to scroll to the begining if it's
//                    //partially visible
//                    moveToVisible = !visible;
//                }
//                if (moveToVisible) {
//                    scrollRectToVisible(r.getX(), r.getY(),
//                            Math.min(r.getSize().getWidth(), getWidth()),
//                            Math.min(r.getSize().getHeight(), getHeight()), c);
//                }
//            }
//        }
//    }
//
//    @Override
//    protected Rectangle getBounds() {
//        return super.getBounds();
//    }
//
//    public void scrollComponentToAbsolutePosition(final Component c, int absX, int absY) {
//        if (isScrollable()) {
//            if (c != null) {
////                Rectangle r = c.getVisibleBounds();
//                Rectangle r = c.getBounds();
//                if (c.getParent() != null) {
//                    // special case for the first component to allow the user to scroll all the
//                    // way to the top
//                    Form f = getComponentForm();
//                    if (f != null && f.getInvisibleAreaUnderVKB() == 0 &&
//                            f.findFirstFocusable() == c) {
//                        // support this use case only if the component doesn't explicitly declare visible bounds
//                        if (r == c.getBounds() && !Display.getInstance().isTouchScreenDevice()) {
//                            scrollRectToVisible(new Rectangle(0, 0,
//                                    c.getX() + Math.min(c.getWidth(), getWidth()),
//                                    c.getY() + Math.min(c.getHeight(), getHeight())), this);
//                            return;
//                        }
//                    }
//                }
//                boolean moveToVisible = true;
//                Dimension size = r.getSize();
//                boolean large = size.getHeight() > getHeight() ||
//                        size.getWidth() > getWidth();
//                if (large) {
//                    int x = getScrollX();
//                    int y = getScrollY();
//                    int w = getWidth();
//                    int h = getHeight();
//                    boolean visible = contains(c) && Rectangle.intersects(c.getAbsoluteX(),
//                            c.getAbsoluteY(),
//                            c.getWidth(),
//                            c.getHeight(),
//                            getAbsoluteX() + x,
//                            getAbsoluteY() + y,
//                            w,
//                            h);
//                    //if this is a big component no need to scroll to the begining if it's
//                    //partially visible
//                    moveToVisible = !visible;
//                }
//                if (moveToVisible) {
//                    scrollRectToVisible(r.getX(), r.getY(),
//                            Math.min(r.getSize().getWidth(), getWidth()),
//                            Math.min(r.getSize().getHeight(), getHeight()), c);
//                }
//            }
//        }
//    }
//</editor-fold>
}

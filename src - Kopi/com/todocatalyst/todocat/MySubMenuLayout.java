/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Style;
import java.util.Hashtable;

/**
 *
 * @author Thomas
 */
public class MySubMenuLayout extends Layout {
//public class BorderLayout extends Layout {

    
    /**
     * Horizontal layout where components are arranged from left to right
     */
//    public static final int X_AXIS = 1;

    /**
     * Vertical layout where components are arranged from top to bottom
     */
//    public static final int Y_AXIS = 2;
    
//    private int axis;
    
    /** 
     * Creates a new instance of BoxLayout
     * 
     * @param axis the axis to lay out components along. 
     * Can be: BoxLayout.X_AXIS or BoxLayout.Y_AXIS
     */
    public MySubMenuLayout() {
//        this.axis = axis;
    }
    
    /**
     * @inheritDoc
     */
    public void layoutContainer(Container parent) {        
        int width = parent.getLayoutWidth() - parent.getSideGap() - parent.getStyle().getPadding(false, Component.RIGHT) - parent.getStyle().getPadding(false, Component.LEFT);
        int height = parent.getLayoutHeight() - parent.getBottomGap() - parent.getStyle().getPadding(false, Component.BOTTOM) - parent.getStyle().getPadding(false, Component.TOP);
        int x = parent.getStyle().getPadding(parent.isRTL(), Component.LEFT);
        int y = parent.getStyle().getPadding(false, Component.TOP);
        int numOfcomponents = parent.getComponentCount();
        
        boolean rtl = parent.isRTL();
        if(rtl) {
        	x += parent.getSideGap();
        }
        int initX = x;

        for(int i=0; i< numOfcomponents; i++){
            Component cmp = parent.getComponentAt(i);
            Style stl = cmp.getStyle();
            
//            if(axis == Y_AXIS){
                int cmpBottom = height;
                int cmpH = cmp.getPreferredH();
                
                y += stl.getMargin(false, Component.TOP);
                
                if(y >= cmpBottom){
                    cmpH = 0;
                }else if(y + cmpH - parent.getStyle().getPadding(false, Component.TOP) > cmpBottom){
                    cmpH = cmpBottom - y - stl.getMargin(false, Component.BOTTOM);
                }
//                cmp.setWidth(width - stl.getMargin(parent.isRTL(), Component.LEFT) - stl.getMargin(parent.isRTL(), Component.RIGHT));
                cmp.setWidth(cmp.getPreferredW());
                cmp.setHeight(cmpH);
                cmp.setX(x + stl.getMargin(parent.isRTL(), Component.LEFT));
                cmp.setY(y);
                y += cmp.getHeight() + stl.getMargin(false, Component.BOTTOM);
//            }else{
//                int cmpRight = width;
//                int cmpW = cmp.getPreferredW();
//                
//                x += stl.getMargin(false, Component.LEFT);
//
//                if(x >= cmpRight){
//                    cmpW = 0;
//                } else {
//                    if(x + cmpW - parent.getStyle().getPadding(false, Component.LEFT) > cmpRight){
//                        cmpW = cmpRight - x - stl.getMargin(false, Component.RIGHT);
//                    }
//                }
//                cmp.setWidth(cmpW);
//                cmp.setHeight(height- stl.getMargin(false, Component.TOP) - stl.getMargin(false, Component.BOTTOM));
//                if(rtl) {
//                	cmp.setX(width + initX - (x - initX) - cmpW);
//                } else {
//                	cmp.setX(x);
//                }
//                cmp.setY(y + stl.getMargin(false, Component.TOP));
//                x += cmp.getWidth() + stl.getMargin(false, Component.RIGHT);
//            }
        }
    }
    
    /**
     * @inheritDoc
     */
    public Dimension getPreferredSize(Container parent) {
        int width = 0;
        int height = 0;

        int numOfcomponents = parent.getComponentCount();
        for(int i=0; i< numOfcomponents; i++){
            Component cmp = parent.getComponentAt(i);
            Style stl = cmp.getStyle();
            
//            if(axis == Y_AXIS){
                int cmpH = cmp.getPreferredH() + stl.getMargin(false, Component.TOP) + stl.getMargin(false, Component.BOTTOM);
                height += cmpH;
                width = Math.max(width , cmp.getPreferredW()+ stl.getMargin(false, Component.LEFT) + stl.getMargin(false, Component.RIGHT));
//            }else{
//                int cmpW = cmp.getPreferredW() + stl.getMargin(false, Component.LEFT) + stl.getMargin(false, Component.RIGHT);
//                width += cmpW;
//                height = Math.max(height, cmp.getPreferredH() + stl.getMargin(false, Component.TOP) + stl.getMargin(false, Component.BOTTOM));
//            }
        }
        Dimension d = new Dimension(width + parent.getStyle().getPadding(false, Component.LEFT)+ parent.getStyle().getPadding(false, Component.RIGHT),
        height + parent.getStyle().getPadding(false, Component.TOP)+ parent.getStyle().getPadding(false, Component.BOTTOM));
        return d;
    }  

    /**
     * Returns the layout axis x/y
     * 
     * @return the layout axis
     */
//    public int getAxis() {
//        return axis;
//    }

    /**
     * @inheritDoc
     */
    public String toString() {
//        if(axis == X_AXIS) {
            return "MySubMenuLayout";
//        }
//        return "BoxLayout Y";
    }

    /**
     * @inheritDoc
     */
    public boolean equals(Object o) {
        return super.equals(o); // && axis == ((BoxLayout)o).axis;
    }
}

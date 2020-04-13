package com.todocatalyst.todocatalyst;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.layouts.BorderLayout;
import java.util.ArrayList;

/**
 * Modified version of StickyHeader in comment here https://www.codenameone.com/blog/sticky-headers.html
 * by Chibuike Mba.
 * @author Chen
 */
public class StickyHeaderMod extends Container implements ScrollListener {

    private int previousPosition;

    private boolean needToCheck = false;
    private Component comp = null;
    private static String KEY_STICKY = "sticky";

    public StickyHeaderMod() {
    }
    
        public StickyHeaderMod(String uiid) {
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
        } else if (previousPosition > 0) {
            needToCheck = true;
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

    //TODO fix below override (paintGlassImpl does not exist?!)
//    @Override
    void paintGlassImpl(Graphics g) {
    }

    private void popFromHeader() {
        ArrayList stack = (ArrayList) getParent().getClientProperty(KEY_STICKY);
        stack.remove(0);

        if (!stack.isEmpty()) {
            StickyHeaderMod h = (StickyHeaderMod) stack.get(0);
            h.installSticky();
        } else {
            getComponentForm().getLayeredPane().removeComponent(comp);
            addComponent(comp);
        }
    }

    private void pushToHeader() {
        ArrayList stack = (ArrayList) getParent().getClientProperty(KEY_STICKY);
        if (stack == null) {
            stack = new ArrayList();
//            getParent().putClientProperty("sticky", stack);
            getParent().putClientProperty(KEY_STICKY, stack);
        }
        if (!stack.isEmpty()) {
            StickyHeaderMod h = (StickyHeaderMod) stack.get(0);
            if (getY() < h.getY()) {
                return;
            }
        }
        stack.add(0, this);
        installSticky();
    }

    void installSticky() {
        if (getComponentCount() < 1) {
            return;
        }

        comp = getComponentAt(0);
        removeComponent(comp);
        getComponentForm().getLayeredPane().addComponent(BorderLayout.NORTH, comp);
    }
}


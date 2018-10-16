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
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.Layout;
import static com.todocatalyst.todocatalyst.Config.TEST;

/**
 * used to ensure that a list in a screen is shown with same position, even if
 * the list is modified (eg items added/removed). To avoid scrolling a screen
 * which is revalidated back to the top making the user lose the position he was
 * in before the editing action. Examples: marking a task Done in a filtered
 * list, editing a task in a category list and removing the category etc. Works
 * by saving the Item/Object that needs to be shown in the same position as
 * before together with the position on the screen and then scrolling to the new
 * position of that same Item after the screen has been recreated.
 *
 * Special cases: when the item itself is removed from the list, then simply
 * scroll to same position as the Item was shown at before (will appear as if
 * the item in the list below simply moved up to take the space of the removed
 * item).
 *
 *
 * @author Thomas
 */
class KeepInSameScreenPosition {

    private int relScroll; //store the 'relative scroll' (magic number) to 
    private int scrollY = Integer.MIN_VALUE; //store the total scrollY in case we cannot find the new component corresponding to the old one
    private Object itemOrg = null; //keep the item we want to keep in the same scroll position
    private Component newComponent = null; //the component we want to place in same scroll position
    private Component someComponent = null; //keep some (random) component from the container to be able to find the ScrollableContainer
//    private MyForm form = null; //keep some (random) component from the container to be able to find the ScrollableContainer

//    KeepInSameScreenPosition() {
//    }
    /**
     * keep same scrollY position of the list
     *
     * @param scrollableContainer
     */
//    KeepInSameScreenPosition(int scrollY) {
    KeepInSameScreenPosition(Container scrollableContainer) {
        ASSERT.that(scrollableContainer.isScrollableY(), ()->"KeepInSameScreenPosition called with non-scrollableY container=" + scrollableContainer);
//        form = (MyForm) scrollableContainer.getComponentForm();
        this.scrollY = scrollableContainer.getScrollY();
    }

    /**
     * will use the current form, attempt to find the scrollable container and
     * store the scrollY position
     */
    KeepInSameScreenPosition() {
//        this(findScrollableContainer());
//        form = (MyForm) Display.getInstance().getCurrent();
        Component cont = findScrollableContainerN();
        if (cont != null) {
            this.scrollY = cont.getScrollY();
        }
    }

    KeepInSameScreenPosition(Object item, Component oldItemComponent) {
        this(item, oldItemComponent, 0);
    }

    /**
     * scroll item to visible, used eg when creating a new item which is
     * inserted somewhere in a list
     *
     * @param item
     */
    KeepInSameScreenPosition(Object item) {
        this(item, null, 0);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /// v TEST v ///
//    int y;
//    int yScroll;
//    int yAbsolute;
//
//    int yParent;
//    int yParentAbsolute;
//    int yParentScroll;
//
//    int yScrollableCont;
//    int yScrollableContAbsolute;
//    int yScrollableContScroll;
//    int yParent;
//    int yParentAbsolute;
//    int yParentScroll;
//    private void setTestValues(Component comp, Component parentInScrollableCont, Component scrollableContainer) {
//        int y = comp.getY();
//        int yAbsolute = comp.getAbsoluteY();
//        int yScroll = comp.getScrollY();
//        System.out.println("y="+y+" YAbs="+yAbsolute+" yScroll="+yScroll);
//
//        Component parent = comp.getParent();
//        int yParent = parentInScrollableCont.getY();
//        int yParentAbsolute = parentInScrollableCont.getAbsoluteY();
//        int yParentScroll = parentInScrollableCont.getScrollY();
//        System.out.println("yParent="+yParent+" YParentAbs="+yParentAbsolute+" yParentScroll="+yParentScroll);
//
//        int yScrollableCont = scrollableContainer.getY();
//        int yScrollableContAbsolute = scrollableContainer.getAbsoluteY();
//        int yScrollableContScroll = scrollableContainer.getScrollY();
//        System.out.println("yScrollableCont="+yScrollableCont+" YScrollableContAbs="+yScrollableContAbsolute+" yScrollableContScroll="+yScrollableContScroll);
//        boolean t=true; //breakpoint
//    }
    //// ^ TEST ^///
//</editor-fold>
    /**
     * keep the item (in oldItemComponent) at the same position once the list is
     * regenerated
     *
     * @param item
     * @param oldItemComponent used to get scrollably Y position
     * @param indexOfCompToKeepFixedRelativeToOldItemComp relative position of
     * element to keep in same position, eg to keep insertNewTaskCont in same
     * position on screen even as more new items are inserted above it, point to
     * position of the following (+1) item. This will place the new item with
     * additional scroll (sorry, unclear description!)
     */
    KeepInSameScreenPosition(Object item, Component oldItemComponent, int indexOfCompToKeepFixedRelativeToOldItemComp) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            setKeepItemInSameScreenPosition(item, oldItemComponent);
//        Container scrollableCont = oldItemComponent.getParent();
//        while (scrollableCont != null && !scrollableCont.isScrollableY()) {
//            scrollableCont = scrollableCont.getParent();
//        }
//</editor-fold>
//        form = (MyForm) Display.getInstance().getCurrent();
        itemOrg = item;
        Container scrollableCont = getScrollableContainer(oldItemComponent);
        if (scrollableCont == null) { //this may happen in ScreenListOfItems if the list is empty, eg. only showing QuickEntry container

            return;
        }

        //get the parent of the component that is in the scrollableContainer
//<editor-fold defaultstate="collapsed" desc="comment">
//        while (oldItemComponent != null && oldItemComponent.getParent() != scrollableCont) {
//            oldItemComponent = oldItemComponent.getParent();
//        }
//</editor-fold>
        Component parentInScrollable = getParentInScrollableContainer(oldItemComponent, scrollableCont);

//        if (TEST.DEBUG)
//        setTestValues(oldItemComponent, parentInScrollable, scrollableCont);
        if (indexOfCompToKeepFixedRelativeToOldItemComp != 0) {
//            Container parent = parentInScrollable.getParent();
            int oldParentInScrollableIndex = scrollableCont.getComponentIndex(parentInScrollable);
            int indexAdjusted = oldParentInScrollableIndex + indexOfCompToKeepFixedRelativeToOldItemComp;
            //check if index is within valid range, it not adjust //TODO!! performance: rewrite to Math.min/max
            if (indexAdjusted < 0) {
                indexAdjusted = 0;
            } else if (indexAdjusted >= scrollableCont.getComponentCount()) {
                indexAdjusted = scrollableCont.getComponentCount() - 1;
            }
            parentInScrollable = scrollableCont.getComponentAt(indexAdjusted);
        }

//        assert oldItemComponent.getComponentForm().getContentPane().isScrollableY():"not using scrollable container";
        assert scrollableCont == null || scrollableCont.isScrollableY() : "no scrollable parent container found";
//        int y = oldItemComponent.getY();
        int y = parentInScrollable.getY();
//        int scrollY = oldItemComponent.getComponentForm().getContentPane().getScrollY();
        if (scrollableCont != null) { //TODO!!! is this check really the right approach to handle if scrollableCont is null??
            scrollY = scrollableCont.getScrollY();
            relScroll = y - scrollY;
        }
    }

    /**
     * copied from Form (where it is **private)
     *
     * @param c
     * @return
     */
    Component findScrollableChild(Container c) {
        if (c.isScrollableY()) {
            return c;
        }
        int count = c.getComponentCount();
        for (int iter = 0; iter < count; iter++) {
            Component comp = c.getComponentAt(iter);
            if (comp.isScrollableY()) {
                return comp;
            }
            if (comp instanceof Container) {
                Component chld = findScrollableChild((Container) comp);
                if (chld != null) {
                    return chld;
                }
            }
        }
        return null;
    }

    public String toString() {
        return (itemOrg != null && itemOrg instanceof ItemAndListCommonInterface ? ((ItemAndListCommonInterface) itemOrg).getText() : "itemOrg=null")
                + " rel:" + relScroll + " scrollY:" + scrollY;
//          public String toString(){
//        return (itemOrg!=null?itemOrg:"itemOrg=null")+
//    }
    }

    /**
     * iterates up the parent hierarchy and returns the parent of comp which is
     * scrollable
     *
     * @param comp
     * @return null if comp is null or nothing found
     */
//    private Container getParentInScrollableContainer(Component comp) {
    private Container getScrollableContainer(Component comp) {
        if (comp == null) {
            return null;
        }
        Component scrollable = comp.getScrollable();
        if (scrollable instanceof Container) {
            return (Container) scrollable;
        } else {
            return null;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        Form f = comp.getComponentForm();
//        if (f instanceof MyForm) {
//            return ((MyForm)f).getSc
//        }
//        while (comp != null && !comp.isScrollableY()) {
////            scrollableCont = scrollableCont.getParent();
//            comp = comp.getParent();
//        };
////        return scrollableCont;
//        return (Container) comp;
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container getScrollableContainerOLD(Component comp) {
////        Container scrollableCont = comp.getParent();
////        while (scrollableCont != null && !scrollableCont.isScrollableY()) {
//        while (comp != null && !comp.isScrollableY()) {
////            scrollableCont = scrollableCont.getParent();
//            comp = comp.getParent();
//        };
////        return scrollableCont;
//        return (Container) comp;
//    }
//</editor-fold>
//    private Component getScrollableParent(Component comp, Container scrollableCont) {
    /**
     * returns comp, or a parent of comp, which is in the scrollable container.
     * this should be the top-level component in the scrollable container (e.g.
     * when the comp is just part of a more complex container)
     *
     * @param comp
     * @param scrollableCont
     * @return
     */
    private Component getParentInScrollableContainer(Component comp, Container scrollableCont) {
        Component parentBelongingToScrollableContiner = comp; //
        while (parentBelongingToScrollableContiner != null && parentBelongingToScrollableContiner.getParent() != scrollableCont) { //iterate until the parent==scrollableCont (meaning it belongs to scrollablCont)
            parentBelongingToScrollableContiner = parentBelongingToScrollableContiner.getParent();
        }
//        return comp; //comp==null or comp.getParent() == scrollableCont
        return parentBelongingToScrollableContiner; //comp==null or comp.getParent() == scrollableCont
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//        void setKeepItemInSameScreenPosition(Object item, Component oldItemComponent) {
//            relScroll = oldItemComponent.getY() - oldItemComponent.getComponentForm().getContentPane().getScrollY();
//            itemOrg = item;
//        }
//</editor-fold>
    /**
     * called when rebuilding a tree. Called when rebuilding the tree to find the (new) component which corresponds to the item that should be kept in the same position. 
     *
     * @param item
     * @param possibleNewComponent
     */
    void testItemToKeepInSameScreenPosition(Object item, Component possibleNewComponent) {
//        if (item != null && item == itemOrg) {
        if (itemOrg != null && item == itemOrg) {
            this.newComponent = possibleNewComponent;
            itemOrg = null;
        }
        someComponent = possibleNewComponent;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//        Object getItemToKeepInSameScreenPosition() {
//            return item;
//        }
//        void setNewScrollYPosition(Container newScrollYContainer, Component newComponent) {
//</editor-fold>
    /**
     * try to find the scrollable container (from the top of the hierarchy)
     *
     * @return the found scrollableContainer or null if none found
     */
    private Component findScrollableContainerN() {
        if (Config.TEST) {
//            Form currentForm = Display.getInstance().getCurrent();
        }
//        if (true) {
//            Form currentForm = Display.getInstance().getCurrent();
//            if (currentForm != null) {
//                if (currentForm.getContentPane() != null) {
//                    return findScrollableChild(currentForm.getContentPane());
//                }
//            }
        if (newComponent != null) { //if we found the new component
            return findScrollableChild(newComponent.getComponentForm().getContentPane());
        } else if (someComponent != null) { //else lets use some other component from the form
            return findScrollableChild(someComponent.getComponentForm().getContentPane());
        } else { //otherwise we'll simply get the form
//                return findScrollableChild(Display.getInstance().getCurrent().getContentPane());
            Form f = Display.getInstance().getCurrent();
            if (f != null) {
                return findScrollableChild(f);
            } else {
                return null;
            }
        }

//        } else {
//            if (newComponent != null) {
//                return getScrollableContainer(newComponent);
//            } else { //we didn't find newComponent so must find the scrollable container in some other way
////<editor-fold defaultstate="collapsed" desc="comment">
////            Container scrollableCont = Display.getInstance().getCurrent().getContentPane(); //if simple scrollable BoxLayout.y
////            if (Test.DEBUG) {
////                Container parent = scrollableCont.getParent();
////            }
////            if (scrollableCont.isScrollableY()) {
////                return scrollableCont;
////            } else {
////                //is the ContentPane a BorderLayout with a scrollable CENTER?
////                Layout layout = scrollableCont.getLayout();
////                if (layout instanceof BorderLayout) {
////                    Component centerComp = ((BorderLayout) layout).getCenter();
////                    if (centerComp instanceof Container && centerComp.isScrollableY()) {
//////                        scrollableCont = ((Container) centerComp); //if usual construction with scrollable center container
////                        return ((Container) centerComp); //if usual construction with scrollable center container
////                    }
////                }
////            }
////            return null;
////</editor-fold>
//                return getScrollableContainer(someComponent);
//            }
//        }
//        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container findScrollableContainerOLD() {
//        if (Config.TEST) {
//            Form currentForm = Display.getInstance().getCurrent();
//        }
//        if (newComponent != null) {
//            return getScrollableContainer(newComponent);
//        } else { //we didn't find newComponent so must find the scrollable container in some other way
//            Container scrollableCont = Display.getInstance().getCurrent().getContentPane(); //if simple scrollable BoxLayout.y
//            if (Config.TEST) {
//                Container parent = scrollableCont.getParent();
//            }
//            if (scrollableCont.isScrollableY()) {
//                return scrollableCont;
//            } else {
//                //is the ContentPane a BorderLayout with a scrollable CENTER?
//                Layout layout = scrollableCont.getLayout();
//                if (layout instanceof BorderLayout) {
//                    Component centerComp = ((BorderLayout) layout).getCenter();
//                    if (centerComp instanceof Container && centerComp.isScrollableY()) {
////                        scrollableCont = ((Container) centerComp); //if usual construction with scrollable center container
//return ((Container) centerComp); //if usual construction with scrollable center container
//                    }
//                }
//            }
//            return null;
//        }
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="comment">
//    private static Container findScrollableContainerStatic() {
//        if (Test.DEBUG) {
//            Form currentForm = Display.getInstance().getCurrent();
//        }
//        Container scrollCont = Display.getInstance().getCurrent().getContentPane(); //if simple scrollable BoxLayout.y
//        if (Test.DEBUG) {
//            Container parent = scrollCont.getParent();
//        }
//        if (!scrollCont.isScrollableY()) {
//            Layout layout = scrollCont.getLayout();
//            if (layout instanceof BorderLayout) {
//                Component centerComp = ((BorderLayout) layout).getCenter();
//                if (centerComp instanceof Container && centerComp.isScrollableY()) {
//                    scrollCont = ((Container) centerComp); //if usual construction with scrollable center container
//                }
//            }
//        }
//        return scrollCont;
//    }
//</editor-fold>

    /**
     * call after the new Container has been laid out/revalidate()
     *
     * @param newComponent
     */
    void setNewScrollYPosition() {
        if (newComponent == null) {
//            if (scrollY != 0) {
            if (scrollY != Integer.MIN_VALUE) {
                //original object has disappeared from the list (eg filtered after set Done) so simply scroll to same Y position
                //try to find the scrollable container (from the top of the hierarchy):
//<editor-fold defaultstate="collapsed" desc="comment">
//            Container scrollCont = Display.getInstance().getCurrent().getContentPane(); //if simple scrollable BoxLayout.y
//            if (!scrollCont.isScrollableY()) {
//                Layout layout = scrollCont.getLayout();
//                if (layout instanceof BorderLayout) {
//                    Component centerComp = ((BorderLayout) layout).getCenter();
//                    if (centerComp instanceof Container && centerComp.isScrollableY()) {
//                        scrollCont=((Container) centerComp); //if usual construction with scrollable center container
//                    }
//                }
//            }
//</editor-fold>
                Component scrollCont = findScrollableContainerN();
                if (scrollCont != null) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    scrollCont.setScrollY(scrollY - relScroll);
//                    if (scrollCont instanceof ContainerScrollY) {
//                        ((ContainerScrollY) scrollCont).setScrollYPublic(scrollY);
//                    } else {
//                        ASSERT.that(false, "Container "+scrollCont+" is not ContainerScrollY");
//                    }
//</editor-fold>
                    ASSERT.that(scrollCont instanceof ContainerScrollY,
                            ()->"Scrollable container not found, must improve findScrollableContainer(), scrollCont not ContainerScrollY: " + scrollCont);
                    ((ContainerScrollY) scrollCont).setScrollYPublic(scrollY);
                }
            } //else //UI: do nothing, no scroll
        } else { // (newComponent != null) 
//            Container scrollableContainer = newComponent.getComponentForm().getContentPane();
            Container scrollableContainer = getScrollableContainer(newComponent);
            if (scrollableContainer != null) { //necessary if filtering
//            newComponent=getParentInScrollableContainer(newComponent,scrollableContainer);
                Component scrollableComp = getParentInScrollableContainer(newComponent, scrollableContainer);
                if (scrollableComp != null) {
                    scrollableContainer.setSmoothScrolling(false);
                    if (relScroll != 0) {
//            scrollableContainer.setScrollY(Math.max(0, newComponent.getY() - relScroll)); //Math.max since scroll position cannot be maintainer if earlier components shrink
//                    scrollableContainer.setScrollY(Math.max(0, scrollableComp.getY() - relScroll)); //Math.max since scroll position cannot be maintained if earlier components shrink
                        ((ContainerScrollY) scrollableContainer).setScrollYPublic(Math.max(0, scrollableComp.getY() - relScroll)); //Math.max since scroll position cannot be maintained if earlier components shrink
                    } else {
                        scrollableContainer.scrollComponentToVisible(newComponent); //scroll to show the container of an item when no position was known - //TODO not tested
                    }
//                scrollableContainer.animateHierarchy(300);
                    scrollableContainer.repaint();
                    scrollableContainer.setSmoothScrolling(true);
                }
            }
            newComponent = null;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (newScrollYContainer.isScrollableY()) {
//                newScrollYContainer.setScrollY(Math.max(0, newComponent.getY() - relScroll)); //Math.max since scroll position cannot be maintainer if earlier components shrink
//            } else {
//                assert false : "trying to setScrollY on non-YScrollable container=" + newScrollYContainer;
//            }
//</editor-fold>
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 *
 * @author Thomas
 */
public class CodenameOneChanges {
    
    /*
    RadioButton.java: FINAL VERSION:
        //THJ: vvvvvvvvvvvvvvvvvvvvvvvvvvvvv
    private boolean unselectAllowed=false; //THJ

    /**
     * returns true if this RadioButton can be unselected
     * @return 
     * /
    public boolean isUnselectAllowed() {
        return unselectAllowed;
    }

    /**
     * allows users to unselect a selected RadioButton, useful e.g. when ButtonGroups should support unselecting
     * @param unselectAllowed 
     * /
    public void setUnselectAllowed(boolean unselectAllowed) {
        this.unselectAllowed = unselectAllowed;
    }
    //THJ: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    
    /**
     * {@inheritDoc}
     * /
    public void released(int x, int y) {
        // prevent the radio button from being "turned off"
        if(!isSelected() || unselectAllowed) { //THJ
//            setSelected(true); 
            setSelected(!isSelected()); //THJ
        }
        super.released(x, y);
    }
    
    -----------------------------
    
    RadioButton.java:
    ---------------------
     public void released(int x, int y) {
        // prevent the radio button from being "turned off"
//        if (isToggle()) { //THJ
//            setSelected(!isSelected()); //THJ
//        } else //THJ
//        {
//            if (!isSelected()) {
//                setSelected(true);
//            }
//        }
        setSelected(!isSelected());
        super.released(x, y);
    }
    */
    
    /*
    Container.java:
    ---------------------
    //NO LONGER used (was used in a modified version of drop()
        public Component findDropTargetAt(int x, int y) { //THJ: added public
---------------------
       public void drop(Component dragged, int x, int y) {
        int i = getComponentIndex(dragged);
        if(i > -1) {
//            Component dest = getComponentAt(x, y); //THJ
            Component dest = findDropTargetAt(x, y); //THJ
            if(dest != dragged) {
                int destIndex = getComponentIndex(dest);
                if(destIndex > -1 && destIndex != i) {
                    removeComponent(dragged);
                    Object con = getLayout().getComponentConstraint(dragged);
                    if(con != null) {
                        addComponent(destIndex, con, dragged);
                    } else {
                        addComponent(destIndex, dragged);
                    }
                }
            }
            animateLayout(400);
        } else {
            Container oldParent = dragged.getParent();
            if(oldParent != null) {
                oldParent.removeComponent(dragged);
            }
            Component pos = getComponentAt(x, y);
            i = getComponentIndex(pos);
            if(i > -1) {
                addComponent(i, dragged);
            } else {
                addComponent(dragged);
            }
            getComponentForm().animateHierarchy(400);
        }
    }
    */
    
    /*
    Component.java:
    ----------------
    //NO LONGER USED
    //    Component getLeadComponent() { //THJ
    public Component getLeadComponent() { //THJ
        Container p = getParent();
        if(p != null) {
            return p.getLeadComponent();
        }
        return null;
    }
    */
    
    /*
    Tree.java: 
    NOT NECESSARY
    -------------
            public void actionPerformed(ActionEvent evt) {
            if(current != null) {
                leafListener.fireActionEvent(new ActionEvent(current,ActionEvent.Type.Other));
                return;
            }
//            Component c = (Component)evt.getSource(); //THJ:
            Component c = (Component)evt.getComponent(); //THJ: fix for type cast error when source is a command
            Container lead = c.getParent().getLeadParent();
            if(lead != null) {
                c = lead;
            }
            Object e = c.getClientProperty(KEY_EXPANDED);
            if(e != null && e.equals("true")) {
                collapseNode(c);
            } else {
                expandNode(isInitialized(), c);
            }
        }

    */
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.tree.TreeModel;
import java.util.List;

/**
 *
 * @author Thomas
 */
    interface MyTreeModel {

        /**
         * Returns the list of child objects representing the given parent.
         * Returns null if there are no child objects (to avoid creating an
         * empty Vector).
         *
         * @return the children of the given node within the tree
         */
        public List getChildrenList(Object parent);

        /**
         * Is the node a leaf or a folder
         *
         * @param node a node within the tree
         * @return true if the node is a leaf that can't be expanded
         */
            /**
     * Is the node a leaf or a folder
     *
     * @param node a node within the tree
     * @return true if the node is a leaf that can't be expanded
     */
    public boolean isLeaf(Object node);

    }


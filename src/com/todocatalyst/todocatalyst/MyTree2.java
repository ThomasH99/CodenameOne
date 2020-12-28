/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores
 * CA 94065 USA or visit www.oracle.com if you need additional information or
 * have any questions.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.StickyHeader;
//import com.codename1.ui.StickyHeader;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.animations.Transition;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.EventDispatcher;
import java.util.List;
//import com.todocatalyst.todocatalyst.InlineInsertNewElementContainer.InsertNewElementFunc;

/**
 * <p>
 * The {@code Tree} component allows constructing simple tree component
 * hierarchies that can be expanded seamlessly with no limit. The tree is bound
 * to a model that can provide data with free form depth such as file system or
 * similarly structured data.<br>
 * To customize the look of the tree the component can be derived and component
 * creation can be replaced.</p>
 *
 * <script src="https://gist.github.com/codenameone/870d4412694bca3092c4.js"></script>
 * <img src="https://www.codenameone.com/img/developer-guide/tree.png" alt="Tree sample code" />
 *
 * <p>
 * And heres a more "real world" example showing an XML hierarchy in a
 * {@code Tree}:
 * </p>
 * <script src="https://gist.github.com/codenameone/5361ad7339c1ae26e0b8.js"></script>
 * <img src="https://www.codenameone.com/img/developer-guide/components-tree-xml.png" alt="Tree with XML data" />
 *
 * <p>
 * Another real world example showing the
 * {@link com.codename1.io.FileSystemStorage} as a tree:
 * </p>
 * <script src="https://gist.github.com/codenameone/2877412809a8cff646af.js"></script>
 * <img src="https://www.codenameone.com/img/developer-guide/filesystem-tree.png" alt="Simple sample of a tree for the FileSystemStorage API">
 *
 * @author Shai Almog
 */
public class MyTree2 extends ContainerScrollY {

    static final String KEY_OBJECT = "TREE_OBJECT";
    static final String KEY_PARENT = "TREE_PARENT";
//    static final String KEY_LIST = "TreeList"; //THJ
    static final String KEY_EXPANDED = "TREE_NODE_EXPANDED";
    static final String KEY_DEPTH = "TREE_DEPTH";
    static final String KEY_TOP_NODE = "TreeContainer";
    static final String KEY_ACTION_ORIGIN = "subTasksButton";
    static final String KEY_LIST_CONTAINER = "listContainer"; //THJ: mark the container which is used as list for insertNewTask container
    static final String KEY_LONG_PRESS = "LongPress"; //THJ: mark the container which is used as list for insertNewTask container
    static final int DEPTH_INDENT = 15;
    private EventDispatcher leafListener = new EventDispatcher();

    private ActionListener expandCollapseListener = new Handler();
    private MyTreeModel model;
    private static Image folder;
    private static Image openFolder;
    private static Image nodeImage;
    private int depthIndent = DEPTH_INDENT;
//    private boolean multilineMode;
//    private HashSet expandedObjects; // = new HashSet();
    private ExpandedObjects expandedObjects; // = new HashSet();
//    private FilterSortDef itemListFilteredSorted;
//    private InsertNewElementFunc insertNewElementFunc = null;
//    private InsertNewElementFunc newInsertContainer = null;
    private PinchInsertContainer insertNewElementFunc = null;
    private PinchInsertContainer newInsertContainer = null;
//    private TextArea startEditTextArea = null;

//    private void setInsertField(InsertNewElementFunc newInsertContainer) {
    private void setInsertField(PinchInsertContainer newInsertContainer) {
        this.newInsertContainer = newInsertContainer;
    }

//    public InsertNewElementFunc getInlineInsertField() {
    public PinchInsertContainer getInlineInsertField() {
        return newInsertContainer;
    }
//    private void setAsyncEditField(TextArea startEditTextArea) {
//        this.startEditTextArea = startEditTextArea;
//    }
//
//    public TextArea getAsyncEditField() {
//        return startEditTextArea;
//    }

    StickyHeaderGenerator stickyHeaderGen = null;

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * Constructor for usage by GUI builder and automated tools, normally one
     * should use the version that accepts the model
     */
//    public MyTree2() {
//        this(new StringArrayTreeModel(new String[][]{
//            {"Colors", "Letters", "Numbers"},
//            {"Red", "Green", "Blue"},
//            {"A", "B", "C"},
//            {"1", "2", "3"}
//        }));
//    }
//</editor-fold>
    /**
     * Construct a tree with the given tree model
     *
     * @param model represents the contents of the tree
     */
//    public MyTree2(MyTreeModel model) {
//        this(model, null);
//    }
//    public MyTree2(MyTreeModel model, HashSet expandedObjects) {
//    public MyTree2(MyTreeModel model, ExpandedObjects expandedObjects) {
//        this(model, expandedObjects, null);
//    }
//    public MyTree2(MyTreeModel model, HashSet expandedObjects, FilterSortDef itemListFilteredSorted) {
//        this(model, expandedObjects, itemListFilteredSorted, null);
//    }
//    public MyTree2(MyTreeModel model, HashSet expandedObjects, FilterSortDef itemListFilteredSorted, InsertNewTaskFunc insertNewTask) {
    /**
     *
     * @param model the model to expand, e.g. an Item or ItemList
     * @param expandedObjects list of expanded objects to know it should be
     * expanded by default when displaying
     * @param insertNewTask function to determine a newTaskContainer should be
     * insert below an Item
     */
//    public MyTree2(MyTreeModel model, HashSet expandedObjects, InsertNewElementFunc insertNewTask) {
//    public MyTree2(MyTreeModel model, ExpandedObjects expandedObjects, InsertNewElementFunc insertNewTask) {
//        this(model, expandedObjects, insertNewTask, null);
//    }
//    public MyTree2(MyTreeModel model, HashSet expandedObjects, InsertNewElementFunc insertNewTask, StickyHeaderGenerator stickyHeaderGen) {
//    public MyTree2(MyTreeModel model, ExpandedObjects expandedObjects, InsertNewElementFunc insertNewTask, StickyHeaderGenerator stickyHeaderGen) {
    public MyTree2(MyTreeModel model, ExpandedObjects expandedObjects, PinchInsertContainer insertNewTask, StickyHeaderGenerator stickyHeaderGen) {
        super();
        this.model = model;
//        setUIID("MyTree2");
        if (Config.TEST) {
            setName("MyTree2");
        }
//        if (expandedObjects != null) {
//            this.expandedObjects = new HashSet(expandedObjects);
//        } else {
//            this.expandedObjects = new HashSet();
//        }
        this.expandedObjects = expandedObjects;
//        this.itemListFilteredSorted = itemListFilteredSorted;
        this.insertNewElementFunc = insertNewTask;
        this.stickyHeaderGen = stickyHeaderGen;

        FilterSortDef itemListFilteredSorted;
        if (MyPrefs.hideStickyHeadersForSortedLists.getBoolean()) {
            this.stickyHeaderGen = (item) -> null; //will also override any alerady given value for stickyHeaderGen
        } else if (this.stickyHeaderGen == null) {
            if (this.model instanceof ItemList
                    //                    && (itemListFilteredSorted = ((ItemList) this.model).getFilterSortDefN()) != null
                    && (itemListFilteredSorted = ((ItemList) this.model).getFilterSortDef(true)) != null
                    && itemListFilteredSorted.isSortOn()) {
//            FilterSortDef itemListFilteredSorted = ((ItemList) model).getFilterSortDef();
//        if (this.itemListFilteredSorted != null && this.itemListFilteredSorted.isSortOn()) {
//            if (itemListFilteredSorted != null && itemListFilteredSorted.isSortOn()) {
//            stickyHeaderGen = makeStickyHeaderGen(this.itemListFilteredSorted.getSortFieldId());
                this.stickyHeaderGen = makeStickyHeaderGen(itemListFilteredSorted.getSortFieldId());
//            }
            } else {
                this.stickyHeaderGen = (item) -> null;
            }
        }

//        BoxLayout layout;
//        layout = new BoxLayout(BoxLayout.Y_AXIS);
//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        FlowLayout layout ;
//        layout =(new FlowLayout(Component.CENTER));
//        layout.
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);
        setAlwaysTensile(true); //always allow pull down even of short lists

//        if (folder == null) {
//            folder = UIManager.getInstance().getThemeImageConstant("treeFolderImage");
//            openFolder = UIManager.getInstance().getThemeImageConstant("treeFolderOpenImage");
//            nodeImage = UIManager.getInstance().getThemeImageConstant("treeNodeImage");
//        }
        buildBranch(null, 0, this, false);
        setUIID("Tree");
    }

//    void resetExpandedObjects() {
//        //TODO implement this if needed
////        setExpandedObject(new HashSet()); //use instead of set.clear to avoid changing the original set?? No better to clear the original set so changes are stored.
//        expandedObjects.clear();
////        this.subTreeExpanded = false;
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * sets the list of automatically expanded objects (objects which will
//     * automatically be expanded when encountered when building the Tree.
//     * Objects which are not part of the item hierarchy (deleted, filtered) or
//     * which are below a not shown object (e.g. one not previously expanded) are
//     * not expanded.
//     *
//     * @param setOfAutomaticallyExpandedObjects
//     */
//    void setExpandedObjects(Collection setOfAutomaticallyExpandedObjects) {
//        if (expandedObjects == null) {
////            expandedObjects = new HashSet();
//            expandedObjects = new HashSet();
//        }
//        expandedObjects.addAll(setOfAutomaticallyExpandedObjects);
//    }
//
//    Collection getExpandedObjects() {
//        return expandedObjects;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * Toggles a mode where rows in the tree can be broken since span buttons
     * will be used instead of plain buttons.
     *
     * @return the multilineMode
     */
//    public boolean isMultilineMode() {
//        return multilineMode;
//    }
    /**
     * Toggles a mode where rows in the tree can be broken since span buttons
     * will be used instead of plain buttons.
     *
     * @param multilineMode the multilineMode to set
     */
//    public void setMultilineMode(boolean multilineMode) {
//        this.multilineMode = multilineMode;
//    }
//    static class StringArrayTreeModel implements TreeModel {
//
//        String[][] arr;
//
//        StringArrayTreeModel(String[][] arr) {
//            this.arr = arr;
//        }
//
//        public Vector getChildren(Object parent) {
//            if (parent == null) {
//                Vector v = new Vector();
//                int a0len = arr[0].length;
//                for (int iter = 0; iter < a0len; iter++) {
//                    v.addElement(arr[0][iter]);
//                }
//                return v;
//            }
//            int alen = arr.length;
//            int aolen = arr[0].length;
//            Vector v = new Vector();
//            for (int iter = 0; iter < aolen; iter++) {
//                if (parent == arr[0][iter]) {
//                    if (alen > iter + 1 && arr[iter + 1] != null) {
//                        int ailen = arr[iter + 1].length;
//                        for (int i = 0; i < ailen; i++) {
//                            v.addElement(arr[iter + 1][i]);
//                        }
//                    }
//                }
//            }
//            return v;
//        }
//
//        public boolean isLeaf(Object node) {
//            Vector v = getChildren(node);
//            return v == null || v.size() == 0;
//        }
//    }
//</editor-fold>
    /**
     * {@inheritDoc}
     */
    public String[] getPropertyNames() {
        return new String[]{"data"};
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getPropertyTypes() {
        return new Class[]{com.codename1.impl.CodenameOneImplementation.getStringArray2DClass()};
    }

    /**
     * {@inheritDoc}
     */
    public String[] getPropertyTypeNames() {
        return new String[]{"String[][]"};
    }

    /**
     * {@inheritDoc}
     */
    public Object getPropertyValue(String name) {
//        if (name.equals("data")) {
////            return ((StringArrayTreeModel) model).arr;
//            return ((StringTreeModel) model).arr;
//        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String setPropertyValue(String name, Object value) {
//        if (name.equals("data")) {
//            setModel(new StringArrayTreeModel((String[][]) value));
//            return null;
//        }
        return super.setPropertyValue(name, value);
    }

    /**
     * Returns the tree model instance
     *
     * @return the tree model
     */
    public MyTreeModel getModel() {
        return model;
    }

    /**
     * Sets the tree model to a new value
     *
     * @param model the model of the tree
     */
    public void setModel(MyTreeModel model) {
        this.model = model;
        removeAll();
        buildBranch(null, 0, this, false);
    }

    /**
     * Sets the icon for a tree folder
     *
     * @param folderIcon the icon for a folder within the tree
     */
    public static void setFolderIcon(Image folderIcon) {
        folder = folderIcon;
    }

    /**
     * Sets the icon for a tree folder in its expanded state
     *
     * @param folderIcon the icon for a folder within the tree
     */
    public static void setFolderOpenIcon(Image folderIcon) {
        openFolder = folderIcon;
    }

    /**
     * Sets the icon for a tree node
     *
     * @param nodeIcon the icon for a node within the tree
     */
    public static void setNodeIcon(Image nodeIcon) {
        nodeImage = nodeIcon;
    }

    Container expandNode(boolean animate, Component c, boolean expandAllLevels) {
        Container cont = expandNodeImpl(animate, c, expandAllLevels);
        Container parent = c.getParent();
        if (parent != null) { //added due to nullpoint in expand/collapse hierarchy
            if (isInitialized() && animate) {
                // prevent a race condition on node expansion contraction
//                parent.animateHierarchyAndWait(300);
//parent.getAnimationManager().
                parent.animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
//            if (multilineMode) {
//                revalidate();
//            }
            } else {
//                parent.revalidate();
                parent.revalidateWithAnimationSafety();
            }
        }
        return cont;
    }

    /**
     * insert a
     *
     * @param parent
     * @return
     */
    static ContainerScrollY getInsertSubtaskCont(Container parent) {
        //if there is already a subtask container, return it
        if (parent.getLayout() instanceof BorderLayout) {
            BorderLayout borderLayout = (BorderLayout) parent.getLayout();
            if (borderLayout.getCenter() instanceof ContainerScrollY) {
                return (ContainerScrollY) borderLayout.getCenter();
            }
        }
        //else create new container:
        ContainerScrollY dest = new ContainerScrollY(new BoxLayout(BoxLayout.Y_AXIS));
        dest.setName("ExpandedSubtasks");
        dest.setUIID("ExpandedList");
        parent.addComponent(BorderLayout.CENTER, dest);
        return dest;
    }

    /**
     * insert a subtask into a parent container. Called when dragging a subtask
     * back under its original owner.
     *
     * @param parent
     * @param subtaskComp
     */
    static void insertAtPositionOfFirstSubtask(Container parent, Component subtaskComp) {
        ContainerScrollY dest = getInsertSubtaskCont(parent);
        dest.addComponent(0, subtaskComp); //always insert at first position
    }

    private Container expandNodeImpl(boolean animate, Component c, boolean expandAllLevels) {
        Container p = c.getParent().getLeadParent();
        if (p != null) {
            c = p;
        }
        c.putClientProperty(KEY_EXPANDED, "true");
//        setNodeIcon(openFolder, c);
        int depth = ((Integer) c.getClientProperty(KEY_DEPTH)).intValue();
        Container parent = c.getParent();
        Object o = c.getClientProperty(KEY_OBJECT);
//        ContainerScrollY dest = new ContainerScrollY(new BoxLayout(BoxLayout.Y_AXIS));
//        dest.setUIID("ExpandedList");
//        parent.addComponent(BorderLayout.CENTER, dest);
        ContainerScrollY dest = getInsertSubtaskCont(parent);
        buildBranch(o, depth, dest, expandAllLevels);
//        if (isInitialized() && animate) {
//            // prevent a race condition on node expansion contraction
//            parent.animateHierarchyAndWait(300);
////            if (multilineMode) {
////                revalidate();
////            }
//        } else {
//            parent.revalidate();
//        }
        return dest;
    }

    private boolean isExpanded(Component c) {
        Object e = c.getClientProperty(KEY_EXPANDED);
        return e != null && e.equals("true");
    }

    Container expandPathNode(boolean animate, Container parent, Object node) {
        int cc = parent.getComponentCount();
        for (int iter = 0; iter < cc; iter++) {
            Component current = parent.getComponentAt(iter);
            if (current instanceof Container) {
                BorderLayout bl = (BorderLayout) ((Container) current).getLayout();

                // the tree component is always at north expanded or otherwise
                current = bl.getNorth();
                if (current != null) {
                    Object o = current.getClientProperty(KEY_OBJECT);
                    if (o != null && o.equals(node)) {
                        if (isExpanded(current)) {
                            return (Container) bl.getCenter();
                        }
//                        return expandNodeImpl(animate, current, false);
                        return expandNode(animate, current, false);
                    }
                }
            }
        }
        return null;
    }

    void collapsePathNode(Container parent, Object node) {
        int cc = parent.getComponentCount();
        for (int iter = 0; iter < cc; iter++) {
            Component current = parent.getComponentAt(iter);
            if (isExpanded(current)) {
                BorderLayout bl = (BorderLayout) ((Container) current).getLayout();

                // the tree component is always at north expanded or otherwise
                current = bl.getNorth();
                if (current != null) {
                    Object o = current.getClientProperty(KEY_OBJECT);
                    if (o != null && o.equals(node)) {
                        if (isExpanded(current)) {
                            collapseNode(current, null, false);
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * Expands the tree path
     *
     * @param path the path to expand
     */
    public void expandPath(Object... path) {
        expandPath(isInitialized(), path);
    }

    /**
     * Expands the tree path
     *
     * @param path the path to expand
     * @param animate whether to animate expansion
     */
    public void expandPath(boolean animate, Object... path) {
        Container c = this;
        int plen = path.length;
        for (int iter = 0; iter < plen; iter++) {
            c = expandPathNode(animate, c, path[iter]);
        }
    }

    /**
     * Collapses the last element in the path
     *
     * @param path the path to the element that should be collapsed
     */
    public void collapsePath(Object... path) {
        Container c = this;
        int plen = path.length;
        for (int iter = 0; iter < plen - 1; iter++) {
            c = expandPathNode(isInitialized(), c, path[iter]);
        }
        collapsePathNode(c, path[plen - 1]);
    }

    void collapseNode(Component c, boolean collapseAllLevels) {
        collapseNode(c, CommonTransitions.createSlide(CommonTransitions.SLIDE_VERTICAL, false, MyForm.ANIMATION_TIME_DEFAULT), collapseAllLevels);
    }

    /**
     * remove all expandedObjects below the component so they are not
     * automatically expanded next time the component above is shown.
     *
     * @param containerWithExpandedSubNodes
     * @param collapseAllLevels
     */
    private void removeExpandedSubObjects(Container containerWithExpandedSubNodes) {
        //containerWithExpandedSubNodes = BoxY { [BorderL.NORTH=ItemNode, BorderL.CENTER=list of expanded subnodes], ...}
        for (int i = 0, size = containerWithExpandedSubNodes.getComponentCount(); i < size; i++) { //for each subnode
            Component comp = containerWithExpandedSubNodes.getComponentAt(i);
            if (comp instanceof Container) {
                Container cont = (Container) comp;
                for (int i2 = 0, size2 = cont.getComponentCount(); i2 < size2; i2++) {
                    Component subNodeCont = cont.getComponentAt(i2);
                    Object expanded = subNodeCont.getClientProperty(KEY_EXPANDED);
                    if (expanded != null && expanded.equals("true")) {
                        Object nodeElement = subNodeCont.getClientProperty(KEY_OBJECT);
//                        if (nodeElement != null && expandedObjects != null && expandedObjects.contains(nodeElement)) {
                        if (nodeElement instanceof ItemAndListCommonInterface && expandedObjects != null && expandedObjects.contains((ItemAndListCommonInterface) nodeElement)) {
                            expandedObjects.remove((ItemAndListCommonInterface) nodeElement);
                        } else {
                            assert false : "if KEY_EXPANDED==true, there should be a KEY_OBJECT";
                        }
                    } else if (subNodeCont instanceof Container) {
                        removeExpandedSubObjects((Container) subNodeCont);
                    }
                }
            }
        }
    }

    private void collapseNode(Component itemNode, Transition t, boolean collapseAllLevels) {
//        Container lead = c.getParent().getLeadParent();
//        if (lead != null) {
//            c = lead;
//        }
        itemNode.putClientProperty(KEY_EXPANDED, null);
//        setNodeIcon(folder, c);
        if (expandedObjects != null) {
            expandedObjects.remove((ItemAndListCommonInterface) itemNode.getClientProperty(KEY_OBJECT));
        }
        //Parent = {North: ItemNode; Center: expandedSubTasks}
        Container itemNodeParent = itemNode.getParent();
        for (int i = 0, size = itemNodeParent.getComponentCount(); i < size; i++) {
            Component comp = itemNodeParent.getComponentAt(i);
            if (comp != itemNode) { //don't collapse North:ItemNode
                Label dummy = new Label();
                itemNodeParent.replaceAndWait(comp, dummy, t, true);
                itemNodeParent.removeComponent(dummy);
                if (collapseAllLevels && expandedObjects != null && comp instanceof Container) {
//                    Object nodeObject = comp.getClientProperty(KEY_OBJECT); //comp is a list of expanded subnodes, so no KEY_OBJECT
//                    if (expandedObjects.contains(nodeObject) && collapseAllLevels) {
//                        expandedObjects.remove(nodeObject);
//                    }
                    removeExpandedSubObjects((Container) comp);
                }
            }
        }
    }

    /**
     * Returns the currently selected item in the tree
     *
     * @return the object selected within the tree
     */
    public Object getSelectedItem() {
        Component c = getComponentForm().getFocused();
        if (c != null) {
            return c.getClientProperty(KEY_OBJECT);
        }
        return null;
    }

    /**
     * Adds the child components of a tree branch to the destination container.
     * Expands the entire hierarchy if expandAllLevels==true.
     */
    private void buildBranch(Object parent, int depth, Container destination, boolean expandAllLevels) {
        List children = model.getChildrenList(parent);
        if (expandedObjects != null) {
            if (parent == null) {
                //parent==null corresponds to expanding the list
//                assert model instanceof Item;
                expandedObjects.add(model);
            } else {
//                assert parent instanceof Item;
                expandedObjects.add(parent);
            }
        }
        int size = children.size();
        Integer depthVal = new Integer(depth + 1);
        destination.putClientProperty(KEY_LIST_CONTAINER, "true"); //mark the container in which the new containers are inserted
        for (int iter = 0; iter < size; iter++) {
            final Object current = children.get(iter);

            //STICKYHEADER
//            Component stickyHeader = makeStickyHeader(current);
//            if (stickyHeader != null) {
//                destination.add(stickyHeader); //add the sticky header to the tree container(??)
//            }
            if (depth == 0 && stickyHeaderGen != null) {
                Component stickyHeader = stickyHeaderGen.getComp(current);
                if (stickyHeader != null) {
                    destination.add(stickyHeader); //add a Label with the header
                }
            }

            Category category = null;
            Component nodeComponent;
            //Hack to 
            if (parent instanceof Category) {
                nodeComponent = createNode(current, depth, (Category) parent);
                category = (Category) parent;
            } else if (parent == null && model instanceof Category) {
                nodeComponent = createNode(current, depth, (Category) model); //hack when buildBranch is called with parent==null 
                category = (Category) model;
            } else if (parent == null && current instanceof Category) {
                nodeComponent = createNode(current, depth, (Category) current); //hack when buildBranch is called with parent==null 
                category = (Category) current;
            } else if (parent instanceof ItemAndListCommonInterface) {
//                nodeComponent = createNode(current, depth);
                nodeComponent = createNode(current, depth, (ItemAndListCommonInterface) parent, null);
//            } else if (current instanceof ExpiredAlarm) {
//                nodeComponent = createNode(current, depth, (ItemAndListCommonInterface) parent, null);
            } else {
                nodeComponent = createNode(current, depth);
            }
//            if (Config.TEST && current instanceof ItemAndListCommonInterface) nodeComponent.setName("TreeNode-" + ((ItemAndListCommonInterface) current).getText()); //NO, each create sets the name itself
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (model.isLeaf(current)) {
//                destination.addComponent(nodeComponent); //in CN1 impl, leafs are not encapsulated in 'expandable' BorderLayouts, but here we do it since tree nodes may change from leafs to trees
////                bindNodeListener(new Handler(current), nodeComponent);
//            } else {
//</editor-fold>
            Container componentArea = new Container(new BorderLayout());
            componentArea.setUIID("TreeContainer"); //wraps a possibly expanded task

            componentArea.addComponent(BorderLayout.NORTH, nodeComponent);
            if (Config.TEST) {
                componentArea.setName("TreeCont-" + nodeComponent.getName()); //reuse name 
            }
            destination.addComponent(componentArea);

//            if (Config.TEST) destination.setName("TreeTop-" + componentArea.getName()); //reuse name 
            bindNodeListener(expandCollapseListener, nodeComponent);
            nodeComponent.putClientProperty(KEY_OBJECT, current);
            nodeComponent.putClientProperty(KEY_PARENT, parent);
            //store the list of this item for use in drag and drop
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (parent == null) {
//                nodeComponent.putClientProperty(KEY_LIST, model);
//            } else {
//                nodeComponent.putClientProperty(KEY_LIST, parent);
//            }
//</editor-fold>
            nodeComponent.putClientProperty(KEY_DEPTH, depthVal);
//            if (expandedObjects != null && expandedObjects.contains(current) || expandAllLevels) {
            if (expandedObjects != null && current instanceof ItemAndListCommonInterface
                    && expandedObjects.contains((ItemAndListCommonInterface) current) || expandAllLevels) {
                if (expandAllLevels) {
                    expandedObjects.add(current);
                }
                expandNode(true, nodeComponent, expandAllLevels);
//                    nodeComponent.putClientProperty(KEY_EXPANDED, "true"); //done in expandNode()
            }
            //check if a new insertNewTask container should be created for current and if so insert it:
//            if (insertNewTask != null && current instanceof Item && model instanceof ItemAndListCommonInterface) {
//            InsertNewElementFunc newInsertCont;
            PinchInsertContainer newInsertCont;
            if (false && insertNewElementFunc != null) { //now handled directly in the Screen.refreshAfterEdit
                if (current instanceof WorkSlot) {
//                    newInsertCont = insertNewElementFunc.make((ItemAndListCommonInterface) current, null, null);
                    newInsertCont = insertNewElementFunc.make((ItemAndListCommonInterface) current, ((WorkSlot) current).getOwner(), null); //insert add'l workslots in same list as current, category has no sense for workslots
                    if (newInsertCont != null) {
                        if (Config.TEST && current instanceof ItemAndListCommonInterface) {
                            ((Component) newInsertCont).setName("TreeInsertContainer-" + ((ItemAndListCommonInterface) current).getText());
                        }
                        destination.add((Component) newInsertCont);
                        setInsertField(newInsertCont);
                    }
                } else if ((current instanceof ItemAndListCommonInterface //instanceof false if current==null!
                        && (parent instanceof ItemAndListCommonInterface
                        || (parent == null && model instanceof ItemAndListCommonInterface)))) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                Component insertNewTsk = insertNewTask.make((Item) current, (ItemAndListCommonInterface) model);
//                InsertNewTaskContainer insertNewTsk = insertNewTask.make((Item) current, (ItemAndListCommonInterface) parent);
//                InlineInsertNewElementContainer insertNewTask = insertNewElementFunc.make((Item) current, parent != null ? (ItemAndListCommonInterface) parent : (ItemAndListCommonInterface) model);
//                MyForm myForm = (MyForm) getComponentForm();
//                InsertNewElementFunc insertNewElement = myForm.getInlineInsertContainer(); //insertNewElementFunc.make((Item) current, parent != null ? (ItemAndListCommonInterface) parent : (ItemAndListCommonInterface) model);
//                if (insertNewElement != null) {
//</editor-fold>
                    newInsertCont = insertNewElementFunc.make((ItemAndListCommonInterface) current,
                            parent != null ? (ItemAndListCommonInterface) parent : (ItemAndListCommonInterface) model, category);
                    if (newInsertCont != null) {
//                    destination.add(insertNewElement);
                        if (Config.TEST && current instanceof ItemAndListCommonInterface) {
                            ((Component) newInsertCont).setName("TreeInsertContainer-" + ((ItemAndListCommonInterface) current).getText());
                        }
                        destination.add((Component) newInsertCont);
//                        myForm.setInlineInsertContainer(newInsertContainer);
//                    setAsyncEditField(newInsertContainer.getTextArea());
                        setInsertField(newInsertCont);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    getComponentForm().setEditOnShow(insertNewTask.getTextField()); //ComponentForm should never be undefined here since MyTree should already be in a form
//                    destination.getComponentForm().setEditOnShow(insertNewTask.getTextField()); //UI: set for edit
//</editor-fold>
                    }
//                }
                }
            }
        }
    }

    public static class ListAndIndex {

        Container list;
        int index;

        ListAndIndex(Container list, int index) {
            this.list = list;
            this.index = index;
        }
    }

    /**
     * return the container in which the list of components is inserted. Used to
     * insert the insertNewTaskContainer.
     *
     * @param comp
     * @return
     */
    public static ListAndIndex getListContainer(Component comp) {
        Component cont = comp;
        Component prevCont = null;
//<editor-fold defaultstate="collapsed" desc="comment">
//        while (cont != null && cont.getPropertyValue(KEY_DEPTH) == null) {
//            prevCont = cont;
//            cont = cont.getParent();
//        }
//        while (cont != null && cont.getPropertyValue(KEY_LIST_CONTAINER) == null && !(cont instanceof MyTree2)) {
//</editor-fold>
        while (cont != null && cont.getClientProperty(KEY_LIST_CONTAINER) == null) {
            prevCont = cont;
            cont = cont.getParent();
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        //list is parent above the container with getPropertyValue(KEY_DEPTH)
//        prevCont = cont;
//        cont = cont.getParent();
//        prevCont = cont;
//        cont = cont.getParent();

//        Result res = new Result();
//if (cont instanceof MyTree2)
//    else
//        return new Result((Container) cont, ((Container) cont).getComponentIndex(prevCont));
//</editor-fold>
        return new ListAndIndex((Container) cont, ((Container) cont).getComponentIndex(prevCont));
    }

    /**
     * THJ: refreshes the tree (rebuild all nodes with updated values)
     */
    public void refresh() {
        removeAll();
        buildBranch(null, 0, this, false);
    }

    /**
     * Creates a node within the tree, this method is protected allowing tree to
     * be subclassed to replace the rendering logic of individual tree buttons.
     *
     * @param node the node object from the model to display on the button
     * @param depth the depth within the tree (normally represented by indenting
     * the entry)
     * @return a button representing the node within the tree
     * @deprecated replaced with createNode, bindNodeListener and setNodeIcon
     */
    protected Button createNodeComponent(Object node, int depth) {
        Button cmp = new Button(childToDisplayLabel(node));
        cmp.setUIID("TreeNode");
        if (model.isLeaf(node)) {
            cmp.setIcon(nodeImage);
        } else {
            cmp.setIcon(folder);
        }
        if (false) {
            updateNodeComponentStyle(cmp.getSelectedStyle(), depth);
            updateNodeComponentStyle(cmp.getUnselectedStyle(), depth);
            updateNodeComponentStyle(cmp.getPressedStyle(), depth);
        }
        return cmp;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * Since a node may be any component type developers should override this
     * method to add support for binding the click listener to the given
     * component.
     *
     * @param l listener interface
     * @param node node component returned by createNode
     */
//    protected void bindNodeListenerXXX(ActionListener l, Component node) {
//        if (node instanceof Button) {
//            ((Button) node).addActionListener(l);
//            return;
//        }
//        ((SpanButton) node).addActionListener(l);
//    }
//</editor-fold>
    /**
     * Sets the icon for the given node similar in scope to bindNodeListener
     *
     * @param icon the icon for the node
     * @param node the node instance
     */
    protected void setNodeIcon(Image icon, Component node) {
        if (node instanceof Button) {
            ((Button) node).setIcon(icon);
            return;
        }
        ((SpanButton) node).setIcon(icon);
    }

    /**
     * Creates a node within the tree, this method is protected allowing tree to
     * be subclassed to replace the rendering logic of individual tree buttons.
     *
     * @param node the node object from the model to display on the button
     * @param depth the depth within the tree (normally represented by indenting
     * the entry)
     * @return a button representing the node within the tree
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected Component createNodeXXX(Object node, int depth) {
////        if (multilineMode) {
////            SpanButton cmp = new SpanButton(childToDisplayLabel(node));
////            cmp.setUIID("TreeNode");
////            cmp.setTextUIID("TreeNode");
////            if (model.isLeaf(node)) {
////                cmp.setIcon(nodeImage);
////            } else {
////                cmp.setIcon(folder);
////            }
////            updateNodeComponentStyle(cmp.getSelectedStyle(), depth);
////            updateNodeComponentStyle(cmp.getUnselectedStyle(), depth);
////            updateNodeComponentStyle(cmp.getPressedStyle(), depth);
////            return cmp;
////        }
//        return createNodeComponent(node, depth);
//    }
//</editor-fold>
    private void updateNodeComponentStyle(Style s, int depth) {
        s.setMargin(LEFT, depth * depthIndent);
    }

    /**
     * Converts a tree child to a label, this method can be overriden for simple
     * rendering effects
     *
     * @return a string representing the given tree node
     */
    protected String childToDisplayLabel(Object child) {
        return child.toString();
    }

    /**
     * A listener that fires when a leaf is clicked
     *
     * @param l listener to fire when the leaf is clicked
     */
    public void addLeafListener(ActionListener l) {
        leafListener.addListener(l);
    }

    /**
     * Removes the listener that fires when a leaf is clicked
     *
     * @param l listener to remove
     */
    public void removeLeafListener(ActionListener l) {
        leafListener.removeListener(l);
    }

    /**
     * {@inheritDoc}
     */
    protected Dimension calcPreferredSize() {
        Dimension d = super.calcPreferredSize();

        // if the tree is entirely collapsed try to reserve at least 6 rows for the content
        int count = getComponentCount();
        for (int iter = 0; iter < count; iter++) {
            if (getComponentAt(iter) instanceof Container) {
                return d;
            }
        }
        int size = Math.max(1, model.getChildrenList(null).size());
        if (size < 6) {
            return new Dimension(Math.max(d.getWidth(), Display.getInstance().getDisplayWidth() / 4 * 3),
                    d.getHeight() / size * 6);
        }
        return d;
    }

    /**
     * This class unifies two action listeners into a single class to reduce the
     * size overhead
     */
    private class Handler implements ActionListener {

        private Object current;

        public Handler() {
        }

        public Handler(Object current) {
            this.current = current;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Component c = (Component) evt.getComponent(); //THJ: fix for type cast error when source is a command
            if (c.getClientProperty(KEY_LONG_PRESS) != null) {
                c.putClientProperty(KEY_LONG_PRESS, null); //is set in ScreenItemListofItems.subtask
                return;
            }

            if (current != null) {
                leafListener.fireActionEvent(new ActionEvent(current, ActionEvent.Type.Other));
                return;
            }
//            Component c = (Component)evt.getSource(); //THJ:
            Container lead = c.getParent().getLeadParent();
            if (lead != null) {
                c = lead;
            }
            //if event comes from eg a button inside the original node, get the original node
            if (c.getClientProperty(KEY_TOP_NODE) != null) {
                c = (Component) c.getClientProperty(KEY_TOP_NODE);
            };
            Object e = c.getClientProperty(KEY_EXPANDED);
            if (e != null && e.equals("true")) {
                collapseNode(c, evt.isLongEvent());
            } else {
                expandNode(isInitialized(), c, evt.isLongEvent());
            }
        }
    }

    /**
     * traverses up the hierarchy of parent containers until it finds a
     * container of class MyTree2 or until the parent is null
     *
     * @param cont
     * @return found MyTree2 container or null if no parent of type MyTree2 is
     * found
     */
    static public MyTree2 getMyTreeTopLevelContainer(Container cont) {
        while (cont != null && !(cont instanceof MyTree2) && (cont = cont.getParent()) != null);
        return cont instanceof MyTree2 ? (MyTree2) cont : null;
    }

    protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList, Category category) {
//        assert false;
//        return createNode(node, depth, category);
//        return null;
        return createNode(node, depth); //NB! The 'circular' calls between the different createNode is because one will be overridden when instantiating the Tree
    }

    protected Component createNode(Object node, int depth, Category category) {
//        assert false;
//        return null;
        return createNode(node, depth, null, null); //NB! The 'circular' calls between the different createNode is because one will be overridden when instantiating the Tree
    }

    protected Component createNode(Object node, int depth) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        assert false;
//        Container cmp = null;
//        if (node instanceof Item) {
////                cmp = ItemContainer.buildItemContainer((Item) node, (ItemList) treeParent);
////                cmp = new MyTree((Item) node, (ItemList) treeParent);
//            cmp = ScreenListOfItems.buildItemListContainer((ItemList) node);
//        } else if (node instanceof ItemList) {
//            cmp = buildItemListContainer((ItemList) node, (ItemList) treeParent);
//        } else {
//            assert false : "treeParent should only be Item or ItemList: treeParent=" + treeParent;
//        }
//
////                cmp.setUIID("TreeNode"); cmp.setTextUIID("TreeNode"); if(model.isLeaf(node)) {cmp.setIcon(nodeImage);} else {cmp.setIcon(folder);}
//        cmp.getSelectedStyle().setMargin(LEFT, depth * depthIndent);
//        cmp.getUnselectedStyle().setMargin(LEFT, depth * depthIndent);
//        cmp.getPressedStyle().setMargin(LEFT, depth * depthIndent);
//        cmp.setScrollable(false); //to avoid nested scrolling, http://stackoverflow.com/questions/36044418/how-to-extend-infinitecontainer-with-the-capability-of-expanding-the-nodes-in-th
//        return cmp;
//        return null;
//</editor-fold>
        return createNode(node, depth, null); //NB! The 'circular' calls between the different createNode is because one will be overridden when instantiating the Tree
    }

    protected void bindNodeListener(ActionListener l, Component node) {
        Object expandCollapseButton = node.getClientProperty(KEY_ACTION_ORIGIN);
        if (expandCollapseButton != null && expandCollapseButton instanceof Button) {
            ((Button) (expandCollapseButton)).addActionListener(l); //in a tree of ItemLists there shall always be a subTasksButton
            ((Button) (expandCollapseButton)).putClientProperty(KEY_TOP_NODE, node);
        }
//        else {
//            Log.p("no subTasksButton defined for " + node); //happens eg for empty lists
//        }
    }

    static void setIndent(Component cmp, int depth) {
        if (false) { //don't indent but use frame instead
            cmp.getSelectedStyle().setMargin(LEFT, depth * DEPTH_INDENT);
            cmp.getUnselectedStyle().setMargin(LEFT, depth * DEPTH_INDENT);
            cmp.getPressedStyle().setMargin(LEFT, depth * DEPTH_INDENT);
        }
        if (cmp instanceof Container) {
            ((Container) cmp).setScrollable(false); //to avoid nested scrolling, http://stackoverflow.com/questions/36044418/how-to-extend-infinitecontainer-with-the-capability-of-expanding-the-nodes-in-th
        }
    }

    private String type = "";

    /**
     * Usually same field as what is being sorted on. Eg. Item.PARSE_PRIORITY
     *
     * @param itemParseFieldId
     */
    void setStickyHeaderField(String itemParseFieldId) {
        type = itemParseFieldId;
    }

    interface StickyHeaderGenerator {

        Component getComp(Object item);
    }

    void setStickyHeaderGenerator(StickyHeaderGenerator stickyHeaderGen) {
        this.stickyHeaderGen = stickyHeaderGen;
    }

    StickyHeaderGenerator getStickyHeaderGenerator() {
        return stickyHeaderGen;
    }

    static String newTimeString(long timeInMillis) {
        String str;
        if (timeInMillis == 0) {
            str = "0m";
        } else if (timeInMillis < MyDate.MINUTE_IN_MILLISECONDS * 5l) {
            str = "1-5m";
        } else if (timeInMillis < MyDate.MINUTE_IN_MILLISECONDS * 15l) {
            str = "5-15m";
        } else if (timeInMillis < MyDate.MINUTE_IN_MILLISECONDS * 30l) {
            str = "15-30m";
        } else if (timeInMillis < MyDate.MINUTE_IN_MILLISECONDS * 60l) {
            str = "30-60m";
        } else if (timeInMillis < MyDate.MINUTE_IN_MILLISECONDS * 120l) {
            str = "1-2h";
        } else if (timeInMillis < MyDate.MINUTE_IN_MILLISECONDS * 240l) {
            str = "2-4h";
        } else if (timeInMillis < MyDate.MINUTE_IN_MILLISECONDS * 480l) {
            str = "4-8h";
        } else {
            str = ">8h";
        }
        return str;
    }

    /**
     * returns newStr if different from oldStr
     *
     * @param oldStr
     * @param newStr
     * @return
     */
    static String getDiffStr(String oldStr, String newStr) {
        if (oldStr == null || (newStr != null && !newStr.equals(oldStr))) {
            return newStr;
        } else {
            return oldStr;
        }
    }

    String newStickyStr = null;
    private int todayViewState = 0;

    interface StringGet {

        String get();
    }

    interface StringPut {

        void put(String str);
    }

    private static String makeHeader(String fieldName, String fieldStr, Object fieldValue, Object undefinedValue) {
        if (fieldValue == null || fieldValue.equals(undefinedValue)) {
            return MyPrefs.listDefaultHeaderForUndefinedValue.getString();
        }
        StringBuilder s = new StringBuilder(fieldName);
        s.append(" ").append(fieldStr);
        return s.toString();
    }

    private static String makeHeader(String fieldName, Object fieldValue, Object undefinedValue) {
        return makeHeader(fieldName, fieldValue.toString(), fieldValue, undefinedValue);
    }

    private static String makeHeader(String fieldName, Object fieldValue) {
        return makeHeader(fieldName, fieldValue.toString(), fieldValue, null);
    }

    /**
     * will calculate if a (new) StickyHeader is needed and if so, return it for
     * insertion into the Container. otherwise returns null.
     */
    private StickyHeaderGenerator makeStickyHeaderGen(String parseId) {
        return makeStickyHeaderGen(parseId, () -> newStickyStr, (s) -> newStickyStr = s);
    }

    static StickyHeaderGenerator makeStickyHeaderGen(String parseId, StringGet stringGen, StringPut stringPut) {
        //TODO support grouping dates by eg week or month: simply replace formatDateNew with a call that returns a string indicating week or month
        return (current) -> {
            final String previousStickyStr = stringGen.get();
            String newStr = null;
            if (current instanceof Item) {
                Item item = (Item) current;

                switch (parseId) {
                    case Item.PARSE_PRIORITY:
//                        newStr = getDiffStr(newStickyStr, "Priority " + item.getPriority());
//                        newStr = getDiffStr(previousStickyStr, Item.PRIORITY + " " + item.getPriority());
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.PRIORITY, item.getPriority(), 0));
                        break;
                    case Item.PARSE_STATUS:
//                        newStr = getDiffStr(newStickyStr, "Status " + item.getStatus().getName());
//                        newStr = getDiffStr(previousStickyStr, Item.STATUS + " " + item.getStatus().getName());
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.STATUS, item.getStatus().getName())); //no undefined value exists for ItemStatus
                        break;
                    case Item.PARSE_REMAINING_EFFORT_TOTAL:
//                        newStr = getDiffStr(previousStickyStr, Item.EFFORT_REMAINING + " " + newTimeString(item.getRemaining()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.EFFORT_REMAINING, newTimeString(item.getRemainingTotal()), item.getRemainingTotal(), 0));
                        break;
                    case Item.PARSE_ACTUAL_EFFORT:
//                        newStr = getDiffStr(previousStickyStr, Item.EFFORT_ACTUAL + " " + newTimeString(item.getActual()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.EFFORT_ACTUAL, newTimeString(item.getActualTotal()), item.getActualTotal(), 0));
                        break;
                    case Item.PARSE_EFFORT_ESTIMATE:
//                        newStr = getDiffStr(previousStickyStr, Item.EFFORT_ESTIMATE + " " + newTimeString(item.getEstimate()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.EFFORT_ESTIMATE, newTimeString(item.getEstimateTotal()), item.getEstimateTotal(), 0));
                        break;
                    case Item.PARSE_UPDATED_AT:
//                        newStr = getDiffStr(previousStickyStr, Item.UPDATED_DATE + " " + MyDate.formatDateNew(item.getUpdatedAt()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.UPDATED_DATE, MyDate.formatDateNew(item.getUpdatedAt()), item.getUpdatedAt(), new MyDate(0)));
                        break;
                    case Item.PARSE_COMPLETED_DATE:
//                        newStr = getDiffStr(previousStickyStr, Item.COMPLETED_DATE + " " + MyDate.formatDateNew(item.getCompletedDateD()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.COMPLETED_DATE, MyDate.formatDateNew(item.getCompletedDate()), item.getCompletedDate(), new MyDate(0)));
                        break;
                    case Item.PARSE_CREATED_AT:
//                        newStr = getDiffStr(previousStickyStr, Item.CREATED_DATE + " " + MyDate.formatDateNew(item.getCreatedAt()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.CREATED_DATE, MyDate.formatDateNew(item.getCreatedAt()), item.getCreatedAt(), new MyDate(0)));
                        break;
                    case Item.PARSE_STARTED_ON_DATE:
//                        newStr = getDiffStr(previousStickyStr, Item.STARTED_ON_DATE + " " + MyDate.formatDateNew(item.getStartedOnDate()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.STARTED_ON_DATE, MyDate.formatDateNew(item.getStartedOnDate()), item.getStartedOnDate(), new MyDate(0)));
                        break;
                    case Item.PARSE_START_BY_DATE:
//                        newStr = getDiffStr(previousStickyStr, Item.START_BY_TIME + " " + MyDate.formatDateNew(item.getStartByDateD()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.START_BY_TIME, MyDate.formatDateNew(item.getStartByDateD()), item.getStartByDateD(), new MyDate(0)));
                        break;
                    case Item.PARSE_DUE_DATE:
//                        newStr = getDiffStr(previousStickyStr, Item.DUE_DATE + " " + MyDate.formatDateNew(item.getDueDateD()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.DUE_DATE, MyDate.formatDateNew(item.getDueDate()), item.getDueDate(), new MyDate(0)));
                        break;
                    case Item.PARSE_WAITING_TILL_DATE:
//                        newStr = getDiffStr(previousStickyStr, Item.WAIT_UNTIL_DATE + " " + MyDate.formatDateNew(item.getWaitingTillDateD()));
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.WAIT_UNTIL_DATE, MyDate.formatDateNew(item.getWaitingTillDate()), item.getWaitingTillDate(), new MyDate(0)));
                        break;
                    case Item.PARSE_IMPORTANCE_URGENCY:
//                        newStr = getDiffStr(previousStickyStr, Item.IMPORTANCE_URGENCY + " " + item.getImpUrgPrioValueAsString());
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.IMPORTANCE_URGENCY, item.getImpUrgPrioValue(), 0));
                        break;
                    case Item.PARSE_IMPORTANCE:
//                        newStr = getDiffStr(previousStickyStr, Item.IMPORTANCE_URGENCY + " " + item.getImpUrgPrioValueAsString());
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.IMPORTANCE, item.getImportanceN(), null));
                        break;
                    case Item.PARSE_URGENCY:
//                        newStr = getDiffStr(previousStickyStr, Item.IMPORTANCE_URGENCY + " " + item.getImpUrgPrioValueAsString());
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.URGENCY, item.getUrgencyN(), null));
                        break;
                    case Item.PARSE_CHALLENGE:
//                        newStr = getDiffStr(previousStickyStr, Item.CHALLENGE + " " + item.getChallengeN().toString());
                        newStr = getDiffStr(previousStickyStr, makeHeader(Item.CHALLENGE, item.getChallengeN(), null));
                        break;
                    case Item.PARSE_TEXT: //no header for text, could do a letter 'A' but not valuable //TODO - add the right-side menu with letters to jump directly to tasks starting with that letter
//                        newStr = getDiffStr(newStickyStr, Item.CHALLENGE + " " + item.getChallengeN().toString());
                        break;
                    case FilterSortDef.FILTER_SORT_TODAY_VIEW:
                        //show headers "Due today", "Waiting until today", "Start today", "WorkSlots?!"
                        //if due date is today, show "Due today", otherwise if WatingTillDate==today, show "Waiting", otherwise if StartDate==today show "Start.."
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if ()
//                        String str;
//                        if (todayViewState == 0) {
//                            str = Item.DUE_DATE + " " + MyDate.formatDateNew(item.getDueDateD());
//                            if (newStr != null && !str.equals(newStr)) {
//                                todayViewState = 1;
//                            }// else newStr=str;
//                        } else if (todayViewState == 1) {
//                            str = Item.WAIT_DATE + " " + MyDate.formatDateNew(item.getWaitingTillDateD());
//                            if (!str.equals(newStr)) {
//                                todayViewState = 2;
//                            }
//                        } else {
//                            str = Item.START_BY_TIME + " " + MyDate.formatDateNew(item.getStartByDateD());
//                        }
//                        newStr = str;
//</editor-fold>
                        Item.TodaySortOrder sortBy = item.getTodaySortOrder();
                        switch (sortBy) {
                            case DUE_TODAY_CREATED:
                            case DUE_TODAY_ONGOING:
                            case DUE_TODAY_WAITING:
                                newStr = Item.DUE_DATE + " " + MyDate.formatDateNew(item.getDueDate());
                                break;
                            case WAITING_TODAY:
                                newStr = Item.WAIT_UNTIL_DATE + " " + MyDate.formatDateNew(item.getWaitingTillDate());
                                break;
                            case STARTING_TODAY_CREATED:
                            case STARTING_TODAY_ONGOING:
                            case STARTING_TODAY_WAITING:
                                newStr = Item.START_BY_TIME + " " + MyDate.formatDateNew(item.getStartByDateD());
                                break;
                        }
                        break;
                    default:
                        ASSERT.that(false, "Unhandled parseId in StickyHeader = " + parseId);
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (newStr != null && !newStr.equals(previousStickyStr)) {
////                    newStickyStr = newStr;
//                    stringPut.put(newStr); //store to compare next time
////                     Label headerLbl = new Label(newStickyStr, "ToggleButton"); //TODO!! define separate style for stickyheaders
////                    Label headerLbl = new Label(newStickyStr, "ListOfItemsSectionHeader"); //TODO!! define separate style for stickyheaders
////                    StickyHeaderDiamond headerLbl = new StickyHeaderDiamond("ToggleButton"); //TODO!! define separate style for stickyheaders //overwrites titlebar
////                    StickyHeaderMod headerLbl = new StickyHeaderMod("ToggleButton"); //TODO!! define separate style for stickyheaders
////                    StickyHeader headerLbl = new StickyHeader("ToggleButton"); //TODO!! define separate style for stickyheaders
//                    StickyHeader headerLbl = new StickyHeader("StickyHeader"); //TODO!! define separate style for stickyheaders
////                    headerLbl.add(newStickyStr);
//                    headerLbl.add(newStr);
////                    if (false) headerLbl.putClientProperty("STICKY_HEADER", true);
//                    return headerLbl;
//                }
//</editor-fold>
            } else if (current instanceof WorkSlot) {
                WorkSlot workSlot = (WorkSlot) current;
                switch (parseId) {
                    case WorkSlot.PARSE_START_TIME:
                        newStr = getDiffStr(previousStickyStr, WorkSlot.START_TIME + " " + MyDate.formatDateNew(workSlot.getStartAdjusted(MyDate.currentTimeMillis()))); //UI: use adjusted, so a workslot stretching over midnight will get the right date after midnignt
                        break;
                }
            }
            if (newStr != null && !newStr.equals(previousStickyStr)) {
                stringPut.put(newStr); //store to compare next time
//                StickyHeader stickyHeader = new StickyHeader("StickyHeader", "StickyHeaderIcon"); //TODO!! define separate style for stickyheaders
                StickyHeader stickyHeader = new StickyHeader("StickyHeader"); //TODO!! define separate style for stickyheaders
//                Label stickyHeaderLabel = new Label(newStr, "StickyHeaderLabel");
//                stickyHeader.add(stickyHeaderLabel);
                stickyHeader.setText(newStr);
                return stickyHeader;
            }

            return null;
        };
    }

}

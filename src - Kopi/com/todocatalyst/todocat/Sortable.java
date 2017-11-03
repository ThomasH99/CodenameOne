/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.todocatalyst.todocat;

/**
 *
 * @author Thomas
 */
public interface Sortable {

        /**
     * Keep list sorted.
         * @param sortDef
     */
//    public void setSorting(boolean maintainSorted, int itemSortField, boolean sortAscending) {
//    public void setSortDefinition(SortDefinition sortDef);

//    public SortDefinition getSortDefinition();

    public boolean isSorted();

    public void setSorted(boolean on);


}

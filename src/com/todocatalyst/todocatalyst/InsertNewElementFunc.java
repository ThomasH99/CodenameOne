/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Component;

/**
 *
 * @author THJ
 */
public interface InsertNewElementFunc {

    /**
     * checks on element and if it corresponds to the previous element created
     * by the Insert function, returns a new container to enter another element
     * inline
     *
     * @param element the element which determines if a new insertContainer
     * should be inserted *after* the container for element
     * @param targetList list into which a new element will be inserted
     * @return
     */
    Component make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList);
}

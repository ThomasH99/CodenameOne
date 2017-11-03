/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.Layout;

/**
 *
 * @author Thomas
 */
public class ContainerScrollY extends Container {
    
    
    public ContainerScrollY() {
        super();
    }
    public ContainerScrollY(Layout l) {
        super(l);
    }
    
    public void setScrollYPublic(int scrollY) {
        setScrollY(scrollY);
    }
    
}

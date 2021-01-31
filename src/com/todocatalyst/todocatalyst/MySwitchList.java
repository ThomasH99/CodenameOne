/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;
//package com.codename1.components;

import com.codename1.components.OnOffSwitch;
import com.codename1.components.SwitchList;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentSelector;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.MultipleSelectionListModel;

/**
 *
 * @author thomashjelm
 */
public class MySwitchList extends SwitchList {

    public MySwitchList(MultipleSelectionListModel model) {
        super(model);

        fireReady();

    }

    /*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
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
 * Please contact Codename One through http://www.codenameone.com/ if you 
 * need additional information or have any questions.
     */
    @Override
    protected Component createButton(Object model) {
        MyOnOffSwitch sw = new MyOnOffSwitch();
        $(sw).addTags("switch");
        return BorderLayout.center(new Label(String.valueOf(model))).add(BorderLayout.EAST, sw);

    }

}

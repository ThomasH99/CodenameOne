/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.ui.Form;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.tree.Tree;
import com.codename1.ui.tree.TreeModel;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

/**
 * https://www.codenameone.com/manual/files-storage-networking.html
 *
 * @author Thomas
 */
public class ScreenFileSystemTree extends MyForm {

    ScreenFileSystemTree() {
        super("FileSystemTree", getCurrentFormAfterClosingDialogOrMenu(), () -> {
        });
//        setLayout(new BorderLayout());
        TreeModel tm = new TreeModel() {
            @Override
            public Vector getChildren(Object parent) {
                String[] files;
                if (parent == null) {
                    files = FileSystemStorage.getInstance().getRoots();
                    return new Vector<Object>(Arrays.asList(files));
                } else {
                    try {
                        files = FileSystemStorage.getInstance().listFiles((String) parent);
                    } catch (IOException err) {
                        Log.e(err);
                        files = new String[0];
                    }
                }
                String p = (String) parent;
                Vector result = new Vector();
                for (String s : files) {
                    result.add(p + s);
                }
                return result;
            }

            @Override
            public boolean isLeaf(Object node) {
                return !FileSystemStorage.getInstance().isDirectory((String) node);
            }
        };

        Tree t = new Tree(tm) {
            @Override
            protected String childToDisplayLabel(Object child) {
                String n = (String) child;
                int pos = n.lastIndexOf("/");
                if (pos < 0) {
                    return n;
                }
                return n.substring(pos);
            }
        };

        add(BorderLayout.CENTER, t);
        addCommandsToToolbar();
    }

//    hi.add (BorderLayout.CENTER, t);
//
//    hi.show ();
    @Override
    public void refreshAfterEdit() {
        super.refreshAfterEdit();
    }
    
        public void addCommandsToToolbar() {
        Toolbar toolbar = getToolbar();
        //DONE/BACK
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
        addStandardBackCommand();
//        toolbar.addCommandToOverflowMenu(makeCancelCommand());
//        toolbar.addCommandToOverflowMenu(new Command("Reset to default")); //reset to default values
    }


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.io.Log;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Thomas
 */
public class DAO {

    protected static DAO INSTANCE;

    public static DAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DAO();
        }
        return INSTANCE;
    }

    int getItemCount(boolean onlyDone) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
        int count = 0;
        if (onlyDone) {
            query.whereEqualTo(Item.PARSE_STATUS, Item.ItemStatus.STATUS_DONE.toString());
            try {
                count = query.count();
            } catch (ParseException ex) {
//                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
                Log.e(ex);
            }
        } else {
            try {
                count = query.count();
            } catch (ParseException ex) {
                Log.e(ex);
            }
        }
        return count;
    }

    List getAll(String parseClassName) {
        ParseQuery query2 = ParseQuery.getQuery(parseClassName);
        java.util.List results = null;
        try {
            results = query2.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    List<Category> getCategories() {
//                    ParseQuery<Item> query2 = ParseQuery.getQuery(Category.CLASS_NAME);
//                java.util.List<Item> results = null;
//                try {
//                    results = query2.find();
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//                return results;
        return (List<Category>) getAll(Category.CLASS_NAME);
    }

    List<Item> getItems() {
//                    ParseQuery<Item> query2 = ParseQuery.getQuery(Item.CLASS_NAME);
//                java.util.List<Item> results = null;
//                try {
//                    results = query2.find();
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//                return results;
        return (List<Item>) getAll(Item.CLASS_NAME);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;
import java.util.List;

/**
 *
 * @author thomashjelm
 */
    class TemplatePicker extends Picker {

//        List<Item> templateList = DAO.getInstance().getTemplateList();
        List<Item> templateList = TemplateList.getInstance();
        String[] stringArray = new String[templateList.size()];

        TemplatePicker() {
            for (int i = 0, size = templateList.size(); i < size; i++) {
                stringArray[i] = templateList.get(i).getText();
            }
            setType(Display.PICKER_TYPE_STRINGS);
            setStrings(stringArray);
        }

        Item getTemplate() {
            String s = this.getSelectedString();
            if (s != null) {
                for (int i = 0, size = templateList.size(); i < size; i++) {
                    if (s.equals(stringArray[i])) {
                        return templateList.get(i);
                    }
                }
            }
            return null;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static Item pickTemplate() {
//                            new ScreenObjectPicker(ScreenMain.SCREEN_TEMPLATE_PICKER, DAO.getInstance().getTemplateList(), selectedTemplates, ScreenItem.this, () -> {
//                        if (selectedTemplates.size() >= 1) {
//                            Item template = (Item) selectedTemplates.get(0);
//                            Dialog ip = new InfiniteProgress().showInifiniteBlocking();
//                            template.copyMeInto(item, Item.CopyMode.COPY_FROM_TEMPLATE);
//                            locallyEditedCategories = null; //HACK needed to force update of locallyEditedCategories (which shouldn't be refreshed when eg editing subtasks to avoid losing the edited categories)
//                            ip.dispose();
//                            refreshAfterEdit();
//                        } else {
//                            Dialog.show("INFO", "No templates yet. \n\nGo to "+ScreenMain.SCREEN_TEMPLATES_TITLE+" to create templates or save existing tasks or projects as templates", "OK", null);
//                        }
//                    }, (obj) -> {
//                        return ((Item) obj).getText();
//                    }, 1, true).show();
//
//    }
//</editor-fold>
//    static Item pickTemplateOLD() {
//        List<Item> templateList = DAO.getInstance().getTemplateList();
//        Picker templatePicker = new Picker();
////                templatePicker.s;
//        String[] stringArray = new String[templateList.size()];
//        for (int i = 0, size = templateList.size(); i < size; i++) {
//            stringArray[i] = templateList.get(i).getText();
//        }
//        templatePicker.setType(Display.PICKER_TYPE_STRINGS);
//        templatePicker.setStrings(stringArray);
//        templatePicker.pressed();
//        templatePicker.released(); //simulate pressing the key to make the Picker pop up without a physical key
//        String s = templatePicker.getSelectedString();
//        Item selectedTemplate = null;
//        if (s != null) {
//            for (int i = 0, size = templateList.size(); i < size; i++) {
//                if (s.equals(stringArray[i])) {
//                    selectedTemplate = templateList.get(i);
//                    break;
//                }
//            }
//        }
//        return selectedTemplate;
//    }



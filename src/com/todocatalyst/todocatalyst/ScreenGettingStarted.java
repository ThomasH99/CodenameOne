/*
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextField;
import com.codename1.ui.animations.Motion;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.DefaultLookAndFeel;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
//import com.codename1.admob;
//import com.codename1.nui.*;
//import com.codename1.nui.NTextField;

/**
 * The Getting Started form
 *
 * @author Thomas Hjelm
 */
//public class ScreenLogin extends BaseForm {
public class ScreenGettingStarted extends MyForm {
    //https://uxplanet.org/designing-ux-login-form-and-process-8b17167ed5b9
    //Sign Up Free
    //Log In

    /*
    final static String INTRO_TEXT = "Welcome to TodoCatalyst. A todo list like no other."
            + " Manage your projects, manage your time, manage your priorities. Be efficient."
            + " Achieve personal efficiency. "
            + " Be efficient"
            + " Be efficient"
            + " Most features are free. "
            + " Unique powerful features available for Pro and Advanced subscribers. "
            + " Learn more here. "
            + " Learn more here. ";
    
     */
//    private final static String welcome1 = "Most things in TodoCatalyst are pretty straightforward. But here are a few hidden gems that you may appreciate"; "you may find useful"
//    private final static String welcome1 = "Most things in TodoCatalyst are pretty easy. But here are a few tips that are useful to know before you start";
//    private final static String dragnAndDrop = "You can drag and drop element around to reorder manually sorted lists, simply long-press the task or list title";
//    private final static String swipe = "In most lists you can swipe an element to access useful functions";
//    private final static String swipeDates = "To delete a date or duration, left-swipe and press the (x)";
//    private final static String pinchInsert = "You can easily insert new tasks (or lists, categories, ...) directly where you want with pinch-insert (squeeze out with two fingers)";
//    private final static String help = "There is help available almost everywhere. Click the text of task fields, screen titles or settings to reveal/hide it. Long-press";
//    private final static String longPress = "Many elements have additional functionality when you long-press";
//    private final static String longPressMainMenu = "In the home screen, long-press one of the Plan/Do/Check sections to expand that and collapse the other sections";
//    private final static String longPressTaskStatus = "Long-press the task status to set a task Waiting or Cancel it";
//    private final static String longPressStickyHeaders = "Ir sorted lists, long-press a section header to collapse/expand ALL sections at once";
//    private final static String longPressToExpandHiearchy = "Long-press to expand the full hierarchy of a project";
//    private final static String tasksDetailLevel = "Tap a task's text to select how much detail to show";
//    private final static String tapStatusBar = "In addition to the usual tap-statusbar-to-scroll-up, you can long-press to scroll a list to the end or double-tap to switch between the two last positions";
//    private final static String richSettings = "Out of the box, TodoCatalyst behaves like you'd expect, but there are settings available to adapt it your liking. Screen settings are available both directly from each screen and from global settings";
//    private final static String[] titles = new String[]{
//        "Tips",
//        "Drag and drop",
//        "Long-press task status",
//        "Swipe commands",
//        "Swipe time fields",
//        "Pinch insert tasks",
//        "Help everywhere",
//        "Long-press",
//        "Long-press",
//        "Long-press to collapse all list headers",
//        "Long-press to expand all project subtasks",
//        "Tab task to see details",
//        "Tab statusbar to navigate lists",
//        "Customize to suit your needs"
//    };
    private final static String[] full = new String[]{
        "Tips", "Most things in TodoCatalyst are pretty easy. But here are a few tips that are useful to know before you start",
        "Drag and drop", "You can drag and drop element around to reorder manually sorted lists, simply long-press the task or list title",
        "Long-press task status", "Long-press the task status to set a task Waiting or Cancel it",
        "Swipe commands", "In most lists you can swipe an element to access useful functions",
        "Swipe time fields", "To delete a date or duration, left-swipe and press the (x)",
        "Pinch insert tasks", "You can easily insert new tasks (or lists, categories, ...) directly where you want with pinch-insert (squeeze out with two fingers)",
        "Help everywhere", "There is help available almost everywhere. Click the text of task fields, screen titles or settings to reveal/hide it. Long-press",
        "Long-press", "Many elements have additional functionality when you long-press, swipe on...",
        //        "Long-press", "In the home screen, long-press one of the Plan/Do/Check sections to expand that and collapse the other sections",
        "Long-press to collapse all list headers", "In sorted lists, long-press a section header to collapse/expand ALL sections at once",
        "Long-press to expand all project subtasks", "Long-press to expand the full hierarchy of a project",
        "Tab task to see details", "Tap a task's text to select how much detail to show",
        "Tab statusbar to navigate lists", "In addition to the usual tap-statusbar-to-scroll-up, you can long-press to scroll a list to the end or double-tap to switch between the two last positions",
        "Customize to suit your needs", "Out of the box, TodoCatalyst behaves like you'd expect, but there are settings available to adapt it your liking. Screen settings are available both directly from each screen and from global settings"
    };
    TextField email;

    TextField password;
    private boolean test;

    private void makeTab(Tabs tabs, String header, String titleText, String text, char icon, Font font) {
        makeTab(tabs, header, titleText, text, icon, font, -1);
    }

    private void makeTab(Tabs tabs, String header, String titleText, String text, char icon, Font font, int color) {
        if (false) {
            Label iconLabel = new Label("", "WelcomeIcon");
            iconLabel.setIconUIID("WelcomeIcon");
//        if (color != -1) { 
//            iconLabel.getAllStyles().setBgColor(color);
//            iconLabel.getIconStyleComponent().getAllStyles().setBgColor(color);
//        }
//        iconLabel.setGap(0); //ensure icons is centered!
            //NB!!! must set below colors *before* setting icons, otherwise won't get right colors
            setIconLabelColor(iconLabel, color);
            if (font == null) {
                iconLabel.setMaterialIcon(icon);
            } else {
                iconLabel.setFontIcon(font, icon);
            }
        }

        SpanLabel headerLabel = new SpanLabel(header, "WelcomeHeader");
        if (color != -1) {
            headerLabel.getAllStyles().setFgColor(color);
        }
        SpanLabel titleLabel = new SpanLabel(titleText, "WelcomeTitle");
        SpanLabel textLabel = new SpanLabel(text, "WelcomeText");
//        textLabel.setAutoSizeMode(true); //adapt to small screens

//        Container page = BoxLayout.encloseY(iconLabel, headerLabel, titleLabel, textLabel);
        BorderLayout borderLayout = new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER);
        borderLayout.defineLandscapeSwap(BorderLayout.NORTH, BorderLayout.WEST);
//        Container page = BorderLayout.centerCenter(titleLabel);

        Container page = new Container(borderLayout);
//        page.addComponent(BorderLayout.NORTH, BoxLayout.encloseY(iconLabel, headerLabel));
//        page.addComponent(BorderLayout.NORTH, iconLabel);
//        page.addComponent(BorderLayout.SOUTH, textLabel);
        page.addComponent(BorderLayout.CENTER, BoxLayout.encloseY(headerLabel, textLabel));
        page.setUIID("WelcomeContainer");
//        page.getAllStyles().getBorder().set //TODO: how to set color of border programatically??!!

        tabs.addTab("", page);
//        return page;
    }

    public ScreenGettingStarted(MyForm previousForm, boolean showBackButton) {
        super("Getting started tips", previousForm, () -> {
        });
        test = showBackButton;
//        getTitleArea().setUIID("Container");
//        setTitle("TodoCatalyst");
        getContentPane().setScrollVisible(false);
        getContentPane().setAlwaysTensile(false); //only scroll if needed(???)
        setUIID("GettingStartedScreen");

        //hide titlebar: http://stackoverflow.com/questions/42871223/how-do-i-hide-get-rid-the-title-bar-on-a-form-codename-one
        getToolbar().setUIID("Container");

        Container cont = getContentPane();

        if (test) {
            addStandardBackCommand();
        }
        if (!test || Config.TEST) {
            //hide titlebar: http://stackoverflow.com/questions/42871223/how-do-i-hide-get-rid-the-title-bar-on-a-form-codename-one
            Button getStarted = new Button(CommandTracked.create("Go to main screen", Icons.iconGetStarted, (e) -> {
                new ScreenMain(null).show();
            }, "GetStarted"));
            getStarted.setUIID("BigButton");
            getStarted.setTextPosition(Button.LEFT);
            cont.add(BorderLayout.SOUTH, BoxLayout.encloseY(getStarted, ScreenSettingsCommon.makeHelpText("You see this screen again via Settings -> Help -> Getting Started")));
        }

//        cont.add(BorderLayout.SOUTH, getStarted);
//        super.addSideMenu(res);
        Tabs tabs = new Tabs() {
//            @Override
//            protected Motion createTabSlideMotion(int start, int end) {
////        return Motion.createSplineMotion(start, end, getUIManager().getThemeConstant("tabsSlideSpeedInt", 200));
//                return Motion.createEaseInMotion(start, end, 200);
//            }
        };
        tabs.setTabPlacement(Component.RIGHT);
        tabs.setUIID("Container");
        tabs.getContentPane().setUIID("Container");
        tabs.hideTabs();
        tabs.setEagerSwipeMode(true);
        tabs.setTabsContentGap(50);
        tabs.setAnimateTabSelection(true);
        tabs.setTensileLength(0);
//        cont.add(BorderLayout.CENTER_BEHAVIOR_SCALE, tabs); //shows in upper part of screen
//        cont.add(BorderLayout.CENTER, tabs);

//        makeTab(tabs, "TodoCatalyst", "The Todo app that doesn't just pile but helps you focus",
//                "Block time slots and see when your tasks will finish. Reorganize. Make realistic commitments. Preditable, professional, stressfree.",
//                Icons.iconActualCurrentCust, Icons.myIconFont);
//        for (int i = 0, size = all.length; i < size; i++) {
        for (int i = 0, size = full.length; i < size - 1; i += 2) {
            makeTab(tabs,
                    full[i], "",
                    full[i + 1], 'x', Icons.myIconFont, colorClearBlue);
        }

        setSameSize(tabs.getChildrenAsList(true).toArray(new Component[]{}));

        Style s = UIManager.getInstance().getComponentStyle("Button");
        FontImage radioEmptyImage = FontImage.createMaterial(FontImage.MATERIAL_RADIO_BUTTON_UNCHECKED, s);
        FontImage radioFullImage = FontImage.createMaterial(FontImage.MATERIAL_RADIO_BUTTON_CHECKED, s);
        ((DefaultLookAndFeel) UIManager.getInstance().getLookAndFeel()).setRadioButtonImages(radioFullImage, radioEmptyImage, radioFullImage, radioEmptyImage);

        ButtonGroup buttons = new ButtonGroup();
        FlowLayout layout = new FlowLayout();
        layout.setAlign(Component.CENTER);
        Container tabsFlow = new Container(layout);
        for (int i = 0, size = tabs.getTabCount(); i < size; i++) {
            RadioButton radio = new RadioButton("");
            radio.setUIID("WelcomeButton");
            radio.setGap(0);
//                radio.addChangeListener((e) -> {
            radio.addActionListener((e) -> {
                int sel = buttons.getSelectedIndex();
                if (sel >= 0 && sel < buttons.getButtonCount()) { //necessary for initialization case
                    tabs.setSelectedIndex(sel, true);
                }
            });
            buttons.add(radio);
            tabsFlow.add(radio);
            if (i == 0) {
                radio.setSelected(true);
            }
        }

        tabs.addSelectionListener((i1, i2) -> {
//                buttons.getRadioButton(i1).setSelected(false);
            buttons.getRadioButton(i2).setSelected(true);
        });
//                        cont.add(BorderLayout.CENTER, BoxLayout.encloseY(tabs, radioContainer));
//        cont.add(BorderLayout.CENTER, BoxLayout.encloseY(tabs, tabsFlow));
        cont.add(BorderLayout.CENTER, BorderLayout.centerAbsolute(BoxLayout.encloseY(tabs, tabsFlow)));

        revalidate(); //ensure correct size of all components

    }

//    @Override
    public void refreshAfterEditXXX() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        super.refreshAfterEdit();
    }

}

/*
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.messaging.Message;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.animations.Motion;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.util.EasyThread;
import com.parse4cn1.ParseACL;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import com.parse4cn1.ParseUser;
import static com.todocatalyst.todocatalyst.MyUtil.cleanEmail;
//import com.codename1.admob;
//import com.codename1.nui.*;
//import com.codename1.nui.NTextField;

/**
 * The welcome form
 *
 * @author Thomas Hjelm
 */
//public class ScreenLogin extends BaseForm {
public class ScreenWelcome extends MyForm {
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
    private final static String welcome1 = "TodoCatalyst - a todo list like no other";
    private final static String welcome11
            = "TodoCatalyst - probably the best Todo app in the world. \nMost ToDo lists remind you have a lot of work. "
            + "TodoCatalyst lets you know when you'll be done";
    private final static String welcome12 = "TodoCatalyst\n\nProbably the best Todo app in the world.\n\n"
            + "Most ToDo lists remind you have too much to do. TodoCatalyst lets you know when you can be done. \n\n"
            + "Swipe to see more"; // Unparalleded features
    private final static String welcome2 = "For people with more work than time, self-managed, wanting to highly professional/reliable/predictable.";
    private final static String welcome21 = "For demanding users who need the features no other todo apps offer. See how new tasks impacts your deadlines or commitments. Be efficient.";
    private final static String welcome3 = "Master priorities. Master time. \nTime-saving features like templates, copy-paste, multiple selections, ...";
    private final static String welcome4 = "Time-saving features like templates, copy-paste, multiple selections, ...";
    TextField email;

    TextField password;
    private boolean test;
    //https://www.schemecolor.com/red-orange-yellow-blue.php
    private int colorLightGreen = 0x89e19c; //??
    private int colorDarkGreen = 0x89e19c; //https://www.colorcombos.com/color-schemes/380/ColorCombo380.html
    private int colorClearBlue = 0x0088dd; //https://www.schemecolor.com/red-orange-yellow-blue.php
    private int colorYellow = 0xffd301; //https://www.schemecolor.com/red-orange-yellow-blue.php
    private int colorRoyalBlue = 0x2a52bd; //https://www.schemecolor.com/red-orange-yellow-blue.php
    private int colorDeepOrange = 0xc23b21; //https://www.schemecolor.com/red-orange-yellow-blue.php
    private int colorOrange = 0xff8b01; //https://www.schemecolor.com/red-orange-yellow-blue.php
    private int colorRed = 0xa40001; //https://www.schemecolor.com/red-orange-yellow-blue.php
    private int colorDarkGrape = 0x4a2748; //https://stitchpalettes.com/palette/pink-clouds-under-blue-sky-spa0189/
    private int colorMauve = 0xa25a76; //https://stitchpalettes.com/palette/pink-clouds-under-blue-sky-spa0189/
    private int colorMelon = 0xe07b8d; //https://stitchpalettes.com/palette/pink-clouds-under-blue-sky-spa0189/

    private void makeTab(Tabs tabs, String header, String titleText, String text, char icon, Font font) {
        makeTab(tabs, header, titleText, text, icon, font, -1);
    }

    private void makeTab(Tabs tabs, String header, String titleText, String text, char icon, Font font, int color) {
        Label iconLabel = new Label("", "WelcomeIcon");
        iconLabel.setIconUIID("WelcomeIcon");
        if (color != -1) { //NB!!! must set below colors *before* setting icons, otherwise won't get right colors
            iconLabel.getAllStyles().setBgColor(color);
            iconLabel.getIconStyleComponent().getAllStyles().setBgColor(color);
        }
        if (font == null) {
            iconLabel.setMaterialIcon(icon);
        } else {
            iconLabel.setFontIcon(font, icon);
        }
        iconLabel.setGap(0); //ensure icons is centered!
        
        Label headerLabel = new Label(header, "WelcomeHeader");
        if (color != -1) {
            headerLabel.getAllStyles().setFgColor(color);
        }
        SpanLabel titleLabel = new SpanLabel(titleText, "WelcomeTitle");
        SpanLabel textLabel = new SpanLabel(text, "WelcomeText");
//        textLabel.setAutoSizeMode(true); //adapt to small screens

//        Container page = BoxLayout.encloseY(iconLabel, headerLabel, titleLabel, textLabel);
        Container page = BorderLayout.centerCenter(titleLabel);
        page.addComponent(BorderLayout.NORTH, BoxLayout.encloseY(iconLabel, headerLabel));
        page.addComponent(BorderLayout.SOUTH, textLabel);
        page.setUIID("WelcomeContainer");
//        page.getAllStyles().getBorder().set //TODO: how to set color of border programatically??!!

        tabs.addTab("", page);
//        return page;
    }

    public ScreenWelcome(MyForm previousForm, boolean forTesting) {
        super("TodoCatalyst - to-do magic", previousForm, () -> {
        });
        test = forTesting;
//        getTitleArea().setUIID("Container");
//        setTitle("TodoCatalyst");
        getContentPane().setScrollVisible(false);
        getContentPane().setAlwaysTensile(false); //only scroll if needed(???)
        setUIID("WelcomeScreen");

        //hide titlebar: http://stackoverflow.com/questions/42871223/how-do-i-hide-get-rid-the-title-bar-on-a-form-codename-one
        getToolbar().setUIID("Container");
        if (!test) {
            getToolbar().hideToolbar();
        }

        //TODO intro quiz: you tired of running into limitations in (miss features) in other ToDO apps, your tasks tend to pile up endlessly?, it is important for to be fully control/appear professional?
        setLayout(new BorderLayout());
        if (test) {
            addStandardBackCommand();
        }
        Container cont = getContentPane();

        //hide titlebar: http://stackoverflow.com/questions/42871223/how-do-i-hide-get-rid-the-title-bar-on-a-form-codename-one
        Button getStarted = new Button("Get started", Icons.iconGetStarted, "BigButton");
        getStarted.setTextPosition(Button.LEFT);
        cont.add(BorderLayout.SOUTH, getStarted);

//        super.addSideMenu(res);
        Tabs tabs = new Tabs() {
            protected Motion createTabSlideMotion(int start, int end) {
//        return Motion.createSplineMotion(start, end, getUIManager().getThemeConstant("tabsSlideSpeedInt", 200));
                return Motion.createEaseInMotion(start, end, 200);
            }
        };
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
        makeTab(tabs,
                "TodoCatalyst",
                "How you manage your time changes your life",
                "The smart task and time manager that transforms the way to stay on top of things. Full control. No stress.",//Professional. Reliable. 
                Icons.iconCompletedDateCust, Icons.myIconFont, colorLightGreen);
        makeTab(tabs,
                "Predict",
                "Know when your work can be done",
                "Use the unique SmartPlanning to reserve time and see when your tasks will finish", // Predictable, professional, stressfree.",
                Icons.iconFinishDateCust, Icons.myIconFont,colorClearBlue);
        makeTab(tabs,
                "Projects",
                "Real work is more than simple tasks",
                "Turn any task into a fully fledged project ",
                Icons.iconMainProjectsCust, Icons.myIconFont, colorDeepOrange);
        makeTab(tabs,
                "Time",
//                "Never lose sight of your most precious and non-renewable resource", //"Be focused with the Timer"
                "Never lose sight of your most precious resource", //"Be focused with the Timer"
                "Set tasks waiting until you want to come back to them",
                Icons.iconLaunchTimer, null, colorDarkGreen);
        makeTab(tabs,
                "Today",
//                "Everything that needs attention Today at one glance",//"Today is the first day of the rest of your life.",
                "Don't miss a thing that needs your attention Today",//"Today is the first day of the rest of your life.",
                "Today view shows everything relevant: due today, waiting until today, reminders today, or reserved time",
                Icons.iconMainToday, null, colorRoyalBlue);
        makeTab(tabs,
                "Prioritize",
                "Work on the right things",
                "Set priorities based on Importance/Urgency, life goals**, value",
                Icons.iconPrioTab, null, colorOrange);
        makeTab(tabs, 
                "Track", 
                "Review all your recent work at a glance", 
                "See completed tasks and their real vs estimated effort, organized by week or month, grouped by List or Category", 
                Icons.iconMainStatistics, null, colorMelon);
        makeTab(tabs, 
                "Usability", 
                "Easy and fast", 
                "Pinch insert, reorder with drag & drop, navigate around long lists, ...", 
                Icons.iconActualCurrentCust, Icons.myIconFont);
        makeTab(tabs,
                "Templates",
                "Never need to create the same project twice",
                "Turn existing projects into templates, build complex projects by insert multiple templates, use templates for sub-projects",
                Icons.iconActualCurrentCust, Icons.myIconFont, colorRed);
        makeTab(tabs, 
                "Powerful", 
                "The most complete features available", 
                "Repeat tasks in exactly the way you need. Assign as many categories as you like. ** And much more...", 
                Icons.iconActualCurrentCust, Icons.myIconFont);
        makeTab(tabs, 
                "Adaptable", 
                "Configure to match your working style, not the other way around", 
                "**", 
                Icons.iconActualCurrentCust, Icons.myIconFont);

        ButtonGroup bg = new ButtonGroup();
        int size = Display.getInstance().convertToPixels(1);
        Image unselectedWalkthru = Image.createImage(size, size, 0);
        Graphics g = unselectedWalkthru.getGraphics();
        g.setColor(0xffffff);
        g.setAlpha(100);
        g.setAntiAliased(true);
        g.fillArc(0, 0, size, size, 0, 360);
        Image selectedWalkthru = Image.createImage(size, size, 0);
        g = selectedWalkthru.getGraphics();
        g.setColor(0xffffff);
        g.setAntiAliased(true);
        g.fillArc(0, 0, size, size, 0, 360);
        RadioButton[] rbs = new RadioButton[tabs.getTabCount()];
        FlowLayout flow = new FlowLayout(CENTER);
        flow.setValign(BOTTOM);
        Container radioContainer = new Container(flow);
        for (int iter = 0; iter < rbs.length; iter++) {
            rbs[iter] = RadioButton.createToggle(unselectedWalkthru, bg);
            rbs[iter].setPressedIcon(selectedWalkthru);
            rbs[iter].setUIID("WelcomeButton");
            radioContainer.add(rbs[iter]);
        }

        rbs[0].setSelected(true);
        tabs.addSelectionListener((i, ii) -> {
            if (!rbs[ii].isSelected()) {
                rbs[ii].setSelected(true);
            }
        });

        cont.add(BorderLayout.CENTER, BoxLayout.encloseY(tabs, radioContainer));

//        Component.setSameSize(radioContainer, spacer1, spacer2, spacer3);
        // special case for rotation
        addOrientationListener(e -> {
//            updateArrowPosition(barGroup.getRadioButton(barGroup.getSelectedIndex()), arrow);
        });

        revalidate(); //ensure correct size of all components

    }

    @Override
    public void refreshAfterEdit() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        super.refreshAfterEdit();
    }

}

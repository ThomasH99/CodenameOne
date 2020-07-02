package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.Label;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.TextArea;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.table.TableLayout;
import com.codename1.util.StringUtil;
import com.parse4cn1.ParseBatch;
import com.parse4cn1.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import net.informaticalibera.cn1.nativelogreader.NativeLogs;

/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenRepair extends MyForm {

//    private final static String CURRENT_USER_STORAGE_ID = "parseCurrentUser";
//    MyForm mainScreen;
    public final static String SCREEN_TITLE = "Internal/Repair";

    ScreenRepair(MyForm mainScreen) { // throws ParseException, IOException {
        super(SCREEN_TITLE, null, () -> {
        });
        this.previousForm = mainScreen;
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);
        addCommandsToToolbar();
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        restoreKeepPos();
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

    private static String FONT_TEXT = "Task status DONE 123490";

    private Component createForFont(Font fnt, String s) {
        Container c = new Container(BoxLayout.y());
        Label l = new Label(s);
        l.getUnselectedStyle().setFont(fnt);
//        SpanLabel lt = new SpanLabel(FONT_TEXT); //NB. SpanLabel changes the font size to fit in screen width!
        Label lt = new Label(FONT_TEXT);
        lt.getUnselectedStyle().setFont(fnt);
        c.add(l).add(lt);
//        return l;
        return c;
    }

    public MyForm makeShowBuiltinFontsForm() {
//        GridLayout gr = new GridLayout(5);
        Layout gr = BoxLayout.y();
//        gr.setAutoFit(true);
//        Form hi = new Form("Fonts", gr);
        MyForm hi = new MyForm("Fonts", ScreenRepair.this, () -> {
        });
        hi.getToolbar().setBackCommand(Command.createMaterial("", Icons.iconBackToPreviousScreen, (e) -> this.showBack()));

        hi.setLayout(gr);
        hi.setScrollableY(true);

//        hi.getToolbar().setBackCommand(Command.create("", Icons.iconBackToPrevFormToolbarStyle, (e) -> this.show()));
        getToolbar().setBackCommand(makeDoneUpdateWithParseIdMapCommand());

        int fontSize = Display.getInstance().convertToPixels(3);

        Font smallPlainSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        Font mediumPlainSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        Font largePlainSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
        Font smallBoldSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
        Font mediumBoldSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        Font largeBoldSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
        Font smallItalicSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL);
        Font mediumItalicSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_MEDIUM);
        Font largeItalicSystemFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_LARGE);

        Font smallPlainMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        Font mediumPlainMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        Font largePlainMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_LARGE);
        Font smallBoldMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL);
        Font mediumBoldMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        Font largeBoldMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_LARGE);
        Font smallItalicMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_ITALIC, Font.SIZE_SMALL);
        Font mediumItalicMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_ITALIC, Font.SIZE_MEDIUM);
        Font largeItalicMonospaceFont = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_ITALIC, Font.SIZE_LARGE);

        Font smallPlainProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        Font mediumPlainProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        Font largePlainProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE);
        Font smallBoldProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
        Font mediumBoldProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        Font largeBoldProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
        Font smallItalicProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_SMALL);
        Font mediumItalicProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_MEDIUM);
        Font largeItalicProportionalFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_LARGE);

        String[] nativeFontTypes = {
            "native:MainThin", "native:MainLight", "native:MainRegular", "native:MainBold", "native:MainBlack",
            "native:ItalicThin", "native:ItalicLight", "native:ItalicRegular", "native:ItalicBold", "native:ItalicBlack"};

        for (String s : nativeFontTypes) {
            Font tt = Font.createTrueTypeFont(s, s).derive(fontSize, Font.STYLE_PLAIN);
            hi.add(createForFont(tt, s));
        }

//        if (false) {
        if (true) {
            // requires Handlee-Regular.ttf in the src folder root!
//            Font ttfFont = Font.createTrueTypeFont("Handlee", "Handlee-Regular.ttf").derive(fontSize, Font.STYLE_PLAIN);

            hi.
                    //                    add(createForFont(ttfFont, "Handlee TTF Font")).
                    add(createForFont(smallPlainSystemFont, "smallPlainSystemFont")).
                    add(createForFont(mediumPlainSystemFont, "mediumPlainSystemFont")).
                    add(createForFont(largePlainSystemFont, "largePlainSystemFont")).
                    add(createForFont(smallBoldSystemFont, "smallBoldSystemFont")).
                    add(createForFont(mediumBoldSystemFont, "mediumBoldSystemFont")).
                    add(createForFont(largeBoldSystemFont, "largeBoldSystemFont")).
                    add(createForFont(smallPlainSystemFont, "smallItalicSystemFont")).
                    add(createForFont(mediumItalicSystemFont, "mediumItalicSystemFont")).
                    add(createForFont(largeItalicSystemFont, "largeItalicSystemFont")).
                    add(createForFont(smallPlainMonospaceFont, "smallPlainMonospaceFont")).
                    add(createForFont(mediumPlainMonospaceFont, "mediumPlainMonospaceFont")).
                    add(createForFont(largePlainMonospaceFont, "largePlainMonospaceFont")).
                    add(createForFont(smallBoldMonospaceFont, "smallBoldMonospaceFont")).
                    add(createForFont(mediumBoldMonospaceFont, "mediumBoldMonospaceFont")).
                    add(createForFont(largeBoldMonospaceFont, "largeBoldMonospaceFont")).
                    add(createForFont(smallItalicMonospaceFont, "smallItalicMonospaceFont")).
                    add(createForFont(mediumItalicMonospaceFont, "mediumItalicMonospaceFont")).
                    add(createForFont(largeItalicMonospaceFont, "largeItalicMonospaceFont")).
                    add(createForFont(smallPlainProportionalFont, "smallPlainProportionalFont")).
                    add(createForFont(mediumPlainProportionalFont, "mediumPlainProportionalFont")).
                    add(createForFont(largePlainProportionalFont, "largePlainProportionalFont")).
                    add(createForFont(smallBoldProportionalFont, "smallBoldProportionalFont")).
                    add(createForFont(mediumBoldProportionalFont, "mediumBoldProportionalFont")).
                    add(createForFont(largeBoldProportionalFont, "largeBoldProportionalFont")).
                    add(createForFont(smallItalicProportionalFont, "smallItalicProportionalFont")).
                    add(createForFont(mediumItalicProportionalFont, "mediumItalicProportionalFont")).
                    add(createForFont(largeItalicProportionalFont, "largeItalicProportionalFont"));
        }
//  hi.show();
        return hi;
    }

    private MyForm showDeviceInfo() {
        MyForm hi = new MyForm("Device info", ScreenRepair.this, () -> {
        });
//        getToolbar().setBackCommand(Command.create("", null, (e) -> this.show()));
        hi.getToolbar().setBackCommand(Command.createMaterial("", Icons.iconBackToPreviousScreen, (e) -> this.showBack()));
//        hi.getToolbar().addCommandToLeftBar(Command.create("", Icons.iconBackToPrevFormToolbarStyle, (e) -> this.show()));
        Display d = Display.getInstance();
        String density = "";
        switch (Display.getInstance().getDeviceDensity()) {
            case Display.DENSITY_2HD:
                density = "DENSITY_2HD";
                break;
            case Display.DENSITY_4K:
                density = "DENSITY_4K";
                break;
            case Display.DENSITY_560:
                density = "DENSITY_560";
                break;
            case Display.DENSITY_HD:
                density = "DENSITY_HD";
                break;
            case Display.DENSITY_HIGH:
                density = "DENSITY_HIGH";
                break;
            case Display.DENSITY_LOW:
                density = "DENSITY_LOW";
                break;
            case Display.DENSITY_MEDIUM:
                density = "DENSITY_MEDIUM";
                break;
            case Display.DENSITY_VERY_HIGH:
                density = "DENSITY_VERY_HIGH";
                break;
            case Display.DENSITY_VERY_LOW:
                density = "DENSITY_VERY_LOW";
                break;
        }

        double pixelsPerMM = (((double) d.convertToPixels(1000)) / 1000.0);
        L10NManager l10n = L10NManager.getInstance();

        hi.setLayout(new TableLayout(50, 2));
        TableLayout.Constraint span2 = new TableLayout.Constraint().horizontalSpan(2);
        TableLayout.Constraint w40 = new TableLayout.Constraint().widthPercentage(40);
        TableLayout.Constraint right = new TableLayout.Constraint().horizontalAlign(Component.RIGHT);
        hi.setScrollableY(true);

        hi.
                add(w40, new SpanLabel("Density:")).add(right, new SpanLabel(density)).
                //                add(" ").
                add(w40, new SpanLabel("Platform Name:")).add(right, new SpanLabel(d.getPlatformName())).
                //                add(" ").
                add(w40, new SpanLabel("User Agent:")).add(right, new SpanLabel(d.getProperty("User-Agent", ""))).
                //                add(" ").
                add(w40, new SpanLabel("OS:")).add(right, new SpanLabel(d.getProperty("OS", ""))).
                //                add(" ").
                add(w40, new SpanLabel("OS Version:")).add(right, new SpanLabel(d.getProperty("OSVer", ""))).
                //                add(" ").
                add(w40, new SpanLabel("UDID:")).add(right, new SpanLabel(d.getUdid())).
                //                add(" ").
                add(w40, new SpanLabel("MSISDN:")).add(right, new SpanLabel(d.getMsisdn())).
                //                add(" ").
                add(w40, new SpanLabel("Display Width X Height:")).add(right, new SpanLabel(d.getDisplayWidth() + "X" + d.getDisplayHeight())).
                //                add(" ").
                add(w40, new SpanLabel("1mm In Pixels:")).add(right, new SpanLabel(l10n.format(pixelsPerMM))).
                //                add(" ").
                add(w40, new SpanLabel("Language:")).add(right, new SpanLabel(l10n.getLanguage())).
                //                add(" ").
                add(w40, new SpanLabel("Locale:")).add(right, new SpanLabel(l10n.getLocale())).
                //                add(" ").
                add(w40, new SpanLabel("Currency Symbol:")).add(right, new SpanLabel(l10n.getCurrencySymbol())).
                //                add(" ").
                add(span2, uneditableCheck("Is sound available for "+Display.SOUND_TYPE_ALARM, d.isBuiltinSoundAvailable(Display.SOUND_TYPE_ALARM))).
                add(span2, uneditableCheck("Is sound available for "+Display.SOUND_TYPE_BUTTON_PRESS, d.isBuiltinSoundAvailable(Display.SOUND_TYPE_BUTTON_PRESS))).
                add(span2, uneditableCheck("Is sound available for "+Display.SOUND_TYPE_CONFIRMATION, d.isBuiltinSoundAvailable(Display.SOUND_TYPE_CONFIRMATION))).
                add(span2, uneditableCheck("Is sound available for "+Display.SOUND_TYPE_ERROR, d.isBuiltinSoundAvailable(Display.SOUND_TYPE_ERROR))).
                add(span2, uneditableCheck("Is sound available for "+Display.SOUND_TYPE_INFO, d.isBuiltinSoundAvailable(Display.SOUND_TYPE_INFO))).
                add(span2, uneditableCheck("Is sound available for "+Display.SOUND_TYPE_WARNING, d.isBuiltinSoundAvailable(Display.SOUND_TYPE_WARNING))).
                
                add(span2, uneditableCheck("isAllowMinimizing ", d.isAllowMinimizing())).
                add(span2, uneditableCheck("isBuiltinSoundsEnabled ", d.isBuiltinSoundsEnabled())).
                
                add(span2, uneditableCheck("isNativePicker supported for Calendar", d.isNativePickerTypeSupported(Display.PICKER_TYPE_CALENDAR))).
                add(span2, uneditableCheck("isNativePicker supported for Date", d.isNativePickerTypeSupported(Display.PICKER_TYPE_DATE))).
                add(span2, uneditableCheck("isNativePicker supported for Date and Time", d.isNativePickerTypeSupported(Display.PICKER_TYPE_DATE_AND_TIME))).
                add(span2, uneditableCheck("isNativePicker supported for Duration", d.isNativePickerTypeSupported(Display.PICKER_TYPE_DURATION))).
                add(span2, uneditableCheck("isNativePicker supported for Duration Hours", d.isNativePickerTypeSupported(Display.PICKER_TYPE_DURATION_HOURS))).
                add(span2, uneditableCheck("isNativePicker supported for Duration Minutes", d.isNativePickerTypeSupported(Display.PICKER_TYPE_DURATION_MINUTES))).
                add(span2, uneditableCheck("isNativePicker supported for Strings", d.isNativePickerTypeSupported(Display.PICKER_TYPE_STRINGS))).
                add(span2, uneditableCheck("isNativePicker supported for Time", d.isNativePickerTypeSupported(Display.PICKER_TYPE_TIME))).
                
                add(span2, uneditableCheck("isNativeTitle ", d.isNativeTitle())).
                add(span2, uneditableCheck("isNotificationSupported ", d.isNotificationSupported())).
                
                add(span2, uneditableCheck("Are Mutable Images Fast", d.areMutableImagesFast())).
                add(span2, uneditableCheck("Can Dial", d.canDial())).
                add(span2, uneditableCheck("Can Force Orientation", d.canForceOrientation())).
                add(span2, uneditableCheck("Has Camera", d.hasCamera())).
                add(span2, uneditableCheck("Badging", d.isBadgingSupported())).
                add(span2, uneditableCheck("Desktop", d.isDesktop())).
                add(span2, uneditableCheck("Tablet", d.isTablet())).
                add(span2, uneditableCheck("Gaussian Blur Support", d.isGaussianBlurSupported())).
                add(span2, uneditableCheck("Get All Contacts Fast", d.isGetAllContactsFast())).
                add(span2, uneditableCheck("Multi Touch", d.isMultiTouch())).
                add(span2, uneditableCheck("PICKER_TYPE_DATE", d.isNativePickerTypeSupported(Display.PICKER_TYPE_DATE))).
                add(span2, uneditableCheck("PICKER_TYPE_DATE_AND_TIME", d.isNativePickerTypeSupported(Display.PICKER_TYPE_DATE_AND_TIME))).
                add(span2, uneditableCheck("PICKER_TYPE_STRINGS", d.isNativePickerTypeSupported(Display.PICKER_TYPE_STRINGS))).
                add(span2, uneditableCheck("PICKER_TYPE_TIME", d.isNativePickerTypeSupported(Display.PICKER_TYPE_TIME))).
                add(span2, uneditableCheck("Native Share", d.isNativeShareSupported())).
                add(span2, uneditableCheck("Native Video Player Controls", d.isNativeVideoPlayerControlsIncluded())).
                add(span2, uneditableCheck("Notification", d.isNotificationSupported())).
                add(span2, uneditableCheck("Open Native Navigation", d.isOpenNativeNavigationAppSupported())).
                add(span2, uneditableCheck("Screen Saver Disable", d.isScreenSaverDisableSupported())).
                add(span2, uneditableCheck("Simulator", d.isSimulator()));

        add(w40, new SpanLabel("Display.getInstance().getProperty:")).add(right, new SpanLabel("")).
                add(w40, new SpanLabel("AppName:")).add(right, new SpanLabel(Display.getInstance().getProperty("AppName", "NONE"))).
                add(w40, new SpanLabel("appVersion:")).add(right, new SpanLabel(Display.getInstance().getProperty("appVersion", "NONE"))).
                //https://github.com/codenameone/CodenameOne/issues/2677
                add(w40, new SpanLabel("AppArg:")).add(right, new SpanLabel(Display.getInstance().getProperty("AppArg", "NONE"))).
                add(w40, new SpanLabel("buildKey:")).add(right, new SpanLabel(Display.getInstance().getProperty("build_key", "NONE"))).
                add(w40, new SpanLabel("builtByUser:")).add(right, new SpanLabel(Display.getInstance().getProperty("builtByUser", "NONE"))).
                add(w40, new SpanLabel("Build-Date(?):")).add(right, new SpanLabel(Display.getInstance().getProperty("Build-Date", "NONE"))).
                add(w40, new SpanLabel("BuildDate(?):")).add(right, new SpanLabel(Display.getInstance().getProperty("BuildDate", "NONE"))).
                add(w40, new SpanLabel("builddate(?):")).add(right, new SpanLabel(Display.getInstance().getProperty("builddcodename onetae", "NONE"))).
                add(w40, new SpanLabel("builtByUser:")).add(right, new SpanLabel(Display.getInstance().getProperty("builtByUser", "NONE"))).
                add(w40, new SpanLabel("packageName:")).add(right, new SpanLabel(Display.getInstance().getProperty("packageName", "NONE"))).
                add(w40, new SpanLabel("getPlatformName():")).add(right, new SpanLabel(Display.getInstance().getPlatformName())).
                add(w40, new SpanLabel("platform:")).add(right, new SpanLabel(Display.getInstance().getProperty("platform", "NONE"))).
                add(w40, new SpanLabel("User-Agent:")).add(right, new SpanLabel(Display.getInstance().getProperty("User-Agent", "NONE"))).
                add(w40, new SpanLabel("androidId:")).add(right, new SpanLabel(Display.getInstance().getProperty("androidId", "NONE"))).
                add(w40, new SpanLabel("uuid:")).add(right, new SpanLabel(Display.getInstance().getProperty("uuid", "NONE"))).
                add(w40, new SpanLabel("UDID:")).add(right, new SpanLabel(Display.getInstance().getProperty("UDID", "NONE"))).
                add(w40, new SpanLabel("imei:")).add(right, new SpanLabel(Display.getInstance().getProperty("imei", "NONE"))).
                add(w40, new SpanLabel("IMEI:")).add(right, new SpanLabel(Display.getInstance().getProperty("IMEI", "NONE"))).
                add(w40, new SpanLabel("MSISDN:")).add(right, new SpanLabel(Display.getInstance().getProperty("MSISDN", "NONE"))).
                add(w40, new SpanLabel("sdcard:")).add(right, new SpanLabel(Display.getInstance().getProperty("sdcard", "NONE")));

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            //TODO enable below code to send an email with device info
//            final String densityStr = density;
//            hi.getToolbar().addMaterialCommandToRightBar("", FontImage.MATERIAL_SEND, e -> {
//                StringBuilder body = new StringBuilder("Density: ").
//                        append(densityStr).
//                        append("\n").
//                        append("Platform Name: ").
//                        append(d.getPlatformName()).
//                        append("\n").
//                        append("User Agent: ").
//                        append(d.getProperty("User-Agent", "")).
//                        append("\n").
//                        append("OS: ").
//                        append(d.getProperty("OS", "")).
//                        append("\n").
//                        append("OS Version: ").
//                        append(d.getProperty("OSVer", "")).
//                        append("\n").
//                        append("UDID: ").
//                        append(d.getUdid()).
//                        append("\n").
//                        append("MSISDN: ").
//                        append(d.getMsisdn()).
//                        append("\n").
//                        append("Display Width X Height: ").
//                        append(d.getDisplayWidth()).append("X").append(d.getDisplayHeight()).
//                        append("\n").
//                        append("1mm In Pixels: ").
//                        append(l10n.format(pixelsPerMM)).
//                        append("\n").
//                        append("Language: ").
//                        append(l10n.getLanguage()).
//                        append("\n").
//                        append("Locale: ").
//                        append(l10n.getLocale()).
//                        append("\n").
//                        append("Currency Symbol: ").
//                        append(l10n.getCurrencySymbol()).
//                        append("\nAre Mutable Images Fast: ").
//                        append(d.areMutableImagesFast()).
//                        append("\nCan Dial: ").
//                        append(d.canDial()).
//                        append("\nCan Force Orientation: ").append(d.canForceOrientation()).
//                        append("\nHas Camera: ").append(d.hasCamera()).
//                        append("\nBadging: ").append(d.isBadgingSupported()).
//                        append("\nDesktop: ").append(d.isDesktop()).
//                        append("\nTablet: ").append(d.isTablet()).
//                        append("\nGaussian Blur Support: ").append(d.isGaussianBlurSupported()).
//                        append("\nGet All Contacts Fast: ").append(d.isGetAllContactsFast()).
//                        append("\nMulti Touch: ").append(d.isMultiTouch()).
//                        append("\nPICKER_TYPE_DATE: ").append(d.isNativePickerTypeSupported(Display.PICKER_TYPE_DATE)).
//                        append("\nPICKER_TYPE_DATE_AND_TIME: ").append(d.isNativePickerTypeSupported(Display.PICKER_TYPE_DATE_AND_TIME)).
//                        append("\nPICKER_TYPE_STRINGS: ").append(d.isNativePickerTypeSupported(Display.PICKER_TYPE_STRINGS)).
//                        append("\nPICKER_TYPE_TIME: ").append(d.isNativePickerTypeSupported(Display.PICKER_TYPE_TIME)).
//                        append("\nNative Share: ").append(d.isNativeShareSupported()).
//                        append("\nNative Video Player Controls: ").append(d.isNativeVideoPlayerControlsIncluded()).
//                        append("\nNotification: ").append(d.isNotificationSupported()).
//                        append("\nOpen Native Navigation: ").append(d.isOpenNativeNavigationAppSupported()).
//                        append("\nScreen Saver Disable: ").append(d.isScreenSaverDisableSupported()).
//                        append("\nSimulator: ").append(d.isSimulator());
//
//                Message msg = new Message(body.toString());
//
//                Display.getInstance().sendMessage(new String[]{Display.getInstance().getProperty("built_by_user", "youremail@somewhere.com")}, "Device Details", msg);
//            });
//        }
//</editor-fold>
//    hi.show();
        return hi;
    }

    private MyForm showLocalizationInfo() {
//        Form hi = new Form("L10N", new TableLayout(16, 2));
        MyForm hi = new MyForm("L10N", ScreenRepair.this, () -> {
        });
        hi.setLayout(new TableLayout(16, 2));
        hi.setScrollableY(true);

        hi.getToolbar().setBackCommand(Command.createMaterial("", Icons.iconBackToPreviousScreen, (e) -> this.showBack()));

        L10NManager l10n = L10NManager.getInstance();
        hi.add(new SpanLabel("format(double)")).add(l10n.format(11.11)).
                add(new SpanLabel("Locale (Display.getInstance().getLocalizationManager().getLocale()) = " 
                        + Display.getInstance().getLocalizationManager().getLocale())).
                add(new SpanLabel("TimeZone (TimeZone.getDefault()) zone id = " + TimeZone.getDefault().getID())).
                add(new SpanLabel("TimeZone (TimeZone.getDefault()) raw offset = " + MyDate.formatDuration(TimeZone.getDefault().getRawOffset()))).
                add(new SpanLabel("TimeZone (TimeZone.getDefault()) useDaylightZone = " + TimeZone.getDefault().useDaylightTime())).
                add(new SpanLabel("TimeZone (TimeZone.getDefault()) toString = " + TimeZone.getDefault().toString())).
                add(new SpanLabel("format(int)")).add(l10n.format(33)).
                add(new SpanLabel("formatCurrency")).add(l10n.formatCurrency(53.267)).
                add(new SpanLabel("formatDateLongStyle")).add(l10n.formatDateLongStyle(new Date())).
                add(new SpanLabel("formatDateShortStyle")).add(l10n.formatDateShortStyle(new Date())).
                add(new SpanLabel("formatDateTime")).add(l10n.formatDateTime(new Date())).
                add(new SpanLabel("formatDateTimeMedium")).add(l10n.formatDateTimeMedium(new Date())).
                add(new SpanLabel("formatDateTimeShort")).add(l10n.formatDateTimeShort(new Date())).
                add(new SpanLabel("getCurrencySymbol")).add(l10n.getCurrencySymbol()).
                add(new SpanLabel("getLanguage")).add(l10n.getLanguage()).
                add(new SpanLabel("getLocale")).add(l10n.getLocale()).
                add(new SpanLabel("isRTLLocale")).add("" + l10n.isRTLLocale()).
                add(new SpanLabel("parseCurrency")).add(l10n.formatCurrency(l10n.parseCurrency("33.77$"))).
                add(new SpanLabel("parseDouble")).add(l10n.format(l10n.parseDouble("34.35"))).
                add(new SpanLabel("parseInt")).add(l10n.format(l10n.parseInt("56"))).
                add(new SpanLabel("parseLong")).add("" + l10n.parseLong("4444444"));
        //hi.show();
        return hi;
    }

    private Label getSysProp(String key) {
        return new Label(Display.getInstance().getProperty(key, "None"));
    }

//    private Form showSystemPropertiesXXXX() { //NB!! Already in showDeviceInfo
////        Form hi = new Form("L10N", new TableLayout(16, 2));
//        Form hi = new MyForm("System Properties", ScreenRepair.this, () -> {
//        });
//        hi.setLayout(new TableLayout(16, 2));
//
//        hi.getToolbar().setBackCommand(Command.create("", Icons.iconBackToPrevFormToolbarStyle(), (e) -> this.showBack()));
//        //https://www.codenameone.com/javadoc/com/codename1/ui/Display.html
//        //getProperty(String key, String defaultValue)
//        //Returns the property from the underlying platform deployment or the default value if no deployment values are supported. This is equivalent to the getAppProperty from the jad file.
//        //The implementation should be responsible for the following keys to return reasonable valid values for the application:
//        //AppName
//        //User-Agent
//        //AppVersion
//        //Platform - Similar to microedition.platform
//        //OS - returns what is the underlying platform e.g. - iOS, Android, RIM, SE...
//        //OSVer - OS version when available as a user readable string (not necessarily a number e.g: 3.2.1).
//
//        hi.add("AppName").add(getSysProp("AppName")).
//                add("User-Agent").add(getSysProp("User-Agent")).
//                add("AppVersion").add(getSysProp("AppVersion")).
//                add("Platform").add(getSysProp("Platform")).
//                add("OS").add(getSysProp("OS")).
//                add("OSVer").add(getSysProp("OSVer"));
//        return hi;
//    }
    private CheckBox uneditableCheck(String t, boolean v) {
        CheckBox c = new CheckBox(t);
        c.setSelected(v);
        c.setEnabled(false);
        return c;
    }

    ///////////////////////////////////////////////////////////
    Form fPinchOut;// = new Form(new BorderLayout());
    Container disp1;// = new Container(BoxLayout.y());
    Label title;//= new Label();
    Label xLabel;// = new Label();
    Label yLabel;// = new Label();
    Label distLabel;// = new Label();
    Label comp1Label;// = new Label();
    Label comp2Label;//= new Label();
    Label cont1Label;//= new Label();
    Label cont2Label;//= new Label();
    Label dropTarget1Label;// = new Label();
    Label dropTarget2Label;//= new Label();
    private PinchInsertItemContainer pinchContainer;
    Item pinchItem;

    private boolean minimumPinchSizeReached() {
        return false;
    }

    public void setInsertItemValues(Object obj, Object sortField, Object objBefore, Object objAfter) {//, getValueFunction, makeNewValueFunction) {
        if (obj instanceof Item) {
            Item item = (Item) obj;

        } else if (obj instanceof WorkSlot) {
        } else if (obj instanceof Category) {
        } else if (obj instanceof ItemList) {

        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private int pinchInitialYDistance = -1;
//
//    private void initPinch() {
//        fPinchOut = new Form(new BorderLayout()) {
//            @Override
//            public void pointerDragged(int[] x, int[] y) {
//                ItemList itemList = null;
//                int pos = 0; //TODO find position of *lowest* container (==highest index, == thumb position == most 'stable' position)
//                if (x.length > 1) { //PINCH == TWO FINGERS
//                    //TODO!!! What happens if a pinch in is changed to PinchOut while moving fingers? Should *not* insert a new container but just leave the old one)
//                    //TODO!!! What happens if a pinch out is changed to PinchIn while moving fingers? Simply remove the inserted container!
//                    //INIT pinch out
//                    boolean templateEditMode = false;
//                    boolean pinchIncreasing = false;
//                    Component finger1Comp = findDropTargetAt(x[0], y[0]); //TODO!!!! find right ocmponent (should work in any list with any type of objects actually! WorkSlots, ...
//                    Component finger2Comp = findDropTargetAt(x[1], y[1]);
//                    Container containerList = null; //TODO find container (==srollable list?)
//                    if (pinchContainer == null) {
//                        if (!pinchIncreasing) { //Pinch IN - to delete a just inserted container (or any other item? NO, don't make Delete easy)
//                            Component pinchedInComp = null; //TODO find a possible pinchContainer between the
////                            if (pinchedInComp instanceof InlineInsertNewElementContainer) {
////                                pinchContainer = (InlineInsertNewElementContainer) pinchedInComp;
//                            if (pinchedInComp instanceof InlineInsertNewItemContainer2) {
//                                pinchContainer = (InlineInsertNewItemContainer2) pinchedInComp;
//                            }
//                        } else {
//
//                            pinchItem = new Item();
////                            pinchContainer = new InlineInsertNewElementContainer(ScreenRepair.this, pinchItem, itemList) {
//                            pinchContainer = new InlineInsertNewItemContainer2(ScreenRepair.this, pinchItem, itemList) {
//                                public Dimension getPreferredSize() {
//                                    return new Dimension(getPreferredW(), Math.min(getPreferredH(), y[0] - y[1]));
//                                }
//                            };
//                            //insert
//                            containerList.addComponent(pos, pinchContainer);
//                            ScreenRepair.this.refreshAfterEdit(); //really necessary?
//                        }
//                    }
//
//                    double currentDis = distance(x, y);
//
//                    // prevent division by 0
//                    if (pinchDistance <= 0) {
//                        pinchDistance = currentDis;
//                    }
//                    double scale = currentDis / pinchDistance;
//                    if (pinch((float) scale)) {
//                        return;
//                    }
//                    Log.p("PointerDragged dist=" + pinchDistance + ", x=" + x + ", y=" + y);
//                    display(x, y, true);
//                } else {
//                    //PinchOut is (maybe) finished (newPinchContainer!=null means a pinch was ongoing before)
//                    if (pinchContainer != null) { //a pinch container is either created or found (on PinchInToDelete)
//
//                        if (minimumPinchSizeReached()) { //TODO implement
//                            //add new item into underlying list
//                            itemList.addItemAtIndex(pinchItem, pos);
//                        } else {
//                            //delete inserted container (whether a new container not sufficiently pinched OUT or an existing SubtaskContainer pinched IN)
//                            Container list = null;
//                            list.removeComponent(pinchContainer);
//                            ScreenRepair.this.refreshAfterEdit();
//                        }
//                        pinchContainer = null; //indicates done with this container
//                    }
//                    display(x, y, false);
//                }
//                super.pointerDragged(x[0], y[0]);
//            }
//        };
//        fPinchOut.setScrollableY(true);
//        disp1 = new Container(BoxLayout.y());
//        title = new Label("XX");
//        xLabel = new Label("XX");
//        yLabel = new Label("XX");
//        distLabel = new Label("XX");
//        comp1Label = new Label("XX");
//        comp2Label = new Label("XX");
//        cont1Label = new Label("XX");
//        cont2Label = new Label("XX");
//        dropTarget1Label = new Label("XX");
//        dropTarget2Label = new Label("XX");
////        disp1.removeAll();
//        disp1.addAll(title, xLabel, yLabel, distLabel, comp1Label, comp2Label, cont1Label, cont2Label, dropTarget1Label, dropTarget2Label);
//        fPinchOut.addComponent(BorderLayout.SOUTH, disp1);
//
//        Container labelCont = new Container(BoxLayout.y());
//        labelCont.setScrollableY(true);
//        for (int i = 0, size = 40; i < size; i++) {
//            Label l = new Label("Label " + i);
//            l.setName("Label" + i);
//            labelCont.add(l);
//        }
//        fPinchOut.addComponent(BorderLayout.CENTER, labelCont);
//    }
//
//    private double distance(int[] x, int[] y) {
//        int disx = x[0] - x[1];
//        int disy = y[0] - y[1];
//        return Math.sqrt(disx * disx + disy * disy);
//    }
//
//    private void display(int[] x, int[] y, boolean inPinch) {
//        title.setText(inPinch ? "***PINCH***" : "MOVE");
//        xLabel.setText("(x[0],y[0])=(" + x[0] + "," + y[0] + ")");
//        if (x.length > 1) {
//            yLabel.setText("(x[1],y[1])=(" + x[1] + "," + y[1] + ")");
//            distLabel.setText("dist=" + distance(x, y));
//        }
//
//        Component comp1 = getComponentAt(x[0], y[0]);
//        comp1Label.setText("Comp1=" + comp1);
//        if (comp1 instanceof Container) {
//            Container cont1 = (Container) comp1;
//            cont1Label.setText("Cont1=" + cont1);
//            Component dropTarget1 = cont1.findDropTargetAt(x[0], y[0]);
//            dropTarget1Label.setText("dropTarget1=" + dropTarget1);
//        }
//
//        if (x.length > 1) {
//            Component comp2 = getComponentAt(x[1], y[1]);
//            comp1Label.setText("Comp2=" + comp2);
//            if (comp2 instanceof Container) {
//                Container cont2 = (Container) comp2;
//                cont2Label.setText("Cont2=" + cont2);
//                Component dropTarget2 = cont2.findDropTargetAt(x[1], y[1]);
//                dropTarget2Label.setText("dropTarget2=" + dropTarget2);
//            }
//        } else {
//            comp1Label.setText("Comp2=");
//            cont2Label.setText("Cont2=");
//            dropTarget2Label.setText("dropTarget2=");
//        }
//
//        fPinchOut.revalidate();
//    }
//</editor-fold>
    /**
     * If this Component is focused, the pointer dragged event will call this
     * method
     *
     * @param x the pointer x coordinate
     * @param y the pointer y coordinate
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void pointerDragged(int[] x, int[] y) {
//        if (x.length > 1) {
//            double currentDis = distance(x, y);
//
//            // prevent division by 0
//            if (pinchDistance <= 0) {
//                pinchDistance = currentDis;
//            }
//            double scale = currentDis / pinchDistance;
//            if (pinch((float) scale)) {
//                return;
//            }
////            Log.p("PointerDragged dist=" + pinchDistance + ", x=" + x + ", y=" + y);
//            display(x, y, true);
//        } else {
//            display(x, y, false);
//        }
//        pointerDragged(x[0], y[0]);
//    }
//</editor-fold>
    double pinchDistance;

    /**
     * display a container in a meaningful for debug
     *
     * @param comp
     * @return
     */
    private String disp(Component comp) {
        if (comp == null) {
            return "nullx";
        } else if (comp.getName() != null) {
            return comp.getName();
        } else if (comp instanceof Container) {
            String s = "Container(";
            String sep = "";
            for (int i = 0, size = ((Container) comp).getComponentCount(); i < size; i++) {
                s += sep + disp(((Container) comp).getComponentAt(i));
                sep = ", ";
            }
            s += ")";
            return s;
        } else {
            return comp.toString();
        }

    }

    //////////////////////////////////////////////////////////
    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    private Container buildContentPane(Container content) {
//        Container content = new Container();
        content.setScrollableY(true);
        TableLayout tl;
        if (Display.getInstance().isTablet() || !Display.getInstance().isPortrait()) {
            tl = new TableLayout(7, 2);
        } else {
            tl = new TableLayout(14, 1);
        }
        tl.setGrowHorizontally(true);
        content.setLayout(tl);

//        content.add(new Button(MyCommand.create("Repair data menu", null, (e) -> {
        content.add(new Button(MyReplayCommand.create("Repair data menu", null, (e) -> {
            new ScreenRepairData(ScreenRepair.this).show();
        })));

        content.add(new Button(new Command("Refresh cache") {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                DAO.getInstance().cacheClearAndRefreshAllData();
//                DAO.getInstance().cacheLoadDataChangedOnServerAndInitIfNecessary(true);
//TODO!!!! show waiting turning symbol + message: "Do not make changes to data on the server while refreshing locally stored data"
                DAO.getInstance().resetAndDeleteAndReloadAllCachedData();
                Dialog.show("Info", "Finished updating cache", "OK", null);
            }
        }));

        Label labelCoord = new Label("LabelCoord");
        SpanLabel labelInfo = new SpanLabel("LabelInfo\nline2\nline3\nline4\nLine5\nLine6\nLine7");

        content.add(new Button(new Command("Test Pinch") {
            @Override
            public void actionPerformed(ActionEvent evt) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                initPinch();
//                pointerDragged(new int[]{50, 100}, new int[]{50, 100});
//                fPinchOut.setToolbar(new Toolbar());
//                fPinchOut.getToolbar().setBackCommand(Command.create("", Icons.iconBackToPrevFormToolbarStyle(), (e) -> ScreenRepair.this.showBack()));
//                initPinch();
//                fPinchOut.show();
//</editor-fold>
                MyForm pinchForm = new MyForm("TestPinch", ScreenRepair.this, () -> {
                }) {

//                    @Override
                    protected void displayTest(int[] x, int[] y, boolean inPinch) {
                        Component compAbove2 = getContentPane().getComponentAt(x[0], y[0]);
                        Component compAbove3 = getComponentForm().getComponentAt(x[0], y[0]);
                        Component compAbove1 = getComponentAt(x[0], y[0]);
                        Component compAbove = findDropTargetAt(x[0], y[0]);
                        Component responder = getResponderAt(x[0], y[0]);
                        Component dropTarget = findDropTargetAt(x[0], y[0]);
                        Component closest = getClosestComponentTo(x[0], y[0]);
                        Container parentContAbove = compAbove != null ? compAbove.getParent() : null;
//                        MyDragAndDropSwipeableContainer dropComponentAbove = MyForm.findDropContainerStartingFrom(compAbove);
                        MyDragAndDropSwipeableContainer dropCompAbove = MyDragAndDropSwipeableContainer.findMyDDContStartingFrom(parentContAbove);
                        ItemAndListCommonInterface objAbove = dropCompAbove != null
                                ? (ItemAndListCommonInterface) dropCompAbove.getDragAndDropObject()
                                : null;

//<editor-fold defaultstate="collapsed" desc="comment">
//                        String txt = "cont=" + (compAbove != null ? compAbove.getName() : "nullx")
//                                + "\ndropTarget=" + (dropTarget != null ? dropTarget.getName() : "nullx")
//                                + "\nresponder=" + (responder != null ? responder.getName() : "nullx")
//                                + "\nclosest=" + (closest != null ? closest.getName() : "nullx")
//                                + "\nparent=" + (parentContAbove != null ? parentContAbove.getName() : "nullx")
//                                + "\ndrop=" + (dropCompAbove != null ? dropCompAbove.getName() : "nullx")
//                                + "\nelt.text=" + (objAbove != null ? objAbove.getText() : "nullx");
//</editor-fold>
                        String txt = "cont=" + disp(compAbove)
                                + "\ndropTarget=" + disp(dropTarget)
                                + "\nresponder=" + disp(responder)
                                + "\nclosest=" + disp(closest)
                                + "\nparent=" + disp(parentContAbove)
                                + "\ndrop=" + disp(dropCompAbove)
                                + "\nelt.text=" + (objAbove != null ? objAbove.getText() : "nullx");
                        labelInfo.setText(txt);
                        labelCoord.setText("(x[0],y[0])=(" + x[0] + "," + y[0] + ")");
//                        fPinchOut.revalidate();
//                        getComponentForm().revalidate();
                        labelCoord.repaint();
                        labelInfo.repaint();
                    }
                };
                pinchForm.setLayout(BorderLayout.center());
                pinchForm.setPinchInsertEnabled(false);
                pinchForm.getContentPane().setName("ContentPane");
                pinchForm.getToolbar().setBackCommand(Command.createMaterial("", Icons.iconBackToPreviousScreen, (e) -> ScreenRepair.this.showBack()));

                Container cont = new Container(BoxLayout.y());
                cont.setScrollableY(true);
                cont.setName("Container.y");
                for (Item item : new Item[]{new Item("item1 5m", true), new Item("item22 2h1", true), new Item("item3", true),
                    new Item("item4 3h", true), new Item("item5 5min", true)}) {
                    Component itemCont = ScreenListOfItems.buildItemContainer(ScreenRepair.this, item, null, null);
                    itemCont.setName("itemCont:" + item.getText());
                    Container contCont = new Container(BorderLayout.center());
                    contCont.setName("WrapContNorth (" + item.getText() + ")");
                    contCont.addComponent(BorderLayout.NORTH, itemCont);
                    cont.addComponent(contCont);
                }
                pinchForm.addComponent(BorderLayout.CENTER, cont);

                Container south = new Container(BoxLayout.y());
                south.setName("SouthCont");
                south.addAll(labelCoord, labelInfo);
                pinchForm.addComponent(BorderLayout.SOUTH, south);
                labelCoord.setName("LabelCoord");
                labelInfo.setName("LabelInfo");
                pinchForm.show();
            }
        }));

//<editor-fold defaultstate="collapsed" desc="comment">
//        content.add(new Button(new Command("Move workslots into owners' list") {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                List<WorkSlot> unallocated = DAO.getInstance().initWorkSlotListsForWorkSlotOwners();
//                if (unallocated.size() > 0 && Dialog.show("", "Delete WorkSlots with no owner (" + unallocated + ")?", "OK", null)) {
//                    for (WorkSlot workSlot : unallocated) {
//                        Log.p("Deleting: "+workSlot);
//                        DAO.getInstance().delete(workSlot);
//                    }
//                }
//            }
//        }));
//</editor-fold>
        content.add(new Button(Command.create("Native logs", null, (e9) -> {
//            Form hi = new MyForm("Native Logs Reader", BoxLayout.y());
            Form hi = new MyForm("Native Logs Reader", null, null);

            hi.getToolbar().setBackCommand(Command.createMaterial("", Icons.iconBackToPreviousScreen, (e) -> ScreenRepair.this.showBack()));
            String logs = NativeLogs.getNativeLogs();
            TextArea textArea = new TextArea(logs);
            hi.add(BorderLayout.CENTER, textArea);
//<editor-fold defaultstate="collapsed" desc="comment">
//            hi.getToolbar().addSearchCommand((e) -> {
//                String txt = (String) e.getSource();
//                boolean searchOnLowerCaseOnly;
////                    if (!txt.equals(txt.toLowerCase()))
//                searchOnLowerCaseOnly = txt.equals(txt.toLowerCase()); //if search string is all lower case, then search on lower case only, otherwise search on
////                    Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
//                Container compList = hi.getContentPane();
//                boolean showAll = txt == null || txt.length() == 0;
//                for (int i = 0, size = textArea.getLines(); i < size; i++) {
//                    //https://www.codenameone.com/blog/toolbar-search-mode.html:
//                    if (searchOnLowerCaseOnly) {
//                        compList.getComponentAt(i).setHidden(((String) textArea.get(i)).toLowerCase().indexOf(txt) < 0);
//                    } else {
//                        compList.getComponentAt(i).setHidden(((String) textArea.get(i)).indexOf(txt) < 0);
//                    }
//                }
//                compList.animateLayout(150);
//            });
//</editor-fold>
            hi.show();
        })));

        content.add(new Button(Command.create("Play all built-in sounds", null, (e9) -> {
            Display.getInstance().isBuiltinSoundAvailable(SCREEN_TITLE);
            Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_ALARM);
            Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_BUTTON_PRESS);
            Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_CONFIRMATION);
            Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_ERROR);
            Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_INFO);
            Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_WARNING);
        })));

//        content.add(new Button(MyCommand.create("Force current time", null, (e) -> {
        content.add(new Button(Command.create("Force current time", null, (e) -> {
            MyForm fd = new MyForm("Set current time", ScreenRepair.this, () -> {
            });
            fd.setLayout(BoxLayout.y());
            fd.getToolbar().setBackCommand(Command.createMaterial("", Icons.iconBackToPreviousScreen, (e2) -> ScreenRepair.this.showBack()));
            fd.add(new Label("Current shift:"));
            fd.add(new Label(MyDate.formatDurationStd(MyDate.getCurrentTimeShift())));
            fd.add(new Label("Current time:"));
            MyDateAndTimePicker picker = new MyDateAndTimePicker(new Date(MyDate.currentTimeMillis()));
            picker.addActionListener(evt -> {
                MyDate.setCurrentTime(picker.getDate().getTime());
                fd.revalidateWithAnimationSafety();
            });
//            fd.add(BoxLayout.encloseX(new Label("Current time:"), new Label())picker);
            fd.add(picker);
            Button reset = new Button(Command.create("Reset to normal time", null, (e3) -> {
                MyDate.resetCurrentTime();
                fd.revalidateWithAnimationSafety();
            }));
            fd.add(reset);
            fd.show();
        })));

        content.add(new Button(Command.create("xxxRun tests menu", null, (e) -> {
            new ScreenRunTests(ScreenRepair.this).show();
        })));

        content.add(new Button(new Command("Storage location info") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String[] roots = FileSystemStorage.getInstance().getRoots();
                String str = "FileSystemStorage.getInstance().getRoots()=\n";
                for (String s : roots) {
                    str += s + "\n";
                }
                for (String s : roots) {
                    str += s + "\n";
                }
                str += "FileSystemStorage.getInstance().getAppHomePath()=" + FileSystemStorage.getInstance().getAppHomePath();
                str += "FileSystemStorage.getInstance().getCachesDir()=" + FileSystemStorage.getInstance().getCachesDir();
                Dialog.show("Info", str, "OK", null);
            }
        }));
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            content.add(new Button(Command.create("Test ScreenEdit2", null, (e) -> {
////            @Override
////            public void actionPerformed(ActionEvent evt) {
//                new ScreenItem2(new Item(), ScreenRepair.this, () -> {
//                }, false, null).show();
//            }
//            )));
//        }
//</editor-fold>

        content.add(new Button(new Command("Show files in FileSystemStorage") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new ScreenFileSystemTree().show();
            }
        }));

        content.add(
                new Button(new Command("Simulate notification", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (false) {
                            Date alarm = new Date(System.currentTimeMillis() + MyDate.DAY_IN_MILLISECONDS * 14);
                            Item item = new Item("TestAlarm on " + alarm, 25, new Date(System.currentTimeMillis() + MyDate.DAY_IN_MILLISECONDS * 28));
                            item.setAlarmDate(alarm);
//                            DAO.getInstance().saveNew(item, true);
                            DAO.getInstance().saveNew(item);
                            DAO.getInstance().saveNewExecuteUpdate();
                            alarm = new Date(System.currentTimeMillis() + MyDate.DAY_IN_MILLISECONDS * 12);
                            item = new Item("TestWaitingAlarm on " + alarm, 25, new Date(System.currentTimeMillis() + MyDate.DAY_IN_MILLISECONDS * 28));
                            item.setWaitingAlarmDate(alarm);
//                            DAO.getInstance().saveNew(item, true);
                            DAO.getInstance().saveNew(item);
                            DAO.getInstance().saveNewExecuteUpdate();
                        }
                        switch (3) {
                            case 1:
                                AlarmHandler.getInstance().simulateNotificationReceived_TEST(15);
                                break;
                            case 2:
                                //test alarm and waiting on same time
                                AlarmHandler.getInstance().simulateNotificationReceived_TEST("test2", MyDate.makeDate(20), 15, 15);
                                break;
                            case 3:
                                //test two alarms expiring on same time
                                Date alarm = MyDate.makeDate(20);
                                AlarmHandler.getInstance().simulateNotificationReceived_TEST("test1 in 30s", null, MyDate.makeDate(30), null);
                                AlarmHandler.getInstance().simulateNotificationReceived_TEST("test2 in 30s", null, MyDate.makeDate(30), null);
                                AlarmHandler.getInstance().simulateNotificationReceived_TEST("test3 in 60s", null, MyDate.makeDate(60), null);
                                break;
                        }
//                        AlarmHandler.getInstance().simulateNotificationReceived_TEST(60);
                    }
                }
                ));

        content.add(
                new Button(new Command("Simulate LocalNotif reception", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (AlarmHandler.getInstance().getLocalNotificationsTEST().size() > 0) {
                            AlarmHandler.getInstance().localNotificationReceived(AlarmHandler.getInstance().getLocalNotificationsTEST().get(0).notificationId);
                        } else {
                            Dialog.show("INFO", "No pending local notifications", "OK", null);
                        }
                    }
                }
                ));

        content.add(
                new Button(new Command("Update AlarmHandler", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        AlarmHandler.getInstance().setupAlarmHandlingOnAppStart();
                    }
                }
                ));

        content.add(
                new Button(new Command("Refresh first alarms", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        List<Item> itemsWithAlarms = DAO.getInstance().getItemsWithAlarms(10000, new Date(0), new Date(0), new Date(0), 10000);
                        List<Item> updated = new ArrayList();
                        for (int i = 0, size = itemsWithAlarms.size(); i < size; i++) {
                            Item expItem = itemsWithAlarms.get(i);
                            Date oldFirstAlarm = expItem.getNextcomingAlarmFromParseN();
                            expItem.updateNextcomingAlarm();//update the first alarm to new value (or null if no more alarms). NB! Must update even when no first alarm (firstFutureAlarm returns null)
                            Date newFirstAlarm = expItem.getNextcomingAlarmFromParseN(); //optimization: this statement and next both call Item.getAllFutureAlarmRecordsSorted() which is a bit expensive
                            if ((oldFirstAlarm == null && newFirstAlarm != null) //newFirst now defined
                                    || (newFirstAlarm == null && oldFirstAlarm != null) //oldFirst now invalid
                                    || (newFirstAlarm != null && oldFirstAlarm != null && oldFirstAlarm.getTime() != newFirstAlarm.getTime()) //First has changed
                                    ) {
                                updated.add(expItem); //save for a ParseServer update whether now null or with new value
                            }
                        }

                        // save the updated Items in a batch //optimization: do this as background task to avoid blocking the event thread
                        if (!updated.isEmpty()) {
                            try {
                                ParseBatch parseBatch = ParseBatch.create();
                                parseBatch.addObjects(updated, ParseBatch.EBatchOpType.UPDATE);
                                parseBatch.execute();
                            } catch (ParseException ex) {
                                Log.e(ex);
                            }
                        }
                    }
                }
                ));

        content.add(new Button(new Command("Show local notifications") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Form form = new MyForm("Local notifiations", null, null);
                form.setLayout(BoxLayout.y());
                form.getToolbar().setBackCommand(Command.createMaterial("", Icons.iconBackToPreviousScreen, (e) -> ScreenRepair.this.showBack()));
                LocalNotificationsShadowList list = AlarmHandler.getInstance().getLocalNotificationsTEST();
                for (int i = 0, size = list.size(); i < size; i++) {
                    form.addComponent(new SpanLabel(list.get(i).toString()));
                }
                form.show();
            }
        }));

        content.add(new Button(new Command("Show Today badge count") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                long start = System.currentTimeMillis(); //time query
                List<ItemAndListCommonInterface> todayList = DAO.getInstance().getToday();
                long end = System.currentTimeMillis();

                List<ItemAndListCommonInterface> todayLeafList = DAO.getTodayLeafTaskList(todayList);

//                Dialog.show("INFO", "Today Badge Count = " + DAO.getInstance().getBadgeCount(true, true), "OK", null);
                Dialog.show("INFO", "Today Badge Count = " + todayLeafList.size() + "; millis=" + (end - start)
                        + "\nLEAF TASKS(" + todayLeafList.size() + ")= [" + todayLeafList + "]"
                        + "\nTOPLEVEL(" + todayList.size() + ")=[" + todayList + "]",
                        "OK", null);
            }
        }));

//        content.add(new Button(new Command("Show error log", Icons.get().iconSettingsLabelStyle) {
        content.add(new Button(new Command("Show error log", Icons.iconSettings) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                String log = Log.getInstance().getFileURL();
//                String log = Log.getLogContent();
//                if (Log.getInstance().isFileWriteEnabled()) {
//                if(Log.getInstance().getFileURL() == null) {
//                    Log.getInstance().setFileURL("file:///" + FileSystemStorage.getInstance().getRoots()[0] + "/codenameOne.log");
//                }
//</editor-fold>
                String text = "";
                ArrayList<String> list = new ArrayList();
//                if (Log.getInstance().getFileURL() != null) {

                //from here: http://stackoverflow.com/questions/39745935/how-to-read-textfile-line-by-line-into-textarea-in-codename-one
//                    InputStream is = Display.getInstance().getResourceAsStream(this.getClass(), "/b1.txt");
//                    InputStream is = Display.getInstance().getResourceAsStream(this.getClass(), Log.getInstance().getFileURL());
//            byte[] read = com.codename1.io.Util.readInputStream(Storage.getInstance().createInputStream("CN1Log__$"));
                try {
                    InputStream is = Storage.getInstance().createInputStream("CN1Log__$");
                    //String s = com.codename1.util.StringUtil.readToString(is, "UTF-8");
                    String s;
                    s = com.codename1.io.Util.readToString(is, "UTF-8");
                    for (String line : StringUtil.tokenize(s, '\n')) {
                        // line represents each line in the file...
                        list.add(line);
                    }
                } catch (IOException ex) {
                    Log.e(ex);
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                    Reader r;
//                    try {
//                        r = new InputStreamReader(FileSystemStorage.getInstance().openInputStream(Log.getInstance().getFileURL()));
//                        char[] buffer = new char[1024];
//                        int size = r.read(buffer);
//                        while (size > -1) {
//                            String t = new String(buffer, 0, size);
//                            text += t; //new String(buffer, 0, size);
//                            size = r.read(buffer);
//                        }
//                        r.close();
//                    } catch (IOException ex) {
////                            Logger.getLogger(ScreenRepair.class.getName()).log(Level.SEVERE, null, ex);
//                        Log.e(ex);
//                    }
//                }
//</editor-fold>
//                Command showMoreCmd = new Command("Show 10");
//                int startLineNb = list.size();
//                do {
//                    startLineNb -= 10;
//                } while (Dialog.show("Last log lines", new com.codename1.ui.List(list.subList(startLineNb, list.size()).toArray()),
//                        new Command[]{new Command("Exit"), startLineNb > 0 ? showMoreCmd : new Command("No more")}) == showMoreCmd && startLineNb > 0);
                MyForm logForm = new MyForm("Log", ScreenRepair.this, () -> {
                }) {
                    public void refreshAfterEdit() {
                    }
                ;
                };
                logForm.setLayout(BoxLayout.y());
                logForm.setScrollableY(true);
//                logForm.getToolbar().addSearchCommand((e) -> {
//                    String txt = (String) e.getSource();
//                    boolean searchOnLowerCaseOnly;
////                    if (!txt.equals(txt.toLowerCase()))
//                    searchOnLowerCaseOnly = txt.equals(txt.toLowerCase()); //if search string is all lower case, then search on lower case only, otherwise search on 
////                    Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
//                    Container compList = logForm.getContentPane();
//                    boolean showAll = txt == null || txt.length() == 0;
//                    for (int i = 0, size = list.size(); i < size; i++) {
//                        //https://www.codenameone.com/blog/toolbar-search-mode.html:
//                        if (searchOnLowerCaseOnly) {
//                            compList.getComponentAt(i).setHidden(((String) list.get(i)).toLowerCase().indexOf(txt) < 0);
//                        } else {
//                            compList.getComponentAt(i).setHidden(((String) list.get(i)).indexOf(txt) < 0);
//                        }
//                    }
//                    compList.animateLayout(ANIMATION_TIME_FAST);
//                }, MyPrefs.defaultIconSizeInMM.getFloat());

                logForm.getToolbar().addSearchCommand(new MySearchCommand(logForm.getContentPane(), (e) -> {
                    String txt = (String) e.getSource();
                    boolean searchOnLowerCaseOnly;
//                    if (!txt.equals(txt.toLowerCase()))
                    searchOnLowerCaseOnly = txt.equals(txt.toLowerCase()); //if search string is all lower case, then search on lower case only, otherwise search on 
//                    Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
                    Container compList = logForm.getContentPane();
                    boolean showAll = txt == null || txt.length() == 0;
                    for (int i = 0, size = list.size(); i < size; i++) {
                        //https://www.codenameone.com/blog/toolbar-search-mode.html:
                        if (searchOnLowerCaseOnly) {
                            compList.getComponentAt(i).setHidden(((String) list.get(i)).toLowerCase().indexOf(txt) < 0);
                        } else {
                            compList.getComponentAt(i).setHidden(((String) list.get(i)).indexOf(txt) < 0);
                        }
                    }
                    compList.animateLayout(ANIMATION_TIME_FAST);
                }));

                Container c = null;
                for (String s : list) {
                    c = new SpanLabel(s);
                    logForm.addComponent(c);
                }
//                logForm.getToolbar().addCommandToLeftBar(logForm.makeDoneCommandWithNoUpdate());
                logForm.getToolbar().addCommandToLeftBar(logForm.makeDoneUpdateWithParseIdMapCommand());
                logForm.show();
                logForm.scrollComponentToVisible(c); //scroll down to show last line in list
            }
        }
        ));

        content.add(
                new Button(new Command("Send error log", Icons.iconSettings) {
                    @Override
                    public void actionPerformed(ActionEvent evt
                    ) {
//                String log = Log.getLogContent();
                        if (Dialog.show("Send log", "", "OK", "Cancel")) {
//                            Log.sendLog();
                            DAO.emailLog(evt);
                        }
                    }
                }
                ));

        content.add(new Button(new Command("Show device info") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showDeviceInfo().show();
            }
        }));

        content.add(new Button(new Command("Show built-in fonts") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                makeShowBuiltinFontsForm().show();
            }
        }));

//        content.add(new Button(new Command("Show system properties") {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                showSystemPropertiesXXXX().show();
//            }
//        }));
//        content.add(
//                new Button(new Command("Delete Timer storage", Icons.get().iconSettingsLabelStyle) {
//                    @Override
//                    public void actionPerformed(ActionEvent evt
//                    ) {
//                        if (Dialog.show("Timers", "Reset Timers (delete Timer storage)", "OK", "Cancel")) {
//                            ScreenTimer2.getInstance().deleteTimerInfoInStorage();
//                        }
//                    }
//                }
//                ));
        content.add(new Button(new Command("Show localization info") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showLocalizationInfo().show();
            }
        }));

//        content.add(
//                new Button(new Command("Delete Timer storage", Icons.get().iconSettingsLabelStyle) {
//                    @Override
//                    public void actionPerformed(ActionEvent evt
//                    ) {
//                        if (Dialog.show("Timers", "Reset Timers (delete Timer storage)", "OK", "Cancel")) {
//                            ScreenTimer2.getInstance().deleteTimerInfoInStorage();
//                        }
//                    }
//                }
//                ));
        content.add(
                new Button(new Command("Delete all local storage", Icons.iconSettings) {
                    @Override
                    public void actionPerformed(ActionEvent evt
                    ) {
//                    (Dialog.show(FIRST, new com.codename1.ui.List(datesVector),
//                            new Command[]{new Command("Exit"),
//                                tempDateList.hasMoreDates() ? showMore : new Command("No more")}) == showMore && tempDateList.hasMoreDates())
//                Command okCmd = new Command("OK");
                        String res = "";
                        for (String str : Storage.getInstance().listEntries()) {
                            res += str + "\n";
                        }
//                        new com.codename1.ui.List(Storage.getInstance().listEntries()),
//                        new com.codename1.ui.List((Object[])Storage.getInstance().listEntries()),
//                        new com.codename1.ui.List(Arrays.asList(Storage.getInstance().listEntries())),
//                if (Dialog.show("Local storage content", new com.codename1.ui.List(Storage.getInstance().listEntries()), new Command("OK"), new Command("Cancel"))) {
                        if (Dialog.show("Delete this content?", res, "OK", "Cancel")) {
                            Storage.getInstance().clearStorage();
                            Storage.getInstance().clearCache();
                            //TODO!! does app need to restart after deleting all content? Do any of the features using local storage assume a file is there if it was there earlier?
//                            TodoCatalystParse.exit();
                        }
                    }
                }
                ));

//        content.add(
//                new Button(new Command("Edit RepeatRule", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
//                    @Override
//                    public void actionPerformed(ActionEvent evt
//                    ) {
//                        RepeatRuleParseObject repeatRule = new RepeatRuleParseObject();
//                        repeatRule.setSpecifiedStartDate(new Date(System.currentTimeMillis() + MyDate.HOUR_IN_MILISECONDS * 48).getTime());
//                        new ScreenRepeatRule("test", repeatRule, new Item("taskX", 15, new Date(System.currentTimeMillis() + MyDate.HOUR_IN_MILISECONDS * 24)), 
//                                (MyForm) content.getComponentForm(), () -> {
//                        }, true, null, null, false).show();
//                    }
//                }
//                ));
//        Button doubleOwnerButton = new Button(new Command("Showtasks belonging to more than one list") {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                List<Item> list = DAO.getInstance().debugGetItemsInMultipleLists();
//                Form f = new Form("Select");
//                for (Item item : list) {
//                    f.add(new Button(Command.create(item.getText(), null, (e) -> {
//                    })));
//                }
//            }
//        });
//        content.add(doubleOwnerButton);
//
//        Button noOwnerButton = new Button(new Command("Find tasks without an owner") {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//            }
//        });
//        content.add(noOwnerButton);
//
//        content.add(new Button(new Command("Show tasks with a category but category does not contain task")));
//        content.add(new Button(new Command("Show categories with tasks but category not in task")));
//        content.add(new Button(Command.create("Show items in another list than the item's ownerList", null, (e) -> {
//
//        })));
//        content.add(new Button(new Command("Show items with both ownerItem and ownerList")));
        return content;
    }
}

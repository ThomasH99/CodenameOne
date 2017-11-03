package com.todocatalyst.todocat;

//import com.codename1.io.Log;
import com.codename1.components.MultiButton;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.Resources;
import com.parse4cn1.ParseException;
import java.io.IOException;
import java.util.Map;
//import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;
import com.parse4cn1.ParseUser;
import com.sun.scenario.effect.impl.prism.sw.PSWDrawable;
import java.util.HashMap;

//import com.codename1.ui.*;
//import com.codename1.ui.events.ActionEvent;
//import com.codename1.ui.layouts.BoxLayout;
//import com.codename1.ui.table.TableLayout;
//import com.codename1.ui.util.Resources;
//import com.parse4cn1.ParseException;
//import java.io.IOException;
//import java.util.Map;
/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenLogin extends MyForm {

    MyForm mainScreen;

    ScreenLogin(MyForm mainScreen) throws ParseException, IOException {
        super("Login");
        this.mainScreen = mainScreen;
        // we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
        // we use border layout so the list will take up all the available space
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        form.setToolbar(new Toolbar());
//        addCommandsToToolbar(form.getToolbar(), theme);
        buildContentPane(getContentPane());
    }

//    public static void addCommandsToToolbar(Toolbar toolbar, Resources theme) {
//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
//    }
    /**
     * This method shows the main user interface of the app
     *
     * @param back indicates if we are currently going back to the main form
     * which will display it with a back transition
     * @param errorMessage an error message in case we are returning from a
     * search error
     * @param listings the listing of alternate spellings in case there was an
     * error on the server that wants us to prompt the user for different
     * spellings
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    private Container buildContentPane(Container content) {
//        Container content = new Container();
        TableLayout tl;
        if (Display.getInstance().isTablet() || !Display.getInstance().isPortrait()) {
            tl = new TableLayout(7, 2);
        } else {
            tl = new TableLayout(14, 1);
        }
        tl.setGrowHorizontally(true);
        content.setLayout(tl);
        String loginId = ""; //use email as login id
        String psword = "";
        TextField login = new TextField(loginId, "Email address", 20, TextArea.EMAILADDR);
//        login.setSingleLineTextArea(true);
        content.add("Email").add(login);
        TextField password = new TextField(psword, "Password", 20, TextArea.ANY);
        content.add("Password").add(password);

        Validator v = new Validator();
        v.addConstraint(login, RegexConstraint.validEmail("Please enter valid email address (xxx@yyy.zzz)"));
        v.addConstraint(password, new LengthConstraint(2, "Password must be at least 2 characters"));

        Button loginButton = new Button(new Command("Login") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
//                    ParseUser parseUser = ParseUser.create(loginId,psword);
                    ParseUser parseUser = ParseUser.create(login.getText(), password.getText());
                    parseUser.login();//perform login
                } catch (ParseException ex) {
                    Log.p("***login failed***");
                    Log.e(ex);
                }
                mainScreen.show();
            }
        });
        content.add(loginButton);

        Button createUserButton = new Button(new Command("Create my account") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
//                    ParseUser parseUser = ParseUser.create(loginId,psword);
                    ParseUser parseUser = ParseUser.create(login.getText(), password.getText());
                    parseUser.setEmail(login.getText()); //
                    parseUser.signUp();//perform login
                    parseUser.save(); //save this new user
                    //TODO: user must confirm email before syncing(?)
                } catch (ParseException ex) {
                    Log.p(ex.getMessage());
                    Log.e(ex);
                }
                mainScreen.show();
            }
        });
        content.add(createUserButton);

        Button createSkipButton = new Button(new Command("Skip") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                mainScreen.show();
            }
        });
        content.add(createSkipButton);

        Button masterLoginButton = new Button(new Command("MasterLogin") {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
                 mainScreen.show();
            }
        });
//        v.addSubmitButtons(loginButton); //disable Login button until both valid 
        content.add(masterLoginButton);
        return content;
    }
}

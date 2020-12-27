//require CodeRAD
package com.codename1.samples;


import static com.codename1.ui.CN.*;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Toolbar;
import java.io.IOException;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.io.NetworkEvent;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import static com.codename1.rad.models.EntityTypeBuilder.entityTypeBuilder;
import com.codename1.rad.models.Tag;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.UI;
import com.codename1.rad.ui.entityviews.EntityListView;
import com.codename1.rad.ui.entityviews.ProfileListView;
import com.codename1.ui.FontImage;
import com.codename1.ui.layouts.BorderLayout;

/**
 * A CodeRAD sample with an action that changes text and UIID depending on model state.  This sample
 * was created for <a href="https://github.com/shannah/CodeRAD/issues/16">this issue</a>
 * 
 * <p>This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.</p>
 */
public class RADActionBoundUIIDSample {

    private Form current;
    private Resources theme;
    
    public void init(Object context) {
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });        
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        Form hi = new Form("Toggled Actions Sample", new BorderLayout());
        
        // Create a tag fo the online status property.
        Tag TAG_ONLINE = new Tag("online");
        
        // Create an action that will indicte the online/offline status
        ActionNode status = UI.action(
                
                //Label toggles between "Offline" and "Online" depending
                // on state of TAG_ONLINE property.
                UI.label(person -> {
                    if (person.isFalsey(TAG_ONLINE)) {
                        return "Offline";
                    } else {
                        return "Online";
                    }
                }),
                
                // UIID toggles between LoggedOutStatusButton and LoggedInStatusButton
                // Depending on state of TAG_ONLINE property
                UI.uiid(person -> {
                    if (person.isFalsey(TAG_ONLINE)) {
                        return "LoggedOutStatusButton";
                    } else {
                        return "LoggedInStatusButton";
                    }
                }),
                
                // Icon for the action
                UI.icon(FontImage.MATERIAL_PERSON),
                
                // You could use the condition directive to conditionally
                // display or not display the action.  In this case we
                // define it to always return true so action is always visible.
                UI.condition(person -> {
                    return true;
                })
        );
        
        // A User entity we use for the models.
        class User extends Entity {}
        entityTypeBuilder(User.class)
                .Boolean(TAG_ONLINE)
                .string(Thing.name)
                .factory(cls -> {return new User();})
                .build();
        
        
        // Create an entity list that will hold several users.
        EntityList el = new EntityList();
        for (int i=0; i<200; i++) {
            User u = new User();
            u.set(Thing.name, "User "+i);
            u.set(TAG_ONLINE, i % 2 == 0);
            el.add(u);
        }
        
        // The ListNode is a wrapper that will be passed to our View so that
        // they can access our action.
        ListNode node = new ListNode(
                
                // Register our "status" action in teh ACCOUNT_LIST_ROW_ACTIONS
                // category which is used by ProfileListView to display actions
                // for each row.
                UI.actions(ProfileListView.ACCOUNT_LIST_ROW_ACTIONS, status)
        );
        
        // Use a ProfileListView to display all of the users
        // https://shannah.github.io/CodeRAD/javadoc/com/codename1/rad/ui/entityviews/ProfileListView.html
        ProfileListView plv = new ProfileListView(el, node, 8);
        plv.setScrollableY(true);
        
        // In order to respond to events raised by the action, our view needs to be wrapped
        // in a controller.  Normally our form would have a FormViewController so we could
        // just use FormController, but this sample is compressed to be inside 
        // a single method here so we'll create a dedicated ViewController for the list
        ViewController ctrl = new ViewController(null);
        ctrl.setView(plv);
        ctrl.addActionListener(status, evt->{
            // The action was pressed by the user
            // Update the model's online status
            User u = (User)evt.getEntity(); 
            u.set(TAG_ONLINE, u.isFalsey(TAG_ONLINE)); 
            
            // This will trigger a property change in the model which will update the view.
        });
        
        
        hi.add(CENTER, plv);
        
        hi.show();
    }

    public void stop() {
        current = getCurrentForm();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = getCurrentForm();
        }
    }
    
    public void destroy() {
    }

}

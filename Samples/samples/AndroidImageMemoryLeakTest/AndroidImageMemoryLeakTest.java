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
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename
 * One</a> for the purpose of building native mobile applications using Java.
 */
public class AndroidImageMemoryLeakTest {

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
            if (err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });
    }

    public void start() {
        if (current != null) {
            current.show();
            return;
        }
        Form hi = new Form("Hi World", BoxLayout.y());
        Button b = new Button("Open Image Form");
        b.addActionListener(evt->{
            new ImageForm().show();
        });
        hi.add(b);
        hi.show();
    }

    public void stop() {
        current = getCurrentForm();
        if (current instanceof Dialog) {
            ((Dialog) current).dispose();
            current = getCurrentForm();
        }
    }

    public void destroy() {
    }

}

class ImageForm extends Form {

    public ImageForm() {
        super(new BorderLayout());

        Container container = new Container(new GridLayout(2, 2));
        Button button = new Button("Empty Form");
        Image image = com.codename1.ui.util.Resources.getGlobalResources().getImage("chicken-sandwich.jpg");

        for (int i = 0; i < 4; i++) {
            Container c = new Container();

            c.getAllStyles().setBgImage(image);
            c.getAllStyles().setBackgroundType(Style.BACKGROUND_IMAGE_ALIGNED_CENTER);
            c.getAllStyles().setBorder(RoundRectBorder.create());

            container.add(c);
        }

        button.addActionListener((ev) -> new ImageForm().show());

        add(BorderLayout.CENTER, container);
        add(BorderLayout.SOUTH, BoxLayout.encloseXCenter(button));
    }
}

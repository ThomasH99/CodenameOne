//require cn1-websockets
package com.codename1.samples;


import com.codename1.components.SpanLabel;
import static com.codename1.ui.CN.*;
import com.codename1.io.websocket.WebSocket;
import com.codename1.io.websocket.WebSocketState;
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
import com.codename1.ui.CN;
import com.codename1.ui.util.UITimer;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class WebSocketReconnectTest {

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
        String uniqueId = "1234";
        Form hi = new Form("WebSocket Test", BoxLayout.y());
        hi.getToolbar().addCommandToRightBar("Start Test", null, evt->{
            hi.removeAll();
            String url = "wss://weblite.ca/ws/cn1-websocket-demo/chat";
            if (!"HTML5".equals(CN.getPlatformName())) {
                url = "wss://chat.mydissent.net/wsMsg";
            }
            WebSocket.setDebugLoggingEnabled(true);
            final WebSocket sock = new WebSocket(url) {
                @Override
                protected void onOpen() {
                    Log.p("WebSocket onOpen - Sending uniqueId: " + uniqueId);
                    hi.add("WebSocket onOpen - Sending uniqueId: " + uniqueId);
                    hi.animateLayout(400);
                    send(uniqueId);
                }

                @Override
                protected void onClose(int statusCode, String reason) {
                    Log.p("WebSocket onClose");
                    hi.add("WebSocket onClose");
                    hi.animateLayout(400);
                }

                @Override
                protected void onMessage(String message) {
                    Log.p("WebSocket onMessage: " + message);
                    hi.add(new SpanLabel("WebSocket onMessage: " + message));
                    hi.animateLayout(400);
                }

                @Override
                protected void onMessage(byte[] message) {
                    Log.p("WebSocket onMessage (byte[]): " + message.toString());
                }

                @Override
                protected void onError(Exception ex) {
                    Log.e(ex);
                    hi.add(new SpanLabel("WebSocket onError:\n" + ex.getMessage()));
                }
            };
            sock.connect();
            sock.autoReconnect(5000);

            UITimer.timer(5000, false, hi, () -> {
                sock.close();
            });
        });
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
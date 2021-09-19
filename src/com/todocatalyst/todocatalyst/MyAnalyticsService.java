/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *  
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Codename One through http://www.codenameone.com/ if you 
 * need additional information or have any questions.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.ui.Display;
import com.codename1.ui.Form;

/**
 * <p>
 * The analytics service allows an application to report its usage, it is
 * seamlessly invoked by GUI builder applications if analytics is enabled for
 * your application but can work just as well for handcoded apps!</p>
 * <p>
 * To enable analytics just use the
 * {@link #init(java.lang.String, java.lang.String)} method of the analytics
 * service. For most typical usage you should also invoke the
 * {@link #setAppsMode(boolean)} method with {@code true}. If you are not using
 * the GUI builder invoke the visit method whenever you would like to log a page
 * view event.</p>
 *
 *
 * @author Shai Almog
 */
public class MyAnalyticsService {

    private static MyAnalyticsService instance;

    private static boolean appsMode = true;

    /*
    references:
    https://support.google.com/analytics/answer/1033068?hl=en
    https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#apptracking
    https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters
    https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#apptracking
    https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#exception
     */
    /**
     * Indicates whether analytics server failures should brodcast an error
     * event
     *
     * @return the failSilently
     */
    public static boolean isFailSilently() {
        return failSilently;
    }

    /**
     * Indicates whether analytics server failures should brodcast an error
     * event
     *
     * @param aFailSilently the failSilently to set
     */
    public static void setFailSilently(boolean aFailSilently) {
        failSilently = aFailSilently;
    }

    /**
     * Apps mode allows improved analytics using the newer google analytics API
     * designed for apps
     *
     * @return the appsMode
     */
    public static boolean isAppsMode() {
        return appsMode;
    }

    /**
     * Apps mode allows improved analytics using the newer google analytics API
     * designed for apps. Most developers should invoke this method with
     * {@code true}.
     *
     * @param aAppsMode the appsMode to set
     */
    public static void setAppsMode(boolean aAppsMode) {
        appsMode = aAppsMode;
    }

    private String agent;
    private String domain;
    private static boolean failSilently = true;
    private static boolean enableLocalTrace = false;
    private static boolean enableAnalytics = false;

    /**
     * Indicates whether analytics is enabled for this application
     *
     * @return true if analytics is enabled
     */
    public static boolean isEnabled() {
        return (instance != null && instance.isAnalyticsEnabled());
    }

    /**
     * Indicates if the analytics is enabled, subclasses must override this
     * method to process their information
     *
     * @return true if analytics is enabled
     */
    protected boolean isAnalyticsEnabled() {
        return agent != null;
    }

    /**
     * Initializes google analytics for this application
     *
     * @param agent the google analytics tracking agent
     * @param domain a domain to represent your application, commonly you should
     * use your package name as a URL (e.g. com.mycompany.myapp should become:
     * myapp.mycompany.com)
     */
    public static void init(String agent, String domain) {
        init(agent, domain, true, false);
    }

    public static void init(String agent, String domain, boolean enableAnalytics, boolean enableLocalTrace) {
        if (instance == null) {
            instance = new MyAnalyticsService();
        }
        instance.agent = agent;
        instance.domain = domain;
        instance.enableLocalTrace = enableLocalTrace; //when true, no analytics will be send, but the analytic content will be traced locally in the Log
        instance.enableAnalytics = enableAnalytics; //when true, no analytics will be send, but the analytic content will be traced locally in the Log
    }

    /**
     * Allows installing an analytics service other than the default
     *
     * @param i the analytics service implementation.
     */
    public static void init(MyAnalyticsService i) {
        instance = i;
    }

    private String clean(String s) {
        return MyUtil.cleanToSingleLineNoSpacesString(s);
    }

    /**
     * Sends an asynchronous notice to the server regarding a page in the
     * application being viewed, notice that you don't need to append the URL
     * prefix to the page string.
     *
     * @param page the page viewed
     * @param referer the source page
     */
    public static void visit(String page, String referer) {
        instance.visitPage(page, referer);
    }

    /**
     * Subclasses should override this method to track page visits
     *
     * @param page the page visited
     * @param referer the page from which the user came
     */
    private void visitPage(String page, String referer) {
//        if (MyPrefs.disableGoogleAnalytics.getBoolean()) {
//            return;
//        }
        if (appsMode) {
            // https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#apptracking
            ConnectionRequest req = GetGARequest();
//            req.addArgument("t", "appview");
            req.addArgument("t", "screenview");
            req.addArgument("an", Display.getInstance().getProperty("AppName", "Codename One App"));
            String version = Display.getInstance().getProperty("AppVersion", "1.0");
            req.addArgument("av", version);
//            String cleanedPage = clean(page); //remove spaces etc
            String cleanedPageId = clean(page);
            req.addArgument("cd", cleanedPageId);
            if (false) { //no support for referer in 
                String cleanReferer = clean(referer);
                req.addArgument("cd", cleanReferer);
            }
//            if (!Config.FULLY_LOCAL_STORAGE)
            if (enableLocalTrace) { //            Log.p("Analytics VISIT: " + req.getRequestBody());
                Log.p("Analytics VISIT: CleanedPage=" + cleanedPageId);// + " Ref=" + cleanReferer);
            }
            if (enableAnalytics) {
                NetworkManager.getInstance().addToQueue(req);
            }
        } else {
            String url = Display.getInstance().getProperty("cloudServerURL", "https://codename-one.appspot.com/") + "anal";
            ConnectionRequest r = new ConnectionRequest();
            r.setUrl(url);
            r.setPost(false);
            r.setFailSilently(failSilently);
            r.addArgument("guid", "ON");
            r.addArgument("utmac", instance.agent);
            r.addArgument("utmn", Integer.toString((int) (System.currentTimeMillis() % 0x7fffffff)));
            if (page == null || page.length() == 0) {
                page = "-";
            }
            r.addArgument("utmp", page);
            if (referer == null || referer.length() == 0) {
                referer = "-";
            }
            r.addArgument("utmr", referer);
            r.addArgument("d", instance.domain);
            r.setPriority(ConnectionRequest.PRIORITY_LOW);
//            if (!Config.FULLY_LOCAL_STORAGE)
            if (enableLocalTrace) { //            Log.p("Analytics VISIT: " + req.getRequestBody());
                Log.p("Analytics VISIT: Page=" + page);// + " Ref=" + cleanReferer);
            }
            if (enableAnalytics) {
                NetworkManager.getInstance().addToQueue(r);
            }
        }
    }

    /**
     *
     * @param eventCategory can be null
     * @param eventAction can be null
     * @param eventLabel can be null
     * @param eventValue only send if >=0
     */
    private void eventHit(String eventCategory, String eventAction, String eventLabel, int eventValue) {
//        if (MyPrefs.disableGoogleAnalytics.getBoolean()) {
//            return;
//        }
//        if(appsMode) {
        // https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#apptracking
        ConnectionRequest req = GetGARequest();
        req.addArgument("t", "event");
        if (true) { //not clear if used, but probably
            req.addArgument("an", Display.getInstance().getProperty("AppName", "Codename One App"));
            String version = Display.getInstance().getProperty("AppVersion", "1.0");
            req.addArgument("av", version);
        }
//&ec=video        // Event Category. Required.
//&ea=play         // Event Action. Required.
//&el=holiday      // Event label.
//&ev=300          // Event value.
//        if (eventCategory!=null&&eventCategory.length()>0) 
        String ecCleaned = eventCategory != null ? clean(eventCategory) : null;
        if (ecCleaned != null) { //            req.addArgument("ec", clean(eventCategory));
            req.addArgument("ec", ecCleaned);
        }
        String eaCleaned = eventAction != null ? clean(eventAction) : null;
        if (eaCleaned != null) {
            req.addArgument("ea", eaCleaned);
        }
        String elCleaned = eventLabel != null ? clean(eventLabel) : null;
        if (elCleaned != null) {
            req.addArgument("el", elCleaned);
        }
        if (eventValue >= 0) {
            req.addArgument("ev", Integer.toString(eventValue));
        }

//        Log.p("Analytics EVENT: " + req.getRequestBody()); //return null
//        if (MyPrefs.logGoogleAnalytics.getBoolean()) {
//            Log.p("Analytics EVENT: " + "Cat=" + ecCleaned + " Act=" + eaCleaned + " Lab=" + elCleaned + " Val=" + eventValue);
//        }
//        if (!Config.FULLY_LOCAL_STORAGE)
        if (enableLocalTrace) { //            Log.p("Analytics VISIT: " + req.getRequestBody());
            Log.p("Analytics HIT: Cat=" + eventCategory + " Act=" + eventAction + " Lbl=" + eventLabel + " Val=" + eventValue);// + " Ref=" + cleanReferer);
        }
        if (enableAnalytics) {
            NetworkManager.getInstance().addToQueue(req);
        }
    }

    static void event(String eventCategory, String eventAction, String eventLabel, int eventValue) {
        instance.eventHit(eventCategory, eventAction, eventLabel, eventValue);
    }

    static void event(MyForm sourceForm, String eventAction, String eventLabel, int evenValue) {
//        instance.eventHit(sourceForm != null ? sourceForm.getTitle() : "<NoForm>", eventAction, eventLabel, evenValue);
        instance.eventHit(sourceForm != null ? ((MyForm) sourceForm).getUniqueFormId() : "<NoForm>", eventAction, eventLabel, evenValue);
    }

    static void event(MyForm sourceForm, String eventAction) {
        event(sourceForm, eventAction, null, -1);
    }

    static void event(String eventAction) {
//        event((MyForm) Display.getInstance().getCurrent(), eventAction == null || eventAction.length() == 0 ? "<none?!>" : eventAction, null, -1);
//        event((MyForm) MyForm.getCurrentFormAfter ClosingDialogOrMenu(), eventAction == null || eventAction.length() == 0 ? "<none?!>" : eventAction, null, -1);
        Form f = Display.getInstance().getCurrent();
        event(f instanceof MyForm ? (MyForm) f : null, eventAction == null || eventAction.length() == 0 ? "<none?!>" : eventAction, null, -1);
    }

    /**
     * In apps mode we can send information about an exception to the analytics
     * server
     *
     * @param t the exception
     * @param message up to 150 character message,
     * @param fatal is the exception fatal
     */
    public static void sendCrashReport(Throwable t, String message, boolean fatal) {
//        if (MyPrefs.disableGoogleAnalytics.getBoolean() || !appsMode) {
//            return;
//        }

        // https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#exception
        ConnectionRequest req = GetGARequest();
        req.addArgument("t", "exception");

//        req.addArgument("t", "screenview");
        req.addArgument("an", Display.getInstance().getProperty("AppName", "Codename One App"));
        String version = Display.getInstance().getProperty("AppVersion", "1.0");
        req.addArgument("av", version);

//        System.out.println(message);
//max 150 bytes: https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters: "exd" 
        String messageCleaned = message.substring(0, Math.min(message.length(), 150 - 1) - 1); //TODO!!! extract only relevant/core data from stack trace (app version, line number, class.method)
        req.addArgument("exd", messageCleaned);
        if (fatal) {
            req.addArgument("exf", "1");
        } else {
            req.addArgument("exf", "0");
        }

        if (enableLocalTrace) { //            Log.p("Analytics VISIT: " + req.getRequestBody());
//        Log.p("Analytics CRASH: " + req.getRequestBody());
            Log.p("Analytics CRASH: " + "Throwable=\"" + t + "\", Msg (len=" + messageCleaned.length() + ")=\"" + messageCleaned + "\", Fatal=\"" + (fatal ? "YES\"" : "no\""));
        }
//        if (!Config.FULLY_LOCAL_STORAGE)
        if (enableAnalytics) {
            NetworkManager.getInstance().addToQueue(req);
        }
    }

    private static ConnectionRequest GetGARequest() {
        ConnectionRequest req = new ConnectionRequest();
        req.setUrl("https://www.google-analytics.com/collect");
        req.setPost(true);
        req.setFailSilently(true);
        req.addArgument("v", "1");
        req.addArgument("tid", instance.agent);
//        long uniqueId = Log.getUniqueDeviceId();
        String uniqueId = Log.getUniqueDeviceKey();
//        req.addArgument("cid", String.valueOf(uniqueId));
        req.addArgument("cid", uniqueId);
        return req;
    }
}

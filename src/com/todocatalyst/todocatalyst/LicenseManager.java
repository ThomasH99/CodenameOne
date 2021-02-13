/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Dialog;
import java.util.Date;
import java.util.HashMap;

/**
 *
 *
 * @author thomashjelm
 */
public class LicenseManager {
    //TODO each key can have its own expiration date (allow for trial features)
    //both server and client must know the secret key - how to initiatlize it at the server side?

    private static LicenseManager INSTANCE;
    private Date expirationDate;
    private HashMap keys = new HashMap();

    public static LicenseManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LicenseManager();
        }
        return INSTANCE;
    }

    private LicenseManager() {
    }

    public enum LicensedFeature {

        FeatureA("39mfj*'0I2"),
        FeatureB("39mfj*'0I2");

        String key;

        LicensedFeature(String secretKey) {
            this.key = secretKey;
        }
    }

    public boolean isLicensed(LicensedFeature feature) {
//        return System.currentTimeMillis() < expirationDate.getTime() && keys.get(feature.key) != null;
        return MyDate.currentTimeMillis() < expirationDate.getTime() && keys.get(feature.key) != null;
    }

    /**
     * return the customer visible feature description (localized)
     *
     * @param featureName
     * @return
     */
    private String featureDescription(String featureName) {
        return "";
    }

    /**
     * return the customer visible feature name (localized)
     *
     * @param featureName
     * @return
     */
    private String featureVisibleName(String featureName) {
        return featureName;
    }

    /**
     * return the (list of) name(s) of the subscriptions that enable this
     * feature
     *
     * @param featureName
     * @return
     */
    private String featureSubscriptionName(String featureName) {
        return featureName;
    }

    /**
     * returns true if the featureName is enabled and can be executed/launched
     * by the code
     *
     * @param featureName
     * @return
     */
    public boolean check(String featureName) {
        switch (featureName) {
            case "test":
                Dialog.show("Paid feature", Format.f("{0} is a paid feature which is part of {2}. \n\n{1}\n\n Click Subscribe to visit the subscription page, or Return to continue.", 
                        featureDescription(featureName), featureVisibleName(featureName), featureSubscriptionName(featureName)),
                        "Subscribe", "Return");
                return false;
            default:
                return true;
        }
    }

}

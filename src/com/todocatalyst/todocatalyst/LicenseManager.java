/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

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

    private Date expirationDate;
    private HashMap keys = new HashMap();

    public enum LicensedFeature {

        FeatureA("39mfj*'0I2"),
        FeatureB("39mfj*'0I2");

        String key;

        LicensedFeature(String secretKey) {
            this.key = secretKey;
        }
    }
    
    

    public boolean isLicensed(LicensedFeature feature) {
        return System.currentTimeMillis() < expirationDate.getTime() && keys.get(feature.key) != null;
    }

}

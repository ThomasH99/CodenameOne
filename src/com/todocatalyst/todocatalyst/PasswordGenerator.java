/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.util.regex.RE;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Thomas
 */
public final class PasswordGenerator {
    //code from here: http://stackoverflow.com/questions/19743124/java-password-generator

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
    private boolean useLower;
    private boolean useUpper;
    private boolean useDigits;
    private boolean usePunctuation;

    private static PasswordGenerator INSTANCE;

    public static PasswordGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PasswordGenerator();
        }
        return INSTANCE;
    }

    private PasswordGenerator() {
//        throw new UnsupportedOperationException("Empty constructor is not supported.");
//        this(new PasswordGeneratorBuilder());
    }

//    private PasswordGenerator(PasswordGeneratorBuilder builder) {
    private PasswordGenerator(boolean useLower, boolean useUpper, boolean useDigits, boolean usePunctuation) {
        this.useLower = useLower;
        this.useUpper = useUpper;
        this.useDigits = useDigits;
        this.usePunctuation = usePunctuation;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public static class PasswordGeneratorBuilder {
//
//        private boolean useLower;
//        private boolean useUpper;
//        private boolean useDigits;
//        private boolean usePunctuation;
//
//        public PasswordGeneratorBuilder() {
////            this.useLower = false;
////            this.useUpper = false;
////            this.useDigits = false;
////            this.usePunctuation = false;
//            this.useLower = true;
//            this.useUpper = true;
//            this.useDigits = true;
//            this.usePunctuation = true;
//        }
//
//        /**
//         * Set true in case you would like to include lower characters
//         * (abc...xyz). Default false.
//         *
//         * @param useLower true in case you would like to include lower
//         * characters (abc...xyz). Default false.
//         * @return the builder for chaining.
//         */
//        public PasswordGeneratorBuilder useLower(boolean useLower) {
//            this.useLower = useLower;
//            return this;
//        }
//
//        /**
//         * Set true in case you would like to include upper characters
//         * (ABC...XYZ). Default false.
//         *
//         * @param useUpper true in case you would like to include upper
//         * characters (ABC...XYZ). Default false.
//         * @return the builder for chaining.
//         */
//        public PasswordGeneratorBuilder useUpper(boolean useUpper) {
//            this.useUpper = useUpper;
//            return this;
//        }
//
//        /**
//         * Set true in case you would like to include digit characters (123..).
//         * Default false.
//         *
//         * @param useDigits true in case you would like to include digit
//         * characters (123..). Default false.
//         * @return the builder for chaining.
//         */
//        public PasswordGeneratorBuilder useDigits(boolean useDigits) {
//            this.useDigits = useDigits;
//            return this;
//        }
//
//        /**
//         * Set true in case you would like to include punctuation characters
//         * (!@#..). Default false.
//         *
//         * @param usePunctuation true in case you would like to include
//         * punctuation characters (!@#..). Default false.
//         * @return the builder for chaining.
//         */
//        public PasswordGeneratorBuilder usePunctuation(boolean usePunctuation) {
//            this.usePunctuation = usePunctuation;
//            return this;
//        }
//
//        /**
//         * Get an object to use.
//         *
//         * @return the {@link gr.idrymavmela.business.lib.PasswordGenerator}
//         * object.
//         */
//        public PasswordGenerator build() {
//            return new PasswordGenerator(this);
//        }
//    }
//</editor-fold>
    /**
     * This method will generate a password depending the use* properties you
     * define. It will use the categories with a probability. It is not sure
     * that all of the defined categories will be used.
     *
     * @param length the length of the password you would like to generate.
     * @return a password that uses the categories you define when constructing
     * the object with a probability.
     */
    public String generate(String prefix, int length) {
        return generate(prefix, length, true, true, true, true);
    }

    public String generateNumbers(String prefix, int length) {
        return generate(prefix, length, false, false, true, false);
    }

    public String generate() {
        return generate("", 12, true, true, true, true);
    }

    public String generate(String prefix) {
//        return generate(prefix,12);
        return generate(prefix, 12, true, true, true, true);
    }

    public String generate(int length) {
//        return generate(length, null);
        return generate("", length, true, true, true, true);
    }

    public String generate(String prefix, int length, boolean useLower, boolean useUpper, boolean useDigits, boolean usePunctuation) {
        // Argument Validation.
        if (length <= 0) {
            return "";
        }

        // Variables.
//        StringBuilder password = new StringBuilder(length);
        String password = "";

//        Random random = str!=null? new Random(System.nanoTime()*str.hashCode()):new Random(System.nanoTime());
//        Random random = seed != null ? new Random(System.currentTimeMillis() * seed.hashCode()) : new Random(System.currentTimeMillis());
        Random random = new Random(System.currentTimeMillis() * System.currentTimeMillis() * 99);

        // Collect the categories to use.
        List<String> charCategories = new ArrayList<>(4);
        if (useLower) {
            charCategories.add(LOWER);
        }
        if (useUpper) {
            charCategories.add(UPPER);
        }
        if (useDigits) {
            charCategories.add(DIGITS);
        }
        if (usePunctuation) {
            charCategories.add(PUNCTUATION);
        }

        // Build the password.
        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
//            password.append(charCategory.charAt(position));
            password += charCategory.charAt(position);
        }
//        return new String(password);
        return prefix != null ? prefix + password : password;
    }

    //make them static to only initialize once for the whole app
    private static RE priority = new RE("[p|P][19]"); //eg "p1", "P9" but not "P0"

    /**
     * https://www.javacodeexamples.com/check-password-strength-in-java-example/668
     *
     * @param password
     * @return
     */
    static int calculatePasswordStrength(String password) {

        //total score of password
        int iPasswordScore = 0;

//        if (password.length() < 8) {
        if (password.length() < 6) {
            iPasswordScore = 0;
        } else if (password.length() >= 10) {
            iPasswordScore += 2;
        } else {
            iPasswordScore += 1;
        }

        //if it contains one digit, add 2 to total score
        RE regExp = new RE("(?=.*[0-9]).*");
//                if( password.matches("(?=.*[0-9]).*") )
        if (regExp.match(password)) {
            iPasswordScore += 2;
        }

        //if it contains one lower case letter, add 2 to total score
        regExp = new RE("(?=.*[a-z]).*");
//        if( password.matches("(?=.*[a-z]).*") )
        if (regExp.match(password)) {
            iPasswordScore += 2;
        }

        //if it contains one upper case letter, add 2 to total score
        regExp = new RE("(?=.*[A-Z]).*");
//        if( password.matches("(?=.*[A-Z]).*") )
        if (regExp.match(password)) {
            iPasswordScore += 2;
        }

        //if it contains one special character, add 2 to total score
        regExp = new RE("(?=.*[~!@#$%^&*()_-]).*");
//        if( password.matches("(?=.*[~!@#$%^&*()_-]).*") )
        if (regExp.match(password)) {
            iPasswordScore += 2;
        }

        return iPasswordScore;
    }

    static int calculatePasswordLevel(String password) {
        int strength = calculatePasswordStrength(password);
        if (strength > 8) {
            return 2;
        } else if (strength > 4) {
            return 1;
        } else {
            return 0;
        }
    }
    static String calculatePassword(String password) {
        int strength = calculatePasswordStrength(password);
        if (strength > 8) {
            return "High"; //9+
        } else if (strength > 4) {
            return "Med"; //5-8
        } else {
            return "Low"; //0-4
        }
    }
}

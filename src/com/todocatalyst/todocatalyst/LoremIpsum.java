/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package de.svenjacobs.loremipsum;
package com.todocatalyst.todocatalyst;

import com.codename1.util.StringUtil;
import java.util.Random;

/**
 *
 * @author thomashjelm
 */
//public class LoremIpsum {
/* Copyright (c) 2008 Sven Jacobs

   Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in
   all copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
   THE SOFTWARE. 
 */
//http://loremipsum.sourceforge.net
//https://sourceforge.net/p/loremipsum/code/HEAD/tree/trunk/src/main/java/de/svenjacobs/loremipsum/LoremIpsum.java#l122
//THJ changes: used CN1 tokenize, make the calls static
//TODO more text, random selection of words, avoid commas, transform to sentence case.
/**
 * Simple lorem ipsum text generator.
 *
 * <p>
 * Suitable for creating sample data for test cases and performance tests.
 * </p>
 *
 * @author Sven Jacobs
 * @version 1.0
 */
public class LoremIpsum {

    public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    private static String[] loremIpsumWords;
    private static String[] loremIpsumWords2;
    private static Random r = new Random(System.currentTimeMillis());

    public LoremIpsum() {
//    this.loremIpsumWords = LOREM_IPSUM.split( "\\s" );
//      for (String line : StringUtil.tokenize(projectText, '\n'))
//        this.loremIpsumWords = (String[]) StringUtil.tokenize(LOREM_IPSUM, "\\s").toArray();
        this.loremIpsumWords = (String[]) StringUtil.tokenize(LOREM_IPSUM, "\\s").toArray();
        this.loremIpsumWords2 = (String[]) StringUtil.tokenize(LOREM_IPSUM.toLowerCase(), " ,").toArray();
    }

    /**
     * Returns one sentence (50 words) of the lorem ipsum text.
     *
     * @return 50 words of lorem ipsum text
     */
    public static String getWords() {
        return getWords(50);
    }

    public static String getRandomWord() {
        return loremIpsumWords2[r.nextInt(loremIpsumWords2.length)];
    }

    /**
     * Returns words from the lorem ipsum text.
     *
     * @param amount Amount of words
     * @return Lorem ipsum text
     */
    public static String getWords(int amount) {
        return getWords(amount, 0);
    }

    public static String getPhrase(int nbWords) {
        String s = null;
        for (int i = 0; i < nbWords; i++) {
            getRandomWord();
            if (s == null) {
                s = getRandomWord();
                s = s.toUpperCase().charAt(0) + s.substring(1); //quick hack to make first letter uppercase (sentence case)
            } else {
                s += " " + getRandomWord();
            }
        }
        return s;
    }

    public static String getPhrase(int nbWordsMin, int nbWordsMax) {
        return getPhrase(nbWordsMin + r.nextInt(nbWordsMax - nbWordsMin + 1));
    }

    public static String getPhrase() {
        return getPhrase(3, 12);
    }

    /**
     * Returns words from the lorem ipsum text.
     *
     * @param amount Amount of words
     * @param startIndex Start index of word to begin with (must be >= 0 and <
     * 50) @ return Lorem ipsum text @throws IndexOutOfBoundsException If
     * startIndex is < 0 or > 49
     */
    public static String getWords(int amount, int startIndex) {
        if (startIndex < 0 || startIndex > 49) {
            throw new IndexOutOfBoundsException("startIndex must be >= 0 and < 50");
        }

        int word = startIndex;
        StringBuilder lorem = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            if (word == 50) {
                word = 0;
            }

            lorem.append(loremIpsumWords[word]);

            if (i < amount - 1) {
                lorem.append(' ');
            }

            word++;
        }

        return lorem.toString();
    }

    /**
     * Returns two paragraphs of lorem ipsum.
     *
     * @return Lorem ipsum paragraphs
     */
    public String getParagraphs() {
        return getParagraphs(2);
    }

    /**
     * Returns paragraphs of lorem ipsum.
     *
     * @param amount Amount of paragraphs
     * @return Lorem ipsum paragraphs
     */
    public String getParagraphs(int amount) {
        StringBuilder lorem = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            lorem.append(LOREM_IPSUM);

            if (i < amount - 1) {
                lorem.append("\n\n");
            }
        }

        return lorem.toString();
    }
}

package edu.hkbu.util.stringutil;

import java.util.Locale;

public class StringSearch {

    public static boolean containsIgnoreCase(String tar, String str) {
        tar = tar.toLowerCase();
        str = str.toLowerCase();
        return str.contains(tar);
    }
}

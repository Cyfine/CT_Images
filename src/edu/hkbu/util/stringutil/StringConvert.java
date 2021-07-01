package edu.hkbu.util.stringutil;

public class StringConvert {

    public static boolean containsIgnoreCase(String tar, String str) {
        tar = tar.toLowerCase();
        str = str.toLowerCase();
        return str.contains(tar);
    }
}

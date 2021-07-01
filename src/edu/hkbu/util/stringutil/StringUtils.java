package edu.hkbu.util.stringutil;

import java.util.LinkedList;
import java.util.List;

public class StringUtils {

    public static boolean containsIgnoreCase(String tar, String str) {
        tar = tar.toLowerCase();
        str = str.toLowerCase();
        return str.contains(tar);
    }

    /**
     * @param str a String that may contains number characters
     * @return the number contained in the String, such as Se2Im30, it will return a list [2, 30]
     */
    public static List<Integer> extractNum(String str) {
        boolean isPrevNumChar = false;
        //indicate whether the previous char is a number character (0-9) in the for loop

        List<Integer> result = new LinkedList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {

            if (isNum(str.charAt(i))) {

                sb.append(str.charAt(i));
                isPrevNumChar = true;

                if (i == str.length() - 1) {
                    result.add(Integer.parseInt(sb.toString()));
                }

            } else {

                if (isPrevNumChar) {
                    //If isPrevNumChar true, means previous character is a num-char,
                    //the i just jump from a num-char to a non num-char
                    // the non-num char is the split of num-char,
                    //we should cast the num-char to integer and add it to result list.
                    result.add(Integer.parseInt(sb.toString()));
                    sb = new StringBuilder();
                    isPrevNumChar = false;
                }

            }

        }
        return result;
    }

    private static boolean isNum(char c) {
        return c > 47 && c < 58;
    }

    public static String prependZeroNum(int num, int strLength) {
        String str = "" + num;
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);
            // sb.append(str).append("0");
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }
}

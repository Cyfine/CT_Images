package edu.hkbu.util.stringutil;

import java.util.*;

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

    /**
     * as the data set is small, insertion sort is adopted
     *
     * @param str Strings contain indices, the method will extract
     *            all the numbers in a List
     * @param k   kth position in the list is used to sort the Strings
     */
    public void strIdxSort(String[] str, int k) {
        List<Integer> list;
        String collideIdentifier = "^&*%^&";
        String[] temp = new String[str.length];
        System.arraycopy(str, 0, temp, 0, str.length);
        List<Integer> separateChain = new LinkedList<>();
        HashMap<Integer, String> map = new HashMap<>();
        String tmp;
        for (int i = 0; i < str.length; i++) {
            list = extractNum(str[i]);
            tmp = map.put(list.get(k), str[i]);
            if (tmp != null) {
                separateChain.add(i - 1); // indicate the index of element that has hash collision
            }
        }
    }

    public void strSort(String[] str) {
        Arrays.sort(str, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int n1 = extractNum(o1).get(0);
                int n2 = extractNum(o2).get(0);
                return n1 - n2;
            }
        });
    }
}

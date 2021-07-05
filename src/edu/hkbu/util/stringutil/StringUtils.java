package edu.hkbu.util.stringutil;

import java.io.File;
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
        if(result.size() ==0){
            result.add(-1);
        }
        return result;
    }

    public static int extractNum(String str, int index){
        List<Integer> result = extractNum(str);
        if(result.size() < index + 1){
            return -1 ;
        }else{
            return result.get(index);
        }

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
    public static void strSort(String[] str, int k) {
        Arrays.sort(str, (o1, o2) -> {
            int n1 = extractNum(o1).get(k);
            int n2 = extractNum(o2).get(k);
            return n1 - n2;
        });
    }

    // some of the console tricks
    public static void printlnRed(String str) {
        System.out.println("\033[31m" + str);
    }

    public static void printlnGreen(String str) {
        System.out.println("\033[32m" + str);
    }

    public static void printRed(String str){
        System.out.print("\033[31m" + str);
    }

    public static void printGreen(String str){
        System.out.print("\033[32m" + str);
    }

    public static void main(String [] args){
        printlnGreen("Testing");
    }

    public static void main0(String[]args){
        File f = new File("/home/carter/Pictures/Confidential_Data/CT_images");
        File [] files = f.listFiles();
        LinkedList<String> fileList = new LinkedList<>();
        for(File file: files){
            if(file.isDirectory()){
                fileList.add(file.getName());
            }
        }

        fileList.sort((o1,o2) ->{
            int n1 = extractNum(o1).get(0);
            int n2 = extractNum(o2).get(0);
            return n1 - n2;
        });



//        strSort(fileName, 1);
        for(String str : fileList){
            System.out.println(str);
        }
    }
}

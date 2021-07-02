package edu.hkbu.app;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


public class Test {

    public static void main(String [] args){
//        File f = new File("D:/Confidential_Data/CT_images");
//        System.out.println(f.getParentFile());
//        System.out.println(f.isDirectory());
//        String[] fList = f.list();
//
//        for(String str : fList){
//            System.out.println(str);
//        }

        Integer [] ints = {9,8,7,6,5,4,3,2,1,0};

        Arrays.sort(ints, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1- o2;
            }
        });

        for(Integer num : ints){
            System.out.println(num);
        }

    }




}
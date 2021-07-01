package edu.hkbu.app;

import java.io.File;


public class Test {

    public static void main(String [] args){
        File f = new File("D:/Confidential_Data/CT_images");
        System.out.println(f.getParentFile());
        System.out.println(f.isDirectory());
        String[] fList = f.list();

        for(String str : fList){
            System.out.println(str);
        }






    }


}
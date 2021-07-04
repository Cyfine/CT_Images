package edu.hkbu.app;


import java.io.File;

public class Test {

    public static void main(String [] args){
        File f = new File("D:\\Confidential_Data\\CT_images\\HEP0001\\Se2Im30.jpg");
        System.out.println(f.getAbsolutePath());
    }



}
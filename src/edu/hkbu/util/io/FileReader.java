// The file Reader is designed to load large amount of CT images automatically
/*
The directory structure the FileReader is capable of:
CT_images
         |
         |
         |__HEP0001
         |       |___Se2Im30.jpg
         |       |___Se2Im31.jpg ...
         |__HEP0002
         |       |___Se3Im05.jpg
         |       |___Se3Im06.jpg ...
         |...

 */
package edu.hkbu.util.io;

import processing.core.PImage;

import java.util.List;

public class FileReader {
    private String dir;
    private List<List<Integer>> imageSet;


    public FileReader(String dir){
        this.dir = dir;
    }


    /*
    The inner class CT_Volume, to encapsulate essential information of a CT_volume in a single
    Object
     */
    public static class CT_Volume{
        List<PImage> images ;
        List<>
    }
}

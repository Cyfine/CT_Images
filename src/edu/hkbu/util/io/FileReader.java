
/*
The file Reader is designed to load large amount of CT images automatically
The information in stored in the FileReader instance
The directory structure accepted by the FileReader:
CT_images (refer as master directory)
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

import org.json.JSONObject;
import processing.core.PImage;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static edu.hkbu.util.io.JSONProcessor.*;

import static edu.hkbu.util.stringutil.StringUtils.extractNum;
import static org.apache.commons.io.FilenameUtils.getExtension;


public class FileReader {
    private final String dir;  // The master directory
    private File masterFile;
    private List<List<Integer>> imageSet;
    private List<File> directories;
    private List<CT_Volume> volumes;

    public static void main(String[] args) {
        FileReader reader = new FileReader("D:\\Confidential_Data\\CT_images");
        for (File f : reader.directories) {
            System.out.println(f.getName());
        }
    }

    public FileReader(String dir) {
        this.dir = dir;
        File masterFile = new File(dir);
        File[] files = masterFile.listFiles();
        directories = new LinkedList<>();


        for (File f : files) {
            if (f.isDirectory()) {
                directories.add(f);
            }
        }
        directories.sort(Comparator.comparingInt(o -> extractNum(o.getName()).get(0)));
        // sort the File in ascending order
    }

    public void getVolumes() {
        for (File folder : directories) {

            File[] f = folder.listFiles();
            List<File> imgFile = new LinkedList<>(); // contains all file in the folder, folder may contains multiple volumes
            List<List<File>> volumes;
            List<CTag> tags = new LinkedList<>();

            for (File file : f) {
                if (getExtension(file.getName()).equals("png")) {
                    imgFile.add(file);
                } else if (getExtension(file.getName()).equals("json")) {
                    tags.add(new CTag(new JSONObject(file.getAbsolutePath())));
                }
            }
        }


    }

    public HashMap<Integer, List<File>> separateVolumeIdx(List<File> imgFile) {

        HashMap<Integer, List<File>> map = new HashMap<>();

        String fName;
        int volumeIdx;
        for (File file : imgFile) {
            fName = file.getName();
            volumeIdx = extractNum(fName).get(0);
            if (map.containsKey(volumeIdx)) {
                map.get(volumeIdx).add(file);
            } else {
                LinkedList<File> newList = new LinkedList<>();
                newList.add(file);
                map.put(volumeIdx, newList);
            }
        }

        return map;

    }



    public void readDirectories() {


    }

    /*
    The inner class CT_Volume, to encapsulate essential information of a CT_volume in a single
    Object
     */
    public static class CT_Volume {
        List<PImage> images;
        List<CTag> tags;
        String parentPath;
        String startImageName; // name of the first image in the CT volume

    }
}

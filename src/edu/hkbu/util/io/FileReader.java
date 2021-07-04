
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
import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.util.*;

import static edu.hkbu.util.io.JSONProcessor.CTag;
import static edu.hkbu.util.stringutil.StringUtils.extractNum;


public class FileReader extends PApplet {


    private final List<File> directories;
    private List<CT_Volume> volumes = new LinkedList<>();

    public static void main(String[] args) {
        FileReader reader = new FileReader("D:\\Confidential_Data\\CT_images");
        for (File f : reader.directories) {
            System.out.println(f.getName());
        }
    }

    public List<CT_Volume> getCTVolume() {
        return volumes;
    }

    public FileReader(String dir) {
        // The master directory
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
            List<List<File>> volumes = new LinkedList<>();
            List<CTag> tags = new LinkedList<>();

            for (File file : f) {
                if (getExtension(file.getName()).equals("png") || getExtension(file.getName()).equals("jpg")) {
                    imgFile.add(file);
                } else if (getExtension(file.getName()).equals("json")) {
                    tags.add(new CTag(new JSONObject(file.getAbsolutePath())));
                }
            }

            HashMap<Integer, List<File>> map = separateVolumeIdx(imgFile);

            List<File> currentVolume;
            for (Integer i : map.keySet()) {
                currentVolume = map.get(i);
                HashMap<String, List<File>> formatMap = separateFileType(currentVolume);
                if (formatMap.keySet().size() == 1) {
                    volumes.add(currentVolume);
                    // if the file in the list is homogeneous type, its has only a single volume
                    // add it to the volumes list
                } else {
                    for (String j : formatMap.keySet()) {
                        volumes.add(formatMap.get(i));
                    }
                }
            }

            List<File> vol;
            int volumes_size = volumes.size();
            for (int i = 0; i < volumes_size; i++) {
                vol = volumes.get(i);
                vol.sort(Comparator.comparingInt((o) -> extractNum(o.getName()).get(1)));

                //there may be volume that having the same group index but
                // belongs to different volume
                List<List<File>> subVolumes = separateFileIndex(vol);
                if (subVolumes.size() > 1) {
                    volumes.set(i, subVolumes.remove(0));
                    volumes.addAll(subVolumes);
                }
            }

            this.volumes.addAll(parseCT_Volume(volumes, tags));
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

    /**
     * separate different file type by the file extension
     *
     * @param files a List of files may contains different types of files
     * @return a Hash map which key is the certain file type (String of file extension), and each key binding
     * a list that contains all the files have the file type of its key
     */
    public HashMap<String, List<File>> separateFileType(List<File> files) {
        HashMap<String, List<File>> map = new HashMap<>();

        String fName;
        String typ;
        for (File file : files) {
            typ = getExtension(file.getName());

            if (map.containsKey(typ)) {
                map.get(typ).add(file);
            } else {
                List<File> newList = new LinkedList<>();
                newList.add(file);
                map.put(typ, newList);
            }
        }

        return map;
    }

    /**
     * The files should be presorted
     */
    public List<List<File>> separateFileIndex(List<File> files) {
        List<List<File>> volumes = new LinkedList<>();
        List<File> currentList = new LinkedList<>();
        volumes.add(currentList);

        int currentIdx;
        int prevIdx = extractNum(files.get(0).getName()).get(1);
        File currentFile;
        for (int i = 1; i < files.size(); i++) {
            currentFile = files.get(i);
            currentIdx = extractNum(currentFile.getName()).get(1);
            if (currentIdx - prevIdx > 1) {
                if (currentIdx - prevIdx == 2) {
                    // the gap between noncontinuous image cluster is two should be considered
                    // exists loss of image within single cluster
                    String fName = currentFile.getName();
                    String idxStr = "" + currentIdx;
                    int lastIdx = fName.lastIndexOf(idxStr);
                    String header = fName.substring(0, lastIdx - 1);
                    String extension = fName.substring(lastIdx + idxStr.length());
                    String lossName = header + (currentIdx - 1) + extension;
                    System.out.println("Image loss detected, image " + lossName + " not found.");

                } else {
                    currentList = new LinkedList<>();
                    volumes.add(currentList);
                }
            }
            currentList.add(currentFile);
        }
        return volumes;

    }

    public List<CT_Volume> parseCT_Volume(List<List<File>> volumes, List<CTag> tags) {
        List<CT_Volume> result = new LinkedList<>();
        List<PImage> images;
        List<CTag> temp;

        for (List<File> volume : volumes) {
            images = new LinkedList<>();
            temp = new LinkedList<>();

            for (File file : volume) {
                PImage newImage = loadImage(file.getAbsolutePath());
                images.add(newImage);
                for (int i = 0; i < tags.size(); i++) {
                    CTag tag = tags.get(i);
                    if (tag.getImagePath().equals(file.getName())) {
                        temp.add(tags.remove(i));
                        i--;

                    }
                }
            }

            CT_Volume newVol = new CT_Volume(volume.get(0).getParent(), volume.get(0).getName(),
                    volume.get(volume.size() - 1).getName(), images);
            newVol.addTag(temp);
            result.add(newVol);
        }

        return result;
    }

    /*
    The inner class CT_Volume, to encapsulate essential information of a CT_volume in a single
    Object
     */
    public static class CT_Volume {
        List<PImage> images;
        List<CTag> tags = new LinkedList<>();
        String parentPath;
        String startImageName; // name of the first image in the CT volume
        String endImageName;

        public CT_Volume(String parentPath, String startImageName, String endImageName, List<PImage> images) {
            this.images = new LinkedList<PImage>();
            this.tags = new LinkedList<>();
            this.parentPath = parentPath;
            this.startImageName = startImageName;
            this.endImageName = endImageName;
        }


        void addTag(CTag tag) {
            tags.add(tag);
        }

        void addTag(Collection<CTag> c) {
            tags.addAll(c);
        }

    }
}

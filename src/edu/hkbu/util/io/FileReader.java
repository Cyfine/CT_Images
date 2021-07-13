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
·
 */
package edu.hkbu.util.io;

import edu.hkbu.app.Analyzer;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static edu.hkbu.util.io.JSONProcessor.CTag;
import static edu.hkbu.util.stringutil.StringUtils.extractNum;

public class FileReader extends PApplet {

    private final List<File> directories;
    private final List<CT_Volume> VOLUMES = new LinkedList<>();

    public static void main(String[] args) {
        // List<CT_Volume> volumes = getCTVolume("D:\\Confidential_Data\\CT_Images");
    }

    public static List<CT_Volume> getCTVolume(String masterDir) {
        FileReader reader = new FileReader(masterDir);
        return reader.VOLUMES;
    }

    public List<CT_Volume> getCTVolume() {
        return VOLUMES;
    }

    /**
     * Load the file structure to List of java.io.File
     *
     * @param dir The parent directory of folders that contains CT volumes
     */
    public FileReader(String dir) {
        // The master directory
        File masterFile = new File(dir);
        File[] files = masterFile.listFiles();
        directories = new LinkedList<>();

        try {
            for (File f : files) {
                if (f.isDirectory()) {
                    directories.add(f);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Invalid directory, check file directory correctness.");
        }
        directories.sort(Comparator.comparingInt(o -> extractNum(o.getName()).get(0)));
        // sort the File in ascending order

        getVolumes();
    }

    /**
     * Load from the parent directory of folders that contains CT volumes, and add
     * volumes to class variable VOLUMES.
     */
    public void getVolumes() {
        int folderCnt = 0;
        for (File folder : directories) {
            folderCnt++;
            try {
                loadVolumeFromFolder(folder);
            } catch (OutOfMemoryError e) {
                System.out.println("Load too many images a time, exceed maximum heap size of JVM.");
                System.out.println("Try to set larger heap size for JVM.");
                System.out.printf("Last processed folder %s, %d folders failed to load. ", folder.getName(),
                        directories.size() - folderCnt);
                System.out.println("Program terminated.");
                System.exit(-1);
                break;
            }
        }
        System.out.printf("Total %d volumes in %d folders loaded to the memory.\n", VOLUMES.size(), folderCnt);

    }

    /**
     * Load and add CT volumes to class variable VOLUMES
     *
     * @param folder The folder contains CT volumes
     */
    private void loadVolumeFromFolder(File folder) {
        System.out.printf("Loading from folder %s\n", folder.getName());

        File[] f = folder.listFiles();
        List<File> imgFile = new LinkedList<>(); // contains all file in the folder, folder may contains multiple
        // volumes
        List<List<File>> volumes = new LinkedList<>();
        List<CTag> tags = new LinkedList<>();
        String fName = "";

        try {
            for (File file : f) {

                if (getExtension(file.getName()).equals("png") || getExtension(file.getName()).equals("jpg")) {
                    imgFile.add(file);
                } else if (getExtension(file.getName()).equals("json")) {
                    try {
                        fName = file.getName();
                        String content = FileUtils.readFileToString(file, "UTF-8");
                        tags.add(new CTag(new JSONObject(content)));
                    } catch (JSONException e) {
                        System.out.printf("Invalid JSON file %s.\n", fName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.printf("Empty directory %s\n", folder.getName());
        }

        if (imgFile.size() == 0) {
            System.out.printf("Folder %s do not contains CT volume\n", folder.getName());
            return;
        }

        HashMap<Integer, List<File>> map = separateVolumeId(imgFile);

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
                    volumes.add(formatMap.get(j));
                }
            }
        }

        List<File> vol;
        int volumes_size = volumes.size();
        for (int i = 0; i < volumes_size; i++) {
            vol = volumes.get(i);

            vol.sort(Comparator.comparingInt((o) -> extractNum(o.getName(), 1)));

            // there may be volume that having the same group index but
            // belongs to different volume
            List<List<File>> subVolumes = separateFileIndex(vol);
            if (subVolumes.size() > 1) {
                volumes.set(i, subVolumes.remove(0));
                volumes.addAll(subVolumes);
            }
        }

        int tagSize = tags.size();
        this.VOLUMES.addAll(parseCT_Volume(volumes, tags));

        String vs = "";
        String ts = "";
        if (volumes.size() > 1) {
            vs = "s";
        }

        if (tagSize > 1) {
            ts = "s";
        }

        System.out.printf("Total %d volume%s and %d tag%s loaded from folder %s\n\n", volumes.size(), vs, tagSize, ts,
                folder.getName());
        System.gc();
    }

    /**
     * Initially separate the CT volumes by the volume id.
     *
     * @param imgFile unprocessed list of image files
     * @return Grouped image files
     */
    private HashMap<Integer, List<File>> separateVolumeId(List<File> imgFile) {

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
     * @return a Hash map which key is the certain file type (String of file
     * extension), and each key binding a list that contains all the files
     * having correspond file type of its key
     */
    private HashMap<String, List<File>> separateFileType(List<File> files) {
        HashMap<String, List<File>> map = new HashMap<>();

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
     * This method will separate the images according to its sequential index.
     * Images with continuous index will be put into same group. There may be
     * image loss within single CT volume, when the index difference between two
     * adjacent images is 2. Also the image may have void image index, such as
     * "Se2Im.png". The image with void index will be ignored and reported in the
     * console.
     *
     * @param files List of presorted image files
     * @return A list of grouped image files.
     */
    private List<List<File>> separateFileIndex(List<File> files) {
        List<List<File>> volumes = new LinkedList<>();
        List<File> currentList = new LinkedList<>();
        volumes.add(currentList);
        boolean existInvalidVolume = false;

        currentList.add(files.get(0));
        int currentIdx;
        int prevIdx = extractNum(files.get(0).getName(), 1);
        if (prevIdx == -1) {
            System.out.printf("Image %s has void image index, images with void image index will be ignored.\n",
                    files.get(0).getName());
            existInvalidVolume = true;
        }

        File currentFile;
        for (int i = 1; i < files.size(); i++) {
            currentFile = files.get(i);
            currentIdx = extractNum(currentFile.getName(), 1);

            if (currentIdx - prevIdx > 1) {
                if (currentIdx - prevIdx == 2) {
                    // the gap between noncontinuous image cluster is two should be considered
                    // exists loss of image within single cluster
                    String fName = currentFile.getName();
                    String idxStr = "" + currentIdx;
                    int lastIdx = fName.lastIndexOf(idxStr);
                    String header = fName.substring(0, lastIdx);
                    String extension = fName.substring(lastIdx + idxStr.length());
                    String lossName = header + (currentIdx - 1) + extension;
                    System.out.println("Image loss detected, " + lossName + " not found.");

                } else {
                    currentList = new LinkedList<>();
                    volumes.add(currentList);
                }
            }
            currentList.add(currentFile);
            prevIdx = currentIdx;
        }
        if (existInvalidVolume) {
            volumes.remove(0);
        }
        return volumes;

    }

    /**
     * Parse the CT volumes represented using List<File> in to CT_Volume object,
     * match and embed the correspond tag to the CT_Volume object.
     * <p>
     * As this method contains loadImage() method, which load images on the disk,
     * this method may met several performance issues. Especially OutOfMemory error
     * (OOM) when load a large image set to the memory. When such issue occurs, try
     * to set larger maximum memory for JVM (vmArgs such as -Xmx8192m).
     * <p/>
     *
     * @param volumes The List of CT volume represented using List<File> in a single
     *                folder
     * @param tags    The JSON tags
     * @return List of a parsed CT_Volume object
     */
    private List<CT_Volume> parseCT_Volume(List<List<File>> volumes, List<CTag> tags) {
        List<CT_Volume> result = new LinkedList<>();
        List<PImage> images;
        HashMap<PImage, CTag> tagsMap;

        for (List<File> volume : volumes) {
            images = new LinkedList<>();
            tagsMap = new HashMap<>();

            for (File file : volume) {
                PImage newImage = loadImage(file.getAbsolutePath());
                images.add(newImage);
                for (int j = 0; j < tags.size(); j++) {
                    CTag tag = tags.get(j);
                    if (tag.getImagePath().equals(file.getName())) {
                        tagsMap.put(newImage, tags.remove(j));
                        j--;
                    }
                }
            }

            CT_Volume newVol = new CT_Volume(volume.get(0).getParentFile().getName(), volume.get(0).getName(),
                    volume.get(volume.size() - 1).getName(), images, volume, tagsMap);

            result.add(newVol);
        }

        return result;
    }

    /**
     * A Class used to encapsulate images and essential information of a CT volume.
     */
    public static class CT_Volume {

        private List<PImage> images;
        private HashMap<PImage, CTag> tags;
        private Analyzer analyzer = null;
        private List<File> imageFile = null;

        String parentPath;
        String startImageName; // name of the first image in the CT volume
        String endImageName;

        public CT_Volume(String parentPath, String startImageName, String endImageName, List<PImage> images) {
            this.images = new LinkedList<>();

            this.parentPath = parentPath;
            this.startImageName = startImageName;
            this.endImageName = endImageName;
            this.images = images;
        }

        public CT_Volume(String parentPath, String startImageName, String endImageName, List<PImage> images,
                         List<File> imageFile, HashMap<PImage, CTag> tagsMap) {
            this(parentPath, startImageName, endImageName, images);
            this.imageFile = imageFile;
            this.tags = tagsMap;

        }

        public String toString() {
            return parentPath + " " + startImageName + "~" + endImageName;
        }

        // getters
        public List<File> getFile() {
            return imageFile;
        }

        public List<PImage> getImages() {
            return images;
        }

        public String getImageName(int index) {
            return imageFile.get(index).getName();
        }

        public String getFolderName(){
            return imageFile.get(0).getParentFile().getName();
        }

        public CTag getTag(PImage img) {
            if (tags != null && tags.size() != 0)
                return tags.get(img);
            else return null;
        }

        public CTag getTag(int idx) {
            if (tags != null && tags.size() != 0)
                return tags.get(images.get(idx));
            else return null;
        }


        public Analyzer getAnalyzer() {
            return analyzer;
        }




        public void linkAnalyzer(Analyzer analyzer) {
            this.analyzer = analyzer;
        }
    }
}

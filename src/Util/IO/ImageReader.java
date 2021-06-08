package Util.IO;

import java.util.*;
import java.util.List;

import processing.core.*;

public class ImageReader extends PApplet {

    private List images = new LinkedList<PImage>();
    private String dataPath;
    private String header;
    private imgFormat format;

    /**
     * @param dir    the data path of the image file
     * @param header the header of the image file name
     * @param format the enumeration type for image format, png, jpg, jpeg, gif, tga is supported
     */
    public ImageReader(String dir, String header, imgFormat format) {
        dataPath = dir;
        this.header = header;
        this.format = format;
        readImages();
    }


    /**
     * A set of images in the directory with structured filename will be loaded to the program
     * For example, {img_01, img_02}, The header of the image file is "img_"
     *
     * @return List contains the image set, ordered by the index in the file name.
     */
    private void readImages() {
        images = new LinkedList<PImage>();
        String imgName;
        int cnt = 1;
        for (;;) {
            try {
                imgName = header + cnt++ + "." + format;
                PImage newImage = loadImage(dataPath  + '/'  + imgName);
                if(newImage == null){
                    System.out.println("File reading finished.");
                    break;
                }
                images.add(newImage);
            } catch (Exception e) {
                break;
            }
        }
    }

    public void setup() {

    }

    public void draw() {

    }

    public static void test() {
        ImageReader reader = new ImageReader("C:/Users/30421/Desktop/test", "test_", ImageReader.imgFormat.jpg);
        String[] appletArgs = {"ImageReader"};
        reader.main(appletArgs);
    }

    public String format() {
        return format.toString();
    }

    public List<PImage> getImages() {
        return images;
    }

    public enum imgFormat {
        png, jpg, jpeg, gif, tga
    }
}
package Util.IO;

import java.util.*;
import java.util.List;

import processing.core.*;

public class ImageReader extends PApplet {

    private List images = new LinkedList<PImage>();
    private String dataPath;
    private String header;
    String  format;
    private int startIndex = 1;

    /**
     * @param dir    the data path of the image file
     * @param header the header of the image file name
     * @param format the enumeration type for image format, png, jpg, jpeg, gif, tga is supported
     */
    public ImageReader(String dir, String header, String format) throws Exception {
        dataPath = dir;
        this.header = header;
        this.format = format;
        validateFormat(format);

    }

    public ImageReader(String dir, String header, String format, int startIndex) throws Exception {
        this(dir, header, format);
        this.startIndex = startIndex;
        readImages();
    }

    private void validateFormat(String format) throws Exception {
        format =format.toLowerCase();
        switch(format){
            case"png":  break;
            case"jpg":  break;
            case"jpeg":  break;
            case"gif":  break;
            case"tga":  break;
            default: throw new Exception("ImageReader: Invalid format");
        }
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
        int cnt = startIndex;
        boolean init = true;
        int minNumLength = 1;

        for (; ; ) {

                imgName = header + prependZeroNum(cnt++, minNumLength) + "." + format;
                PImage newImage = loadImage(dataPath + '/' + imgName);
                if (newImage == null) {
                    if(init){
                        minNumLength = 2;
                        continue;
                    }else{
                        break;
                    }

                }
                images.add(newImage);
                init = false;

        }
    }

    public static String prependZeroNum(int num, int strLength) {
        String str = "" +num;
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


    public static void test() throws Exception {
        ImageReader reader = new ImageReader("C:/Users/30421/Desktop/test", "test_", "jpg");
        String[] appletArgs = {"ImageReader"};
        reader.main(appletArgs);
    }

    public String format() {
        return format.toString();
    }

    public List<PImage> getImages() {
        return images;
    }

    public enum ImgFormat {
        png, jpg, jpeg, gif, tga
    }
}
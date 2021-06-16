
/*
Readme: This class is used to display CT images.
There are two display method. It can be displayed by either pass and List<PImage>
or passing the essential arguments to the constructor and load images it self.
 */
package Util.ImageUtil;

import Util.IO.ImageReader;
import Util.IO.ImageReader.ImgFormat;
import processing.core.*;

import java.util.*;


public class Processing extends PApplet {

    private List<PImage> images = new LinkedList<PImage>();
    private PImage currentImage;
    private Scanner in = new Scanner(System.in);
    private int imgIndex = 0;

    //class variable used to load images itself
    private String path;
    private String header;
    private String format;
    private int startIndex;

    public Processing(List<PImage> images) {
        List<PImage> newList = new ArrayList<PImage>();
        for (PImage img : images) {
            newList.add(img);
        }
        this.images = newList;
    }

    public Processing(String path, String header, String format, String startIndex) throws Exception {
        this.path = path;
        this.header = header;
        this.format = format;
        try {
            this.startIndex = Integer.parseInt(startIndex);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number format.");
        }


    }


    public void setup() {
        size(512, 512);
//        try {
//            images = loadImages(path, "header", format, startIndex);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void draw() {
        currentImage = images.get(imgIndex);
        image(currentImage, 0, 0);
        fill(255);
        text("" + imgIndex, 0, 10);

    }

    public void keyPressed() {
        if (keyCode == LEFT && imgIndex > 0) {
            imgIndex--;
        }
        if (keyCode == RIGHT && imgIndex < images.size() - 1) {
            imgIndex++;
        }
        if (keyCode == UP) {
            for (PImage img : images) {
                polarize(img);
            }
        }
        if (keyCode == DOWN) {
            setup();
        }


    }

    private void polarize(PImage image) {

        float greyScale;
        for (int i = 0; i < image.height; i++) {
            for (int j = 0; j < image.width; j++) {
                float r = red(image.get(i, j));
                float g = green(image.get(i, j));
                float b = blue(image.get(i, j));
                greyScale = (r + g + b) / 3;
                if (greyScale < 255)
                    image.set(i, j, color(0));
            }

        }
    }


    private static List<PImage> loadImages(String path, String header, String format, int startIndex) throws Exception {
        ImageReader reader = new ImageReader(path, header, format, startIndex);
        List<PImage> images = reader.getImages();
        for (PImage image : images) {
            image.loadPixels();
        }
        return images;
    }


    public static void displayImage(List<PImage> images) {

        String[] appletArgs = {"Processing"};
        Processing instance = new Processing(images);
        runSketch(appletArgs, instance);

    }

    public static void displayImage(String path, String header, String format, String index) throws Exception {
        String[] appletArgs = {"Processing"};
        Processing instance = new Processing(path, header, format, index);
        runSketch(appletArgs, instance);
    }


}

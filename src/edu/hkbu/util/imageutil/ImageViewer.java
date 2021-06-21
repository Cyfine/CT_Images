
/*
Readme: This class is used to display CT images.
There are two display method. It can be displayed by either pass and List<PImage>
or passing the essential arguments to the constructor and load images it self.
 */
package edu.hkbu.util.imageutil;

import edu.hkbu.util.io.ImageReader;
import processing.core.*;

import java.util.*;

import static edu.hkbu.util.imageutil.DHash.dHashing;

public class ImageViewer extends PApplet {

    private List<PImage> images = new LinkedList<PImage>();
    private PImage currentImage;
    private Scanner in = new Scanner(System.in);
    private int imgIndex = 0;
    private boolean singleVolume;
    private List imgSet;
    private List<int[]> dHash;

    //class variable used to load images itself
    private String path;
    private String header;
    private String format;
    private int startIndex;
    private String title;
    boolean hist = false;
    boolean hashKey = false;

    boolean hash_init = true;


    public ImageViewer(List<PImage> images) {
        List<PImage> newList = new ArrayList<PImage>();
        for (PImage img : images) {
            newList.add(img);
        }
        singleVolume = true;
        this.images = newList;
    }


    public ImageViewer(String path, String header, String format, String startIndex) throws Exception {
        this.path = path;
        this.header = header;
        this.format = format;
        try {
            this.startIndex = Integer.parseInt(startIndex);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number format.");
        }

    }

    public ImageViewer(List<PImage> images, String title) {
        this(images);
        this.title = title;

    }


    public void setup() {
        size(1024, 512);
        this.frame.setTitle(title);
//        try {
//            images = loadImages(path, "header", format, startIndex);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void draw() {
        background(255);
        currentImage = images.get(imgIndex);
        image(currentImage, 0, 0);
        fill(255);

        textSize(30);
        text("" + imgIndex, 0, 30);

        if (hist) {
            pushMatrix();
            translate(512, 0);
            fill(0);
            hist(currentImage, calHist(currentImage));
            popMatrix();
        }

        if (hashKey) {

            if (hash_init) {
                try {
                    dHash = dHashing(images);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            pushMatrix();
            translate(512, 0);

            showHashKey(dHash.get(imgIndex));
            popMatrix();
        }

        noLoop();
    }

    public int[] calHist(PImage img) {
        int[] hist = new int[256];
        for (int i = 0; i < img.width; i++) {
            for (int j = 0; j < img.height; j++) {
                int bright = (int) brightness(get(i, j));
                hist[bright]++;
            }
        }
        return hist;
    }

    public void showHashKey(int[] hash) {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (hash[i * 8 + j] == 1) {
                    fill(255);
                } else {
                    fill(0);
                }
                rect(64 * j, 64 * i, 64, 64);
            }

        }
    }


    public void hist(PImage img, int[] hist) {


        // Find the largest value in the histogram
        int histMax = 4000;

        stroke(0);

        // Draw half of the histogram (skip every second value)
        for (int i = 0; i < img.width; i += 2) {

            // Map i (from 0..img.width) to a location in the histogram (0..255)
            int which = (int) map(i, 0, img.width, 0, 255);

            // Convert the histogram value to a location between
            // the bottom and the top of the picture
            int y = (int) map(hist[which], 0, histMax, img.height, 0);
            line(i, img.height, i, y);
        }
    }

    public void keyPressed() {
        loop();
        if (keyCode == LEFT && imgIndex > 0) {
            imgIndex--;
        }
        if (keyCode == RIGHT && imgIndex < images.size() - 1) {
            imgIndex++;
        }
        if (keyCode == UP) {
            hist = true;
        }
        if (keyCode == DOWN) {
            hist = false;
        }
        if (keyCode == 'h' || keyCode == 'H') {
            hashKey = !hashKey;
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


    private static List<PImage> loadImages(String path, String header, String format, int startIndex) throws
            Exception {
        ImageReader reader = new ImageReader(path, header, format, startIndex);
        List<PImage> images = reader.getImages();
        for (PImage image : images) {
            image.loadPixels();
        }
        return images;
    }


    public static void displayImage(List<PImage> images) {

        String[] appletArgs = {"Processing"};
        ImageViewer instance = new ImageViewer(images);
        runSketch(appletArgs, instance);

    }

    public static void displayImage(List<PImage> images, String title) {
        String[] appletArgs = {"Processing"};
        ImageViewer instance = new ImageViewer(images, title);
        runSketch(appletArgs, instance);
    }


    public static void displayImage(String path, String header, String format, String index) throws Exception {
        String[] appletArgs = {"Processing"};
        ImageViewer instance = new ImageViewer(path, header, format, index);
        runSketch(appletArgs, instance);
    }


}

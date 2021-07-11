
/*
Readme: This class is used to display CT images.
There are two display method. It can be displayed by either pass and List<PImage>
or passing the essential arguments to the constructor and load images it self.
 */
package edu.hkbu.util.imageutil;

import edu.hkbu.util.io.FileReader;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static edu.hkbu.util.imageutil.DHash.dHashing;

@Deprecated
public class ImageViewerOld extends PApplet {
    private FileReader.CT_Volume volume;
    private List<PImage> images = new LinkedList<PImage>();
    private PImage currentImage;
    private Scanner in = new Scanner(System.in);
    private int imgIndex = 0;
    private List imgSet;
    private List<int[]> dHash;

    private String title;
    boolean hist = false;
    boolean hashKey = false;

    boolean hash_init = true;

    public ImageViewerOld(List<PImage> images) {
        List<PImage> newList = new ArrayList<PImage>();
        for (PImage img : images) {
            newList.add(img);
        }
        this.images = newList;
    }

    public ImageViewerOld(List<PImage> images, String title) {
        this(images);
        this.title = title;

    }

    public void setup() {
        size(1024, 512 + 25);
        this.frame.setTitle(title);
        frameRate(15);

        // try {
        // images = loadImages(path, "header", format, startIndex);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    public void draw() {
        background(43 ,43 ,43);
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

        textButton(10, 532, "dHashKey", 25, () -> hashKey = !hashKey);
        textButton(10, 532, "dHashKey", 25, new ButtonAction() {
            @Override
            public void execute() {

            }
        });

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
        printArray(hist);
        printArray(sig(sig2(hist)));
        return hist;
    }

    void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i != arr.length - 1) {
                System.out.print(", ");
            } else {
                System.out.println("]");
            }
        }
    }

    int[] sig(int[] arr) {
        int[] result = new int[arr.length - 1];
        for (int i = 1; i < arr.length; i++) {
            result[i - 1] = Integer.signum(arr[i] - arr[i - 1]);
        }
        return result;
    }

    int[] sig2(int[] arr) {
        int[] result = new int[arr.length - 1];
        for (int i = 1; i < arr.length; i++) {
            result[i - 1] = arr[i] - arr[i - 1];
        }
        return result;
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

    public static void displayImage(List<PImage> images) {

        String[] appletArgs = { "Processing" };
        ImageViewerOld instance = new ImageViewerOld(images);
        runSketch(appletArgs, instance);

    }

    public static void displayImage(List<PImage> images, String title) {
        String[] appletArgs = { "Processing" };
        ImageViewerOld instance = new ImageViewerOld(images, title);
        runSketch(appletArgs, instance);
    }

    public void textButton(int x, int y, String name, int textSize, ButtonAction exe) {
        textSize(textSize);
        fill(165, 179, 194);

        if (mouseListener(x, y, textSize * name.length(), textSize)) {
            if (mousePressed) {
                exe.execute();
            }
        }
        text(name, x, y);
    }

    public void mousePressed() {
        loop();
    }

    public void mouseReleased() {
        loop();
    }


    public boolean mouseListener(int x, int y, int width, int height) {
        return (mouseX > x && mouseX < x + width && mouseY > y - height && y > mouseY);
    }

    @FunctionalInterface
    public interface ButtonAction {
        void execute();
    }

}

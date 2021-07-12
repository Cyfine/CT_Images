package edu.hkbu.util.imageutil;

import edu.hkbu.util.io.FileReader.CT_Volume;
import edu.hkbu.util.io.JSONProcessor.CTag;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;

public class ImageViewer extends PApplet {
    final List<CT_Volume> VOLUMES;
    int currentVolIdx = 0;
    int currentImageIndex = 0;

    List<Float[]> buttonAlpha = new LinkedList<>();
    PFont font;
    int frameRate = 60;
    boolean mouseReleased = true;
    boolean hide = true;
    boolean showMouseCoordinate = false;

    public ImageViewer(List<CT_Volume> volumes) {
        this.VOLUMES = volumes;
    }

    /**
     * Initial configuration of the pop-up window
     */
    public void setup() {
        size(512, 512);
        font = createFont("resources/JetBrainsMonoNL-Regular.ttf", 31);

        for (int i = 0; i < 3; i++) {
            buttonAlpha.add(new Float[] { 255f });
        }

        this.frame.setTitle(VOLUMES.get(currentVolIdx).toString() + " | "
                + this.VOLUMES.get(currentVolIdx).getImageName(currentImageIndex));
        System.out
                .println("Click \"Exit\" button to close the window. Otherwise the whole program will be terminated.");
    }

    /**
     * Drawing on the canvas window
     */
    public void draw() {
        viewerMode();
    }

    /**
     * If the image has a relevant JSON tag, show the JSON tag when displaying the
     * image
     */
    public void showTags(int x, int y, int textSize) {

        CTag tag = VOLUMES.get(currentVolIdx).getTag(currentImageIndex);
        fill(165, 179, 194);
        if (tag != null) {
            pushMatrix();
            translate(x, y);
            double[][] points = tag.getPoints();
            String imagePath = tag.getImagePath();
            int offset = rounding(0.3 * textSize);

            textSize(textSize);

            text("imagePath: " + imagePath, x, y + textSize);
            for (int i = 0; i < points.length; i++) {
                text("points " + i + " : " + arrayToString(points[i]), x, y + textSize + (textSize + offset) * (i + 1));

            }
            popMatrix();

            for (int i = 0; i < points.length; i++) {
                rectMode(CENTER);
                stroke(100, 255, 100);
                fill(255, 0);
                rect((float) points[i][0], (float) points[i][1], 10, 10);
            }

        }

    }

    private void outputSingleImage() {
        mouseX = (int)0.5*width;
        mouseY = (int)0.5*height;
        CT_Volume volume = VOLUMES.get(currentVolIdx);
        String imageName = volume.getImageName(currentImageIndex);
        image(volume.getImages().get(currentImageIndex), 0, 0);
        showTags(0, 5, 12);
        outputCurrentFrame( volume.getFolderName() + "_" + imageName,"./output/");
        System.out.println("Output image: " + volume.getFolderName() + "_" + imageName);
    }

    private void outputMode() {
        noLoop();
        int cnt = 0;
        for (currentVolIdx = 0; currentVolIdx < VOLUMES.size(); currentVolIdx++) {
            CT_Volume volume = VOLUMES.get(currentVolIdx);
            for (currentImageIndex = 0; currentImageIndex < volume.getImages().size(); currentImageIndex++) {
                if (volume.getTag(currentImageIndex) != null) {
                    String imageName = volume.getImageName(currentImageIndex);
                    image(volume.getImages().get(currentImageIndex), 0, 0);
                    showTags(0, 5, 12);
                    outputCurrentFrame(volume.getFolderName() + "_" + imageName,"./output/");
                    System.out.println("Output image: " + volume.getFolderName() + "_" + imageName);
                    cnt++;
                }
            }
        }

        System.out.println("Total " + cnt + " files  outputted.");

    }

    private void viewerMode() {
        textFont(font);
        background(43, 43, 43);
        image(VOLUMES.get(currentVolIdx).getImages().get(currentImageIndex), 0, 0);

        textButton(80, 500, "Coordinate", 25, () -> showMouseCoordinate = !showMouseCoordinate, buttonAlpha.get(2));
        showCoordinate(460, 15);

        int nextButtonX = 462;
        int prevButtonX = 50;
        int arrowButtonY = 253;

        showTags(0, 5, 12);
        // buttons to switch image within volume
        genericButton(nextButtonX, arrowButtonY, 40, () -> {
            if (currentImageIndex < VOLUMES.get(currentVolIdx).getImages().size() - 1)
                currentImageIndex++;
            this.frame.setTitle(VOLUMES.get(currentVolIdx).toString() + " | "
                    + this.VOLUMES.get(currentVolIdx).getImageName(currentImageIndex));

        }, () -> sideEquilateralTri(nextButtonX, arrowButtonY, 30, 0.5f * PI), buttonAlpha.get(0));

        genericButton(prevButtonX, arrowButtonY, 40, () -> {
            if (currentImageIndex > 0)
                currentImageIndex--;
            this.frame.setTitle(VOLUMES.get(currentVolIdx).toString() + " | "
                    + this.VOLUMES.get(currentVolIdx).getImageName(currentImageIndex));

        }, () -> sideEquilateralTri(prevButtonX, arrowButtonY, 30, 1.5f * PI), buttonAlpha.get(0));

        // buttons to switch volumes
        triangleButton(253, 50, 30, () -> {
            if (currentVolIdx > 0 && VOLUMES.size() != 0) {
                currentVolIdx--;
                currentImageIndex = 0;
                this.frame.setTitle(VOLUMES.get(currentVolIdx).toString() + " | "
                        + this.VOLUMES.get(currentVolIdx).getImageName(currentImageIndex));
            }
        }, 0, buttonAlpha.get(1));

        triangleButton(253, 462, 30, () -> {
            if (currentVolIdx < VOLUMES.size() - 1 && VOLUMES.size() != 0) {
                currentVolIdx++;
                currentImageIndex = 0;
                this.frame.setTitle(VOLUMES.get(currentVolIdx).toString() + " | "
                        + this.VOLUMES.get(currentVolIdx).getImageName(currentImageIndex));

            }

        }, PI, buttonAlpha.get(1));

        textButton(10, 500, "Exit", 25, () -> this.frame.dispose(), buttonAlpha.get(2));
        textButton(240, 500, "Output", 25,() -> outputSingleImage(), buttonAlpha.get(2)); 
    }

    private void showCoordinate(int x, int y) {
        pushMatrix();
        translate(x, y);
        if (showMouseCoordinate) {
            textSize(13);
            fill(165, 179, 194);
            text("x: " + mouseX, 0, 0);
            text("y: " + mouseY, 0, 30);
        }
        popMatrix();
    }

    /**
     * The static method to instantiate a ImageViewer instance
     *
     * @param volumes
     */
    public static void showVolumes(List<CT_Volume> volumes) {
        String[] appletArgs = { "ImageViewer" };
        ImageViewer instance = new ImageViewer(volumes);
        runSketch(appletArgs, instance);
    }

    /**
     * Output current frame to image
     */
    public void outputCurrentFrame(String filename, String path) {
        mouseX = (int) (0.5 * width);
        mouseY = (int) (0.5 * height);
        save(path + filename);
    }

    public void hist(PImage img, int[] hist) {

        // Find the largest value in the histogram
        int histMax = 4000;

        stroke(0);

        // Draw half of the histogram (skip every second value)
        for (int i = 0; i < img.width; i++) {

            // Map i (from 0..img.width) to a location in the histogram (0..255)
            int which = (int) map(i, 0, img.width, 0, 255);

            // Convert the histogram value to a location between
            // the bottom and the top of the picture
            int y = (int) map(hist[which], 0, histMax, img.height, 0);
            line(i, img.height, i, y);
        }
    }

    // --------------------------- GUI Parts --------------------------------

    /**
     * Draw a side equilateral triangle
     *
     * @param x      the x coordinate of the button
     * @param y      the y coordinate of the button
     * @param angle  the radius rotate angle
     * @param length the side length of the triangle
     */
    public void sideEquilateralTri(int x, int y, int length, float angle) {
        pushMatrix();
        translate(x, y);
        noStroke();
        rotate(angle); // 1.57079632679f for 90 degree
        triangle(0, -length, -length * sqrt(3) / 2, (1.0f * length / 2), length * sqrt(3) / 2, (1.0f * length / 2));
        popMatrix();
    }

    /**
     * @param x        the x coordinate of the button
     * @param y        the y coordinate of the button
     * @param title    the title displayed on the button
     * @param textSize the text size of the button
     * @param exe      ButtonAction interface with execute() implemented using
     *                 actions triggered by the button
     */
    public void textButton(int x, int y, String title, int textSize, ImageViewer.ButtonAction exe, Float[] alpha) {
        textSize(textSize);

        if (mouseListener(x, y, 0.6 * textSize * title.length(), textSize, "bl")) {
            alpha[0] = 255f;
            rectMode(CORNERS);
            stroke(165, 179, 194);
            fill(255, 0f);
            rect(x, y + 3, x + 0.6f * textSize * title.length(), y - textSize, 7);
            if (mousePressed && mouseReleased) {
                exe.execute();
                mouseReleased = false;
            }
        } else {
            alpha[0] -= 255f / frameRate;
        }

        fill(165, 179, 194, alpha[0]);
        text(title, x, y);

    }

    public void genericButton(int x, int y, int length, ButtonAction exe, ButtonAction display, Float[] alpha) {

        if (mouseListener(x, y, length, length, "center")) {
            alpha[0] = 255f;
            if (mousePressed && mouseReleased) {
                exe.execute();
                mouseReleased = false;
            }
        } else {
            alpha[0] -= 255f / frameRate;
        }

        fill(165, 179, 194, alpha[0]);

        display.execute(); // here should show the display pattern of button

    }

    public void triangleButton(int x, int y, int length, ButtonAction exe, float angle, Float[] alpha) {
        genericButton(x, y, (int) (1.3 * length), exe, () -> sideEquilateralTri(x, y, length, angle), alpha);
    }

    /**
     * @param x      the x coordinate of the detection box
     * @param y      the y coordinate of the detection box
     * @param width  the width of the detection box
     * @param height the height of the detection box
     * @param mode   the detection mode, three modes in total, "bl" for the
     *               coordinate (x,y) is locate at the bottom left of the detection
     *               box, similarly for "tl" (top-left) and "center". The indicator
     *               String is NOT case-sensitive
     * @return true is the mouse is inside the detection box, vice versa false.
     */
    public boolean mouseListener(double x, double y, double width, double height, String mode) {
        switch (mode.toLowerCase()) {
            case "bl":
                return (mouseX > x && mouseX < x + width && mouseY > y - height && y > mouseY);
            case "center":
                return (mouseX < x + 0.5 * width && mouseX > x - 0.5 * width && mouseY > y - 0.5 * height
                        && mouseY < y + 0.5 * height);
            case "tl":
                return (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height);
        }
        return false;
    }

    public void mouseReleased() {
        mouseReleased = true;
    }

    /**
     * Contains only execute() method. execute() should be implemented using codes
     * triggered by the button.
     */
    @FunctionalInterface
    public interface ButtonAction {
        void execute();
    }

    // helper methods
    private int rounding(double num) {
        return (int) (num + 0.5);
    }

    private String arrayToString(double[] a) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < a.length; i++) {
            if (i != a.length - 1)
                sb.append("" + a[i] + ", ");
            else
                sb.append("" + a[i] + "]");
        }
        return sb.toString();
    }

}

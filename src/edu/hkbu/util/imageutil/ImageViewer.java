package edu.hkbu.util.imageutil;

import edu.hkbu.util.io.FileReader.CT_Volume;
import edu.hkbu.util.io.JSONProcessor.CTag;
import processing.core.*;

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
        font = createFont("./resources/JetBrainsMonoNL-Regular.ttf", 31);

        for (int i = 0; i < 3; i++) {
            buttonAlpha.add(new Float[] { 255f });
        }

        this.frame.setTitle(VOLUMES.get(currentVolIdx).toString() + " | "
                + this.VOLUMES.get(currentVolIdx).getImageName(currentImageIndex));
        System.out.println(
                "Click \"Exit\" button to close the window. Otherwise the whole program will be terminated.");
    }

    /**
     * Drawing image and essential information on the canvas
     */
    public void draw() {
        textFont(font);
        background(43, 43, 43);
        image(VOLUMES.get(currentVolIdx).getImages().get(currentImageIndex), 0, 0);

        textButton(80, 500, "Coordinate", 25, () -> showMouseCoordinate = !showMouseCoordinate, buttonAlpha.get(2));
        if (showMouseCoordinate) {
            textSize(13);
            fill(165, 179, 194);
            text("x: " + mouseX, 30, 60);
            text("y: " + mouseY, 30, 90);
        }

        int nextButtonX = 462;
        int prevButtonX = 50;
        int arrowButtonY = 253;

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

    // TODO: Implement
    /**
     * If the image has a relevant JSON tag, show the JSON tag when displaying the
     * image
     */
    public void showTag(String fileName) {
        CTag tag = VOLUMES.get(currentVolIdx).getTag(fileName);
        String label;
        int[][] points;

        if (tag != null) {

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
            rect(x, y+3, x + 0.6f * textSize * title.length(), y - textSize);
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

}

package edu.hkbu.util.imageutil;

import processing.core.*;

import java.util.LinkedList;
import java.util.List;


public class ImgAttributeCal extends Thread {

    public double variance;
    public double average;
    public double standDeviation;
    public int[] histPlot = new int[256];
    private PImage image;

    public ImgAttributeCal(PImage image) {
        this.image = image;
    }

    /**
     * calculate the average and variance of each images with multi-threading
     *
     * @return List[0] average, List[1] standard deviation
     */
    public static List<Double>[] calAttribute(List<PImage> images) throws InterruptedException {
        ImgAttributeCal[] threads = new ImgAttributeCal[images.size()];
        LinkedList<Double> standardDeviation = new LinkedList<>();
        LinkedList<Double> average = new LinkedList<>();
        LinkedList<int[]>  histPlot =  new  LinkedList<>();

        for (int i = 0; i < images.size(); i++) {
            threads[i] = new ImgAttributeCal(images.get(i));
        }

        for (int i = 0; i < images.size(); i++) {
            threads[i].start();
        }

        for (int i = 0; i < images.size(); i++) {
            threads[i].join();
        }

        for (int i = 0; i < images.size(); i++) {
            standardDeviation.add(threads[i].standDeviation);
            average.add(threads[i].average);
            histPlot.add(threads[i].histPlot);
        }

        return new LinkedList[]{average, standardDeviation, histPlot};

    }

    private void calAttributes() {
        image.loadPixels();
        double sum = 0;
        double[] greyScale = new double[image.width * image.height];


        for (int i = 0; i < image.height; i++) {
            for (int j = 0; j < image.width; j++) {
                int red = red(get(j, i));
                int green = green(get(j, i));
                int blue = blue(get(j, i));

                greyScale[i * image.width + j] = 1.0 * (red + green + blue) / 3;
                histPlot[(int) greyScale[i * image.width + j]]++;
                sum += greyScale[i * image.width + j];
            }
        }
        average = sum / (image.width * image.height);

        double squareSum = 0;

        for (int i = 0; i < image.width * image.height; i++) {
            squareSum += Math.pow((greyScale[i] - average), 2);
        }
        variance = squareSum / (image.width * image.height);
        standDeviation = Math.sqrt(variance);
    }


    public void run() {
        calAttributes();
    }

    public int get(int x, int y) {
        return image.pixels[y * image.width + x];
    }

    /**
     * @param rgb composite RGB value
     * @return decomposed RGB red value
     */
    protected static int red(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    /**
     * @param rgb composite RGB value
     * @return decomposed RGB green value
     */
    protected static int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    /**
     * @param rgb composite RGB value
     * @return decomposed RGB blue value
     */
    protected static int blue(int rgb) {
        return rgb & 0xFF;
    }
}

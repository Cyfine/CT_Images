package Util.ImageUtil;

import java.util.*;

import processing.core.*;


public class ImgAttributeCal extends Thread {

    public double variance;
    public double average;
    public double standDeviation;
    private PImage image;

    public ImgAttributeCal(PImage image) {
        this.image = image;
    }

    private void calAttributes() {
        image.loadPixels();
        double sum = 0;
        double[] greyScale = new double[image.width * image.height];


        for (int i = 0; i < image.height; i++) {
            for (int j = 0; j < image.width; j++) {
                int red = red(get(j,i));
                int green = green(get(j,i));
                int  blue = blue(get(j,i));

                greyScale[i*image.width + j]= 1.0 *(red + green + blue)/3;
                sum += greyScale[i*image.width + j];
            }
        }
        average = sum/(image.width * image.height);

        double squareSum = 0 ;

        for(int i = 0 ; i < image.width*image.height; i ++){
            squareSum += Math.pow((greyScale[i] - average),2);
        }
        variance = squareSum/(image.width * image.height);
        standDeviation = Math.sqrt(variance);
    }



    public void run() {
        calAttributes();
    }

    public int get(int x, int y){
        return image.pixels[y * image.width + x];
    }

    /**
     * @param rgb composite RGB value
     * @return decomposed RGB red value
     */
    private static int red(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    /**
     * @param rgb composite RGB value
     * @return decomposed RGB green value
     */
    private static int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    /**
     * @param rgb composite RGB value
     * @return decomposed RGB blue value
     */
    private static int blue(int rgb) {
        return rgb & 0xFF;
    }
}

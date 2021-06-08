/*
The dHash Algorithm. Each image (in processing.core.PImage format) is processed as a thread
*/
package Util.ImageUtil;

import processing.core.*;


public class DHash extends Thread {


    private PImage image;
    private int rowNum;

    public String bitString;

    /**
     * @param image     the image to be processed
     * @param bitLength the length of the bitString produced by dHash algorithm
     */
    public DHash(PImage image, int bitLength) {
        this.image = image;
        this.rowNum = (int) Math.sqrt(bitLength);
    }

    /**
     * cut image into segments according to the length of the bit String,
     * the calculate the average value of pixels in the segment
     *
     * @return the average values of pixels of each image segment
     */
    private float[] imageSegmentation() { //fixme: functionality untested
        int width = image.width;
        int height = image.height;
        int segmentHeight = height / rowNum;
        int segmentWidth = width / (rowNum + 1);
        float[] segmentAvg = new float[rowNum * (rowNum + 1)];

        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < rowNum + 1; j++) {

                int rightBound = segmentWidth * (j + 1);
                int lowerBound = segmentHeight * (i + 1);
                int cnt = 0;
                float sum = 0;


                if (i == rowNum - 1) {
                    i = width;  // if the width of the image can not be divided by the segment width
                }
                if (j == rowNum) {
                    j = height;
                }

                for (int k = segmentWidth * i; k < rightBound; k++) {  // x coordinate
                    for (int l = segmentHeight * j; l < lowerBound; l++) { // y coordinate
                        float r = red(image.pixels[i * width + l]);
                        float g = green(image.pixels[i * width + l]);
                        float b = blue(image.pixels[i * width + l]);
                        sum += (r + g + b) / 3;
                        cnt++;
                    }
                }
                segmentAvg[i * (rowNum + 1) + j] = sum / cnt;
            }
        }
        return segmentAvg;
    }

    public void getBitString(float[] segmentAvg) {

    }


    /**
     * @param rgb composite RGB value
     * @return decomposed RGB  red value
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


    public void run() {

    }

    public static void main(String[] args) {
        System.out.println(red(-9322772));
        System.out.println(green(-9322772));
        System.out.println(blue(-9322772));

    }
}

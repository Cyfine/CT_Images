/*
The dHash Algorithm. Each image (in processing.core.PImage format) is processed as a thread
*/
package Util.ImageUtil;

import processing.core.*;


public class DHash extends Thread {


    private PImage image;
    private int rowNum;

    public int[] dHash;

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
    private float[] imageSegmentation() {
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
                    lowerBound = height;
                    //As the width and height may not be fully dived, when processing the segments right most,
                    //the right bound should be the width of the image
                }
                if (j == rowNum) {
                    rightBound = width;
                    // Same reason as above
                }

                for (int k = segmentHeight * i; k < lowerBound; k++) {  // x coordinate
                    for (int l = segmentWidth * j; l < rightBound; l++) { // y coordinate
                        float r = red(image.pixels[k * width + l]);
                        float g = green(image.pixels[k * width + l]);
                        float b = blue(image.pixels[k * width + l]);
                        sum += (r + g + b) / 3;
                        cnt++;
                    }
                }
                segmentAvg[i * (rowNum + 1) + j] = sum / cnt;
            }
        }
        return segmentAvg;
    }

    /**
     * This is modified dHash algorithm. This only count the number of pure black pixels and number of pure white
     * pixels and sum it up do average. Grey pixels is not included. It makes dHash algorithm more sensitive for Images
     * with different CT window.
     *
     * @return the average value of segments only count the pure black and pure white pixels.
     */
    private float[] imageSegmentationModified() {
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
                    lowerBound = height;
                    //As the width and height may not be fully dived, when processing the segments right most,
                    //the right bound should be the width of the image
                }
                if (j == rowNum) {
                    rightBound = width;
                    // Same reason as above
                }

                for (int k = segmentHeight * i; k < lowerBound; k++) {  // x coordinate
                    for (int l = segmentWidth * j; l < rightBound; l++) { // y coordinate
                        float r = red(image.pixels[k * width + l]);
                        float g = green(image.pixels[k * width + l]);
                        float b = blue(image.pixels[k * width + l]);
                        float greyScale = (r + g + b) / 3;
                        if (greyScale == 255 ) {
                            cnt++;
                        }
                    }
                }
                segmentAvg[i * (rowNum + 1) + j] = cnt;
            }
        }
        return segmentAvg;
    }

    private float[] imageSegmentationModified_1() {
        int width = image.width;
        int height = image.height;

        int wCnt = 0;
        int bCnt = 0;

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
                    lowerBound = height;
                    //As the width and height may not be fully dived, when processing the segments right most,
                    //the right bound should be the width of the image
                }
                if (j == rowNum) {
                    rightBound = width;
                    // Same reason as above
                }

                for (int k = segmentHeight * i; k < lowerBound; k++) {  // x coordinate
                    for (int l = segmentWidth * j; l < rightBound; l++) { // y coordinate
                        float r = red(image.pixels[k * width + l]);
                        float g = green(image.pixels[k * width + l]);
                        float b = blue(image.pixels[k * width + l]);
                        float greyScale = (r + g + b) / 3;
                       if(greyScale == 255){
                           wCnt ++ ;
                       }
                       if(greyScale == 0){
                           bCnt++;
                       }
                    }
                }
                segmentAvg[i * (rowNum + 1) + j] = bCnt - wCnt;
            }
        }
        return segmentAvg;
    }

    private int[] bitString(float[] segmentAvg) {
        int[] result = new int[rowNum * rowNum];
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < rowNum; j++) {
                result[i * rowNum + j] = sigNum((segmentAvg[i * rowNum + j + 1] - segmentAvg[i * rowNum + j]));
            }
        }
        return result;

    }

    private static int sigNum(float diff) {
        if (diff > 0)
            return 0;

        return 1;
    }

    public static int hammingDistance(int[] bs1, int[] bs2) {

        int distance = 0;
        if (bs1.length != bs2.length)
            return -1;

        for (int i = 0; i < bs1.length; i++)
            distance += bs1[i] ^ bs2[i];

        return distance;
    }

    public static double similarity(int[] bs1, int[] bs2) {
        int distance = hammingDistance(bs1, bs2);

        if (distance == -1)
            return distance;

        return 1.0 - (distance * 1.0 / (bs1.length));
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

    /**0
     * @param rgb composite RGB value
     * @return decomposed RGB blue value
     */
    private static int blue(int rgb) {
        return rgb & 0xFF;
    }


    public void run() {
//        dHash = bitString(imageSegmentation());// using the original dHash algorithm
        dHash = bitString(imageSegmentationModified());// using the  adapted dHash algorithm for CT images
    }
}

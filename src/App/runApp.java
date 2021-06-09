package App;

import java.util.*;

import processing.core.*;

import Util.IO.*;
import Util.ImageUtil.DHash;


public class runApp {


    public static void main(String[] args) throws InterruptedException {
        List<PImage> images = loadImages("C:/Users/30421/Desktop/test", "test_", ImageReader.ImgFormat.jpg);

        dHashing(images);
    }

    private static List<PImage> loadImages(String path, String header, ImageReader.ImgFormat format) {
        ImageReader reader = new ImageReader(path, header, format);
        List<PImage> images = reader.getImages();
        for (PImage image : images) {
            image.loadPixels();
        }
        return images;
    }

    /**
     * calculating the dHash values with multi-threading
     *
     * @param images the images set used to calculate dHash values each
     * @return the dHash values of each images in a LinkedList
     * @throws InterruptedException
     */
    private static List<int[]> dHashing(List<PImage> images) throws InterruptedException {
        DHash[] threads = new DHash[images.size()];
        List<int[]> dHashValues = new LinkedList<>();
        for (int i = 0; i < images.size(); i++) {
            threads[i] = new DHash(images.get(i), 64);
        }

        for (int i = 0; i < images.size(); i++) {
            threads[i].start();
        }

        for (int i = 0; i < images.size(); i++) {
            threads[i].join();
        }

        for (int i = 0; i < images.size(); i++) {
            dHashValues.add(threads[i].dHash);
        }

        return dHashValues;
    }

}

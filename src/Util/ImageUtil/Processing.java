package Util.ImageUtil;

import Util.IO.ImageReader;
import Util.IO.ImageReader.ImgFormat;
import processing.core.*;

import java.util.*;


public class Processing extends PApplet {

    List<PImage> images = new LinkedList<PImage>();
    PImage currentImage;
    Scanner in = new Scanner(System.in);
    int imgIndex = 0;


    public void setup() {
        size(512, 512);
        images = loadImages("D:/Confidential_Data/CT images/HEP0001", "Se2Im", ImgFormat.jpg, 30);
    }

    public void draw() {
        currentImage = images.get(imgIndex);
        image(currentImage, 0,0);

    }

    public void keyPressed(){
        if(keyCode == LEFT && imgIndex > 0){
            imgIndex--;
        }
        if(keyCode == RIGHT && imgIndex < images.size() -1){
            imgIndex ++;
        }
        if(keyCode == UP){
            for(PImage img : images) {
                polarize(img);
            }
        }
        if(keyCode == DOWN){
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

    private static List<PImage> loadImages(String path, String header, ImageReader.ImgFormat format, int startIndex) {
        ImageReader reader = new ImageReader(path, header, format, startIndex);
        List<PImage> images = reader.getImages();
        for (PImage image : images) {
            image.loadPixels();
        }
        return images;
    }


    public static void main(String[] args) {
        String[] appletArgs = {"Processing"};
        Processing p = new Processing();
        runSketch(appletArgs, p);
    }
}

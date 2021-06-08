package App;

import Util.IO.*;

import java.util.*;

import processing.core.*;

public class runApp {


    public static void main(String[] args) {
        ImageReader reader = new ImageReader("C:/Users/30421/Desktop/test", "test_", ImageReader.imgFormat.jpg);
        List<PImage> images = reader.getImages();
        for(PImage image: images ){
            image.loadPixels();
        }
    }

}

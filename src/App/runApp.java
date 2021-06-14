package App;

import java.util.*;
import processing.core.*;
import Util.IO.*;
import Util.IO.ImageReader.ImgFormat;
import static Util.IO.FileWriter.*;
import Util.ImageUtil.DHash;
import Util.ImageUtil.ImgAttributeCal;

/*
The path of the test file: D:/Confidential_Data/CT images/HEP0001 , header Se2Im, start Index 30

 */
public class runApp {


    public static void main(String[] args) throws InterruptedException {
//        List<PImage> images = loadImages("C:/Users/30421/Desktop/test", "test_", ImgFormat.jpg, 1);
//
//        List<int[]> hashValues = dHashing(images);
//        System.out.println(DHash.hammingDistance(hashValues.get(6), hashValues.get(7)));
//        System.out.println(DHash.similarity(hashValues.get(6), hashValues.get(7)));
        List<PImage> images = null;
        List<int[]> hashValues = null;
        List<Integer> adjHammingDist = null;
        List<Double> adjSimilarity = null;
        List<Double> standardDeviation = null;
        List<Double> average = null;
        String command;
        Scanner in = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {

            boolean validCmd;
            do {
                System.out.print("Ready>");
                command = in.nextLine().trim().toLowerCase();
                validCmd = command.length() != 0;
            } while (!validCmd);
            switch (command) {
                case "exit":
                    System.out.println("Exit program, goodbye.");
                    exit = true;
                    break;
                case "load":
                    System.out.println("Input path of image set:");
                    System.out.print("Path>");
                    String path = in.nextLine().trim();
                    System.out.println("Input header of image files:");
                    System.out.print("Header>");
                    String header = in.nextLine();

                    boolean valid;
                    int index = 1;
                    do {
                        try {
                            valid = true;
                            System.out.println("Input the start index of images:");
                            System.out.print("Index>");
                            index = in.nextInt();
                            in.nextLine();
                            System.out.println();
                        } catch (Exception e) {
                            valid = false;
                            System.out.println();
                            in.nextLine();
                        }
                    } while (!valid); // Keep asking user to input if the user input is invalid.

                    ImgFormat format;
                    do {
                        System.out.println("Input the source images format:");
                        System.out.print("Format>");
                        format = formatParser(in.nextLine());
                    } while (format == null);
                    images = loadImages(path, header, format, index);
                    break;
                case "hash":
                    if (images != null) {
                        hashValues = dHashing(images);
                        adjHammingDist = adjacentHammingDist(hashValues);
                        adjSimilarity = adjacentSimilarity(hashValues);

                        printHashes(hashValues);
                        printAttributes(adjHammingDist, adjSimilarity);
                    } else {
                        System.out.println("No images loaded, load images first.");
                    }
                    break;

                case "print":
                    if (hashValues != null) {
                        printHashes(hashValues);
                        printAttributes(adjHammingDist, adjSimilarity);
                    } else {
                        System.out.println("No images loaded yet. Load images first.");
                    }
                    break;
                case "attrib":
                    if (images != null) {
                        List[] result = calAttribute(images);
                        standardDeviation = result[1];
                        average = result[0];
                        System.out.println("Average of images: " + average);
                        System.out.println("Standard deviation of images: " + standardDeviation);
                    } else {
                        System.out.println("No images loaded yet. Load images first.");
                    }

                    break;
                case "output" :
                    if (average != null && standardDeviation != null)
                    outputAttribCSV("attrib.csv",average,standardDeviation);

                default:
                    System.out.println("Invalid command");
            }

        }
    }


    private static void printHashes(List<int[]> list) {
        if (list.size() == 0)
            return;
        System.out.println("Calculated dHash value of images:");
        int cnt = 0;
        for (int[] array : list) {
            System.out.print(cnt++ + ": ");
            printIntArray(array);
        }
    }

    private static void printAttributes(List<Integer> list, List<Double> similarity) {
        if (list.size() == 0)
            return;
        System.out.println("Hamming distance of Adjacent images:");
        System.out.println(list);

        System.out.println("Adjacent similarity of images:");
        System.out.println(similarity);

    }


    private static void printIntArray(int[] array) {
        System.out.print("[");

        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i != array.length - 1)
                System.out.print(", ");
            else
                System.out.println("]");
        }
    }


    private static ImgFormat formatParser(String format) {
        format = format.trim().toLowerCase();
        switch (format) {
            case "jpg":
                return ImgFormat.jpg;
            case "png":
                return ImgFormat.png;
            case "gif":
                return ImgFormat.gif;
            case "jpeg":
                return ImgFormat.jpeg;
            case "tga":
                return ImgFormat.tga;
            default:
                return null;
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


    /**
     * calculating the dHash values with multi-threading design, to improve the image processing speed
     *
     * @param images the images set used to calculate dHash values each
     * @return the dHash values of each images in a LinkedList
     */
    private static List<int[]> dHashing(List<PImage> images) throws InterruptedException {
        DHash[] threads = new DHash[images.size()];
        List<int[]> dHashValues = new LinkedList<>();
        for (int i = 0; i < images.size(); i++) {
            threads[i] = new DHash(images.get(i), 64); //modify bitLength here
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

    /**
     * @return List[0] average, List[1] standard deviation
     */
    public static List<Double>[] calAttribute(List<PImage> images) throws InterruptedException {
        ImgAttributeCal[] threads = new ImgAttributeCal[images.size()];
        LinkedList<Double> standardDeviation = new LinkedList<>();
        LinkedList<Double> average = new LinkedList<>();

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
        }

        return new LinkedList[]{average, standardDeviation};

    }


    private static List<Integer> adjacentHammingDist(List<int[]> hashes) {
        List<Integer> list = new LinkedList<>();

        for (int i = 0; i < hashes.size() - 1; i++) {
            list.add(DHash.hammingDistance(hashes.get(i), hashes.get(i + 1)));
        }

        return list;
    }

    private static List<Double> adjacentSimilarity(List<int[]> hashes) {
        List<Double> list = new LinkedList<>();

        for (int i = 0; i < hashes.size() - 1; i++) {
            list.add(DHash.similarity(hashes.get(i), hashes.get(i + 1)));
        }

        return list;
    }




}

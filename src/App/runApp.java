package App;

import java.util.*;

import processing.core.*;
import Util.IO.*;
import Util.IO.ImageReader.ImgFormat;

import static Util.IO.FileWriter.*;
import static Util.IO.Tokenizer.*;

import Util.ImageUtil.DHash;
import Util.ImageUtil.ImgAttributeCal;

import static Util.ImageUtil.Processing.*;

/*
The path of the test file: D:/Confidential_Data/CT images/HEP0001 , header Se2Im, start Index 30
load D:/Confidential_Data/CT_images/HEP0001 Se2Im 30 jpg
 */
public class runApp {
    List<PImage> images = null;


    public static void main_0(String[] args) throws Exception {
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

                    String format;
                    do {
                        System.out.println("Input the source images format:");
                        System.out.print("Format>");
                        format = in.nextLine();
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
                case "output":
                    if (average != null && standardDeviation != null)
                        outputAttribCSV("attrib.csv", average, standardDeviation);
                    break;
                case "analyze":
                    Analyzer thread = new Analyzer(images);
                    thread.start();
                    thread.join();
                    break;
                case "display":
                    displayImage(images);
                default:
                    System.out.println("Invalid command");
            }

        }
    }

    public static void main(String[] args) {
        new runApp().start();
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        while (true) {

            try {
                String[] cmdArgs = getUserInput(in);
                switch (cmdArgs[0].toLowerCase()) {
                    case "load":
                        load(cmdArgs);
                        break;
                    case "help":
                        help(cmdArgs);
                        break;
                    case "exit":
                        System.out.println("Exit Program");
                        System.exit(0);
                        break;
                    case "show":
                        show();
                        break;
                    case "analyze":
                        analyze();
                        break;
                    case "dHash":

                        break;
                    case "test":
                        main_0(new String[]{"main"});
                        break;
                    case "output" :
                        output(cmdArgs);
                        break;
                    default:
                        System.out.println("Unknown command.");
                }
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }


        }

    }

    private void output(String[] cmdArgs) {
        if(cmdArgs.length  != 2){
            System.out.println("Invalid number of arguments");
        }

    }


    private String[] getUserInput(Scanner in, String header) {
        String input;
        String[] result;
        for (; ; ) {
            System.out.print(header);
            input = in.nextLine();
            result = tokenize(input);
            if (result.length != 0) {
                return result;
            }
        }
    }

    private String[] getUserInput(Scanner in) {
        return getUserInput(in, ">");
    }

    //--------------------------commands----------------------------
    private void load(String[] cmdArgs) throws Exception {
        if (cmdArgs.length != 5) {
            throw new Exception("Invalid number of arguments");
        }
        try {
            images = loadImages(cmdArgs[1], cmdArgs[2], cmdArgs[4], Integer.parseInt(cmdArgs[3]));
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number format");
        }
    }

    private void show() throws Exception {
        if (images != null) {
            displayImage(images);
        } else {
            throw new Exception("No images loaded yet.");
        }
    }

    private void analyze() throws InterruptedException {
        Analyzer thread = new Analyzer(images);
        thread.start();
        thread.join();
        return;
    }

    private void help(String[] cmdArgs) {
        if (cmdArgs.length < 2) {
            System.out.println("Available command: load, show, help, exit");
            return;
        }
        switch (cmdArgs[1]) {
            case "load":
                System.out.println("Load CT volume from path");
                System.out.println("Need the header name and index of first image in a CT volume.");
                System.out.println("Syntax: load [path] [header] [start index] [format]");
                break;
            case "exit":
                System.out.println("Exit program");
                break;
            case "show":
                System.out.println("Pop up a window and show the images loaded");
                break;
            default:
                System.out.println("Unknown command");
                System.out.println("Available command: load, show, help, exit");
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

    protected static List<PImage> loadImages(String path, String header, String format, int startIndex) throws Exception {
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
     * calculate the average and variance of each images with multi-threading
     *
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

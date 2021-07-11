package edu.hkbu.app;


public class Test {

    public static void main(String [] args){
        System.out.println(Runtime.getRuntime().maxMemory());

    }
//    public static void main_0(String[] args) throws Exception {
//        List<PImage> images = null;
//        List<int[]> hashValues = null;
//        List<Integer> adjHammingDist = null;
//        List<Double> adjSimilarity = null;
//        List<Double> standardDeviation = null;
//        List<Double> average = null;
//        String command;
//        Scanner in = new Scanner(System.in);
//        boolean exit = false;
//        while (!exit) {
//
//            boolean validCmd;
//            do {
//                System.out.print("Ready>");
//                command = in.nextLine().trim().toLowerCase();
//                validCmd = command.length() != 0;
//            } while (!validCmd);
//            switch (command) {
//                case "exit":
//                    System.out.println("Exit program, goodbye.");
//                    exit = true;
//                    break;
//                case "load":
//                    System.out.println("Input path of image set:");
//                    System.out.print("Path>");
//                    String path = in.nextLine().trim();
//                    System.out.println("Input header of image files:");
//                    System.out.print("Header>");
//                    String header = in.nextLine();
//
//                    boolean valid;
//                    int index = 1;
//                    do {
//                        try {
//                            valid = true;
//                            System.out.println("Input the start index of images:");
//                            System.out.print("Index>");
//                            index = in.nextInt();
//                            in.nextLine();
//                            System.out.println();
//                        } catch (Exception e) {
//                            valid = false;
//                            System.out.println();
//                            in.nextLine();
//                        }
//                    } while (!valid); // Keep asking user to input if the user input is invalid.
//
//                    String format;
//                    do {
//                        System.out.println("Input the source images format:");
//                        format = in.nextLine();
//                    } while (format == null);
//                    images = ImageReader.loadImages(path, header, format, index);
//                    break;
//                case "hash":
//                    if (images != null) {
//                        hashValues = DHash.dHashing(images);
//                        adjHammingDist = adjacentHammingDist(hashValues);
//                        adjSimilarity = adjacentSimilarity(hashValues);
//
//                        printHashes(hashValues);
//                        printAttributes(adjHammingDist, adjSimilarity);
//                    } else {
//                        System.out.println("No images loaded, load images first.");
//                    }
//                    break;
//
//                case "print":
//                    if (hashValues != null) {
//                        printHashes(hashValues);
//                        printAttributes(adjHammingDist, adjSimilarity);
//                    } else {
//                        System.out.println("No images loaded yet. Load images first.");
//                    }
//                    break;
//                case "attrib":
//                    if (images != null) {
//                        List[] result = ImgAttributeCal.calAttribute(images);
//                        standardDeviation = result[1];
//                        average = result[0];
//                        System.out.println("Average of images: " + average);
//                        System.out.println("Standard deviation of images: " + standardDeviation);
//                    } else {
//                        System.out.println("No images loaded yet. Load images first.");
//                    }
//
//                    break;
//                case "output":
//                    if (average != null && standardDeviation != null)
//                        outputAttribCSV("attrib.csv", average, standardDeviation);
//                    break;
//                case "analyze":
//                    Analyzer thread = new Analyzer(images);
//                    thread.start();
//                    thread.join();
//                    break;
//                case "display":
//                    displayImage(images);
//                default:
//                    System.out.println("Invalid command");
//            }
//
//        }
//    }


}
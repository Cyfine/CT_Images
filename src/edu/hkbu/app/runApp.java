package edu.hkbu.app;

import edu.hkbu.util.imageutil.DHash;
import edu.hkbu.util.imageutil.ImageViewer;
import edu.hkbu.util.io.FileReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static edu.hkbu.util.io.FileReader.getCTVolume;
import static edu.hkbu.util.stringutil.StringUtils.containsIgnoreCase;
import static edu.hkbu.util.stringutil.Tokenizer.tokenize;

/*
The path of the test file: D:/Confidential_Data/CT images/HEP0001 , header Se2Im, start Index 30
load D:/Confidential_Data/CT_images/HEP0001 Se2Im 30 jpg


linux :
load /home/carter/Pictures/Confidential_Data/CT_images/HEP0001/ Se2Im 30 jpg
 */
public class runApp {

    private List<Analyzer> threads = new LinkedList<>();
    private List<FileReader.CT_Volume> volumes;
    private int lastProcessThreadIdx = -1;

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
                    case "list":
                        list(cmdArgs);
                    case "dHash":
                        break;
                    // case "test":
                    // main_0(new String[]{"main"});
                    // break;
                    case "output":
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

    private String[] getUserInput(Scanner in, String header) {
        String input;
        String[] result;
        for (;;) {
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



    // =========================== Commands ===================================
    // private void load(String[] cmdArgs) throws Exception {
    // if (cmdArgs.length != 5) {
    // throw new Exception("Invalid number of arguments");
    // }
    // try {
    // String volumePath = cmdArgs[1] + "/" + cmdArgs[2] + cmdArgs[3] + "." +
    // cmdArgs[4];
    // currentImages = ImageReader.loadImages(cmdArgs[1], cmdArgs[2], cmdArgs[4],
    // Integer.parseInt(cmdArgs[3]));
    // if (currentImages.size() == 0 || currentImages == null) {
    // return;
    // }
    // imagesSet.add(currentImages);
    // threads.add(new Analyzer(currentImages, volumePath));
    // } catch (NumberFormatException e) {
    // throw new Exception("Invalid number format");
    // }
    // }

    private void load(String[] cmdArgs) throws Exception {
        if (cmdArgs.length != 2) {
            throw new Exception("Invalid number of arguments");
        }
        volumes = getCTVolume(cmdArgs[1]);
        for (FileReader.CT_Volume volume : volumes) {
            threads.add(new Analyzer(volume));
        }
        System.gc();
    }

    // @Deprecated
    // private void showOld() throws Exception {
    // if (volumes != null) {
    // for (Analyzer thread : threads) {
    // displayImage(thread.volume.getImages(), thread.volume.toString());
    // }
    // } else {
    // throw new Exception("No images loaded yet.");
    // }
    // }

    public void show() throws Exception {
        if (volumes != null) {
            ImageViewer.showVolumes(volumes);
        } else {
            throw new Exception("No images loaded yet.");
        }
    }

    private void analyze() throws Exception {
        if (volumes == null) {
            throw new Exception("No images loaded yet.");
        }

        if (lastProcessThreadIdx < threads.size()) {
            for (int i = lastProcessThreadIdx + 1; i < threads.size(); i++) {
                threads.get(i).start();

            }
            for (int i = lastProcessThreadIdx + 1; i < threads.size(); i++) {
                threads.get(i).join();
            }
        }

        for (Analyzer thread : threads) {
            thread.printAttributes();
            System.out.println();
        }
        lastProcessThreadIdx = threads.size() - 1;

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

    private void output(String[] cmdArgs) {

    }

    private void list(String[] cmdArgs) throws Exception {
        if (threads.size() == 0 || threads == null) {
            throw new Exception("Images unloaded. Load images first");
        }
        if (cmdArgs.length == 1) {
            for (Analyzer thread : threads) {
                System.out.println(thread.volume);
            }
            System.out.println("Total " + threads.size() + " results");
        } else if (cmdArgs.length == 2) {
            List<Analyzer> result = search(cmdArgs[1]);
            for (Analyzer thread : result) {
                System.out.println(thread.volume);
            }
            System.out.println("Total " + result.size() + " results");

        } else {
            System.out.println("Invalid number of arguments");
        }
    }

    /**
     * Scanning through each analyzer.filePath to find if the CT volume with key
     * word exists
     */
    private List<Analyzer> search(String keyword) {
        List<Analyzer> result = new LinkedList<>();
        for (Analyzer thread : threads) {
            if (containsIgnoreCase(keyword, thread.volume.toString())) {
                result.add(thread);
            }
        }
        return result;
    }

    // =========================== helper methods =============================
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

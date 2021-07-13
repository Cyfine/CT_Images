package edu.hkbu.app;

import edu.hkbu.util.imageutil.DHash;
import edu.hkbu.util.imageutil.ImageViewer;
import edu.hkbu.util.io.FileReader;
import edu.hkbu.util.io.FileReader.CT_Volume;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static edu.hkbu.util.io.FileReader.getCTVolume;
import static edu.hkbu.util.stringutil.StringUtils.containsIgnoreCase;
import static edu.hkbu.util.stringutil.Tokenizer.tokenize;

public class runApp {

    private List<Analyzer> threads = new LinkedList<>();
    private List<FileReader.CT_Volume> volumes;
    private int lastProcessThreadIdx = -1;

    public static void main(String[] args) {
        printMenu();
        new runApp().start();
    }

    private static void printMenu() {
        System.out.println("+-------------------------------------+");
        System.out.println("|                                     |");
        System.out.println("|             CT_Images               |");
        System.out.println("|                                     |");
        System.out.println("+-------------------------------------+");
        System.out.println("Available  commands: load, analyze, show, exit, help.");
        System.out.println("Detailed information for each command, use \"help [command]\" ");
        System.out.println("Please visit https://github.com/Cyfine/CT_Images to get source code of this project.\n");
    }

    private void start() {
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

    private void show() throws Exception {
        bootImageViewer(false);
    }

    private void bootImageViewer(boolean isOutputMode) {
        if (volumes != null && volumes.size() != 0) {
            ImageViewer.showVolumes(volumes, isOutputMode);
        } else {
            System.out.println("No images loaded yet.");
        }
    }

    private void output(String[] cmdArgs) throws Exception {
        if (cmdArgs.length < 2) {
            throw new Exception("Invalid number of arguments.");
        }
        if (cmdArgs[1].charAt(0) != '-') {
            throw new Exception("Invalid expression sign \"" + cmdArgs[1].charAt(0) + "\".");
        }
        parameterInterpreter(cmdArgs[1], new char[]{'i', 'a'}, this::outputImages, () -> outputFiles(cmdArgs));


    }

    private void parameterInterpreter(String expression, char[] paramChars, Executable... exe) throws Exception {
        if (paramChars.length != exe.length) {
            return;
        }
        expression = expression.toLowerCase();

        for (int i = 1; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);
            for (int j = 0; j < paramChars.length; j++) {
                if (currentChar == paramChars[j]) {
                    exe[j].execute();
                    break;
                }

                if (j == paramChars.length - 1) {
                    throw new Exception("Invalid parameter " + currentChar);
                }
            }
        }


    }

    private void outputFiles(String[] cmdArgs) {
        String fileName;
        PrintWriter writer;
        boolean unAnalyzed = false;
        if (cmdArgs.length < 3) {
            fileName = "analyzeResult.txt";
        } else if (cmdArgs.length == 3) {
            fileName = cmdArgs[2];
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < cmdArgs.length; i++) {
                sb.append(cmdArgs[i]);
                if (i == cmdArgs.length - 1) {
                    sb.append(".txt");
                } else {
                    sb.append("_");
                }
            }
            fileName = sb.toString();
        }

        try {
            writer = new PrintWriter("./output/files/" + fileName);
            for (CT_Volume vol : volumes) {
                if (vol.getAnalyzer() != null) {
                    vol.getAnalyzer().outPutAttributes(writer);
                } else {
                    System.out.println("CT_Volumes " + vol + " not analyzed yet.");
                    unAnalyzed = true;
                }
            }
            if (unAnalyzed) {
                System.out.println("Hint: Use \"analyze\" command to analyze loaded CT volumes.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found error.");
        }

    }

    private void outputImages() {
        bootImageViewer(true);
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

    @FunctionalInterface
    private interface Executable {
        void execute();
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

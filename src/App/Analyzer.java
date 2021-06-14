package App;


import Util.IO.FileWriter;
import Util.IO.ImageReader;
import processing.core.*;

import java.util.*;

import static App.runApp.*;

public class Analyzer extends Thread {
    private List<PImage> images;
    private List[] attrib;
    public List<List<List<Integer>>> clusters;

    public Analyzer(List<PImage> images) throws InterruptedException {
        this.images = images;
        attrib = calAttribute(images);
    }

    public void mutateIntervalSelect() {
        List<Double> imgAvg = attrib[0];
        List<Double> imgSD = attrib[1];

        List<List<Integer>> avgOverRange = new LinkedList<>();
        List<List<Integer>> sdOverRange = new LinkedList<>();

        double imgAvg_avg = average(imgAvg);
        double imgAvg_sd = sd_pop(imgAvg, imgAvg_avg);

        double imgSD_avg = average(imgSD);
        double imgSD_sd = sd_pop(imgSD, imgSD_avg);

        double imgAvg_upperbound = imgAvg_avg + imgAvg_sd;
        double imgAvg_lowerbound = imgAvg_avg - imgAvg_sd;

        double img_sd_upperbound = imgSD_avg + imgSD_sd;
        double img_sd_lowerbound = imgSD_avg - imgSD_sd;

        boolean img_Avg_adjacent = false;
        int avg_clusterIdx = -1;
        boolean img_SD_adjacent = false;
        int sd_clusterIdx = -1;

        for (int i = 0; i < attrib[1].size(); i++) {
            if (imgAvg.get(i) < imgAvg_lowerbound || imgAvg.get(i) > imgAvg_upperbound) {
                if (img_Avg_adjacent) {
                    avgOverRange.get(avg_clusterIdx).add(i);
                } else {
                    img_Avg_adjacent = true;
                    avgOverRange.add(new LinkedList<Integer>());
                    avgOverRange.get(++avg_clusterIdx).add(i);
                }
            } else {
                img_Avg_adjacent = false;
            }

            if (imgSD.get(i) < img_sd_lowerbound || imgSD.get(i) > img_sd_upperbound) {
                if (img_SD_adjacent) {
                    sdOverRange.get(sd_clusterIdx).add(i);
                } else {
                    img_SD_adjacent = true;
                    sdOverRange.add(new LinkedList<Integer>());
                    sdOverRange.get(++sd_clusterIdx).add(i);
                }
            } else {
                img_SD_adjacent = false;
            }
        }
        List<List<List<Integer>>> result = new LinkedList<>();
        result.add(avgOverRange);
        result.add(sdOverRange);
        clusters = result;
    }

    public void run() {
        mutateIntervalSelect();
        FileWriter.outputAttribCSV("HEP00034_attrib.csv", attrib[0], attrib[1]);
    }

    public static void main(String[] args) throws Exception {
        List<PImage> images = loadImages("D:/Confidential_Data/CT images/HEP00034", "Se2Im", "png", 6);
        Analyzer test = new Analyzer(images);
        test.start();
        test.join();
        List<List<List<Integer>>> tst = test.clusters;
        System.out.println(tst.get(0));
        System.out.println(tst.get(1));
    }


    private static double average(List<Double> list) {
        double sum = 0;
        for (Double num : list) {
            sum += num;
        }
        return sum / list.size();
    }

    private static double sd_pop(List<Double> list, double average) {
        double squareSum = 0;
        for (Double num : list) {
            squareSum += (num - average) * (num - average);
        }
        return Math.sqrt(squareSum / list.size());
    }
}


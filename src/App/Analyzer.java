package App;


import Util.IO.FileWriter;
import processing.core.*;

import java.util.*;

import static App.runApp.*;

public class Analyzer extends Thread {
    private List<PImage> images;
    private List[] attrib;
    private double imgAvg_avg;
    private double imgAvg_sd;
    private double imgSD_avg;
    private double imgSD_sd;
    private List<Integer> avg_mutantCluster; // the index in attrib[0]
    private List<Integer> sd_mutantCluster; // the index in attrib[1]

    public List<List<List<Integer>>> clusters;


    public Analyzer(List<PImage> images) throws InterruptedException {
        this.images = images;
        avg_mutantCluster = new LinkedList<>();
        attrib = calAttribute(images);
    }

    // write back mutate cluster into clusters clusters.get(0) is cluster selected using average
    // clusters.get(1) is cluster selected using standard deviation.
    public void mutateClusterSelect() {
        List<Double> imgAvg = attrib[0];
        List<Double> imgSD = attrib[1];

        List<List<Integer>> avgOverRange = new LinkedList<>();
        List<List<Integer>> sdOverRange = new LinkedList<>();

        imgAvg_avg = average(imgAvg);
        imgAvg_sd = sd_pop(imgAvg, imgAvg_avg);

        imgSD_avg = average(imgSD);
        imgSD_sd = sd_pop(imgSD, imgSD_avg);

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
                    avgOverRange.add(new LinkedList<>());
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
                    sdOverRange.add(new LinkedList<>());
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
        mutateClusterSelect();
        printAttributes();
//        FileWriter.outputAttribCSV("HEP00034_attrib.csv", attrib[0], attrib[1]);
    }

    private void printAttributes() {
        System.out.println("Average of images averages:" + imgAvg_avg);
        System.out.println("Standard deviation of images averages:" + imgAvg_sd);
        System.out.println("Cluster (Avg)" + clusters.get(0));
        System.out.println("Average of images standard deviation:" + imgSD_avg);
        System.out.println("Standard deviation of images standard deviation:" + imgSD_sd);
        System.out.println("Cluster (SD)" + clusters.get(1));

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


    //execute after mutantClusterSelect()
    // write mutant clusters to class variable avg_mutantCluster and sd_mutantCluster
    private void clusterAnalysis() {
        List<List<Integer>> avgCluster = clusters.get(0);
        List<List<Integer>> sdCluster = clusters.get(1);

        List<Integer> sdMutantClusterIdx = new LinkedList<>();
        List<Integer> avgMutantClusterIdx = new LinkedList<>();


        for (int i = 0; i < avgCluster.size(); i++) {
            List<Integer> clusterIdx = avgCluster.get(i);
            if (clusterIdx.size() == 1) {

            }
            List<Double> cluster = parIdxToVal(clusterIdx, true); // calculate the standard deviation and average within the cluster
            double avg = average(cluster);
            double sd = sd_pop(cluster, avg);

            for (int j = 0; j < clusterIdx.size(); j++) {
                int index = clusterIdx.get(j);
                attrib[0].get(index);

            }
        }

        for (int i = 0; i < sdCluster.size(); i++) {

        }

    }

    /**
     * @param indices the list contains indices of image cluster stored in the List[] attrib
     * @param isAvg   if the indices input is cluster indices selected using averages of images, if not
     *                the indices is selected using standard deviation of images
     * @return the list contains the attribute values of images (average or standard deviation)
     */
    private List<Double> parIdxToVal(List<Integer> indices, boolean isAvg) {
        List<Double> list = new LinkedList<>();
        int attribIdx;
        if (isAvg)
            attribIdx = 0;
        else
            attribIdx = 1;

        for (int i = 0; i < indices.size(); i++) {
            list.add((Double) attrib[attribIdx].get(indices.get(i)));
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        List<PImage> images = loadImages("D:/Confidential_Data/CT_images/HEP00034", "Se2Im", "png", 6);
        Analyzer test = new Analyzer(images);
        test.start();
        test.join();
        List<List<List<Integer>>> tst = test.clusters;
        System.out.println(tst.get(0));
        System.out.println(tst.get(1));
    }

}


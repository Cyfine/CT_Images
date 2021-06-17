/*
 * Readme:
 * The analyzer can calculate the attributes(pixel average and pixel standard derivation) of images in a CT volume
 * For each CT volume loaded to the program, an Analyzer instance is used to analyze and find
 * the images that have mutate CT window (Each analyzer is running as a thread).
 * The result will be written back to the class variables.
 *
 * How it works:
 * After calculating the average and standard deviation of the images,
 * the standard deviation and average of "image pixel average" will be calculated
 * and then construct a confidence interval which is [avg - sd, avg + sd], the
 * images that have average (this average refers to average of image pixels) out of the confidence interval
 * will be selected. Similarly, we can also construct confidence interval using standard deviation(refers to pixels)
 * of the images. This program make use of both "pixel standard deviation" and "pixel average"
 */
package App;


import processing.core.*;

import java.util.*;

import static App.runApp.*;

public class Analyzer extends Thread {
    private final List<PImage> images;
    private List[] attrib; // [0] for average, [1] for standard deviation
    private double imgAvg_avg; // the average of "image pixel averages"
    private double imgAvg_sd;  // the average of "image pixel averages"
    private double imgSD_avg;
    private double imgSD_sd;
    private List<List<Integer>> avg_mutateCluster; // the index in attrib[0]
    private List<List<Integer>> sd_mutateCluster;  // the index in attrib[1]

    public List<List<List<Integer>>> clusters;
    // clusters.get(0) is the mutate clusters selected using average
    // clusters.get(1) is the mutate clusters selected using standard deviation.

    //========================= Constructor =============================

    /**
     * Constructor
     *
     * Each analyzer instance contains a single CT volume
     *
     * @param images images in the CT volume
     */
    public Analyzer(List<PImage> images) throws InterruptedException {
        this.images = images;
        avg_mutateCluster = new LinkedList<>();
        sd_mutateCluster = new LinkedList<>();
        attrib = calAttribute(images);
    }

    public void run() {
        mutateClusterSelect();
        clusterAnalysis();
        printAttributes();
//        FileWriter.outputAttribCSV("HEP00034_attrib.csv", attrib[0], attrib[1]);
    }


    //======================== core methods =============================
    // write back mutate cluster into clusters clusters.get(0) is cluster selected using average
    // clusters.get(1) is cluster selected using standard deviation.
    //*

    /**
     * Select the images that is out of the confidence interval
     * [imgAvg_avg ± img_Avg_sd]
     * [imgSD_avg ± imgSD_sd]
     */
    public void mutateClusterSelect() {
        List<Double> imgAvg = (List<Double>)attrib[0];
        List<Double> imgSD = (List<Double>)attrib[1];

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

    /**
     * Analyze the images that are out of the confidence interval constructed using "image pixel average"
     * and "image pixel standard deviation".
     * For continuous images that are out of confidence interval (refers as cluster), it may not caused by
     * mutated CT window, but structural change.
     * <p>
     * To handle such situation, the average and standard deviation within the cluster will be calculated.
     * Then construct a confidence interval [cluster_head ± cluster_sd] and [cluster_tail ± cluster_sd]
     * If the previous images and following image is out of constructed interval respectively, the cluster is judged
     * as mutate interval and added to avg_mutateCluster or sd_mutateCluster which is the images may have
     * abnormal CT window within the the volume.
     * <p>
     * Should execute after mutateClusterSelect(), so that the essential data
     * has already filled into class variables
     */
    private void clusterAnalysis() {
        List<List<Integer>> avgCluster = clusters.get(0);
        List<List<Integer>> sdCluster = clusters.get(1);


        for (List<Integer> clusterIdx : avgCluster) {
            //if the cluster that out of the confidence interval has only one element, directly add to
            // the result array
            if (clusterIdx.size() == 1) {
                avg_mutateCluster.add(clusterIdx); // add to result
                continue;
            }

            List<Double> cluster = parIdxToVal(clusterIdx, true); // calculate the standard deviation and average within the cluster
            double avg = average(cluster);
            double sd = sd_sam(cluster, avg);

            int clusterHeadIdxPrev = clusterIdx.get(0) - 1;
            int clusterTailIdxFollow = clusterIdx.get(1) + 1;
            boolean headMutate = false;
            boolean tailMutate = false;

            if (clusterHeadIdxPrev > 0 && (Double) attrib[0].get(clusterHeadIdxPrev) < cluster.get(0) - sd) {
                headMutate = true;
            }

            if (clusterTailIdxFollow < attrib[0].size() && (Double) attrib[0].get(clusterTailIdxFollow) > cluster.get(cluster.size() - 1) + sd) {
                tailMutate = true;
            }

            if (headMutate || tailMutate) {
                avg_mutateCluster.add(clusterIdx); // add to result (cluster has multi values)
            }

        }


        //Filter selected cluster
        for (List<Integer> clusterIdx : sdCluster) {
            //if the cluster that out of the confidence interval has only one element, directly add to
            // the result array
            if (clusterIdx.size() == 1) {
                sd_mutateCluster.add(clusterIdx); // add to result
                continue;
            }

            List<Double> cluster = parIdxToVal(clusterIdx, false); // calculate the standard deviation and average within the cluster
            double avg = average(cluster);
            double sd = sd_sam(cluster, avg);

            int clusterHeadIdxPrev = clusterIdx.get(0) - 1;
            int clusterTailIdxFollow = clusterIdx.get(1) + 1;
            boolean headMutate = false;
            boolean tailMutate = false;

            if (clusterHeadIdxPrev > 0 && (Double) attrib[1].get(clusterHeadIdxPrev) < cluster.get(0) - sd) {
                headMutate = true;
            }

            if (clusterTailIdxFollow < attrib[1].size() && (Double) attrib[1].get(clusterTailIdxFollow) > cluster.get(cluster.size() - 1) + sd) {
                tailMutate = true;
            }

            if (headMutate || tailMutate) {
                sd_mutateCluster.add(clusterIdx); // add to result (cluster has multi values)
            }

        }


    }


    //========================= Helper Methods ==========================

    private static double average(List<Double> list) {
        double sum = 0;
        for (Double num : list) {
            sum += num;
        }
        return sum / list.size();
    }

    /**
     * @param list    the list contains sample data
     * @param average the average of sample data
     * @return the population standard deviation
     */
    private static double sd_pop(List<Double> list, double average) {
        double squareSum = 0;
        for (Double num : list) {
            squareSum += (num - average) * (num - average);
        }
        return Math.sqrt(squareSum / list.size());
    }

    /**
     * @param list    the lst contains smaple data
     * @param average the average of sample data
     * @return the sample standard deviation
     */
    private static double sd_sam(List<Double> list, double average) {
        double squareSum = 0;
        for (Double num : list) {
            squareSum += (num - average) * (num - average);
        }
        return Math.sqrt(squareSum / (list.size() - 1));
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

    private void printAttributes() {
        System.out.println("Average of images averages:" + imgAvg_avg);
        System.out.println("Standard deviation of images averages:" + imgAvg_sd);
        System.out.println("Cluster (Avg)" + clusters.get(0));
        System.out.println("Average of images standard deviation:" + imgSD_avg);
        System.out.println("Standard deviation of images standard deviation:" + imgSD_sd);
        System.out.println("Cluster (SD)" + clusters.get(1));
        System.out.println("Processed cluster (avg):");
        for (List<Integer> list : avg_mutateCluster) {
            System.out.println(list);
        }
        System.out.println("Processed cluster(sd): ");
        for (List<Integer> list : sd_mutateCluster) {
            System.out.println(list);
        }
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


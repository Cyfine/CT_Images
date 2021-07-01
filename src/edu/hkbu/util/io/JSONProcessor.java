package edu.hkbu.util.io;

import org.json.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class JSONProcessor {


    public static class CTag {
        public Shape shape;
        String imagePath;
        String version;
        String absolutePath;


        //constructor
        public CTag(JSONObject t) {
            this.version = t.getString("version");
            this.imagePath = t.getString("imagePath");
            JSONObject s = t.getJSONArray("shapes").getJSONObject(0);
            this.shape = new Shape(s.getString("label"), s.getString("group_id"), s.getString("shape_type"), s.getJSONArray("points"));
        }

        public CTag(JSONObject t, String absolutePath) {
            this(t);
            this.absolutePath = absolutePath;
        }


        //getters
        String getLabel() {
            return shape.label;
        }

        int[][] getPoints() {
            return shape.points;
        }

        String getGroupID() {
            return shape.groupID;
        }

        String getShapeType() {
            return shape.shapeType;
        }


        // Inner class of CTag, to integrate the shape information
        private class Shape {
            String label;
            int[][] points;
            String groupID;
            String shapeType;

            // constructor
            Shape(String label, String groupID, String shapeType) {
                this.label = label;
                this.groupID = groupID;
                this.shapeType = shapeType;
            }

            Shape(String label, String groupID, String shapeType, JSONArray points) {
                this(label, groupID, shapeType);
                int size = points.length();

                int[][] result = new int[points.length()][2];

                for (int i = 0; i < points.length(); i++) {
                    JSONArray arr = points.getJSONArray(i);
                    result[i][0] = rounding(arr.optDouble(0));
                    result[i][1] = rounding(arr.optDouble(1));
                }

                this.points = result;
            }


            // getters


            public String getLabel() {
                return label;
            }

            public int[][] getPoints() {
                return points;
            }

            public String getGroupID() {
                return groupID;
            }

            public String getShapeType() {
                return shapeType;
            }
        }


    }

    public static int rounding(double num) {
        return (int) (num += 0.5);
    }


    public static void main(String[] args) {
        double num1 = 1.2;
        double num2 = 1.5;
        double num3 = 1.4;


        System.out.println(rounding(num1));
        System.out.println(rounding(num2));
        System.out.println(rounding(num3));

    }

    public static void main_0(String[] args) throws IOException {
        File file = new File("D:/Confidential_Data/CT_images/HEP0001/Se2Im33.json");

        String content = FileUtils.readFileToString(file, "UTF-8");
        JSONObject obj = new JSONObject(content);
        JSONArray jArray = obj.getJSONArray("shapes");
        JSONObject obj1 = jArray.getJSONObject(0);
        JSONArray points = obj1.getJSONArray("points");

        double[][] result = new double[points.length()][2];
        for (int i = 0; i < points.length(); i++) {
            JSONArray arr = points.getJSONArray(i);
            result[i][0] = arr.optDouble(0);
            result[i][1] = arr.optDouble(1);
        }

        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i][0]);
            System.out.println(result[i][1]);
        }

    }


}
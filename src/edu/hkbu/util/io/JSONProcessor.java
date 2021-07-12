package edu.hkbu.util.io;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class JSONProcessor {

    public static CTag getImgTag(String directory) throws IOException {
        File f = new File(directory);
        return getImgTag(f);
    }

    public static CTag getImgTag(File f) {
        try {
            String content = FileUtils.readFileToString(f, "UTF-8");
            JSONObject obj = new JSONObject(content);

            return new CTag(obj);
        } catch (Exception e) {
            if (e instanceof IOException) {
                System.out.println("Invalid directory for JSON file, check directory correctness.");
            } else if (e instanceof JSONException) {
                System.out.println("Unsupported JSON content, requires LabelMe JSON for CT images");
            } else {
                System.out.println("Error occurs when loading JSON tags");
            }
        }
        return null;
    }

    public static class CTag {
        public Shape shape;
        String imagePath;
        String version;
        String absolutePath;

        // constructor
        public CTag(JSONObject t) {
            this.version = t.getString("version");
            this.imagePath = t.getString("imagePath");
            JSONObject s = t.getJSONArray("shapes").getJSONObject(0);
            this.shape = new Shape(s.getString("label"), s.getString("shape_type"), s.getJSONArray("points"));
        }

        public CTag(JSONObject t, String absolutePath) {
            this(t);
            this.absolutePath = absolutePath;
        }

        // getters
        public String getLabel() {
            return shape.label;
        }

        public double[][] getPoints() {
            return shape.points;
        }

        public String getShapeType() {
            return shape.shapeType;

        }

        public String getImagePath() {
            return imagePath;
        }

        public void setAbsolutePath(String absPath) {
            absolutePath = absPath;
        }

        // Inner class of CTag, to integrate the shape information
        private static class Shape {
            String label;
            double[][] points;

            String shapeType;

            // constructor
            Shape(String label, String shapeType) {
                this.label = label;

                this.shapeType = shapeType;
            }

            Shape(String label, String shapeType, JSONArray points) {
                this(label, shapeType);

                double[][] result = new double[points.length()][2];

                for (int i = 0; i < points.length(); i++) {
                    JSONArray arr = points.getJSONArray(i);
                    result[i][0] = arr.optDouble(0);
                    result[i][1] = arr.optDouble(1);
                }

                this.points = result;
            }
            
        }

    }

    public static int rounding(double num) {
        return (int) (num += 0.5);
    }

}
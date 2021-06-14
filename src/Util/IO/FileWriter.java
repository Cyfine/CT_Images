package Util.IO;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

// set of static methods for data output
public class FileWriter {

    public static void outputAttribCSV(String fName, List average, List standardDeviation) {
        try {
            PrintWriter writer = new PrintWriter(fName);
            writer.println("Average, Standard Deviation");
            for(int i = 0 ; i < average.size(); i++){
                writer.println(average.get(i) + ", " + standardDeviation.get(i));
            }
            writer.close();
        }catch(FileNotFoundException e){
            System.err.println("File "+ fName+ "not found");
        }

    }

}

package utility;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportDataToFile {
	public static String dirOutput = "./output";
	public static Logger logger = LoggerFactory.getLogger(ExportDataToFile.class.getName());
	
	public static void exportToFile(HashMap<String, String> hm)
	{
		// Retrieve each postData and create file
		Iterator<String> it = hm.keySet().iterator();
        while (it.hasNext())
        {
            String id = it.next();
            String data = hm.get(id);
            
            try {
				createFile(id, data);
			} catch (IOException e) {
				logger.error("Error in create file: " + e.getMessage());
			}
        }

	}
	
	public static void exportToFile(String id, String data)
	{
		try {
			createFile(id, data);
		} catch (IOException e) {
			logger.error("Error in create file: " + e.getMessage());
		}

	}
	
	private static void createFile(String id, String data) throws IOException
	{
		String outputPath = dirOutput + "/" + id + ".txt";
		FileOutputStream fos = new FileOutputStream(outputPath);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

        writer.write(data);
        writer.flush();
        
        writer.close();
	}
}

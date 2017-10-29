package lightning;

import java.io.*;

/**
 * Export settings
 */
public class OutputSettings extends Gui{

    public void export(String path) throws Exception {
        BufferedWriter writer = null;
        try {
            //create a temporary file
            File logFile = new File(path);

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(super.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the writer regardless of what happens...
            if (writer != null) {
                writer.close();
            }
        }
    }


}

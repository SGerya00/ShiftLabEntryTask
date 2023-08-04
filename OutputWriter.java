import java.io.*;

public class OutputWriter {
    private final String filename;
    private FileWriter fw;
    private PrintWriter pw;

    OutputWriter(String filename) {
        this.filename = filename;
    }

    public void openFileForWriting() throws IOException {
        fw = new FileWriter(filename);
        pw = new PrintWriter(fw);
    }

    public void closeFile() throws IOException {
        pw.close();
        fw.close();
    }

    public void writeToFile(String str) {
        pw.println(str);
    }

    public void writeToFile(Integer i) {
        pw.println(i);
    }
}

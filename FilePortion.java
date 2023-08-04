import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class FilePortion {
    protected String filename;

    //is used as a multiplier, 1 for ascending sort, -1 for descending
    protected int sortingModifier;

    //useful for error messaging
    protected int currentLineNum;

    protected FileReader fr = null;

    protected BufferedReader br = null;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getSortingModifier() {
        return sortingModifier;
    }

    public void setSortingModifier(int sortingModifier) {
        this.sortingModifier = sortingModifier;
    }

    public int getCurrentLineNum() {
        return currentLineNum;
    }

    public void setCurrentLineNum(int currentLineNum) {
        this.currentLineNum = currentLineNum;
    }

    abstract public int compareDataTo(FilePortion that);

    public void openFile() throws FileNotFoundException {
        fr = new FileReader(filename);
        br = new BufferedReader(fr);
    }

    public void closeFile() throws IOException {
        br.close();
        fr = null; //no need to close fr as br is a wrapper around fr
    }

    //0 if line was read without exceptions, 1 if EOF reached
    abstract public int readLineFromFile() throws RuntimeException, IOException;

    abstract public void writeToOutputFile(OutputWriter ow);
}

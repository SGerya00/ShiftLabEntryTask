import java.io.*;

public abstract class FilePortion {
    protected final String filename;

    //is used as a multiplier, 1 for ascending sort, -1 for descending
    protected final int sortingModifier;

    //useful for error messaging
    protected int currentLineNum;

    protected RandomAccessFile raf = null;

    protected long currentFilePointer = 0;

    protected FilePortion(String filename, int sortingModifier) {
        this.filename = filename;
        this.sortingModifier = sortingModifier;
    }

    public String getFilename() {
        return filename;
    }

    public int getSortingModifier() {
        return sortingModifier;
    }

    public int getCurrentLineNum() {
        return currentLineNum;
    }

    public void setCurrentLineNum(int currentLineNum) {
        this.currentLineNum = currentLineNum;
    }

    //this < that returns <0
    //this == that returns 0
    //this > that returns >0
    abstract public int compareDataTo(FilePortion that);

    //returns true if lineData (in children) is not null
    abstract public boolean hasData();

    public void openFile() throws Exception {
        //will not throw IllegalArgumentException as mode is always "r" <- one of allowed
        try {
            raf = new RandomAccessFile(filename, "r");
        } catch (FileNotFoundException | SecurityException e) {
            String exceptionMessage = "Problem with file \""
                    + filename
                    + "\" when trying to open it\n"
                    + e.getMessage()
                    + "\n"
                    + "The program will now ignore it\n";
            throw new Exception(exceptionMessage);
        }
    }

    public void closeFile() throws IOException {
        try {
            raf.close();
        } catch (IOException e) {
            String exceptionMessage = "Problem with file \""
                    + filename
                    + "\" when trying to close it\n"
                    + e.getMessage()
                    + "\n"
                    + "The program will now ignore it\n";
            throw new IOException(exceptionMessage);
        }
    }

    public void moveToPosition() throws IOException {
        try {
            raf.seek(currentFilePointer);
        } catch (IOException e) {
            String exceptionMessage = "Problem with file \""
                    + filename
                    + "\" when trying to seek in it\n"
                    + e.getMessage()
                    + "\n"
                    + "The program will now ignore it\n";
            throw new IOException(exceptionMessage);
        }
    }

    public void rememberPosition() throws IOException {
        try {
            currentFilePointer = raf.getFilePointer();
        } catch (IOException e) {
            String exceptionMessage = "Problem with file \""
                    + filename
                    + "\" when trying to get file pointer in it\n"
                    + e.getMessage()
                    + "\n"
                    + "The program will now ignore it\n";
            throw new IOException(exceptionMessage);
        }
    }

    //0 if line was read without exceptions, 1 if EOF reached
    abstract public int readLineFromFile() throws FormatException, IOException;

    abstract public void writeToOutputFile(OutputWriter ow);
}

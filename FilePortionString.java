import java.io.IOException;

public class FilePortionString extends FilePortion {
    private String lineData = null;

    private String previousLineData = null;

    public FilePortionString(String filename, int sortingModifier) {
        super(filename, sortingModifier);
    }

    public String getLineData() {
        return lineData;
    }

    public void setLineData(String lineData) {
        this.lineData = lineData;
    }

    public String getPreviousLineData() {
        return previousLineData;
    }

    public void setPreviousLineData(String previousLineData) {
        this.previousLineData = previousLineData;
    }

    @Override
    public int compareDataTo(FilePortion that) {
        return this.lineData.compareTo(((FilePortionString)that).getLineData());
    }

    private int comparePrevDataTo(String newData) {
        if (null == previousLineData) {
            return (-1 * sortingModifier); //< 0 (newData > previousLineData)
        }
        return this.previousLineData.compareTo(newData);
    }

    @Override
    public int readLineFromFile() throws RuntimeException, IOException {
        currentLineNum += 1;
        String currentLine;
        try {
            currentLine = raf.readLine(); //IOException
        } catch (IOException e) {
            String exceptionMessage = "In file \""
                    + filename
                    + "\" in line "
                    + currentLineNum
                    + " readLine function could not be performed, file will be ignored";
            throw new IOException(exceptionMessage);
        }
        if (null == currentLine) {
            return 1;
        }
        String fromLine = getStringFromLine(currentLine); //RuntimeException that may occur is propagated
        if (0 < (this.comparePrevDataTo(fromLine) * sortingModifier)) {
            String exceptionMessage = "In file \""
                    + filename
                    + "\" in line "
                    + currentLineNum
                    + " a string read was smaller than the last legitimate string read, line will be ignored";
            throw new RuntimeException(exceptionMessage);
        }
        lineData = fromLine;
        return 0;
    }

    @Override
    public void writeToOutputFile(OutputWriter ow) {
        ow.writeToFile(lineData);
        previousLineData = lineData;
        lineData = null;
    }

    public String getStringFromLine(String line) throws RuntimeException {
        if (line.contains(" ")) {
            String exceptionMessage = "In file \""
                    + filename
                    + "\" in line "
                    + currentLineNum
                    + " a \" \"(space) symbol present, line will be ignored.";
            throw new RuntimeException(exceptionMessage);
        }
        return line;
    }
}

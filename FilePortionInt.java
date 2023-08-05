import java.io.IOException;

public class FilePortionInt extends FilePortion {
    private Integer lineData = null;

    private Integer previousLineData = null;

    public FilePortionInt(String filename, int sortingModifier) {
        super(filename, sortingModifier);
    }

    public Integer getLineData() {
        return lineData;
    }

    public void setLineData(Integer lineData) {
        this.lineData = lineData;
    }

    public Integer getPreviousLineData() {
        return previousLineData;
    }

    public void setPreviousLineData(Integer previousLineData) {
        this.previousLineData = previousLineData;
    }

    @Override
    public int compareDataTo(FilePortion that) {
        return this.lineData.compareTo(((FilePortionInt)that).getLineData());
    }

    @Override
    public boolean hasData() {
        return (lineData != null);
    }

    private int comparePrevDataTo(Integer newData) {
        if (null == previousLineData) {
            return (-1 * sortingModifier); //< 0 (newData > previousLineData)
        }
        return this.previousLineData.compareTo(newData);
    }

    @Override
    public int readLineFromFile() throws FormatException, IOException {
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
        Integer fromLine = getIntFromLine(currentLine); //FormatException that may occur is propagated
        if (0 < (this.comparePrevDataTo(fromLine) * sortingModifier)) {
            String exceptionMessage = "In file \""
                    + filename
                    + "\" in line "
                    + currentLineNum
                    + " an integer read was smaller than the last legitimate integer read, line will be ignored";
            throw new FormatException(exceptionMessage);
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

    private Integer getIntFromLine(String line) throws FormatException {
        Integer res;
        try {
            res = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            lineData = null;
            String exceptionMessage = "In file \""
                    + filename
                    + "\" in line "
                    + currentLineNum
                    + " an integer could not be read, line will be ignored.";
            throw new FormatException(exceptionMessage);
        }
        return res;
    }
}

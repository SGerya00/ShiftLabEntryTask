import java.util.List;

public class ExecutionParameters {

    private final boolean sortAscending;

    //can either be Integer.class or String.class
    private final Object datatype;

    private final String outputFileName;

    private final List<String> inputFileNames;

    ExecutionParameters(boolean sortType, Object datatype, String filename, List<String> filenames) {
        sortAscending = sortType;
        this.datatype = datatype; //this constructor will not execute if datatype is neither of allowed
        outputFileName = filename;
        inputFileNames = filenames;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public Object getDatatype() {
        return datatype;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public List<String> getInputFileNames() {
        return inputFileNames;
    }
}

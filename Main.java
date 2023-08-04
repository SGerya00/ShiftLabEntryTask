import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static ExecutionParameters checkArguments(String[] args) throws RuntimeException {
        boolean sortAscending = true;
        Object datatype;
        String outputFileName;
        List<String> inputFileNames = new ArrayList<>();

        if (args.length < 3) {
            //not enough parameters for program execution
            String exceptionMessage = "Not enough parameters for program execution\n" +
                    "parameters should be as below:\n" +
                    "[-a or -d] <-s or -i> <output file name> <input file names>+\n" +
                    "where [] means that parameter is optional (-a by default)\n" +
                    "<> means that parameter is obligatory\n" +
                    "+ after brackets means there should be no less than 1 of that parameter\n";
            throw new RuntimeException(exceptionMessage);
        }

        int currentParam = 0; //presuming 1st argument is sorting type

        if (args[currentParam].equals("-d")) {
            sortAscending = false;
        }
        else if (args[currentParam].equals("-a")) {
            //sortAscending = true;
            //currentParam = 0;
        }
        else {
            currentParam -= 1; //first argument is not responsible for sorting type
        }
        currentParam += 1;

        if (args[currentParam].equals("-s")) {
            datatype = String.class;
        } else if (args[currentParam].equals("-i")) {
            datatype = Integer.class;
        } else {
            //obligatory parameter responsible for datatype is incorrect
            String exceptionMessage =
                    "Parameter responsible for datatype is incorrect, provided \""
                            + args[currentParam]
                            + "\", expected \"-s\" for String or \"-i\" for integer\n";
            throw new RuntimeException(exceptionMessage);
        }
        currentParam += 1;

        outputFileName = args[currentParam];
        currentParam += 1;

        if (currentParam == args.length) {
            //enough arguments, but no input file names given
            String exceptionMessage = "No input files were specified, expected at least one";
            throw new RuntimeException(exceptionMessage);
        }
        for (int i = currentParam; i < args.length; i++) {
            inputFileNames.add(args[i]);
        }

        return new ExecutionParameters(sortAscending, datatype, outputFileName, inputFileNames);
    }

    public static void main(String[] args) {
        ExecutionParameters ep;
        try {
            ep = checkArguments(args);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("all good in hood");

        OutputWriter ow = new OutputWriter(ep.getOutputFileName());
        try {
            ow.openFileForWriting();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        FilePortionFactory fpf = new FilePortionFactory(ep.getDatatype());
        FilePortion fp1 = fpf.createFilePortion(ep.isSortAscending());
        fp1.setFilename(ep.getInputFileNames().get(0));
        try {
            fp1.openFile();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            try {
                ow.closeFile();
            } catch (IOException ee) {
                System.err.println(ee.getMessage());
            }
            return;
        }

        while(true) {
            int res = 0;
            try {
                res = fp1.readLineFromFile();
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                continue;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                break;
            }
            if (1 == res) {
                System.out.println("EOF reached, file will now be ignored");
                break;
            } else {
                fp1.writeToOutputFile(ow);
            }
        }

        try {
            ow.closeFile();
        } catch (IOException ee) {
            System.err.println(ee.getMessage());
        }
        try {
            fp1.closeFile();
        } catch (IOException ee) {
            System.err.println(ee.getMessage());
        }

        System.out.println("Execution complete!");
    }
}

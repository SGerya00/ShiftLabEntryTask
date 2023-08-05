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

    //returns 0 if a line was successfully read
    //returns 1 if IOException happened with input file or EOF reached -> ignore file from now on
    private static int readOneLineFromInputFile(FilePortion fp) {
        try {
            fp.openFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 1;
        }

        try {
            fp.moveToPosition();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                fp.closeFile();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
            return 1;
        }

        int res = 0;
        while(true) {
            try {
                res = fp.readLineFromFile();
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                continue;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                try {
                    fp.closeFile();
                } catch (IOException ee) {
                    System.err.println(ee.getMessage());
                }
                return 1;
            }
            break;
        }
        if (1 == res) {
            try {
                fp.closeFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            return 1;
        }

        try {
            fp.rememberPosition();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                fp.closeFile();
            } catch (IOException ee) {
                System.err.println(ee.getMessage());
            }
            return 1;
        }

        try {
            fp.closeFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return 1;
        }

        return 0;
    }

    //0 if success
    //1 if IOException
    private static int openOutput(OutputWriter ow) {
        try {
            ow.openFileForWriting();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return 1;
        }
        return 0;
    }

    //0 if success
    //1 if IOException
    private static int closeOutput(OutputWriter ow) {
        try {
            ow.closeFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return 1;
        }
        return 0;
    }

    //0 if execution completed normally
    //1 if execution completed due to an exception
    private static int mainCycle(ExecutionParameters ep, OutputWriter ow) {
        int sortCoef = (ep.isSortAscending() ? (1) : (-1));

        FilePortionFactory fpf = new FilePortionFactory(ep.getDatatype());
        List<FilePortion> filePortions = new ArrayList<>(); //array list is good as random access will be frequent

        for (String filename : ep.getInputFileNames()) {
            try {
                filePortions.add(fpf.createFilePortion(filename, ep.isSortAscending()));
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return 1; //although no exception is expected to arise, catching it is good idea (just in case)
            }
        }

        while(true) {
            int greatestElementIndex = 0; //depending on sorting type, might either be biggest or smallest
            int arrSize = filePortions.size();
            for(int i = 0; i < arrSize; i++) {
                int res = 0;
                boolean hasData;
                try {
                    hasData = filePortions.get(i).hasData();
                } catch(IndexOutOfBoundsException e) {
                    System.err.println(e.getMessage());
                    return 1; //this exception is not expected because of algorithm logic, but let's still catch it
                }
                if (!hasData) {
                    try {
                        res = readOneLineFromInputFile(filePortions.get(i));
                    } catch (IndexOutOfBoundsException e) {
                        System.err.println(e.getMessage());
                        return 1; //this exception is not expected because of algorithm logic, but let's still catch it
                    }
                    if (1 == res) {
                        try {
                            filePortions.remove(i);
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                            return 1; //again, no exceptions are supposed to arise, but let's still catch them
                        }
                        i -= 1;
                        arrSize -= 1;
                        continue;
                    }
                }
                try {
                    res = (filePortions.get(i).compareDataTo(filePortions.get(greatestElementIndex)) * sortCoef);
                } catch (IndexOutOfBoundsException e) {
                    System.err.println(e.getMessage());
                    return 1; //this exception is not expected because of algorithm logic, but let's still catch it
                }
                if (res < 0) {
                    greatestElementIndex = i;
                }
            }
            arrSize = filePortions.size();
            if (arrSize == 0) {
                break; //all files have reached an end or had IOException that prevents further interaction with them
            }
            try {
                filePortions.get(greatestElementIndex).writeToOutputFile(ow);
            } catch (IndexOutOfBoundsException e) {
                System.err.println(e.getMessage());
                return 1; //this exception is not expected because of algorithm logic, but let's still catch it
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        ExecutionParameters ep;
        try {
            ep = checkArguments(args);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return;
        }

        OutputWriter ow = new OutputWriter(ep.getOutputFileName());
        if (1 == openOutput(ow)) {
            return;
        }

        mainCycle(ep, ow);

        closeOutput(ow);
    }
}

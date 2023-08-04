public class FilePortionFactory {
    private final Object typeToSuit;

    FilePortionFactory(Object type) {
        typeToSuit = type;
    }

    public FilePortion createFilePortion(String filename, boolean sortAsc) {
        FilePortion forReturn;
        if (typeToSuit == Integer.class) {
            forReturn = new FilePortionInt(filename, (sortAsc ? (1) : (-1)));
        } else {
            forReturn = new FilePortionString(filename, (sortAsc ? (1) : (-1)));
        }

        return forReturn;
    }

}

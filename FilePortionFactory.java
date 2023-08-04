public class FilePortionFactory {
    private final Object typeToSuit;

    FilePortionFactory(Object type) {
        typeToSuit = type;
    }

    public FilePortion createFilePortion(boolean sortAsc) {
        FilePortion forReturn;
        if (typeToSuit == Integer.class) {
            forReturn = new FilePortionInt();
        } else {
            forReturn = new FilePortionString();
        }

        if (sortAsc) {
            forReturn.setSortingModifier(1);
        } else {
            forReturn.setSortingModifier(-1);
        }

        return forReturn;
    }

}

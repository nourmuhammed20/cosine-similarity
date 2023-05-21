import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PositionalIndex positionalIndex = new PositionalIndex();
        String folderPath = "F:\\FCAI\\#3 year\\#2 term\\#1 NEW MATERIAL\\IR\\Assignments\\TestIR\\src\\Files";  // Provide the path to the folder containing the text files

        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            int docId = 1;

            for (File file : files) {
                if (file.isFile()) {
                    positionalIndex.buildIndex(file.getPath(), docId);
                    docId++;
                }
            }
            System.out.println("Enter Query to Search :");
            Scanner scanner = new Scanner(System.in);
            String phrase = scanner.nextLine();
            positionalIndex.searchQuery(phrase);
//            positionalIndex.printIndex();
        }
    }
}
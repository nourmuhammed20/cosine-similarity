import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {
//  Crawling Part
        int maxDepth = 2; // Specify the maximum depth
        WebCrawlerThreads crawler = new WebCrawlerThreads(maxDepth,"F:\\FCAI\\#3 year\\#2 term\\#1 NEW MATERIAL\\IR\\Assignments\\Crawler\\src\\Files");

//      WebCrawler crawler = new WebCrawler(maxDepth);
        String startURL = "https://en.wikipedia.org/wiki/Information_system"; // Specify the starting URL
        System.out.println("Pleas Wait Until Crawiling The Links");
        crawler.getPageLinks(startURL, 0); // Start crawling with depth 0
        crawler.shutdown(); // Shutdown the executor service after crawling is complete
        crawler.PrintFinish();

        System.out.println("Enter Query :");
        Scanner Scan = new Scanner(System.in);
        String Query = Scan.nextLine();
//  Scrapping Part
        crawler.GetContent(Query);

//  indexing Part
        PositionalIndex positionalIndex = new PositionalIndex();
        String folderPath = "F:\\FCAI\\#3 year\\#2 term\\#1 NEW MATERIAL\\IR\\Assignments\\Crawler\\src\\Files";  // Provide the path to the folder containing the text files

        File folder = new File(folderPath);
        List<String> texts = new ArrayList<>();
        File[] files = folder.listFiles();

        if (files != null) {
            int docId = 1;

            for (File file : files) {
                if (file.isFile()) {
                    positionalIndex.buildIndex(file.getPath(), docId);
                    docId++;
                }
            }
            for (int i = 1; i <= 10; i++) {
                String filePath = "F:\\FCAI\\#3 year\\#2 term\\#1 NEW MATERIAL\\IR\\Assignments\\Crawler\\src\\Files\\file" + i + ".txt";
                String text = null;
                try {
                    text = Files.readAllLines(Paths.get(filePath)).stream().collect(Collectors.joining(" "));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                texts.add(text);
            }
//           positionalIndex.printIndex();
            CosineSimilarity cs = new CosineSimilarity();
            cs.calculateCosineSimilarityScores(texts, Query);
            positionalIndex.searchQuery(Query);
//            positionalIndex.GetDocumentWords();
            double scoreTest = cs.score("the best data science course", "data science is popular");
            System.out.println("Cosine similarity Section Example = " + scoreTest);
            positionalIndex.Calc_TF_IDF(Query);
        }
    }
}


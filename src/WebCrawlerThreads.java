import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebCrawlerThreads {
    private Set<String> crawledLinks = new HashSet<>();
    private int maxDepth;
    private int urlCounter;
    private ExecutorService executorService;
    private int fileCounter;
    private String outputPath;

    public WebCrawlerThreads(int maxDepth, String outputPath) {
        this.maxDepth = maxDepth;
        this.urlCounter = 0;
        this.executorService = Executors.newFixedThreadPool(10);
        this.fileCounter = 1;
        this.outputPath = outputPath;
    }

    public void getPageLinks(String URL, int depth) {
        // Check if the URL has already been crawled or exceeds the maximum depth
        if (crawledLinks.contains(URL) || depth > maxDepth) {
            return;
        }

        try {
            // Add the URL to the set of crawled links
            crawledLinks.add(URL);
            urlCounter++;
            if(urlCounter == 100 || urlCounter ==  200 || urlCounter == 300 ||urlCounter == 400||urlCounter == 500 ||urlCounter == 600){
            System.out.printf("%d Crawled Links%n", urlCounter);
            }
//            System.out.println(urlCounter + ". Crawled URL: " + URL);

            // Fetch the HTML code
            Document document = Jsoup.connect(URL).get();
            // Parse the HTML to extract links to other URLs
            Elements linksOnPage = document.select("a[href]");

            // For each extracted URL... go back to Step 4.
            for (Element page : linksOnPage) {
                String extractedURL = page.attr("abs:href");

                // Execute crawling of the extracted URL in a separate thread if the executor service is still active
                if (!executorService.isShutdown()) {
                    executorService.execute(() -> getPageLinks(extractedURL, depth));
                }
            }
        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        }
    }
    public void PrintFinish(){
        System.err.printf("Web Crawling finished with %d %n", urlCounter);
    }
    public void GetContent(String query) {
        int count = 0;
        for (String url : crawledLinks) {
            if (containsQuery(url, query)) {
                try {
                    Document document = Jsoup.connect(url).get();
                    String text = document.body().text();
                    if (fileCounter <= 10) {
                        String filename = outputPath + "/file" + fileCounter + ".txt";
                        // Save the extracted text to a file
                        saveToFile(text, filename);
                        fileCounter++;
                    } else {
                        break; // Limit reached, stop writing files
                    }
                } catch (IOException e) {
                    System.err.println("Error fetching content from URL: " + url);
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean containsQuery(String url, String query) {
        String[] queryWords = query.toLowerCase().split("\\s+");
        for (String word : queryWords) {
            if (url.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    private void saveToFile(String text, String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println(text);
        } catch (FileNotFoundException e) {
            System.err.println("Error saving content to file: " + filename);
            e.printStackTrace();
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Thread pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

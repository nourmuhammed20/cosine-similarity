import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WebCrawler {
    private Set<String> crawledLinks = new HashSet<>();
    private int maxDepth;
    private int urlCounter;

    public WebCrawler(int maxDepth) {
        this.maxDepth = maxDepth;
        this.urlCounter = 0;
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
            System.out.println( urlCounter +". Crawled URL " + ": " + URL);

            // Fetch the HTML code
            Document document = Jsoup.connect(URL).get();
            // Parse the HTML to extract links to other URLs
            Elements linksOnPage = document.select("a[href]");

            // For each extracted URL... go back to Step 4.
            for (Element page : linksOnPage) {
                String extractedURL = page.attr("abs:href");

                // Recursively crawl the extracted URL
                getPageLinks(extractedURL, depth);
            }
        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        }
    }
}
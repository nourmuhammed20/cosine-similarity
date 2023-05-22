import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class PositionalIndex {
    private Map<String, Map<Integer, StringBuilder>> index;
    private Map<Integer, Map<String, Integer>> termFrequencies;
    ArrayList<Integer> wordCounts;
    public List<Integer> docIds;
    List<String> stopWords = Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with", "about", "above", "after",
            "again", "against", "all", "am", "an", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below",
            "between", "both", "but", "by", "can't", "cannot", "could",
            "couldn't", "did", "didn't", "do", "does", "doesn't", "doing",
            "don't", "down", "during", "each", "few", "for", "from", "further",
            "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he",
            "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself",
            "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm",
            "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its",
            "itself", "let's", "me", "more", "most", "mustn't", "my", "myself",
            "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other",
            "ought", "our", "ours", "ourselves", "out", "over", "own", "same",
            "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't",
            "so", "some", "such", "than", "that", "that's", "the", "their",
            "theirs", "them", "themselves", "then", "there", "there's", "these",
            "they", "they'd", "they'll", "they're", "they've", "this", "those",
            "through", "to", "too", "under", "until", "up", "very", "was",
            "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't",
            "what", "what's", "when", "when's", "where", "where's", "which",
            "while", "who", "who's", "whom", "why", "why's", "with", "won't",
            "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've",
            "your", "yours", "yourself", "yourselves"
    );

    public PositionalIndex() {
        index = new HashMap<>();
        termFrequencies = new HashMap<>();
        wordCounts = new ArrayList<>(); // Initialize the wordCounts map
        docIds = new ArrayList<>(); // Initialize the docIds list
    }


    public void buildIndex(String filePath, int docId) {
        int wordCount =0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int position = 1;
            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\s+");

                for (String word : words) {
                    word = StringCleanser(word);
                    wordCount++;
                    if (!index.containsKey(word)) {
                        index.put(word, new HashMap<>());
                    }

                    if (!index.get(word).containsKey(docId)) {
                        index.get(word).put(docId, new StringBuilder());
                    }

                    index.get(word).get(docId).append(position).append(" ");
                    position++;

                    // Update term frequency for the word in the document
                    Map<String, Integer> docTermFrequencies = termFrequencies.getOrDefault(docId, new HashMap<>());
                    docTermFrequencies.put(word, docTermFrequencies.getOrDefault(word, 0) + 1);
                    termFrequencies.put(docId, docTermFrequencies);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        wordCounts.add(wordCount);
    }

    public void searchQuery(String query) {
        String Query = StringCleanser(query);
        String[] queryWords = Query.toLowerCase().split("\\s+");
        Map<Integer, StringBuilder> firstWordDocs = index.get(queryWords[0]);
        if (firstWordDocs == null) {
            System.out.println("No results found.");
            return;
        }
        for (Map.Entry<Integer, StringBuilder> docEntry : firstWordDocs.entrySet()) {
            Integer docId = docEntry.getKey();
            StringBuilder positions = docEntry.getValue();

            String[] positionsArr = positions.toString().trim().split("\\s+");

            boolean matchFound = true;
            int prevPosition = Integer.parseInt(positionsArr[0]);

            for (int i = 1; i < queryWords.length; i++) {
                Map<Integer, StringBuilder> nextWordDocs = index.get(queryWords[i]);

                if (nextWordDocs == null || !nextWordDocs.containsKey(docId)) {
                    matchFound = false;
                    break;
                }

                StringBuilder nextPositions = nextWordDocs.get(docId);
                String[] nextPositionsArr = nextPositions.toString().trim().split("\\s+");

                int nextPosition = Integer.parseInt(nextPositionsArr[0]);

                if (nextPosition != prevPosition + 1) {
                    matchFound = false;
                    break;
                }

                prevPosition = nextPosition;
            }

            if (matchFound) {
                System.out.println("Match found in Doc " + docId);
//                docIds.add(docId);
            }
        }
    }

    public String StringCleanser(String text) {
        // Define the delimiters you want to remove
        String delimiters = "[()`*|.,;:!?\\-\"']";

        // Remove delimiters and periods from the text
        String cleanedText = text.replaceAll(delimiters, "");

        // Remove stop words
        for (String stopWord : stopWords) {
            cleanedText = cleanedText.replaceAll("\\b" + stopWord + "\\b", "");
        }

        // Remove "ing" and "s" from the end of words
        cleanedText = cleanedText.replaceAll("ing\\b|s\\b", "");

        return cleanedText;
    }



    public void Calc_TF_IDF(String query) {
        String[] queryWords = query.toLowerCase().split("\\s+");
        int totalDocuments = 10;

        for (String word : queryWords) {
            int documentsWithKeyword = 0;
            for (int i = 1; i <= totalDocuments; i++) {
                Map<String, Integer> docTermFrequencies = termFrequencies.getOrDefault(i, new HashMap<>());
                if (docTermFrequencies.containsKey(word)) {
                    documentsWithKeyword++;
                }
            }

            double idf = Math.log((double) totalDocuments / documentsWithKeyword);

            for (int i = 1; i <= totalDocuments; i++) {
                Map<String, Integer> docTermFrequencies = termFrequencies.getOrDefault(i, new HashMap<>());
                int termFrequency = docTermFrequencies.getOrDefault(word, 0);
                float normalizedTF = (float) termFrequency / wordCounts.get(i - 1);
                float tfidf = normalizedTF * (float) idf;

                Map<Integer, StringBuilder> wordIndex = index.getOrDefault(word, new HashMap<>());
                StringBuilder normalizedTFStringBuilder = wordIndex.getOrDefault(i, new StringBuilder());
                normalizedTFStringBuilder.append(tfidf).append(" ");
                wordIndex.put(i, normalizedTFStringBuilder);
                index.put(word, wordIndex);

                // Print the TF-IDF value
                System.out.println("TF-IDF for word '" + word + "' in document " + i + ": " + tfidf);
            }
        }
    }




    public void printIndex() {
        for (Map.Entry<String, Map<Integer, StringBuilder>> entry : index.entrySet()) {
            String term = entry.getKey();
            Map<Integer, StringBuilder> docs = entry.getValue();

            System.out.println("<" + term + ", " + docs.size() + ">");

            for (Map.Entry<Integer, StringBuilder> docEntry : docs.entrySet()) {
                int docId = docEntry.getKey();
                StringBuilder positions = docEntry.getValue();
                // Print the word frequency in the document
                Map<String, Integer> docTermFrequencies = termFrequencies.getOrDefault(docId, new HashMap<>());
                int termFrequency = docTermFrequencies.getOrDefault(term, 0);
                System.out.println("Doc " + docId + ": position " + positions.toString().trim() +" Term Frequency: " + termFrequency);
            }

            System.out.println();
        }
    }
}
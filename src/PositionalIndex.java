import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PositionalIndex {
    private Map<String, Map<Integer, StringBuilder>> index;
    private Map<Integer, Map<String, Integer>> termFrequencies;
    public List<Integer> docIds;
    public PositionalIndex() {
        index = new HashMap<>();
        termFrequencies = new HashMap<>();
        docIds = new ArrayList<>(); // Initialize the docIds list
    }

    public void buildIndex(String filePath, int docId) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int position = 1;

            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\s+");

                for (String word : words) {
                    word = StringCleanser(word);
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
    }

    public void searchQuery(String query) {
        String[] queryWords = query.toLowerCase().split("\\s+");
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
        String delimiters = "[()`.,;:!?\\-\"']";

        // Remove delimiters and periods from the text
        String cleanedText = text.replaceAll(delimiters, "");

        return cleanedText;
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
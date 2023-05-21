import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class PositionalIndex {
    private Map<String, Map<Integer, StringBuilder>> index;

    public PositionalIndex() {
        index = new HashMap<>();
    }

    public void buildIndex(String filePath, int docId) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int position = 1;

            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\s+");

                for (String word : words) {
                    if (!index.containsKey(word)) {
                        index.put(word, new HashMap<>());
                    }

                    if (!index.get(word).containsKey(docId)) {
                        index.get(word).put(docId, new StringBuilder());
                    }

                    index.get(word).get(docId).append(position).append(" ");
                    position++;
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
            int docId = docEntry.getKey();
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

                System.out.println("Doc " + docId + ": position " + positions.toString().trim());
            }

            System.out.println();
        }
    }

//        public void computeCosineSimilarity(String query) {
//        String[] queryWords = query.split("\\W+");
//        Map<String, Double> fileSimilarityMap = new HashMap<>();
//
//        // Calculate the query vector
//        Map<String, Double> queryVector = calculateTermVector(queryWords);
//
//        // Calculate the cosine similarity for each file
//        for (Map.Entry<Integer, String> sourceEntry : sources.entrySet()) {
//            int docId = sourceEntry.getKey();
//            String file = sourceEntry.getValue();
//
//            // Calculate the file vector
//            Map<String, Double> fileVector = calculateTermVectorForFile(docId);
//
//            // Compute the dot product
//            double dotProduct = calculateDotProduct(queryVector, fileVector);
//
//            // Compute the magnitude of query vector
//            double queryMagnitude = calculateVectorMagnitude(queryVector);
//
//            // Compute the magnitude of file vector
//            double fileMagnitude = calculateVectorMagnitude(fileVector);
//
//            // Check for zero vector lengths
//            if (queryMagnitude == 0 || fileMagnitude == 0) {
//                // Handle the case where the vectors contain zero values
//                fileSimilarityMap.put(file, 0.0);
//            } else {
//                // Compute the cosine similarity
//                double cosineSimilarity = dotProduct / (queryMagnitude * fileMagnitude);
//
//                // Store the cosine similarity for the file
//                fileSimilarityMap.put(file, cosineSimilarity);
//            }
//        }
//
//        // Sort the files based on the cosine similarity in descending order
//        List<Map.Entry<String, Double>> sortedFiles = new ArrayList<>(fileSimilarityMap.entrySet());
//        sortedFiles.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
//
//        // Rank and print the top 10 files
//        System.out.println("Ranking of Files based on Cosine Similarity:");
//        int rank = 1;
//        for (Map.Entry<String, Double> fileEntry : sortedFiles) {
//            if (rank > 10) {
//                break;
//            }
//
//            String file = fileEntry.getKey();
//            double similarity = fileEntry.getValue();
//
//            System.out.println(rank + ". " + file + " (Cosine Similarity: " + similarity + ")");
//            rank++;
//        }
//    }
//
//    private Map<String, Double> calculateTermVector(String[] words) {
//        Map<String, Double> termVector = new HashMap<>();
//        int totalWords = words.length;
//
//        // Calculate the term frequency in the query
//        for (String word : words) {
//            word = word.toLowerCase();
//
//            // Check if the word is a stop word
//            if (stopWords.contains(word)) {
//                continue;
//            }
//
//            termVector.put(word, termVector.getOrDefault(word, 0.0) + 1);
//        }
//
//        // Normalize the term frequency
//        for (Map.Entry<String, Double> entry : termVector.entrySet()) {
//            double normalizedFrequency = entry.getValue() / totalWords;
//            termVector.put(entry.getKey(), normalizedFrequency);
//        }
//
//        return termVector;
//    }
//
//    private Map<String, Double> calculateTermVectorForFile(int docId) {
//        Map<String, Double> termVector = new HashMap<>();
//        DictEntry dictEntry = index.getOrDefault(docId, new DictEntry());
//
//        int totalWords = dictEntry.getTerm_freq();
//
//        // Calculate the term frequency in the file
//        Posting posting = dictEntry.pList;
//        while (posting != null) {
//            String word = sources.get(posting.docId);
//
//            double tf = posting.getDtf() / (double) totalWords;
//            termVector.put(word, tf);
//
//            posting = posting.next;
//        }
//
//        return termVector;
//    }
//
//    private double calculateDotProduct(Map<String, Double> vector1, Map<String, Double> vector2) {
//        double dotProduct = 0.0;
//
//        for (Map.Entry<String, Double> entry : vector1.entrySet()) {
//            String term = entry.getKey();
//            double frequency1 = entry.getValue();
//            double frequency2 = vector2.getOrDefault(term, 0.0);
//
//            dotProduct += frequency1 * frequency2;
//        }
//
//        return dotProduct;
//    }
//
//    private double calculateVectorMagnitude(Map<String, Double> vector) {
//        double magnitude = 0.0;
//
//        for (double frequency : vector.values()) {
//            magnitude += Math.pow(frequency, 2);
//        }
//
//        return Math.sqrt(magnitude);
//    }

}

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {
    public static void printHelp() {
        // Get .jar filename.
        String filename = new java.io.File(Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();

        System.err.printf("Usage: java %s.jar filepath\n", filename);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            printHelp();
            System.exit(1);
        }

        String fileLine;
        HashMap<String, Integer> counter = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));

            // Read the first line.
            fileLine = reader.readLine();

            // fileLine == null if EOF.
            while (fileLine != null) {
                // Split the string into words. Sometimes it produces empty strings,
                // we'll filter them out by ourselves.
                // NOTE: Regular String::split() method will not work with UTF-8 strings.
                Pattern pattern = Pattern.compile("[^\\p{IsAlphabetic}']+", Pattern.UNICODE_CHARACTER_CLASS);
                String[] wordsInLine = fileLine.split(pattern.toString());

                for (String word : wordsInLine) {
                    if (word.equals("")) {
                        continue;
                    }
                    // Place words into HashMap.
                    word = word.toLowerCase();
                    Integer currentCount = counter.getOrDefault(word, 0);
                    counter.put(word, currentCount + 1);
                }

                // Read next line.
                fileLine = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            System.err.printf("Error: file \"%s\" not found!\n", args[0]);
            System.exit(1);
        } catch (IOException ex) {
            System.err.printf("Error: exception while reading file! Exception message: %s\n", ex.getMessage());
            System.exit(1);
        }

        // We need to print out 20 the most frequent words.
        // Sometimes there could be less than 20 words, so
        // we use the following code to find the maximum
        // amount of words to output.
        int maxWordsForOutput = Math.min(20, counter.size());

        for (int i = 0; i < maxWordsForOutput; i++) {
            // Find the maximum counter value.
            int max = Collections.max(counter.values());
            // Find the particular entry with the maximum counter value.
            for (Map.Entry<String, Integer> entry : counter.entrySet()) {
                if (entry.getValue() == max) {
                    System.out.printf("%s: %d\n", entry.getKey(), entry.getValue());

                    // Remove that found entry to not find it again.
                    counter.remove(entry.getKey());
                    break;
                }
            }
        }
    }
}

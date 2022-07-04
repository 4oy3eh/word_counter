import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;


public class MainStreamAPI {
    public static void printHelp() {
        // Get .jar filename.
        String filename = new java.io.File(MainStreamAPI.class.getProtectionDomain()
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

                Arrays.stream(wordsInLine)
                        .filter(word -> !word.equals(""))
                        .forEach(word -> {
                            // Place words into HashMap.
                            word = word.toLowerCase();
                            Integer currentCount = counter.getOrDefault(word, 0);
                            counter.put(word, currentCount + 1);
                        });

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
            Optional<Map.Entry<String, Integer>> maxEntryOpt = counter.entrySet().stream().max(Map.Entry.comparingByValue());

            // Should be true every single time.
            // The only reason why this if exists is that
            // otherwise java compiler will produce warnings.
            if (maxEntryOpt.isPresent()) {
                Map.Entry<String, Integer> maxEntry = maxEntryOpt.get();

                System.out.printf("%s: %d\n", maxEntry.getKey(), maxEntry.getValue());
                counter.remove(maxEntry.getKey());
            }
        }
    }
}

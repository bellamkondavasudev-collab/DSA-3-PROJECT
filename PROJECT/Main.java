import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Main {

    // Preprocess text: lowercase, remove punctuation, remove stop words, tokenize
    public static List<String> preprocess(String text) {
        text = text.toLowerCase();
        text = text.replaceAll("[^a-z0-9\\s]", " ");

        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "is", "are", "was", "were",
            "to", "of", "in", "on", "for", "with", "at", "by", "from",
            "this", "that", "it", "as", "be", "has", "have", "had"
        ));

        String[] tokens = text.trim().split("\\s+");
        List<String> words = new ArrayList<>();

        for (String token : tokens) {
            if (!token.isEmpty() && !stopWords.contains(token)) {
                words.add(token);
            }
        }
        return words;
    }

    // Count matching words between the input and reference documents.
    public static int countMatchingWords(List<String> input, List<String> reference) {
        Set<String> referenceWords = new HashSet<>(reference);
        int matches = 0;

        for (String word : input) {
            if (referenceWords.contains(word)) {
                matches++;
            }
        }
        return matches;
    }

    // Calculate similarity as percentage of input words found in reference.
    public static double calculateSimilarity(int matchingWords, int inputWordCount) {
        if (inputWordCount == 0) return 0.0;
        return (matchingWords * 100.0) / inputWordCount;
    }

    // Find common consecutive word sequences.
    public static List<String> findCommonSequences(List<String> input, List<String> reference, int sequenceLength) {
        List<String> sequences = new ArrayList<>();
        Set<String> referenceSequences = new HashSet<>();

        for (int i = 0; i <= reference.size() - sequenceLength; i++) {
            referenceSequences.add(String.join(" ",
                    reference.subList(i, i + sequenceLength)));
        }

        for (int i = 0; i <= input.size() - sequenceLength; i++) {
            String seq = String.join(" ", input.subList(i, i + sequenceLength));
            if (referenceSequences.contains(seq) && !sequences.contains(seq)) {
                sequences.add(seq);
            }
        }
        return sequences;
    }

    public static String readFile(String path) throws IOException {
        Path filePath = Path.of(path);
        if (!Files.exists(filePath)) {
            // If not found, search subdirectories of the working directory
            Path cwd = Path.of(System.getProperty("user.dir"));
            try (var stream = Files.walk(cwd, 3)) {
                Path found = stream
                    .filter(p -> p.getFileName().toString().equals(filePath.getFileName().toString()))
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElse(null);
                if (found != null) {
                    return Files.readString(found);
                }
            }
        }
        return Files.readString(filePath);
    }

    public static void generateReport(
            String inputFile,
            String referenceFile,
            int inputWords,
            int matchingWords,
            double similarity,
            List<String> commonSequences) throws IOException {

        String result;
        if (similarity >= 70) {
            result = "HIGH POTENTIAL PLAGIARISM";
        } else if (similarity >= 40) {
            result = "MODERATE SIMILARITY";
        } else {
            result = "LOW SIMILARITY";
        }

        StringBuilder report = new StringBuilder();
        report.append("WORD-LEVEL PLAGIARISM REPORT\n");
        report.append("====================================\n");
        report.append("Input Document   : ").append(inputFile).append("\n");
        report.append("Reference Document: ").append(referenceFile).append("\n\n");
        report.append("Processed Input Words : ").append(inputWords).append("\n");
        report.append("Matching Words        : ").append(matchingWords).append("\n");
        report.append(String.format(Locale.US, "Similarity Score      : %.2f%%%n", similarity));
        report.append("Result                : ").append(result).append("\n\n");

        report.append("Common Word Sequences:\n");
        if (commonSequences.isEmpty()) {
            report.append("No common sequences found.\n");
        } else {
            for (String sequence : commonSequences) {
                report.append("- ").append(sequence).append("\n");
            }
        }

        Files.writeString(Path.of("plagiarism_report.txt"), report.toString());
        System.out.println("\nReport saved as: plagiarism_report.txt");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("======================================");
            System.out.println(" WORD-LEVEL PLAGIARISM CHECKER");
            System.out.println("======================================");

            System.out.print("Enter input document path (e.g., input.txt): ");
            String inputPath = scanner.nextLine().trim();

            System.out.print("Enter reference document path (e.g., reference.txt): ");
            String referencePath = scanner.nextLine().trim();

            String inputText;
            String referenceText;

            try {
                inputText = readFile(inputPath);
                referenceText = readFile(referencePath);
            } catch (InvalidPathException e) {
                System.out.println("\nError: The path you entered is invalid. Please ensure you are entering a valid file path (like 'input.txt') and not the actual text of the document.");
                return;
            } catch (NoSuchFileException e) {
                System.out.println("\nError: File not found. Please make sure the file exists at the path you provided.");
                return;
            } catch (IOException e) {
                System.out.println("\nError reading file: " + e.getMessage());
                return;
            }

            List<String> inputWords = preprocess(inputText);
            List<String> referenceWords = preprocess(referenceText);

            int matchingWords = countMatchingWords(inputWords, referenceWords);
            double similarity = calculateSimilarity(matchingWords, inputWords.size());

            // Three consecutive matching words are treated as a common sequence.
            List<String> sequences = findCommonSequences(inputWords, referenceWords, 3);

            System.out.println("\n--- PLAGIARISM RESULT ---");
            System.out.println("Input words     : " + inputWords.size());
            System.out.println("Matching words  : " + matchingWords);
            System.out.printf(Locale.US, "Similarity      : %.2f%%%n", similarity);

            if (similarity >= 70) {
                System.out.println("Status          : HIGH POTENTIAL PLAGIARISM");
            } else if (similarity >= 40) {
                System.out.println("Status          : MODERATE SIMILARITY");
            } else {
                System.out.println("Status          : LOW SIMILARITY");
            }

            System.out.println("\nCommon sequences:");
            if (sequences.isEmpty()) {
                System.out.println("No common sequences found.");
            } else {
                for (String sequence : sequences) {
                    System.out.println("- " + sequence);
                }
            }

            try {
                generateReport(
                    inputPath,
                    referencePath,
                    inputWords.size(),
                    matchingWords,
                    similarity,
                    sequences
                );
            } catch (IOException e) {
                System.out.println("\nError writing report file: " + e.getMessage());
            }

        } finally {
            // Consume remaining input if any, to avoid it bleeding into powershell (best effort)
            try {
                while (System.in.available() > 0) {
                    scanner.nextLine();
                }
            } catch (Exception ignored) {}
            scanner.close();
        }
    }
}

WORD-LEVEL PLAGIARISM CHECKER - JAVA

Project purpose:
This project implements the workflow described in the project abstract:
1. Take an input short document and a reference document.
2. Convert text to lowercase.
3. Remove punctuation.
4. Remove common stop words.
5. Tokenize the text into individual words.
6. Compare words between the documents.
7. Calculate a word-level similarity percentage.
8. Identify common three-word sequences.
9. Generate a plagiarism_report.txt file.

Requirements:
- Windows
- Java JDK 11 or later

How to run on Windows:
1. Put Main.java, input.txt and reference.txt in the same folder.
2. Open Command Prompt in that folder.
3. Compile:
   javac Main.java
4. Run:
   java Main
5. When asked:
   Enter input document path: input.txt
   Enter reference document path: reference.txt
6. Open plagiarism_report.txt to see the generated report.

Important:
This is a word-level similarity checker, not a full commercial plagiarism detector.
It does not search the Internet and it may not detect paraphrased or semantically similar text.
The similarity thresholds in the demo are:
- 0-39.99%: Low Similarity
- 40-69.99%: Moderate Similarity
- 70% and above: High Potential Plagiarism

package TextSignature;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author George Slavin A class that creates a TextSignature that can be used
 * for a variety of text analysis. Current this class can: 1) Create a mash-up
 * of different texts 2) Compare two texts to decide if they are from the same
 * author 3) Summarize an article carry sentences that are distinct from a
 * reference text
 */
public class TextSignature {

    private static int tupleSize = 2;
    private Map<List<String>, List<String>> wordMap = new HashMap<>();

    public TextSignature() {
    }

    public TextSignature(String text, int tupleSize) {
        setTupleSize(tupleSize);
        makeMap(text);
    }

    public TextSignature(String text) {
        makeMap(text);
    }

    public TextSignature(File inputFile, int tupleSize) {
        setTupleSize(tupleSize);
        makeMap(getFileText(inputFile));
    }

    public TextSignature(File inputFile) {
        makeMap(getFileText(inputFile));
    }

    private String getFileText(String filename) {
        File inputFile = new File(filename);
        return getFileText(inputFile);
    }

    private String getFileText(File inputFile) {
        String text = "";
        try {
            text = new String(Files.readAllBytes(inputFile.toPath()),
                    StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("Error reading input file!");
            System.exit(1);
        }
        return text;
    }

    /* Used by the constructors */
    private void makeMap(String inputText) {
        addText(inputText);
    }

    public void addText(String inputText) {
        if (inputText.length() < tupleSize + 1) {
            throw new IllegalArgumentException("Number of words in text must be"
                    + "greater than current tuple size:" + tupleSize);
        }
        String[] words = inputText.split(" ");
        for (int i = 0; i < (words.length - (tupleSize)); i++) {
            List<String> key = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for (int j = 0; j < tupleSize; j++) {
                key.add(words[i + j]);
            }
            value.add(words[i + (tupleSize)]);
            if (wordMap.containsKey(key)) {
                value.addAll(wordMap.get(key));
            }
            wordMap.put(key, value);
        }
    }

    public void addFileText(File inputFile) {
        addText(getFileText(inputFile));
    }

    public void addFileText(String inputFileName) {
        addText(getFileText(inputFileName));
    }

    public String writeText(int wordCount) {
        String outputText = "";
        String word;
        List<String> key = new ArrayList<>();
        /* select starting key */
        key.addAll(selectStartKey());
        for (int i = 0; i < wordCount; i++) {
            word = getWord(key);
            outputText += word + " ";
            key.remove(0);
            key.add(word);
        }
        return outputText;
    }

    private List<String> selectStartKey() {
        List<List<String>> keys = new ArrayList<>();
        keys.addAll(wordMap.keySet());
        Random rand = new Random();
        return keys.get(rand.nextInt(keys.size()));

    }

    private String getWord(List<String> key) {
        List<String> valueList = new ArrayList<>();
        Random rand = new Random();
        int size;
        /* return list associated with key */
        if (wordMap.containsKey(key)) {
            valueList = wordMap.get(key);
        } else {
            /* otherwise use all value lists */
            for (List<String> values : wordMap.values()) {
                valueList.addAll(values);
            }
        }
        size = valueList.size();

        return valueList.get(rand.nextInt(size));
    }

    /* compares Texts by computed a weighted error vector between the value lists
     * of keys of the maps in each text.  Keys that have no common values have a error of 1,
     * keys that have the same value list have an error of 0.  Keys whose values
     * differ have vlaues between 0 and 1.
     */
    public static double compareTextSignatures(TextSignature text1, TextSignature text2) {
        return compareMaps(text1.wordMap, text2.wordMap);
    }

    /* computes the total errors from comparing the key/value pairs of each map
     */
    private static double compareMaps(Map<List<String>, List<String>> wordMap1,
            Map<List<String>, List<String>> wordMap2) {
        if (wordMap1.size() < wordMap2.size()) {
            return compareMaps(wordMap2, wordMap1);
        }
        double error = 0;
        int valuesSize = 0;
        List<String> values1;
        List<String> values2;
        for (List<String> key : wordMap2.keySet()) {
            values1 = wordMap1.get(key);
            values1 = (values1 == null) ? new ArrayList<String>() : values1;
            values2 = wordMap2.get(key);
            valuesSize += values2.size();
            error += compareValues(values1, values2);
        }
        error /= valuesSize;
        return error;
    }

    /* computes the difference in frequency of each element in values 2*/
    private static double compareValues(List<String> values1, List<String> values2) {
        double error = 0;
        int count1, count2;
        double freq1, freq2;
        double freqDiff;
        for (String value : values2) {
            count1 = Collections.frequency(values1, value);
            count2 = Collections.frequency(values2, value);
            freq1 = count1 / ((values1.isEmpty()) ? 1 : values1.size());
            freq2 = count2 / values2.size();
            freqDiff = Math.abs(freq2 - freq1);
            error += freqDiff;
        }
        return error;
    }

    /* unrelated sentences seem to consistently
     * have an error of 1 or very close to one
     * summarizeText keeps only sentences that are differnet
     * than the reference text */
    public String summarizeText(String text, double threshold) {
        TextSignature sentenceSig;
        double error;
        String outputText = "";
        for (String sentence : text.split("[.!?;](?<!\\d)(?!\\d)")) {
            sentenceSig = new TextSignature(sentence);
            error = TextSignature.compareTextSignatures(sentenceSig, this);
            System.out.println(error);
            if (error > threshold) {
                outputText += sentence;
            }
        }
        /* compute error between sentence and reference text*/
        return outputText;
    }

    public static int getTupleSize() {
        return tupleSize;
    }

    public static void setTupleSize(int tupleSize) {
        TextSignature.tupleSize = tupleSize;
    }

    public Map<List<String>, List<String>> getWordMap() {
        return wordMap;
    }

    public void setWordMap(Map<List<String>, List<String>> wordMap) {
        this.wordMap = wordMap;
    }

    public void clearWordMap() {
        this.wordMap = new HashMap<>();
    }

    public int getMapSize() {
        return wordMap.size();
    }
}

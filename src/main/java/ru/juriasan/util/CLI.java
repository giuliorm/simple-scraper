package ru.juriasan.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CLI {

    private List<String> urls;
    private List<String> words;
    private String filePath;
    private boolean verbose;
    private boolean wordCount;
    private boolean charactersCount;
    private boolean extractSentences;
    private static final String VERBOSE_SHORT = "-v";
    private static final String VERBOSE_LONG = "--verbose";
    private static final String WORD_COUNT_SHORT = "-w";
    private static final String WORD_COUNT_LONG = "--words";
    private static final String SENTENCES_SHORT = "-e";
    private static final String SENTENCES_LONG = "--extract";
    private static final String CHARECTERS_SHORT = "-c";
    private static final String CHARACTERS_LONG = "--characters";

    public CLI(String... args) throws Exception {
        this.urls = new ArrayList<>();
        this.words = new ArrayList<>();
        this.verbose = false;
        this.wordCount = false;
        this.charactersCount = false;
        this.extractSentences = false;
        read(args);
    }

    public boolean getVerboseFlag(){
        return this.verbose;
    }

    public boolean getWordCountFlag() {
        return this.wordCount;
    }

    public boolean getCharactersCountFlag() {
        return this.charactersCount;
    }

    public boolean getExtractSentencesFlag() {
        return this.extractSentences;
    }

    public List<String> getUrls() {
        return this.urls;
    }

    public List<String> getWords() {
        return this.words;
    }

    private void readUrlsFromFile() throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.urls.add(line);
            }
        }
    }

    private void read(String... args) throws Exception {
        if (args.length < 2 || args.length > 6)
            throw new Exception("Invalid arguments!");

        String urlOrFile = args[0];
        if (urlOrFile.contains("http"))
            this.urls.add(urlOrFile);
        else {
            this.filePath = urlOrFile;
            readUrlsFromFile();
        }
        this.words = Arrays.asList(args[1].split(","));

        for(int i = 2; i < args.length; i++) {
            switch (args[i]) {
                case VERBOSE_SHORT:
                case VERBOSE_LONG:
                    this.verbose = true;
                    break;
                case CHARECTERS_SHORT:
                case CHARACTERS_LONG:
                    this.charactersCount = true;
                    break;
                case WORD_COUNT_SHORT:
                case WORD_COUNT_LONG:
                    this.wordCount = true;
                    break;
                case SENTENCES_SHORT:
                case SENTENCES_LONG:
                    this.extractSentences = true;
                    break;

            }
        }
    }
}

package ru.juriasan.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

public class Parser {

    private Map<String, Integer> wordsCount;
    private Set<String> words;
    private Deque<String> tagStack = new ArrayDeque<>();
    private Map<Character, Integer> symbolsCount;
    private char[] html;
    //currentCharIndex
    private int ch = 0;
    private static final char NODE_BEGIN = '<';
    private static final char NODE_END ='>';
    private static final char EXCLAMATION = '!';
    private static final char SLASH = '/';
    private static final char DASH = '-';

    private <K> Map<K, Integer> buildCountMap(Collection<K> values) {
        Map<K, Integer> map = new HashMap<K, Integer>();
        if (values == null)
            return map;

        for (K value : values) {
            map.put(value, 0);
        }
        return map;
    }

    private <K,V> Map<K, V> cloneMap(Map<K,V> old) {
        try {
            Map<K, V> newMap = old.getClass().newInstance();
            newMap.putAll(old);
            return newMap;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public Parser(String html, Set<String> words) {
        this.html = html.toCharArray();
        this.words = words;
        this.wordsCount = buildCountMap(words);
        this.symbolsCount = new HashMap<Character, Integer>();
    }

    public Map<String, Integer> getWordsCount() {
        return cloneMap(wordsCount);
    }

    public Map<Character, Integer> getSymbolsCount() {
        return cloneMap(symbolsCount);
    }

    private boolean isLetterOrDigit(char letter) {
        return 'a' <=  letter && letter <= 'z' || 'A' <= letter && letter <= 'Z' ||
                '0' <= letter && letter <= '9';
    }

    private void invalidDocument() throws Exception {
        throw new Exception(String.format("Document is invalid at ch %d", ch));
    }

    private void parseComment() throws Exception {
        ch++;
        boolean stop = false;
        if (html[ch] != DASH || html[ch + 1] != DASH)
            invalidDocument();
        while(!stop) {
            switch (html[ch]) {
                case NODE_END:
                        ch++;
                        stop = true;
                default: ch++; break;
            }
        }
    }

    private void parseSingleNode() throws Exception {
        boolean stop = false;
        ch++;
        if (html[ch] == NODE_END)
            invalidDocument();
        while(!stop) {
            switch (html[ch]) {
                case NODE_END:
                    stop = true;
                    break;
                default:
                    ch++;

            }
        }
    }
    private String parseNodeName(boolean end) throws Exception {
        if (html[ch] != NODE_BEGIN)
            invalidDocument();
        ch++;
        StringBuilder tagName = new StringBuilder();
        if(!end && html[ch] == SLASH)
            invalidDocument();

        while(true) {
            switch(html[ch]) {
                case EXCLAMATION:
                    if (html[ch + 1] == DASH)
                        parseComment();
                    else
                        parseSingleNode();
                    break;
                case NODE_END:
                    if (html[ch - 1] != NODE_BEGIN) { ch++; return tagName.toString(); } else invalidDocument();
                    break;
                default: tagName.append(ch); ch++; break;
            }
        }
    }
    private void countSymbol() throws Exception {
        if (symbolsCount.containsKey(html[ch]))
            symbolsCount.put(html[ch], symbolsCount.get(html[ch]) + 1);
        else
            symbolsCount.put(html[ch], 1);
    }

    private StringBuilder handleNotLetterOrDigit(StringBuilder word) throws  Exception {
        String w = word.toString().toLowerCase();
        word = new StringBuilder();
        Integer value = wordsCount.getOrDefault(w, null);
        if (words.size() == 0 || words.contains(w)) {
            wordsCount.put(w, value == null ? 1 : value + 1);
        }
        return word;
    }

    private StringBuilder handleLetterOrDigit(StringBuilder word) throws Exception {
        word.append(html[ch]);
        return word;
    }

    private void parseNode() throws Exception {
        parseNodeName(false);
        StringBuilder word = new StringBuilder();
        boolean stop = false;
        while(!stop) {
            if (ch >= html.length)
                return;
            switch (html[ch]) {
                case NODE_BEGIN:
                    if (word.length() > 0)
                        handleNotLetterOrDigit(word);
                    if (html[ch + 1] == SLASH) {
                        parseNodeName(true);
                        stop = true;
                    } else parseNode();
                    break;
                default: {
                    if (isLetterOrDigit(html[ch]))
                        word = handleLetterOrDigit(word);
                    else
                        word = handleNotLetterOrDigit(word);
                    countSymbol();
                    ch++;
                } break;
            }
        }
    }

    public void parse() throws Exception {
        if (html.length == 0)
            return;
        StringBuilder word = new StringBuilder();
        while(ch < html.length) {
            switch (html[ch]) {
                case NODE_BEGIN: parseNode(); break;
                default: {
                    if (isLetterOrDigit(html[ch]))
                        word = handleLetterOrDigit(word);
                    else
                        word = handleNotLetterOrDigit(word);
                    countSymbol();
                    ch++;
                } break;
            }
        }
        if (word.length() > 0)
            handleNotLetterOrDigit(word);
    }
}

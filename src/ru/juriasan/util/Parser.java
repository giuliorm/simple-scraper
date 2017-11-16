package ru.juriasan.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

public class Parser {

    private Map<String, Integer> wordsCount;
    private Map<Character, Integer> symbolsCount;
    private char[] html;
    //currentCharIndex
    private int ch = 0;
    private static final char NODE_BEGIN = '<';
    private static final char NODE_END ='>';
    private static final char EOF = '0';
    private static final char EXCLAMATION = '!';
    private static final char SLASH = '/';

    private <K> Map<K, Integer> buildCountMap(Collection<K> values) {
        Map<K, Integer> map = new HashMap<>();
        if (values == null)
            return map;

        for (K value : values) {
            map.put(value, 0);
        }
        return map;
    }

    public void Parser(String html, Set<String> words, Set<Character> symbols) {
        this.html = html.toCharArray();
        this.wordsCount = buildCountMap(words);
        this.symbolsCount = buildCountMap(symbols);
    }

    private void checkEOF() throws Exception {
        if (ch >= html.length || html[ch] == EOF)
            throw new Exception("Document is invalid!");
    }
    private void invalidDocument() throws Exception {
        throw new Exception("Document is invalid");
    }

    private void parseNodeName(boolean end) throws Exception {
        boolean dontStop = true;

        while(dontStop) {
            switch(html[ch]) {
                case NODE_BEGIN:
                case EOF:
                 invalidDocument();
                case NODE_END:
                    boolean endValid = true;
                    if (end)
                        endValid = html[ch - 1] == SLASH;
                    endValid &= html[ch - 1] != NODE_BEGIN;
                    if (endValid) {
                        dontStop = false;
                        ch++;
                    } else invalidDocument();
                default: ch++; break;
            }
        }
    }



    private void parseNode() throws Exception {
        parseNodeName(false);
        switch(html[ch]) {
            case NODE_BEGIN: ch++; parseNode();
            case NODE_END: ch++; return;
            case EXCLAMATION:
        }
    }

    public void parse(boolean countWords, boolean countSymbols) throws Exception {
        if (html.length == 0)
            return;

        while(html[ch] != EOF) {
            switch (html[ch]) {
                case NODE_BEGIN: parseNode();
            }
            if (ch == NODE_BEGIN) {

                ch++;
            }
            ch++;
        }
    }
}

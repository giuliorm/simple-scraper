package ru.juriasan.util;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ParserTest {

    @Test
    public void noTagTest() {
        String html = "value";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        Map<String, Integer> wordsExpected = new HashMap<String, Integer>();
        wordsExpected.put("value", 1);
        Assert.assertEquals(wordsExpected, p.getWordsCount());
        Map<Character, Integer> expectedSymbols = new HashMap<Character, Integer>();
        expectedSymbols.put('v', 1);
        expectedSymbols.put('a', 1);
        expectedSymbols.put('l', 1);
        expectedSymbols.put('u', 1);
        expectedSymbols.put('e', 1);
        Assert.assertEquals(p.getSymbolsCount(), expectedSymbols);
    }

    @Test
    public void twoOpeningOuterTagsTest() {
        String html = "<html><t>value t1</t>value node1<html>";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
            Assert.fail();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void twoClosingOuterTagsTest() {
        String html = "</html><t>value t1</t>value node1</html>";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
            Assert.fail();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void missingOpeningOuterTagTest() {
        String html = "<t>value t1</t>value node1</html>";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
            Assert.fail();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void missingClosingOuterTagTest() {
        String html = "<html><node1><t>value t1</t>value node1</node1>";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
            Assert.fail();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Test
    public void parserValidDocTest() {
        String html = "<html><node1><t>value t1</t>value node1</node1></html>";
        Parser p = new Parser(html, new HashSet<String>() {{ this.add ("value"); }});
        try {
            p.parse();
        }
        catch (Exception ex) {
            Assert.fail();
        }
        Map<Character, Integer> symbols = p.getSymbolsCount();
        Map<String, Integer> words = p.getWordsCount();

        Map<Character, Integer> expectedSymbols = new HashMap<Character, Integer>();
        expectedSymbols.put('v', 2);
        expectedSymbols.put('a', 2);
        expectedSymbols.put('l', 2);
        expectedSymbols.put('u', 2);
        expectedSymbols.put('e', 3);
        expectedSymbols.put(' ', 2);
        expectedSymbols.put('1', 2);
        expectedSymbols.put('n', 1);
        expectedSymbols.put('o', 1);
        expectedSymbols.put('d', 1);
        expectedSymbols.put('t', 1);
        Assert.assertEquals(symbols, expectedSymbols);
        Map<String, Integer> wordsExpected = new HashMap<String, Integer>();
        wordsExpected.put("value", 2);
        //wordsExpected.put("t1", 1);
        // wordsExpected.put("node1", 1);
        Assert.assertEquals(words, wordsExpected);
    }

}

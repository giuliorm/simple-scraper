package ru.juriasan;

import ru.juriasan.domain.Data;
import ru.juriasan.domain.Page;
import ru.juriasan.util.Parser;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Consumer implements Runnable {

    private BlockingQueue<Set<Data>> output;
    private long queryInterval;
    private int threadNumber;
    private List<Producer> subscriptions;

    private Set<String> words;
    private Set<Character> characters;

    public Consumer(int threadNumber, long queryIntervalMillis,
                    Set<String> words,
                    Set<Character> characters,
                    Producer... subscriptions) {
        this.threadNumber = threadNumber;
        this.queryInterval = queryIntervalMillis;
        output = new LinkedBlockingQueue<>();
        this.subscriptions = new LinkedList<>(Arrays.asList(subscriptions));
        this.words = words;
        this.characters = characters;
    }

    public synchronized void subscribe(Producer producer) {
        this.subscriptions.add(producer);
    }

    public synchronized void unsubscribe(Producer producer) {
        this.subscriptions.remove(producer);
    }

    private void putData(Set<Data> data) throws InterruptedException {
        this.output.put(data);
    }

    public Set<Data> getData() throws InterruptedException {
        return output.take();
    }

    private Data handlePage(Page page) {
        try {
            String pageData = page.getData();
            Parser parser = new Parser(pageData, words);
            parser.parse();
            return new Data(page.getUrl(), parser.getWordsCount(), parser.getSymbolsCount());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void run()  {
        try {
            while (true) {
                for (Producer producer : this.subscriptions) {
                    Set<Page> pages = producer.getPages();
                    Set<Data> data = new HashSet<>();
                    for (Page page : pages) {
                        data.add(handlePage(page));
                    }
                    putData(data);
                }
                Thread.sleep(queryInterval);
            }
        }
        catch (InterruptedException ex) {
            System.out.println(String.format("Producer thread %d is interrupted.", this.threadNumber));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package ru.juriasan;

import ru.juriasan.domain.Data;
import ru.juriasan.domain.Page;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class Consumer implements Callable<Map<String, Data>> {

    private BlockingQueue<Data> output;
    private long queryInterval;
    private int threadNumber;
    private List<Producer> subscriptions;

    public Consumer(int threadNumber, long queryIntervalMillis, Producer... subscriptions) {
        this.threadNumber = threadNumber;
        this.queryInterval = queryIntervalMillis;
        output = new LinkedBlockingQueue<>();
        this.subscriptions = new LinkedList<>(Arrays.asList(subscriptions));
    }

    public synchronized void subscribe(Producer producer) {
        this.subscriptions.add(producer);
    }

    public synchronized void unsubscribe(Producer producer) {
        this.subscriptions.remove(producer);
    }

    private Data handlePage(Page page) {
        try {
            String pageData = page.getData();
            //... parse hml
        }
        catch (NullPointerException ex) {

        }
        return null;
    }

    @Override
    public Map<String, Data> call() throws Exception {
        Map<String, Data> map = new HashMap<>();
        for (Producer producer : this.subscriptions) {
            Set<Page> pages = producer.getPages();
            for (Page page : pages) {

            }
        }
        return map;
    }
}

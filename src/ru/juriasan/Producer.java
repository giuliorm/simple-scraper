package ru.juriasan;

import ru.juriasan.domain.Page;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class Producer implements Callable<Set<Page>> {

    private Set<String> urls;
    private BlockingQueue<Set<Page>> queue;
    private long queryInterval;
    private int threadNumber;
    /**
     * Queries specified url.
     *
     * @param millisQueryInterval
     * @param urls
     */
    public Producer(int threadNumber, long millisQueryInterval, String... urls) {
        this.urls = new HashSet(Arrays.asList(urls));
        this.queue = new LinkedBlockingQueue<>();
        this.queryInterval = millisQueryInterval;
        this.threadNumber = threadNumber;
    }

    private void putPages(Set<Page> pages) throws InterruptedException {
        this.queue.put(pages);
    }

    public Set<Page> getPages() throws InterruptedException {
        return queue.take();
    }

    private String getData(String targetUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public Set<Page> call() throws Exception {
        try {
            while (true) {
                Set<Page> pages = new HashSet<>();
                for (String url : urls) {
                    String data = getData(url);
                    if (data != null) {
                        System.out.println(String.format("Thread %d has downloaded data from %s",
                                this.threadNumber, url));
                        pages.add(new Page(url, data));
                    }
                }
                putPages(pages);
                Thread.sleep(this.queryInterval);
            }
        }
        catch (InterruptedException e) {
            System.out.println(String.format("Thread %d is interrupted.", this.threadNumber));
            throw e;
        }
    }
}

package ru.juriasan.util;

import org.testng.annotations.Test;
import ru.juriasan.Producer;
import ru.juriasan.domain.Data;
import ru.juriasan.domain.Page;

public class ProducerTest {

    @Test
    public void initialTest() throws Exception {
        Producer p = new Producer(0, 2000, "http://cnn.com");
        new Thread(p).start();
        while(true) {
            for (Page page : p.getPages()) {
                System.out.println(String.format("------%s -------", page.getUrl()));
                String data = page.getData();
                System.out.println(data);
            }
        }
    }
}

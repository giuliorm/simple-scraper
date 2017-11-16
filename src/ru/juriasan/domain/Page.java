package ru.juriasan.domain;

import java.util.Objects;

public class Page {
    private String url;
    private String data;

    public Page(String url, String data) {
        this.url = url;
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public String getUrl() {
        return this.url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.url, this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Page))
            return false;
        Page p = (Page)o;
        return Objects.equals(this.url, p.getUrl()) && Objects.equals(this.data, p.getData());
    }
}

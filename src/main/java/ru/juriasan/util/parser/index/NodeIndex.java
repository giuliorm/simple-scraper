package ru.juriasan.util.parser.index;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NodeIndex {

    private long start;
    private long end;
    private Set<Index> text = new HashSet<>();
    private Set<NodeIndex> children = new HashSet<>();
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Index> getText() {
        return this.text;
    }

    public Set<NodeIndex> getChildren() {
        return this.children;
    }

    public long getStart() {
        return this.start;
    }

    public long getEnd() {
        return this.end;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

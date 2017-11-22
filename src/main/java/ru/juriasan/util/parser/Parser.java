package ru.juriasan.util.parser;

import ru.juriasan.util.parser.entity.Entity;
import ru.juriasan.util.parser.entity.EntityType;
import ru.juriasan.util.parser.index.Index;
import ru.juriasan.util.parser.index.NodeIndex;

import java.io.InputStream;
import java.util.*;

public abstract class Parser {

    protected abstract void move();
    protected abstract char current();
    protected abstract char next();
    protected abstract char prev();
    protected abstract long position();
    protected abstract boolean end();
    protected static final char NODE_BEGIN = '<';
    protected static final char NODE_END ='>';
    protected static final char EXCLAMATION = '!';
    protected static final char SLASH = '/';
    protected static final char DASH = '-';
    protected static final char SPACE = ' ';
    Deque<String> tagStack = new ArrayDeque();
    protected List<String> elements = new ArrayList<>();

    protected static final String START_ELEMENT = "Start element";
    protected static final String END_ELEMENT = "End element";

    protected Set<String> voidElements = new HashSet<>();

    public Parser(String... voidElements) {
        this.voidElements = voidElements.length > 0 ? new HashSet<>(Arrays.asList(voidElements)) :
                new HashSet<>();
    }

    protected boolean isLetter(char symbol) {
        return 'a' <=  symbol && symbol <= 'z' || 'A' <= symbol && symbol <= 'Z';
    }

    protected boolean isDigit(char symbol) {
        return  '0' <= symbol && symbol <= '9';
    }

    protected void exception(String message) throws Exception {
        throw new Exception(message);
    }

    public Set<NodeIndex> parse() throws Exception {
        Set<Index> rootText = new HashSet<>();
        Set<NodeIndex> nodes = new HashSet<>();
        Index currentText = null;
        while(!end()) {
            switch (current()) {
                case NODE_BEGIN:
                    if (currentText != null) {
                        currentText.setEnd(position() - 1);
                        rootText.add(currentText);
                    }
                    //nodes = parseNode();
                    Set<Entity> entities = parseSingle();
                    break;
                default:
                    if (currentText == null) {
                        currentText = new Index();
                        currentText.setStart(position());
                    }
            }
        }
        return nodes;
    }

    protected Entity textEntity(StringBuilder sb) {
        if (sb.length() > 0) {
            Entity textEntity = new Entity();
            textEntity.setValue(sb.toString());
            return textEntity;
        }
        return null;
    }

    protected Set<Entity> parseSingle() throws Exception {
        StringBuilder text  = new StringBuilder();
        Set<Entity> entities;
        while(!end()) {
            switch (current()) {
                case NODE_BEGIN:
                    Entity entity = parseNodeName();
                    if (entity == null) {
                        text.append(current());
                        move();
                    } else {
                        switch (entity.getType()) {
                            case START_NODE:
                                entities = voidElements.contains(entity.getValue()) ?
                                        new HashSet<>() : parseSingle();
                                entities.add(entity);
                                Entity textEntity = textEntity(text);
                                if (textEntity != null)
                                    entities.add(textEntity);
                                return entities;
                            case END_NODE:
                                entities = new HashSet<>();
                                entities.add(entity);
                                return entities;
                        }
                    }

                    break;
                default:
                    text.append(current());
                    move();
                    break;
            }
        }
        return  null;
    }

    //protected Set<Entity> parseNode() throws Exception {

        /*
        node.setName(tag);
        if (tag == null) {
            move();
            return null;
        }
        else if (voidElements.contains(tag)) {
            node.setEnd(position() - 1);
            return node;
        } */
       /* Set<Entity> entities = new HashSet<>();
        Index text = null;
        while(!end()) {
            switch (current()) {
                case NODE_BEGIN:  {
                    if (text != null)
                        text.setEnd(position() - 1);

                    //NodeIndex node = new NodeIndex();
                    //nodes.add(node);
                    //node.setStart(position());
                    Entity entity = parseNodeName();
                    //newNode.setEnd(position());
                    //node.setName(tag);


                    if (entity == null) move();
                    else if (voidElements.contains(entity.getValue())) {
                        //node.setEnd(position() - 1);
                        entities.add(entity);
                    }
                    else {
                        if (text != null) {
                            node.getText().add(text);
                            text = null;
                        }
                        String last = tagStack.peekFirst();
                        if (!Objects.equals(last, tag)) {
                            tagStack.push(tag);
                            ;
                            //newNode.setName(tag);
                            node.getChildren().addAll(parseNode());
                        }
                        else {
                            if(tagStack.isEmpty())
                                exception("Document markup is invalid!");
                            tagStack.pollFirst();
                            node.setEnd(position() - 1);
                            return nodes;
                        }
                    }
                } break;
                default: {
                    if (text == null) {
                        text = new Index();
                        text.setStart(position());
                    }
                    //here goes node insides
                    move();
                } break;
            }
        } */

        /*while(!end()) {
            switch (current()) {
                case NODE_BEGIN:  {
                    if (text != null)
                        text.setEnd(position() - 1);

                    String nextTag = parseNodeName();
                    //newNode.setEnd(position());
                    //node.setName(tag);
                    move();

                    if (nextTag == null) move();
                    else if (voidElements.contains(nextTag)) {
                        node.setEnd(position() - 1);
                        return node;
                    }
                    else {
                        if (text != null) {
                            node.getText().add(text);
                            text = null;
                        }
                        String last = tagStack.peekFirst();
                        if (!Objects.equals(last, tag)) {
                            tagStack.push(tag);
                            NodeIndex newNode = parseNode(tagStack);
                            //newNode.setName(tag);
                            node.getChildren().add(newNode);
                        }
                        else {
                            if(tagStack.isEmpty())
                                exception("Document markup is invalid!");
                            tagStack.pollFirst();
                            node.setEnd(position() - 1);
                            return node;
                        }
                    }
                } break;
                default: {
                    if (text == null) {
                        text = new Index();
                        text.setStart(position());
                    }
                    //here goes node insides
                    move();
                } break;
            }
        } */
     //   return null;
   // }

    protected Entity parseNodeName() throws Exception {
        StringBuilder tagName = new StringBuilder();
        //if(!end && html[ch] == SLASH)
        //    invalidDocument();
        Entity entity = new Entity();
        entity.setType(EntityType.START_NODE);
        while(!end()) {
            switch(current()) {
                case NODE_BEGIN:
                    move();
                    break;
                    //case EXCLAMATION:
                    //    if (html[ch + 1] == DASH) parseComment(); //else  parseSingleNode();
                case SLASH:
                    if (prev() == NODE_BEGIN)
                        entity.setType(EntityType.END_NODE);
                    move();
                    break;
                case NODE_END:
                    entity.setValue(tagName.toString());
                    if (entity.getType() == EntityType.END_NODE && !tagStack.contains(entity.getValue()))
                        exception(String.format("Document is invalid: %s element should have closing tag.",
                                entity.getValue()));
                    move();
                    return entity;
                default:
                    if (!isLetter(current()) && !isDigit(current()) && current() != DASH)
                        return null;
                    tagName.append(current());
                    move();
                    break;
            }
        }
        exception("Document is invalid!");
        return entity;
    }
}

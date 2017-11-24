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

    protected Set<Entity> nil() {
        return new LinkedHashSet<>();
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
        Set<Entity> entities = nil();
        StringBuilder rootText = new StringBuilder();
        do {
            switch (current()) {
                case NODE_BEGIN:
                    Set<Entity> tree = parseSingle();
                    if (tree != null)
                        entities.addAll(tree);
                    break;
                default:
                    if (!tagStack.isEmpty())
                        parseText(rootText);
                    break;
            }
        } while(!end());
        return null;
    }

    protected void parseText(StringBuilder sb) {
        sb.append(current());
        move();
    }

    protected Set<Entity> textEntity(String text) {
        if (text == null)
            return null;
        if (text.length() > 0) {
            Entity textEntity = new Entity();
            textEntity.setType(EntityType.NODE_VALUE);
            textEntity.setValue(text);
            Set<Entity> entitySet = nil();
            entitySet.add(textEntity);
            return entitySet;
        }
        return null;
    }

    protected Set<Entity> parseEndNode(Entity entity) throws  Exception {
        Set<Entity> entities = nil();
        entities.add(entity);
        String last = tagStack.pollFirst();
        if (!Objects.equals(last, entity.getValue()))
            exception("Document markup is invalid!");
        return entities;
    }

    protected Set<Entity> parseStartNode(Entity entity) throws Exception {
        Set<Entity> entities = null;
        if (voidElements.contains(entity.getValue()))
            entities = nil();
        else {
            tagStack.push(entity.getValue());
            entities = parseSingle();
        }
        entities.add(entity);
        //Set<Entity> textEntity = textEntity(text);
        //if (textEntity != null)
        //    entities.addAll(textEntity);
        return entities;
    }

    protected  Set<Entity> parseNode(Entity entity, String nodeText) throws Exception {
        Set<Entity> entities = null;
        switch (entity.getType()) {
            case START_NODE:
                entities = parseStartNode(entity);
                break;
            case END_NODE:
                entities = parseEndNode(entity);
                break;
        }

        Set<Entity> textEntity = textEntity(nodeText);
        if (entities != null && textEntity != null)
            entities.addAll(textEntity);
        return entities;
    }

    protected Set<Entity> parseSingle() throws Exception {
        StringBuilder text  = new StringBuilder();
        do {
            switch (current()) {
                case NODE_BEGIN:
                    Entity entity = parseNodeName();
                    if (entity != null)
                        return parseNode(entity, text.toString());
                default:
                    parseText(text);
                    break;
            }
        } while(!end());
        return textEntity(text.toString());
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

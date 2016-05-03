package model;

import java.util.HashMap;
import java.util.Map;

public class Quote {
    public static String QUOTE_PARAM  = "quote";
    public static String TEXT_PARAM   = "text";
    public static String AUTHOR_PARAM = "author";

    private String text;
    private String author;

    public Quote() {}

    public Quote(String text, String author) {
        setText(text);
        setAuthor(author);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return this.author;
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(TEXT_PARAM, this.text);
        params.put(AUTHOR_PARAM, this.author);
        return params;
    }
}

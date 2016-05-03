package com.joao29a.quote.model;

import org.junit.Test;

import java.util.Map;

import model.Quote;

import static org.junit.Assert.*;

public class QuoteTest {

    @Test
    public void valid_getParams() throws Exception {
        String text   = "Example of a testing unit";
        String author = "joao29a";
        Quote quote = new Quote(text, author);
        Map<String, String> params = quote.getParams();

        assertTrue("Text is not equal", params.get(Quote.TEXT_PARAM).equals(quote.getText()));
        assertTrue("Author is not equal", params.get(Quote.AUTHOR_PARAM).equals(quote.getAuthor()));
    }
}

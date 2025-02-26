package com.hyunwns.demoweb.animal.exception;

public class CrawlingException extends Exception {

    public CrawlingException() {
        super("You got empty data during crawling.");
    }

    public CrawlingException(String message) {
        super(message);
    }

}

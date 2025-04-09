package com.hyunwns.demoweb.animal.exception;

import java.io.IOException;

public class KakaoException extends Exception {

    public KakaoException() {
        super();
    }

    public KakaoException(String message) {
        super(message);
    }

    public KakaoException(String message, Exception e) {
        super(message, e);
    }
}

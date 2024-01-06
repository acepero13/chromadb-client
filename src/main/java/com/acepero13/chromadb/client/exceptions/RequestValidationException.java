package com.acepero13.chromadb.client.exceptions;

import com.acepero13.chromadb.client.handler.ApiException;

public class RequestValidationException extends ApiException {
    public RequestValidationException(String msg) {
        super(msg);
    }
}

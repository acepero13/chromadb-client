package com.acepero13.chromadb.client.result;

import com.google.gson.Gson;
import com.acepero13.chromadb.client.model.HTTPValidationError;
import com.acepero13.chromadb.client.model.ValidationError;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QueryResponse<T> {


    static QueryResponse<String> ofString(Object payload) {
        return of(payload, String.class);
    }

    static QueryResponse<Integer> ofInteger(Object number) {
        return of(number, Integer.class);
    }


    static QueryResponse<Boolean> ofBoolean(Object value) {
        return of(value, Boolean.class);
    }

    static <T> QueryResponse<T> of(Object element, Class<T> type) {
        if (type.isAssignableFrom(element.getClass())) {
            return new Success<>(type.cast(element));
        } else if (element instanceof HTTPValidationError) {
            return new Failure<>((HTTPValidationError) element);
        }
        return new Failure<>(new HTTPValidationError().addDetailItem(new ValidationError().msg("Unknown")));
    }

    static QueryResponse<QueryResult> ofResult(Object result) {
        return ofObjectResult(result, QueryResult.class);
    }

    static QueryResponse<GetResult> ofGetResult(Object result) {
        return ofObjectResult(result, GetResult.class);
    }

    private static <T> QueryResponse<T> ofObjectResult(Object result, Class<T> type) {
        if (result instanceof HTTPValidationError) {
            return new Failure<>((HTTPValidationError) result);
        } else if (result instanceof Map) {
            String json = new Gson().toJson(result);
            return new Success<>(new Gson().fromJson(json, type));
        } else if (result instanceof String) {
            return new Success<>(new Gson().fromJson((String) result, type));
        }
        return new Failure<>(new HTTPValidationError().addDetailItem(new ValidationError().msg("Unknown")));
    }

    static QueryResponse<List<String>> ofList(Object element) {
        if (element instanceof List) {
            return new Success<>((List<String>) element);
        } else if (element instanceof HTTPValidationError) {
            return new Failure<>((HTTPValidationError) element);
        }
        return new Failure<>(new HTTPValidationError().addDetailItem(new ValidationError().msg("Unknown")));
    }

    static QueryResponse<Boolean> ofNullable(Object element) {
        if (element == null) {
            return new Success<>(true);
        } else if (element instanceof HTTPValidationError) {
            return new Failure<>((HTTPValidationError) element);
        }
        return new Failure<>(new HTTPValidationError().addDetailItem(new ValidationError().msg("Unknown")));
    }

    static <T> QueryResponse<T> failed(String msg) {
        HTTPValidationError error = new HTTPValidationError();
        error.addDetailItem(new ValidationError().msg(msg));
        return new Failure<>(error);
    }


    boolean isSuccess();

    Optional<T> payload();

    Optional<HTTPValidationError> error();

    default boolean isError() {
        return !isSuccess();
    }

    class Success<T> implements QueryResponse<T> {
        private final T payload;

        private Success(T payload) {
            this.payload = payload;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public Optional<T> payload() {
            return Optional.ofNullable(payload);
        }

        @Override
        public Optional<HTTPValidationError> error() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "Success: {" + payload().toString() + " }";
        }
    }

    class Failure<T> implements QueryResponse<T> {
        private final HTTPValidationError error;

        Failure(HTTPValidationError error) {
            this.error = error;
        }


        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public Optional<T> payload() {
            return Optional.empty();
        }


        @Override
        public Optional<HTTPValidationError> error() {
            return Optional.ofNullable(error);
        }

        @Override
        public String toString() {
            return "Failure{" +
                    "error=" + error +
                    '}';
        }
    }
}

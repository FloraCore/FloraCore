package team.floracore.common.http;

import okhttp3.*;

public class UnsuccessfulRequestException extends Exception {

    private final Response response;

    public UnsuccessfulRequestException(Response response) {
        super("Request was unsuccessful: " + response.code() + " - " + response.message());
        this.response = response;
    }

    public Response getResponse() {
        return this.response;
    }
}

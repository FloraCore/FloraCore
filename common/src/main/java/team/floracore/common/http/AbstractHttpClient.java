package team.floracore.common.http;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AbstractHttpClient {

    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * The http client
     */
    protected final OkHttpClient okHttp;

    public AbstractHttpClient(OkHttpClient okHttp) {
        this.okHttp = okHttp;
    }

    public Response makeHttpRequest(Request request) throws IOException, UnsuccessfulRequestException {
        Response response = this.okHttp.newCall(request).execute();
        if (!response.isSuccessful()) {
            response.close();
            throw new UnsuccessfulRequestException(response);
        }
        return response;
    }
}

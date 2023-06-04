package team.floracore.common.http;

import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class BytesocksClient extends AbstractHttpClient {

    /* The bytesocks urls */
    private final String httpUrl;
    private final String wsUrl;

    /**
     * The client user agent
     */
    private final String userAgent;

    /**
     * Creates a new bytesocks instance
     *
     * @param host      the bytesocks host
     * @param userAgent the client user agent string
     */
    public BytesocksClient(OkHttpClient okHttpClient, String host, String userAgent) {
        super(okHttpClient);

        this.httpUrl = "https://" + host + "/";
        this.wsUrl = "wss://" + host + "/";
        this.userAgent = userAgent;
    }

    public Socket createSocket(WebSocketListener listener) throws IOException, UnsuccessfulRequestException {
        Request createRequest = new Request.Builder()
                .url(this.httpUrl + "create")
                .header("User-Agent", this.userAgent)
                .build();

        String id;
        try (Response response = makeHttpRequest(createRequest)) {
            if (response.code() != 201) {
                throw new UnsuccessfulRequestException(response);
            }

            id = Objects.requireNonNull(response.header("Location"));
        }

        Request socketRequest = new Request.Builder()
                .url(this.wsUrl + id)
                .header("User-Agent", this.userAgent)
                .build();

        return new Socket(id, this.okHttp.newWebSocket(socketRequest, listener));
    }

    public static final class Socket {
        private final String channelId;
        private final WebSocket socket;

        public Socket(String channelId, WebSocket socket) {
            this.channelId = channelId;
            this.socket = socket;
        }

        public String channelId() {
            return this.channelId;
        }

        public WebSocket socket() {
            return this.socket;
        }
    }

}

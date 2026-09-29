package company.vk.edu.distrib.compute.robert.urlshortener.api.models;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record Response(
    int status,
    Map<String, String> headers,
    String body
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int status;
        private final Map<String, String> headers = new ConcurrentHashMap<>();
        private String body = "";

        public Builder setStatus(int value) {
            status = value;
            return this;
        }

        public Builder putHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder setBody(String value) {
            body = value;
            return this;
        }

        public Response build() {
            return new Response(
                this.status,
                Map.copyOf(this.headers),
                this.body
            );
        }
    }
}

package company.vk.edu.distrib.compute.robert.urlshortener.api.models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.HttpExchange;

public record Request(
    String method,
    String path,
    String body,
    Map<String, String> headers
) {
    public static Request from(HttpExchange exchange) throws IOException {
        Map<String, String> headers = new ConcurrentHashMap<>();
        exchange.getRequestHeaders().forEach(
            (name, values) -> headers.put(
                name.toLowerCase(Locale.ROOT),
                values.getFirst()
            )
        );
        return new Request(
            exchange.getRequestMethod(),
            exchange.getRequestURI().getRawPath(),
            new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8    
            ),
            headers
        );
    }
}

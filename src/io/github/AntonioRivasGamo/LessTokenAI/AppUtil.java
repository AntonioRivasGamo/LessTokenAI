package io.github.AntonioRivasGamo.LessTokenAI;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @author AntonioRivasGamo
 */
public class AppUtil {

    public boolean testApi(String model, String key, String url) {
        try {

            String body = "{\"model\": \"%s\",\"messages\": [{\"role\": \"user\",\"content\": \"reply with just the word ok\"}]}"
                    .formatted(model);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (URISyntaxException | IOException | InterruptedException | IllegalArgumentException e) {
            // TODO Implement logger
            return false;
        }

    }
}
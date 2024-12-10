package ai.zenguard;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZenGuardClient {

    private final String apiKey;
    private final String backendUrl = "https://api.zenguard.ai";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public ZenGuardClient(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException(
                "The API key must be a string type and not empty."
            );
        }
        this.apiKey = apiKey;
    }

    public String detect(String prompt, Detector[] detectors) {
        if (prompt == null || prompt.isEmpty()) {
            throw new RuntimeException("Prompt can't be empty.");
        }
        if (detectors.length == 0) {
            throw new RuntimeException("No detectors were provided");
        }

        String body;
        String detectorUrl;

        if (detectors.length == 1) {
            body = String.format("{\"messages\": [\"%s\"]}", prompt);
            detectorUrl = backendUrl + "/v1/detect/" + detectors[0].getValue();
        } else {
            List<String> detectorValues = new ArrayList<String>();
            for (Detector detector : detectors) {
                detectorValues.add(detector.getValue());
            }
            String wrapedDetectors = wrapWithQuotesAndJoin(detectorValues);
            body = String.format(
                "{\"messages\": [\"%s\"], \"detectors\": [%s]}",
                prompt,
                wrapedDetectors
            );
            System.out.println(body);
            detectorUrl = backendUrl + "/v1/detect";
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(detectorUrl))
            .header("x-api-key", apiKey)
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(body))
            .build();

        try {
            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() != 200) {
                String errorString = String.format(
                    "Received an unexpected status code: %d\nResponse content: %s",
                    response.statusCode(),
                    response.body()
                );
                throw new RuntimeException(errorString);
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            String errorString = String.format(
                "An error occurred while making the request: %s",
                e.toString()
            );
            throw new RuntimeException(errorString);
        }
    }

    private String wrapWithQuotesAndJoin(List<String> strings) {
        return strings
            .stream()
            .map(s -> "\"" + s + "\"")
            .collect(Collectors.joining(", "));
    }
}

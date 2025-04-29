import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public void main() {
    System.out.println(chatGPT("What is the weather in germany?"));
}


public static String chatGPT(String message) {
    String url = "https://api.openai.com/v1/chat/completions";
    String apiKey = "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"; // API key goes here
    String model = "gpt-4.1"; // current model in use for chatgpt api


    try {
        // Create the HTTP POST request
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setRequestProperty("Content-Type", "application/json");


        // Build the request body
        String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
        con.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(body);
        writer.flush();
        writer.close();


        // Get the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();


        // returns the extracted contents of the response.
        return extractContentFromResponse(response.toString());


    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}


// This method extracts the response expected from chatgpt and returns it.
public static String extractContentFromResponse(String response) {
    int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
    int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
    return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
}

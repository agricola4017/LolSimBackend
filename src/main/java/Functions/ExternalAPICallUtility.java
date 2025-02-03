package Functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExternalAPICallUtility {
    
    public static List<String> names = new ArrayList<String>();
    public static int counter = 101;
    public static String generateName() {
        if (counter < 100) {
            counter++;
            return names.get(counter-1);
        }

        names.clear();
        counter = 0;
        String apiUrl = "https://randomuser.me/api/?results=100&inc=name&nat=US,IE"; // Replace with your API URL
        //https://randomuser.me/api/?results=5000
        //can do 5000 at a time later 
        try {
        // Create a URL object
        URL url = new URL(apiUrl);
        // Open a connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // Set the request method
        connection.setRequestMethod("GET");
        // Set request headers if needed
        connection.setRequestProperty("Accept", "application/json");

        // Get the response code
        int responseCode = connection.getResponseCode();
        //System.out.println("Response Code: " + responseCode);

        // Read the response
        if (responseCode == HttpURLConnection.HTTP_OK) { // Success
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response
            try {
                //System.out.println(response.toString());
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray arr = jsonResponse.getJSONArray("results");

                for (int i = 0; i < arr.length(); i++) {
                    jsonResponse = arr.getJSONObject(i);
                    jsonResponse = jsonResponse.getJSONObject("name");
                    String ret = jsonResponse.getString("first") + " " + jsonResponse.getString("last");
                    names.add(ret);
                }
                counter++;
                return names.get(0);
                
            } catch (JSONException e) {
                e.printStackTrace();
                return "failedcall";
            }
        } else {
            System.out.println("GET request failed");
            return "failedcall";
        }
        } catch (Exception e) {
        e.printStackTrace();
        return "failedcall";
        }
    }
}

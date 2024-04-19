package Controller;

import org.json.JSONStringer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsocketEntry {

    public static void main(String[] args) {

        boolean open = true;
        while (open) {
            try (ServerSocket serverSocket = new ServerSocket(8080);
                 Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
                System.out.println("Connected");
                String inputLine, outputLine;

                inputLine = in.readLine();

                //DataInputStream dis = new DataInputStream(inputLine);

                System.out.println(inputLine);
                outputLine = "test";
                out.println(outputLine);
                if (outputLine == "close") {
                    open = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

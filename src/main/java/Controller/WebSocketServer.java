/* package Controller;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnClose;
import org.eclipse.jetty.websocket.api.annotations.OnMessage;
import org.eclipse.jetty.websocket.api.annotations.OnOpen;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getRemoteAddress());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        // Echo the message back to the client
        try {
            session.getRemote().sendString("Echo: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket closed: " + session.getRemoteAddress());
    }

    public static void main(String[] args) {
        Server server = new Server(8080); // Specify your port here
        server.setHandler(new org.eclipse.jetty.websocket.server.WebSocketHandler() {
            @Override
            public void configure(org.eclipse.jetty.websocket.server.WebSocketServletFactory factory) {
                factory.register(WebSocketServer.class);
            }
        });

        try {
            server.start();
            System.out.println("WebSocket server started on ws://localhost:8080");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} */
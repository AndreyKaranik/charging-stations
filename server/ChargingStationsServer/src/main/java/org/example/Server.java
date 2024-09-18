package org.example;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Server {

    public static final String HOSTNAME = "0.0.0.0";

    public static final int PORT = 8000;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
        server.createContext("/", new HttpServerHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("The server has started successfully.");
    }
}

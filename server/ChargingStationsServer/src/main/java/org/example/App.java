package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.request.AuthRequest;
import org.example.request.ChargeRequest;
import org.example.request.MarkRequest;
import org.example.request.RegisterRequest;
import org.example.response.AuthResponse;
import org.example.response.ChargeResponse;
import org.example.response.GetOrderResponse;
import org.example.response.RegisterResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class App {

    public static final String URL = "jdbc:postgresql://localhost:5432/charging_stations_database";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8000), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("The server has started successfully.");

    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    queryParams.put(keyValue[0], keyValue[1]);
                } else {
                    queryParams.put(keyValue[0], "");
                }
            }
        }
        return queryParams;
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println(httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI());
            for (String key : httpExchange.getRequestHeaders().keySet()) {
                System.out.println(key + ": " + httpExchange.getRequestHeaders().get(key).toString());
            }
            String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (httpExchange.getRequestMethod().equals("GET")) {
                Map<String, String> queryParams = parseQueryParams(httpExchange.getRequestURI().getQuery());
                System.out.println("Query Parameters:");
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    System.out.println(entry.getKey() + " = " + entry.getValue());
                }

                String pattern = "/charging-station-images/(\\d+)$";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    String chargingStationImageId = m.group(1);

                    Connection connection = null;

                    try {
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);

                        JSONObject object = Utils.getChargingStationImageById(connection, Integer.parseInt(chargingStationImageId));

                        String response = object.toString();
                        ArrayList<String> list = new ArrayList<>();
                        list.add("application/json");
                        httpExchange.getResponseHeaders().put("Content-Type", list);

                        httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = httpExchange.getResponseBody();
                        os.write(response.getBytes());
                        os.flush();
                        os.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        httpExchange.sendResponseHeaders(500, 0);
                        OutputStream os = httpExchange.getResponseBody();
                        os.flush();
                        os.close();
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                pattern = "/privacy-policy$";
                r = Pattern.compile(pattern);
                m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    File file = new File("/root/chargingstations/privacy-policy.html");
                    String response = Files.readString(file.toPath());
                    ArrayList<String> list = new ArrayList<>();
                    list.add("text/html");
                    httpExchange.getResponseHeaders().put("Content-Type", list);

                    httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(response.getBytes());
                    os.flush();
                    os.close();
                }

                pattern = "/charging-stations/(\\d+)$";
                r = Pattern.compile(pattern);
                m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    String chargingStationId = m.group(1);

                    Connection connection = null;

                    try {
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);

                        JSONObject object = Utils.getChargingStationDetailsByChargingStationId(connection, Integer.parseInt(chargingStationId), httpExchange);

                        String response = object.toString();
                        ArrayList<String> list = new ArrayList<>();
                        list.add("application/json");
                        httpExchange.getResponseHeaders().put("Content-Type", list);

                        httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = httpExchange.getResponseBody();
                        os.write(response.getBytes());
                        os.flush();
                        os.close();
                    } catch (SQLException e) {
                        httpExchange.sendResponseHeaders(500, 0);
                        OutputStream os = httpExchange.getResponseBody();
                        os.flush();
                        os.close();
                        e.printStackTrace();
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                pattern = "/charging-stations";
                r = Pattern.compile(pattern);
                m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    Connection connection = null;

                    try {
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);

                        JSONArray array = Utils.getChargingStations(
                                connection,
                                queryParams.get("level") == null ? null : URLDecoder.decode(queryParams.get("level"), StandardCharsets.UTF_8),
                                queryParams.get("query") == null ? null : URLDecoder.decode(queryParams.get("query"), StandardCharsets.UTF_8)
                        );

                        String response = array.toString();
                        ArrayList<String> list = new ArrayList<>();
                        list.add("application/json");
                        httpExchange.getResponseHeaders().put("Content-Type", list);

                        httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = httpExchange.getResponseBody();
                        os.write(response.getBytes());
                        os.flush();
                        os.close();
                    } catch (SQLException e) {
                        httpExchange.sendResponseHeaders(500, 0);
                        OutputStream os = httpExchange.getResponseBody();
                        os.flush();
                        os.close();
                        e.printStackTrace();
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                pattern = "/confirm";
                r = Pattern.compile(pattern);
                m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    Connection connection = null;
                    try {
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        Utils.confirm(connection, queryParams.get("token") == null ? null : URLDecoder.decode(queryParams.get("token"), StandardCharsets.UTF_8));
                        String response = "<html><body>Success</body></html>";
                        ArrayList<String> list = new ArrayList<>();
                        list.add("text/html");
                        httpExchange.getResponseHeaders().put("Content-Type", list);
                        httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = httpExchange.getResponseBody();
                        os.write(response.getBytes());
                        os.flush();
                        os.close();
                    } catch (SQLException e) {
                        httpExchange.sendResponseHeaders(500, 0);
                        OutputStream os = httpExchange.getResponseBody();
                        os.flush();
                        os.close();
                        e.printStackTrace();
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                pattern = "/orders/(\\d+)$";
                r = Pattern.compile(pattern);
                m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    String orderId = m.group(1);
                    Connection connection = null;
                    try {
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        Order order = Utils.findOrderById(connection, Integer.parseInt(orderId));
                        if (order == null) {
                            Utils.sendHttp500Response(httpExchange);
                        } else {
                            GetOrderResponse response = new GetOrderResponse();
                            response.setConnectorId(order.getConnectorId());
                            response.setUserId(order.getUserId());
                            response.setAmount(order.getAmount());
                            response.setStatus(order.getStatus());
                            response.setProgress(order.getProgress());
                            Utils.sendHttpJsonResponse(httpExchange, response);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Utils.sendHttp500Response(httpExchange);
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

            }


            if (httpExchange.getRequestMethod().equals("POST")) {

                String pattern = "/register";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    Connection connection = null;
                    try {
                        Gson gson = new Gson();
                        RegisterRequest request = gson.fromJson(body, RegisterRequest.class);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        User user = Utils.findUserByEmail(connection, request.getEmail());
                        if (user != null) {
                            if (user.isActive()) {
                                RegisterResponse response = new RegisterResponse(1);
                                Utils.sendHttpJsonResponse(httpExchange, response);
                            } else {
                                String token = Utils.generateNewToken();
                                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                                String hashedPassword = passwordEncoder.encode(request.getPassword());
                                Utils.updateUserNameById(connection, user.getId(), user.getName());
                                Utils.updateUserPasswordById(connection, user.getId(), hashedPassword);
                                Utils.updateUserTokenById(connection, user.getId(), token);
                                EmailSender.sendEmail(request.getEmail(), token);
                                RegisterResponse response = new RegisterResponse(0);
                                Utils.sendHttpJsonResponse(httpExchange, response);
                            }
                        } else {
                            String token = Utils.generateNewToken();
                            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                            String hashedPassword = passwordEncoder.encode(request.getPassword());
                            Utils.insertUser(connection, request.getName(), request.getEmail(), hashedPassword, token);
                            EmailSender.sendEmail(request.getEmail(), token);
                            RegisterResponse response = new RegisterResponse(0);
                            Utils.sendHttpJsonResponse(httpExchange, response);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Utils.sendHttp500Response(httpExchange);
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                pattern = "/auth";
                r = Pattern.compile(pattern);
                m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    Connection connection = null;
                    try {
                        Gson gson = new Gson();
                        AuthRequest request = gson.fromJson(body, AuthRequest.class);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        String token = Utils.auth(connection, request.getEmail(), request.getPassword());
                        AuthResponse authResponse = new AuthResponse();
                        authResponse.setToken(token);
                        String response = gson.toJson(authResponse);
                        ArrayList<String> list = new ArrayList<>();
                        list.add("application/json");
                        httpExchange.getResponseHeaders().put("Content-Type", list);
                        httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                        OutputStream os = httpExchange.getResponseBody();
                        os.write(response.getBytes());
                        os.flush();
                        os.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        httpExchange.sendResponseHeaders(500, 0);
                        OutputStream os = httpExchange.getResponseBody();
                        os.flush();
                        os.close();
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                pattern = "/charge";
                r = Pattern.compile(pattern);
                m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    Connection connection = null;
                    try {
                        Gson gson = new Gson();
                        ChargeRequest request = gson.fromJson(body, ChargeRequest.class);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        int orderId = Utils.charge(connection, request);
                        ChargeResponse chargeResponse = new ChargeResponse(orderId);
                        ChargingThread thread = new ChargingThread(orderId, request.getConnectorId());
                        thread.start();
                        Utils.sendHttpJsonResponse(httpExchange, chargeResponse);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Utils.sendHttp500Response(httpExchange);
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                pattern = "/mark";
                r = Pattern.compile(pattern);
                m = r.matcher(httpExchange.getRequestURI().toString());

                if (m.find()) {
                    Connection connection = null;
                    try {
                        Gson gson = new Gson();
                        MarkRequest request = gson.fromJson(body, MarkRequest.class);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        Utils.mark(connection, request);
                        Utils.sendHttp200Response(httpExchange);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Utils.sendHttp500Response(httpExchange);
                    } finally {
                        try {
                            if (connection != null) connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
            }
        }
    }
}

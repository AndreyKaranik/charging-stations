package org.example;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.entities.Order;
import org.example.entities.User;
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
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServerHandler implements HttpHandler, API {

    public static final String URL = "jdbc:postgresql://localhost:5432/charging_stations_database";

    public static final String USER = "postgres";

    public static final String PASSWORD = "postgres";

    private final List<Route> routes = new ArrayList<>();

    public HttpServerHandler() {
        routes.add(new Route("GET", "/charging-station-images/(\\d+)$", this::getChargingStationImage));
        routes.add(new Route("GET", "/privacy-policy$", this::getPrivacyPolicy));
        routes.add(new Route("GET", "/charging-stations$", this::getChargingStations));
        routes.add(new Route("GET", "/charging-stations/(\\d+)$", this::getChargingStationDetails));

    }

    @Override
    public void getChargingStationImage(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            String chargingStationImageId = matcher.group(1);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            JSONObject object = Utils.getChargingStationImageById(connection, Integer.parseInt(chargingStationImageId));
            String response = object.toString();
            Utils.sendHttpJsonResponse(httpExchange, response);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void getChargingStations(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            Map<String, String> queryParams = Utils.getQueryParams(httpExchange);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            JSONArray array = Utils.getChargingStations(
                    connection,
                    queryParams.get("level") == null ? null : URLDecoder.decode(queryParams.get("level"), StandardCharsets.UTF_8),
                    queryParams.get("query") == null ? null : URLDecoder.decode(queryParams.get("query"), StandardCharsets.UTF_8)
            );
            String response = array.toString();
            Utils.sendHttpJsonResponse(httpExchange, response);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void getChargingStationDetails(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            String chargingStationId = matcher.group(1);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            JSONObject object = Utils.getChargingStationDetailsByChargingStationId(connection, Integer.parseInt(chargingStationId), httpExchange);
            String response = object.toString();
            Utils.sendHttpJsonResponse(httpExchange, response);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void auth(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            String body = Utils.readBodyAsString(httpExchange);
            Gson gson = new Gson();
            AuthRequest request = gson.fromJson(body, AuthRequest.class);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String token = Utils.auth(connection, request.getEmail(), request.getPassword());
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);
            String response = gson.toJson(authResponse);
            Utils.sendHttpJsonResponse(httpExchange, response);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void register(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            String body = Utils.readBodyAsString(httpExchange);
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
                    boolean success = Utils.sendEmail(request.getEmail(), token);
                    if (!success) {
                        throw new RuntimeException();
                    }
                    RegisterResponse response = new RegisterResponse(0);
                    Utils.sendHttpJsonResponse(httpExchange, response);
                }
            } else {
                String token = Utils.generateNewToken();
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(request.getPassword());
                Utils.insertUser(connection, request.getName(), request.getEmail(), hashedPassword, token);
                boolean success = Utils.sendEmail(request.getEmail(), token);
                if (!success) {
                    throw new RuntimeException();
                }
                RegisterResponse response = new RegisterResponse(0);
                Utils.sendHttpJsonResponse(httpExchange, response);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void getPrivacyPolicy(HttpExchange httpExchange, Matcher matcher) {
        try {
            File file = new File("/root/chargingstations/privacy-policy.html");
            String response = Files.readString(file.toPath());
            Utils.sendHttpHtmlResponse(httpExchange, response);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void confirm(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            Map<String, String> queryParams = Utils.getQueryParams(httpExchange);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Utils.confirm(connection, queryParams.get("token") == null ? null : URLDecoder.decode(queryParams.get("token"), StandardCharsets.UTF_8));
            String response = "<html><body>Success</body></html>";
            Utils.sendHttpHtmlResponse(httpExchange, response);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void mark(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            String body = Utils.readBodyAsString(httpExchange);
            Gson gson = new Gson();
            MarkRequest request = gson.fromJson(body, MarkRequest.class);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Utils.mark(connection, request);
            Utils.sendHttp200Response(httpExchange);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void charge(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            String body = Utils.readBodyAsString(httpExchange);
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
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void getOrder(HttpExchange httpExchange, Matcher matcher) {
        Connection connection = null;
        try {
            String orderId = matcher.group(1);
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
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                Utils.sendHttp500Response(httpExchange);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static class Route {
        private final String method;
        private final Pattern pattern;
        private final BiConsumer<HttpExchange, Matcher> handler;

        public Route(String method, String regex, BiConsumer<HttpExchange, Matcher> handler) {
            this.method = method;
            this.pattern = Pattern.compile(regex);
            this.handler = handler;
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println(httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI());
        for (String key : httpExchange.getRequestHeaders().keySet()) {
            System.out.println(key + ": " + httpExchange.getRequestHeaders().get(key).toString());
        }

        Map<String, String> queryParams = Utils.getQueryParams(httpExchange);
        if (!queryParams.entrySet().isEmpty()) {
            System.out.println("Query Parameters:");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        }

        System.out.println();

        String path = httpExchange.getRequestURI().getPath();

        for (Route route : routes) {
            Matcher matcher = route.pattern.matcher(path);
            if (matcher.find() && httpExchange.getRequestMethod().equals(route.method)) {
                route.handler.accept(httpExchange, matcher);
                return;
            }
        }

        try {
            Utils.sendHttp404Response(httpExchange);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

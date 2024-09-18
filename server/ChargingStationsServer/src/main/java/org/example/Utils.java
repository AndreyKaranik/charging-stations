package org.example;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.example.entities.Order;
import org.example.entities.User;
import org.example.request.ChargeRequest;
import org.example.request.MarkRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class Utils {
    public static String generateNewToken() {
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static JSONArray getChargingMarksWithUserNameByChargingStationId(Connection connection, int chargingStationId) throws SQLException {
        String sql = "SELECT * FROM charging_marks WHERE charging_station_id = " + chargingStationId;
        JSONArray array = new JSONArray();
        Statement stmt  = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            int markChargingStationId = rs.getInt("charging_station_id");
            int status = rs.getInt("status");
            int userId = rs.getInt("user_id");
            int chargingTypeId = rs.getInt("charging_type_id");
            Timestamp timestamp = rs.getTimestamp("time");
            JSONObject object = new JSONObject();
            object.put("id", id);
            object.put("charging_station_id", markChargingStationId);
            object.put("status", status);
            if (userId != 0) {
                object.put("user_id", userId);
                object.put("user_name", Utils.getUserNameByUserId(connection, userId));
            }
            object.put("charging_type", Utils.getChargingTypeByChargingTypeId(connection, chargingTypeId));
            object.put("time", timestamp);
            array.put(object);
        }
        return array;
    }


    public static JSONArray getConnectorsByChargingStationId(Connection connection, int chargingStationId) throws SQLException {
        String sql = "SELECT * FROM connectors WHERE charging_station_id = " + chargingStationId;
        JSONArray array = new JSONArray();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            int connectorChargingStationId = rs.getInt("charging_station_id");
            int status = rs.getInt("status");
            int chargingTypeId = rs.getInt("charging_type_id");
            double rate = rs.getDouble("rate");
            JSONObject o = new JSONObject();
            o.put("id", id);
            o.put("charging_station_id", connectorChargingStationId);
            o.put("status", status);
            o.put("rate", rate);
            o.put("charging_type", Utils.getChargingTypeByChargingTypeId(connection, chargingTypeId));
            array.put(o);
        }
        return array;
    }

    public static JSONObject getChargingTypeByChargingTypeId(Connection connection, int chargingTypeId) throws SQLException {
        String sql = "SELECT * FROM charging_types WHERE id = " + chargingTypeId;
        JSONObject object = new JSONObject();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int charging_type_id = rs.getInt("id");
        String charging_type_name = rs.getString("name");
        String currentType = rs.getString("current_type");
        object.put("id", charging_type_id);
        object.put("name", charging_type_name);
        object.put("current_type", currentType);
        return object;
    }

    public static JSONObject getChargingStationDetailsByChargingStationId(Connection connection, int chargingStationId, HttpExchange httpExchange) throws IOException, SQLException {
        String sql = "SELECT * FROM charging_stations WHERE id = " + chargingStationId;
        JSONObject object = new JSONObject();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String address = rs.getString("address");
        double latitude = rs.getDouble("latitude");
        double longitude = rs.getDouble("longitude");
        String hours = rs.getString("opening_hours");
        String description = rs.getString("description");
        object.put("id", id);
        object.put("name", name);
        object.put("address", address);
        object.put("latitude", latitude);
        object.put("longitude", longitude);
        object.put("opening_hours", hours);
        object.put("description", description);
        object.put("connectors", Utils.getConnectorsByChargingStationId(connection, id));
        object.put("charging_marks", Utils.getChargingMarksWithUserNameByChargingStationId(connection, id));
        object.put("image_ids", Utils.getChargingStationImageIdsByChargingStationId(connection, id));
        return object;
    }

    public static String getUserNameByUserId(Connection connection, int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = " + userId;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String name = rs.getString("name");
        return name;
    }

    public static JSONArray getChargingStations(Connection connection, String level, String query) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        JSONArray array = new JSONArray();
        if (level != null) {
            if (level.equals("min")) {
                String sql = "SELECT id, latitude, longitude FROM charging_stations;";
                stmt = connection.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    double latitude = rs.getDouble("latitude");
                    double longitude = rs.getDouble("longitude");
                    JSONObject object = new JSONObject();
                    object.put("id", id);
                    object.put("latitude", latitude);
                    object.put("longitude", longitude);
                    array.put(object);
                }
            }
            if (level.equals("medium")) {
                String sql = "SELECT id, name, address, latitude, longitude FROM charging_stations";
                stmt = connection.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    double latitude = rs.getDouble("latitude");
                    double longitude = rs.getDouble("longitude");
                    JSONObject object = new JSONObject();
                    object.put("id", id);
                    object.put("name", name);
                    object.put("address", address);
                    object.put("latitude", latitude);
                    object.put("longitude", longitude);
                    object.put("charging_types", Utils.getChargingTypesByChargingStationId(connection, id));
                    if (query != null) {
                        if (name.toLowerCase().contains(query.toLowerCase()) ||
                                address.toLowerCase().contains(query.toLowerCase())) {
                            array.put(object);
                        }
                    } else {
                        array.put(object);
                    }
                }
            }
            if (level.equals("full")) {
                String sql = "SELECT * FROM charging_stations";
                stmt = connection.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    double latitude = rs.getDouble("latitude");
                    double longitude = rs.getDouble("longitude");
                    String hours = rs.getString("opening_hours");
                    String description = rs.getString("description");
                    JSONObject object = new JSONObject();
                    object.put("id", id);
                    object.put("name", name);
                    object.put("address", address);
                    object.put("latitude", latitude);
                    object.put("longitude", longitude);
                    object.put("opening_hours", hours);
                    object.put("description", description);
                    array.put(object);
                }
            }
        } else {
            String sql = "SELECT * FROM charging_stations";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                String hours = rs.getString("opening_hours");
                String description = rs.getString("description");
                JSONObject object = new JSONObject();
                object.put("id", id);
                object.put("name", name);
                object.put("address", address);
                object.put("latitude", latitude);
                object.put("longitude", longitude);
                object.put("opening_hours", hours);
                object.put("description", description);
                array.put(object);
            }
        }

        return array;
    }


    public static JSONArray getChargingTypesByChargingStationId(Connection connection, int chargingStationId) throws SQLException {
        String sql = "SELECT DISTINCT ct.id, ct.name, ct.current_type\n" +
                "FROM charging_stations cs\n" +
                "JOIN connectors c ON cs.id = c.charging_station_id\n" +
                "JOIN charging_types ct ON c.charging_type_id = ct.id\n" +
                "WHERE cs.id = " + chargingStationId;
        JSONArray array = new JSONArray();
        Statement stmt  = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String currentType = rs.getString("current_type");
            JSONObject o = new JSONObject();
            o.put("id", id);
            o.put("name", name);
            o.put("current_type", currentType);
            array.put(o);
        }
        return array;
    }

    public static JSONObject getChargingStationImageById(Connection connection, int chargingStationImageId) throws SQLException, IOException {
        String sql = "SELECT * FROM charging_station_images WHERE id = " + chargingStationImageId;
        JSONObject object = new JSONObject();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int id = rs.getInt("id");
        String path = rs.getString("path");
        File imageFile = new File("/root/chargingstations/images/" + path);
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        object.put("id", id);
        object.put("data", base64Image);
        return object;
    }

    public static JSONArray getChargingStationImageIdsByChargingStationId(Connection connection, int chargingStationId) throws SQLException {
        String sql = "SELECT id FROM charging_station_images WHERE charging_station_id = " + chargingStationId;
        JSONArray array = new JSONArray();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            array.put(id);
        }
        return array;
    }

    public static void confirm(Connection connection, String token) throws SQLException {
        String sql = "SELECT id FROM users WHERE token = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, token);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            throw new RuntimeException();
        }
        int id = rs.getInt("id");
        updateUserIsActiveById(connection, id, true);
    }

    public static User findUserByEmail(Connection connection, String email) throws SQLException {
        String sql = "SELECT id, name, password, token, is_active FROM users WHERE email = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String password = rs.getString("password");
        String token = rs.getString("token");
        boolean isActive = rs.getBoolean("is_active");

        return new User(id, name, email, password, token, isActive);
    }

    public static void insertUser(Connection connection, String name, String email, String password, String token) throws SQLException {
        String sql = "INSERT INTO users(name, email, password, token) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setString(3, password);
        stmt.setString(4, token);
        stmt.executeUpdate();
    }

    public static void updateUserNameById(Connection connection, int id, String name) throws SQLException {
        String sql = "UPDATE users SET name = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }

    public static void updateUserPasswordById(Connection connection, int id, String password) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, password);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }

    public static void updateUserTokenById(Connection connection, int id, String token) throws SQLException {
        String sql = "UPDATE users SET token = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, token);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }

    public static void updateUserIsActiveById(Connection connection, int id, boolean isActive) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setBoolean(1, isActive);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }


    public static String auth(Connection connection, String email, String password) throws SQLException {
        String sql = "SELECT password, token FROM users WHERE email = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        String hashedPassword = rs.getString("password");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(password, hashedPassword)) {
            return rs.getString("token");
        } else {
            return null;
        }
    }

    public static int charge(Connection connection, ChargeRequest chargeRequest) throws SQLException {
        String sql = "INSERT INTO orders (connector_id, user_id, amount, status) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, chargeRequest.getConnectorId());
        if (chargeRequest.getToken() == null) {
            stmt.setNull(2, java.sql.Types.NULL);
        } else {
            int userId = getUserIdByToken(connection, chargeRequest.getToken());
            if (userId == -1) {
                throw new RuntimeException();
            }
            stmt.setInt(2, userId);
        }
        stmt.setFloat(3, chargeRequest.getAmount());
        stmt.setInt(4, 0);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (!rs.next()) {
            return -1;
        }
        return rs.getInt("id");
    }

    public static int getUserIdByToken(Connection connection, String token) throws SQLException {
        String sql = "SELECT id FROM users WHERE token = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, token);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return -1;
        }
        return rs.getInt("id");
    }

    public static Order findOrderById(Connection connection, int id) throws SQLException {
        String sql = "SELECT connector_id, user_id, amount, status, progress FROM orders WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        int connectorId = rs.getInt("connector_id");
        int userId = rs.getInt("user_id");
        float amount = rs.getFloat("amount");
        int status = rs.getInt("status");
        int progress = rs.getInt("progress");

        return new Order(id, connectorId, userId, amount, status, progress);
    }

    public static void mark(Connection connection, MarkRequest markRequest) throws SQLException {
        String sql = "INSERT INTO charging_marks (charging_station_id, status, user_id, charging_type_id, time) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, markRequest.getChargingStationId());
        if (markRequest.getToken() == null) {
            stmt.setNull(3, java.sql.Types.NULL);
        } else {
            int userId = getUserIdByToken(connection, markRequest.getToken());
            if (userId == -1) {
                throw new RuntimeException();
            }
            stmt.setInt(3, userId);
        }
        stmt.setInt(2, markRequest.getStatus());
        stmt.setInt(4, markRequest.getChargingTypeId());
        stmt.setTimestamp(5, Timestamp.from(Instant.now()));
        stmt.executeUpdate();
    }

    public static void updateOrderStatusById(Connection connection, int id, int status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, status);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }

    public static void updateOrderProgressById(Connection connection, int id, int progress) throws SQLException {
        String sql = "UPDATE orders SET progress = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, progress);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }

    public static void updateConnectorStatusById(Connection connection, int id, int status) throws SQLException {
        String sql = "UPDATE connectors SET status = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, status);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    }


    public static void sendHttpJsonResponse(HttpExchange httpExchange, Object obj) throws IOException {
        Gson gson = new Gson();
        String response = gson.toJson(obj);
        ArrayList<String> list = new ArrayList<>();
        list.add("application/json");
        httpExchange.getResponseHeaders().put("Content-Type", list);
        httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.flush();
        os.close();
    }

    public static void sendHttpJsonResponse(HttpExchange httpExchange, String response) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        list.add("application/json");
        httpExchange.getResponseHeaders().put("Content-Type", list);
        httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.flush();
        os.close();
    }

    public static void sendHttpHtmlResponse(HttpExchange httpExchange, String response) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        list.add("text/html");
        httpExchange.getResponseHeaders().put("Content-Type", list);
        httpExchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.flush();
        os.close();
    }

    public static void sendHttp200Response(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.flush();
        os.close();
    }

    public static void sendHttp500Response(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(500, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.flush();
        os.close();
    }

    public static void sendHttp404Response(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.flush();
        os.close();
    }

    public static String readBodyAsString(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    public static Map<String, String> getQueryParams(HttpExchange httpExchange) {
        String query = httpExchange.getRequestURI().getQuery();
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

    public static boolean sendEmail(String to, String token) throws IOException {
        String path = "/root/chargingstations/email.txt";
        String content = new String(Files.readAllBytes(Path.of(path)));
        String[] lines = content.split("\n");
        String username = lines[0].strip();
        String password = lines[1].strip();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Registration Confirmation");
            message.setText("Please confirm your registration by clicking the following link: " +
                    "http://194.67.88.154:8000/confirm?token=" + token);

            Transport.send(message);
            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
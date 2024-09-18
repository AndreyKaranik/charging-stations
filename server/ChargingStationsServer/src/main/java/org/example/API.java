package org.example;

import com.sun.net.httpserver.HttpExchange;

import java.util.regex.Matcher;

public interface API {
    void getChargingStationImage(HttpExchange httpExchange, Matcher matcher);
    void getChargingStations(HttpExchange httpExchange, Matcher matcher);
    void getChargingStationDetails(HttpExchange httpExchange, Matcher matcher);
    void auth(HttpExchange httpExchange, Matcher matcher);
    void register(HttpExchange httpExchange, Matcher matcher);
    void getPrivacyPolicy(HttpExchange httpExchange, Matcher matcher);
    void confirm(HttpExchange httpExchange, Matcher matcher);
    void mark(HttpExchange httpExchange, Matcher matcher);
    void charge(HttpExchange httpExchange, Matcher matcher);
    void getOrder(HttpExchange httpExchange, Matcher matcher);
}

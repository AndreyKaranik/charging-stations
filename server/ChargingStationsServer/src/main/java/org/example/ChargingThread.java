package org.example;

import com.google.gson.Gson;
import org.example.request.ChargeRequest;
import org.example.response.ChargeResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ChargingThread extends Thread {

    private int orderId;
    private int connectorId;

    public ChargingThread(int orderId, int connectorId) {
        this.orderId = orderId;
        this.connectorId = connectorId;
    }

    @Override
    public void run() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(App.URL, App.USER, App.PASSWORD);
            Utils.updateConnectorStatusById(connection, connectorId, 2);
            int progress = 0;
            while (progress < 100) {
                Thread.sleep(100);
                progress += 1;
                Utils.updateOrderProgressById(connection, orderId, progress);
            }
            Utils.updateOrderStatusById(connection, orderId, 1);
            Utils.updateConnectorStatusById(connection, connectorId, 1);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

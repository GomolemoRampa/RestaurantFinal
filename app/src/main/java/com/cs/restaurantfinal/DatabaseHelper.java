package com.cs.restaurantfinal;

import android.content.Context;
import android.util.Log;

import com.cs.restaurantfinal.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    //private static final String BASE_URL = "https://lamp.ms.wits.ac.za/home/s2661055/";

    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    public User authenticateUser(String username, String password) {
        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2661055/login.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                Log.d("AUTH_RESPONSE", response.toString()); // Log full response for debugging

                JSONObject obj = new JSONObject(response.toString());
                if (obj.getBoolean("success")) {
                    JSONObject data = obj.getJSONObject("data");

                    return new User(
                            0,
                            data.getString("full_name"),
                            data.getString("username"),
                            "", // email not returned in login response
                            "", // phone not returned in login response
                            "", // password not returned in login response
                            User.UserType.valueOf(data.getString("user_type").toUpperCase())
                    );

                } else {
                    Log.e("AUTH_FAIL", "Login failed: " + obj.getString("message"));
                }
            } else {
                Log.e("AUTH_FAIL", "HTTP error code: " + responseCode);
            }

        } catch (Exception e) {
            Log.e("AUTH_ERROR", "Error authenticating user", e);
        }

        return null;
    }


    public boolean isUsernameTaken(String username) {
        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2661055/register.php?username=" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                String response = in.readLine();
                in.close();
                return Boolean.parseBoolean(response.trim());
            }

        } catch (Exception e) {
            Log.e("USERNAME_CHECK", "Error checking username", e);
        }
        return false;
    }

    public boolean addUser(User user) {
        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2661055/register.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData =
                    "username=" + URLEncoder.encode(user.getUsername(), "UTF-8") +
                            "&full_name=" + URLEncoder.encode(user.getFullName(), "UTF-8") +
                            "&email=" + URLEncoder.encode(user.getEmail(), "UTF-8") +
                            "&phone=" + URLEncoder.encode(user.getPhone(), "UTF-8") +
                            "&password=" + URLEncoder.encode(user.getPassword(), "UTF-8") +
                            "&user_type=" + URLEncoder.encode(user.getUserType().name(), "UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();
                reader.close();
                return response != null && response.contains("success");
            }

        } catch (Exception e) {
            Log.e("ADD_USER", "Error adding user", e);
        }
        return false;
    }


    public List<String> getAllRestaurants() {
        List<String> restaurants = new ArrayList<>();

        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2661055/get_restaurants.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                json.append(line);
            reader.close();

            JSONArray array = new JSONArray(json.toString());
            for (int i = 0; i < array.length(); i++) {
                restaurants.add(array.getString(i));
            }

        } catch (Exception e) {
            Log.e("RESTAURANTS", "Error fetching restaurants", e);
        }

        return restaurants;
    }

    public boolean addOrder(Order order) {
        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2661055/create_order.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData =
                    "customer_name=" + order.getCustomerName() +
                            "&restaurant_name=" + order.getRestaurantName() +
                            "&staff_member=" + order.getStaffMember();

            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();
                reader.close();
                return response.contains("success");
            }

        } catch (Exception e) {
            Log.e("ADD_ORDER", "Error adding order", e);
        }
        return false;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2661055/get_orders.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                json.append(line);
            reader.close();

            JSONArray array = new JSONArray(json.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Order order = new Order(
                        //obj.getString("order_id"),
                        obj.getString("customer_name"),
                        obj.getString("restaurant_name"),
                        obj.getString("staff_member"),
                        obj.getLong("order_time")
                );
                orders.add(order);
            }

        } catch (Exception e) {
            Log.e("GET_ORDERS", "Error fetching orders", e);
        }

        return orders;
    }

    public boolean updateOrderStatus(String orderId, Order.OrderStatus newStatus) {
        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2661055/update_order_status.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "order_id=" + orderId + "&status=" + newStatus.name();

            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();

            return response.contains("success");

        } catch (Exception e) {
            Log.e("UPDATE_STATUS", "Error updating status", e);
        }
        return false;
    }
}

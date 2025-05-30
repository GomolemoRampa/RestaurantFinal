package com.cs.restaurantfinal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUsername, tvPendingCount, tvReadyCount, tvCollectedCount;
    private RecyclerView rvOrders;
    private LinearLayout layoutEmptyState;
    private CircularProgressIndicator progressBar;
    private MaterialButton btnRefresh;

    private int customerId;
    private String customerName;

    private RatedOrderAdapter orderAdapter;
    private List<JSONObject> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        initViews();

        // Get user session
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        customerId = prefs.getInt("customer_id", -1);
        customerName = prefs.getString("customer_name", "Customer");

        tvUsername.setText(customerName);

        setupRecyclerView();
        loadOrders();

        btnRefresh.setOnClickListener(v -> loadOrders());
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUsername = findViewById(R.id.tv_username);
        tvPendingCount = findViewById(R.id.tv_pending_count);
        tvReadyCount = findViewById(R.id.tv_ready_count);
        tvCollectedCount = findViewById(R.id.tv_collected_count);

        rvOrders = findViewById(R.id.rv_orders);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        progressBar = findViewById(R.id.progress_bar);
        btnRefresh = findViewById(R.id.btn_refresh);
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new RatedOrderAdapter(this, orderList, customerId);
        rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        rvOrders.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2676309/get_customer_orders.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("customer_id", customerId);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                conn.disconnect();

                JSONObject result = new JSONObject(response.toString());

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    try { // Add try block here
                        if (result.getString("status").equals("success")) {
                            JSONArray ordersArray = result.getJSONArray("orders");
                            orderList.clear();

                            int pending = 0, ready = 0, collected = 0;

                            for (int i = 0; i < ordersArray.length(); i++) {
                                JSONObject order = ordersArray.getJSONObject(i);
                                orderList.add(order);

                                String status = order.optString("Status", "").toLowerCase();
                                if (status.equals("pending")) pending++;
                                else if (status.equals("ready")) ready++;
                                else if (status.equals("collected")) collected++;
                            }

                            tvPendingCount.setText(String.valueOf(pending));
                            tvReadyCount.setText(String.valueOf(ready));
                            tvCollectedCount.setText(String.valueOf(collected));

                            if (orderList.isEmpty()) {
                                layoutEmptyState.setVisibility(View.VISIBLE);
                                rvOrders.setVisibility(View.GONE);
                            } else {
                                layoutEmptyState.setVisibility(View.GONE);
                                rvOrders.setVisibility(View.VISIBLE);
                            }

                            orderAdapter.notifyDataSetChanged();

                        } else {
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            // It's good practice to also handle potential JSONException here
                            // if the 'status' itself is missing or not a string.
                            // However, the original error is for the 'success' case.
                            Toast.makeText(this, result.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) { // Catch the JSONException
                        e.printStackTrace();
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Failed to load orders: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}

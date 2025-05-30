package com.cs.restaurantfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RatedOrderAdapter extends RecyclerView.Adapter<RatedOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<JSONObject> orders;
    private int customerId;

    public RatedOrderAdapter(Context context, List<JSONObject> orders, int customerId) {
        this.context = context;
        this.orders = orders;
        this.customerId = customerId;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvRestaurantName, tvOrderTime, tvRated;
        Button btnThumbsUp, btnThumbsDown;

        public OrderViewHolder(View view) {
            super(view);
            tvOrderId = view.findViewById(R.id.tvOrderId);
            tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
            tvRestaurantName = view.findViewById(R.id.tvRestaurantName);
            tvOrderTime = view.findViewById(R.id.tvOrderTime);
            tvRated = view.findViewById(R.id.tvRated);
            btnThumbsUp = view.findViewById(R.id.btnThumbsUp);
            btnThumbsDown = view.findViewById(R.id.btnThumbsDown);
        }
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.customer_order_retrieval, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        JSONObject order = orders.get(position);

        try {
            int orderId = order.has("Order_ID") ? order.getInt("Order_ID") : -1;
            int restaurantId = order.has("Restaurant_ID") ? order.getInt("Restaurant_ID") : -1;
            String status = order.has("Status") ? order.getString("Status") : "Unknown";
            String time = order.has("Order_Time") ? order.getString("Order_Time") : "Unknown time";
            String restaurant = order.has("Restaurant_Name") ? order.getString("Restaurant_Name") : "Unknown Restaurant";

            // Show order info
            holder.tvOrderId.setText("Order #" + orderId);
            holder.tvOrderStatus.setText(status);
            holder.tvRestaurantName.setText(restaurant);
            holder.tvOrderTime.setText("Ordered at " + time);

            // Only allow rating if IDs are valid
            if (orderId != -1 && restaurantId != -1) {
                holder.btnThumbsUp.setOnClickListener(v ->
                        submitRating(orderId, restaurantId, "thumbs_up", "", holder));

                holder.btnThumbsDown.setOnClickListener(v ->
                        submitRating(orderId, restaurantId, "thumbs_down", "", holder));
            } else {
                // Disable buttons if IDs are invalid
                holder.btnThumbsUp.setEnabled(false);
                holder.btnThumbsDown.setEnabled(false);
                holder.tvRated.setVisibility(View.VISIBLE);
                holder.tvRated.setText("Invalid order data.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error displaying order", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableRating(OrderViewHolder holder) {
        holder.btnThumbsUp.setEnabled(false);
        holder.btnThumbsDown.setEnabled(false);
    }

    private void submitRating(int orderId, int restaurantId, String ratingType, String comment, OrderViewHolder holder) {
        new Thread(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2676309/rate_order.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("order_id", orderId);
                json.put("customer_id", customerId);
                json.put("restaurant_id", restaurantId);
                json.put("rating_type", ratingType);
                json.put("comment", comment);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject result = new JSONObject(response.toString());

                ((AppCompatActivity) context).runOnUiThread(() -> {
                    try { // Add try block here
                        if (result.getString("status").equals("success")) {
                            holder.tvRated.setVisibility(View.VISIBLE);
                            holder.tvRated.setText("You rated this order " + (ratingType.equals("thumbs_up") ? "👍" : "👎"));
                            disableRating(holder);
                        } else {
                            // It's good practice to also check for "message" key existence
                            String message = result.has("message") ? result.getString("message") : "Unknown error";
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) { // Catch JSONException specifically
                        e.printStackTrace();
                        Toast.makeText(context, "Error parsing rating response.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                ((AppCompatActivity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Rating failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}

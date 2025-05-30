package com.cs.restaurantfinal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Order extends AppCompatActivity {
    EditText etCustomerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnCreateOrderOrder), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



public class Order {
    public class Order {
        private String orderId;
        private String customerName;
        private String restaurantName;
        private String staffMember;
        private long orderTime;
        private OrderStatus status;
        private Rating rating;

        public enum OrderStatus {
            PENDING, READY, COLLECTED
        }

        public enum Rating {
            NONE, THUMBS_UP, THUMBS_DOWN
        }

        public Order() {
            this.orderId = generateOrderId();
            this.orderTime = System.currentTimeMillis();
            this.status = OrderStatus.PENDING;
            this.rating = Rating.NONE;
        }

        public Order(String customerName, String restaurantName, String staffMember) {
            this();
            this.customerName = customerName;
            this.restaurantName = restaurantName;
            this.staffMember = staffMember;
        }

        private String generateOrderId() {
            return "ORD" + System.currentTimeMillis() % 100000;
        }

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getRestaurantName() { return restaurantName; }
        public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

        public String getStaffMember() { return staffMember; }
        public void setStaffMember(String staffMember) { this.staffMember = staffMember; }

        public long getOrderTime() { return orderTime; }
        public void setOrderTime(long orderTime) { this.orderTime = orderTime; }

        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }

        public Rating getRating() { return rating; }
        public void setRating(Rating rating) { this.rating = rating; }

        public String getFormattedTime() {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(orderTime));
        }

        public String getStatusColor() {
            switch (status) {
                case PENDING: return "#FF9800";
                case READY: return "#4CAF50";
                case COLLECTED: return "#9E9E9E";
                default: return "#757575";
            }
        }
    }
}

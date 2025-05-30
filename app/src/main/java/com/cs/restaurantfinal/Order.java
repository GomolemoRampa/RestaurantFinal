package  com.cs.restaurantfinal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Order extends AppCompatActivity {

    EditText etCustomerName,etRestaurant;
    int staffId;
    private String orderId;
    private String customerName;
    private String restaurantName;
    private String staffMember;
    private long orderTime;
    private OrderStatus status;
    private Rating rating;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_order);
    }

    //etCustomerName,etRestaurant

    public Order(String customerName, String restaurantName, String staffMember, long orderTime) {
        //this.orderId = orderId;
        this.customerName = customerName;
        this.restaurantName = restaurantName;
        this.staffMember = staffMember;
        this.orderTime = orderTime;
        this.status = OrderStatus.PENDING;
        this.rating = Rating.NONE;
    }

    public enum OrderStatus {
        PENDING("Pending"),
        READY("Ready"),
        COLLECTED("Collected");

        private String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Rating {
        NONE(0),
        THUMBS_UP(1),
        THUMBS_DOWN(-1);

        private int value;

        Rating(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
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
}
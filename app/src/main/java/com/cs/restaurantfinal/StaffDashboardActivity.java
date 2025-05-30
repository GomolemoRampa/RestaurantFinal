package com.cs.restaurantfinal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StaffDashboardActivity extends AppCompatActivity implements StaffOrderAdapter.OnOrderStatusChangeListener {
    private RecyclerView rvOrders;
    private RatingBar rbAverageRating;
    private TextView tvAverageRating;
    private TextView btnAddOrder;

    private StaffOrderAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupToolbar();
        setupRecyclerView();
        loadOrders();
        //updateAverageRating();

        btnAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDashboardActivity.this, Order.class);
                startActivity(intent);
                finish();
                Toast.makeText(StaffDashboardActivity.this, "Creating another order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rvOrders);
        rbAverageRating = findViewById(R.id.ratingBar);
        //tvAverageRating = findViewById(R.id.tvAverageRating);
        btnAddOrder = findViewById(R.id.btnAddOrder);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Staff Dashboard");
        }
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StaffOrderAdapter(this, orders);
        adapter.setOnOrderStatusChangeListener(this);
        rvOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        orders = dbHelper.getAllOrders();
        adapter.updateOrders(orders);
    }

    /*private void updateAverageRating() {
        List<RestaurantRating> ratings = dbHelper.getRestaurantRatings();
        double total = 0;
        int count = 0;

        for (RestaurantRating rating : ratings) {
            if (rating.getAverageRating() > 0) {
                total += rating.getAverageRating();
                count++;
            }
        }

        if (count > 0) {
            float average = (float) (total / count);
            rbAverageRating.setRating(average);
            tvAverageRating.setText(String.format("%.1f", average));
        }
    }*/



    @Override
    public void onStatusChanged(Order order, Order.OrderStatus newStatus) {
        dbHelper.updateOrderStatus(order.getOrderId(), newStatus);
        loadOrders();
        Toast.makeText(this, "Order status updated", Toast.LENGTH_SHORT).show();
    }
}

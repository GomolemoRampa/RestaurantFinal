package com.cs.restaurantfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cs.restaurantfinal.Order;
import com.cs.restaurantfinal.R;

import java.util.List;

public class StaffOrderAdapter extends RecyclerView.Adapter<StaffOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderStatusChangeListener listener;

    public interface OnOrderStatusChangeListener {
        void onStatusChanged(Order order, Order.OrderStatus newStatus);
    }

    public StaffOrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void setOnOrderStatusChangeListener(OnOrderStatusChangeListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_staff_dashboard, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return (orders != null) ? orders.size() : 0;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvRestaurantName, tvStaffMember, tvOrderTime;
        Button btnPending, btnReady, btnCollected;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvStaffMember = itemView.findViewById(R.id.tvStaffMember);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            btnPending = itemView.findViewById(R.id.btnPending);
            btnReady = itemView.findViewById(R.id.btnReady);
            btnCollected = itemView.findViewById(R.id.btnCollected);
        }

        public void bind(Order order) {
            tvOrderId.setText("Order #" + order.getOrderId());
            tvCustomerName.setText("Customer: " + order.getCustomerName());
            tvRestaurantName.setText("Restaurant: " + order.getRestaurantName());
            tvStaffMember.setText("Staff: " + order.getStaffMember());
            tvOrderTime.setText(DateTimeUtils.formatTime(order.getOrderTime()));

            resetButtonStyles();

            // Highlight current status button by changing text color
            switch (order.getStatus()) {
                case PENDING:
                    highlightButton(btnPending, R.color.status_pending);
                    break;
                case READY:
                    highlightButton(btnReady, R.color.status_ready);
                    break;
                case COLLECTED:
                    highlightButton(btnCollected, R.color.status_collected);
                    break;
            }

            btnPending.setOnClickListener(v -> updateStatus(order, Order.OrderStatus.PENDING));
            btnReady.setOnClickListener(v -> updateStatus(order, Order.OrderStatus.READY));
            btnCollected.setOnClickListener(v -> updateStatus(order, Order.OrderStatus.COLLECTED));
        }

        private void resetButtonStyles() {
            setButtonStyle(btnPending, R.color.text_secondary);
            setButtonStyle(btnReady, R.color.text_secondary);
            setButtonStyle(btnCollected, R.color.text_secondary);
        }

        private void highlightButton(Button button, int colorRes) {
            setButtonStyle(button, colorRes);
        }

        private void setButtonStyle(Button button, int colorRes) {
            int color = ContextCompat.getColor(context, colorRes);
            button.setTextColor(color);
            // Removed button.setStrokeColorResource - use a MaterialButton if you want border color changes
        }

        private void updateStatus(Order order, Order.OrderStatus newStatus) {
            if (listener != null && order.getStatus() != newStatus) {
                listener.onStatusChanged(order, newStatus);
            }
        }
    }
}

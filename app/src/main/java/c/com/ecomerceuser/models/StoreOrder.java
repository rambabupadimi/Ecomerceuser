package c.com.ecomerceuser.models;

/**
 * Created by Ramu on 16-12-2017.
 */

public class StoreOrder {

    OrderModel orderModel;
    int position;

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

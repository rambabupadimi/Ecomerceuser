package c.com.ecomerceuser.models;

import java.io.Serializable;

/**
 * Created by Ramu on 24-12-2017.
 */

public class CartModel implements Serializable {

    String itemId;
    String groupId;
    int quantity;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

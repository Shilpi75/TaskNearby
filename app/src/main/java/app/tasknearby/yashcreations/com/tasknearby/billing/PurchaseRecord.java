package app.tasknearby.yashcreations.com.tasknearby.billing;

import com.android.billingclient.api.Purchase;

import java.util.Date;

/**
 * DataModel that will be used to store the purchase details on Firebase.
 *
 * @author vermayash8
 */
public class PurchaseRecord {

    String orderId;
    String productId;
    String price;
    String datePurchased;
    String purchaseToken;

    public PurchaseRecord() {

    }

    public PurchaseRecord(Purchase purchase, String price) {
        this.orderId = purchase.getOrderId();
        this.productId = purchase.getSku();
        this.price = price;
        this.datePurchased = new Date(purchase.getPurchaseTime()).toString();
        this.purchaseToken = purchase.getPurchaseToken();
    }
}

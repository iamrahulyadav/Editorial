package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import utils.AdsSubscriptionManager;


public class SubscriptionActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        bp = new BillingProcessor(this,
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4lE0nDHaciyEpkXJ2dceN9EPLiuP7hSSOIjzzOF40am/QD4Hwd4CxZg+tco/f7G6GIFW1aJgRiHOCm+crhWUJk854MmWNs3JC1hxe15vH7h0C9s4d6Iw7fTJn4GN5a5tPrQESLd/OFPixFXS7gwePWUCnYl85Uge8tqwPtf2rcotqs3bScxYQQMmCb1fNxXOgB/kULJr9hy9FIzxYdKnSrUMib3rKQTEPKFqyLZgYGOfUwvvclJ7baouZfWemW0nwWKvIxMCsBGdEBI0aCb0on+J8A+pN3f+in5HM8F3eBAHF/MTVkOVoS1EGvIJgjj5exlZJePN+NJI3WtKVFiaPQIDAQAB",
                this);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    public void onDoneClick(View view) {

        bp.subscribe(this, "monthly_subscription");

    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(this, "product purchased - "+productId, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

        Toast.makeText(this, "billing initilized", Toast.LENGTH_SHORT).show();

        SkuDetails skuDetails= bp.getSubscriptionListingDetails("monthly_subscription");
        Toast.makeText(this, "billing "+skuDetails.productId+" - "+skuDetails.isSubscription, Toast.LENGTH_SHORT).show();

        AdsSubscriptionManager.setSubscription(this,skuDetails.isSubscription);

    }
}

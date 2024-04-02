package com.remi.remiads.a_pro;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.remi.remiads.R;
import com.remi.remiads.utils.RmSave;

import java.util.ArrayList;

public class NewBilling {

    private final Activity activity;
    private final boolean isCheck;
    private final BillingConnectListen billingConnectListen;
    private BillingClient billingClient;
    private boolean isConnected;
    private PURCHASE_TYPE mPurchaseType = PURCHASE_TYPE.PURCHASE_UNSPECIFIED;

    private MyBillingResult myBillingResult;

    public NewBilling(Activity a, boolean isCheck, BillingConnectListen billingConnectListen) {
        this.activity = a;
        this.isCheck = isCheck;
        this.billingConnectListen = billingConnectListen;
        isConnected = false;
        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, list) -> {
            if ((billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    || billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)
                    && list != null) {
                if (mPurchaseType == PURCHASE_TYPE.PURCHASE_ONE_TIME) {
                    for (Purchase purchase : list) {
                        for (String arrId : purchase.getProducts()) {
                            if (arrId.equals(activity.getString(R.string.id_pay))) {
                                RmSave.putPayAll(activity, true);
                                handlePurchaseOneTime(purchase);
                                if (myBillingResult != null)
                                    myBillingResult.onPurchasesDone();
                            } else {
                                if (myBillingResult != null)
                                    myBillingResult.onPurchasesProcessing();
                                handlePurchaseMoreTime(purchase);
                            }
                        }
                    }
                } else if (mPurchaseType == PURCHASE_TYPE.PURCHASE_SUBSCRIPTION) {
                    for (Purchase purchase : list) {
                        for (String purchaseId : purchase.getProducts()) {
                            if (purchaseId.equals(activity.getString(R.string.id_sub_month)) || purchaseId.equals(activity.getString(R.string.id_sub_year))
                                    || purchaseId.equals(activity.getString(R.string.id_sub_week))) {
                                saveCache(purchase);
                                RmSave.putPaySubYear(activity, purchaseId.equals(activity.getString(R.string.id_sub_year)));
                                RmSave.putPaySubMonth(activity, purchaseId.equals(activity.getString(R.string.id_sub_month)));
                                RmSave.putPaySubWeek(activity, purchaseId.equals(activity.getString(R.string.id_sub_week)));
                                if (myBillingResult != null)
                                    myBillingResult.onPurchasesDone();
                                handlePurchaseSubscription(purchase);
                                return;
                            }
                        }
                    }
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR
                    || billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED
                    || billingResult.getResponseCode() == BillingClient.BillingResponseCode.DEVELOPER_ERROR
                    || billingResult.getResponseCode() < 0) {
                if (myBillingResult != null)
                    myBillingResult.onPurchasesCancel();
            }
        };
        billingClient = BillingClient.newBuilder(a)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(billingClientStateListener);

    }

    private final BillingClientStateListener billingClientStateListener = new BillingClientStateListener() {
        @Override
        public void onBillingServiceDisconnected() {
            isConnected = false;
            reConnectToGooglePlay();
        }

        @Override
        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
            int responseCode = billingResult.getResponseCode();
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                isConnected = true;
                if (isCheck) {
                    checkHistory();
                }
                if (billingConnectListen != null)
                    billingConnectListen.onConnected();
            } else {
                reConnectToGooglePlay();
            }
        }
    };

    private void reConnectToGooglePlay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (billingClient != null)
                billingClient.startConnection(billingClientStateListener);
        }, 1000);
    }

    private void checkHistory() {
        QueryPurchaseHistoryParams queryPurchaseHistoryParams = QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
        billingClient.queryPurchaseHistoryAsync(queryPurchaseHistoryParams, (billingResult, list) -> {
            if (list != null && !list.isEmpty()) {
                boolean flag = (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ||
                        billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK);
                for (PurchaseHistoryRecord purchase : list) {
                    if (purchase != null) {
                        for (String arrId : purchase.getProducts()) {
                            if (arrId.equals(activity.getString(R.string.id_pay))) {
                                RmSave.putPayAll(activity, flag);
                            }
                        }
                    }
                }
            }
        });
        QueryPurchaseHistoryParams pSub = QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build();
        billingClient.queryPurchaseHistoryAsync(pSub, (billingResult, list) -> {

            RmSave.putPaySubYear(activity, false);
            RmSave.putPaySubMonth(activity, false);
            RmSave.putPaySubWeek(activity, false);
            if (list == null || list.isEmpty()) {
                RmSave.putSubToken(activity, "");
                RmSave.putSubType(activity, "");
            } else {
                for (PurchaseHistoryRecord purchase : list) {
                    if (purchase != null) {
                        for (String purchaseId : purchase.getProducts()) {
                            RmSave.putPaySubYear(activity, purchaseId.equals(activity.getString(R.string.id_sub_year)));
                            RmSave.putPaySubMonth(activity, purchaseId.equals(activity.getString(R.string.id_sub_month)));
                            RmSave.putPaySubWeek(activity, purchaseId.equals(activity.getString(R.string.id_sub_week)));
                        }
                        saveCache(purchase);
                        break;
                    }
                }
            }
        });
    }

    private void saveCache(PurchaseHistoryRecord purchase) {
        RmSave.putSubToken(activity, purchase.getPurchaseToken());
        for (String skus : purchase.getProducts())
            RmSave.putSubType(activity, skus);
    }

    private void saveCache(Purchase purchase) {
        RmSave.putSubToken(activity, purchase.getPurchaseToken());
        for (String skus : purchase.getProducts())
            RmSave.putSubType(activity, skus);
    }

    public void getSkuList(NewPriceResult priceResult, String... list) {
        if (!isConnected || list.length == 0) {
            priceResult.onListPrice(new ArrayList<>());
            return;
        }
        ArrayList<QueryProductDetailsParams.Product> arr = new ArrayList<>();
        for (String s : list) {
            arr.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(s)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build());
        }
        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(arr)
                .build();
        billingClient.queryProductDetailsAsync(queryProductDetailsParams, (billingResult, productDetailsList) -> {
            int responseCode = billingResult.getResponseCode();
            if (responseCode == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                priceResult.onListPrice(productDetailsList);
            }
        });
        arr.clear();
        for (String s : list) {
            arr.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(s)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build());
        }
        QueryProductDetailsParams p = QueryProductDetailsParams.newBuilder()
                .setProductList(arr)
                .build();
        billingClient.queryProductDetailsAsync(p, (billingResult, productDetailsList) -> {
            int responseCode = billingResult.getResponseCode();
            if (responseCode == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                priceResult.onListPrice(productDetailsList);
            }
        });
    }

    private void handlePurchaseOneTime(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {

                });
            }
        }
    }

    private void handlePurchaseMoreTime(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (myBillingResult != null)
                    myBillingResult.onPurchasesProcessing();
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    private void handlePurchaseSubscription(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (purchase.isAcknowledged()) {
                if (myBillingResult != null)
                    myBillingResult.onPurchasesDone();
            } else {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (myBillingResult != null)
                            myBillingResult.onPurchasesDone();
                    }
                });
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            if (myBillingResult != null)
                myBillingResult.onPurchasesProcessing();
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            if (myBillingResult != null)
                myBillingResult.onPurchasesCancel();
        }
    }

    public void makePurchase(ProductDetails productDetails, MyBillingResult _myBillingResult) {
        mPurchaseType = PURCHASE_TYPE.PURCHASE_ONE_TIME;
        makeBuy(productDetails, _myBillingResult);
    }

    public void makePurchaseSubscription(ProductDetails productDetails, MyBillingResult _myBillingResult) {
        mPurchaseType = PURCHASE_TYPE.PURCHASE_SUBSCRIPTION;
        makeBuy(productDetails, _myBillingResult);
    }

    public void makePurchaseWithKey(String key, MyBillingResult _myBillingResult) {
        this.myBillingResult = _myBillingResult;
        if (!isConnected) {
            if (myBillingResult != null)
                myBillingResult.onNotConnect();
            return;
        }
        ArrayList<QueryProductDetailsParams.Product> arr = new ArrayList<>();
        arr.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(key)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        QueryProductDetailsParams p = QueryProductDetailsParams.newBuilder()
                .setProductList(arr)
                .build();
        billingClient.queryProductDetailsAsync(p, (billingResult, productDetailsList) -> {
            int responseCode = billingResult.getResponseCode();
            if (responseCode == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                for (ProductDetails productDetails : productDetailsList) {
                    if (productDetails.getProductId().equals(key)) {
                        makePurchase(productDetails, _myBillingResult);
                        return;
                    }
                }
            }
        });


        ArrayList<String> arrKey = new ArrayList<>();
        arrKey.add(key);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(arrKey).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(), ((billingResult, listSkuDetails) -> {
            int responseCode = billingResult.getResponseCode();
            if (responseCode == BillingClient.BillingResponseCode.OK && listSkuDetails != null && listSkuDetails.size() > 0) {
                for (SkuDetails skuDetails : listSkuDetails) {
                    if (skuDetails.getSku().equals(key)) {
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();
                        billingClient.launchBillingFlow(activity, flowParams);
                        return;
                    }
                }
            } else {
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void makeBuy(ProductDetails productDetails, MyBillingResult _myBillingResult) {
        this.myBillingResult = _myBillingResult;
        if (!isConnected) {
            if (myBillingResult != null)
                myBillingResult.onNotConnect();
            return;
        }
        ArrayList<BillingFlowParams.ProductDetailsParams> arrKey = new ArrayList<>();
        arrKey.add(BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build());
        BillingFlowParams billingFlowParams;
        String tokenOld = RmSave.getSubToken(activity);
        if (mPurchaseType == PURCHASE_TYPE.PURCHASE_ONE_TIME || tokenOld.isEmpty())
            billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(arrKey)
                    .build();
        else
            billingFlowParams = BillingFlowParams.newBuilder()
                    .setSubscriptionUpdateParams(BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                            .setOldPurchaseToken(tokenOld)
                            .setSubscriptionReplacementMode(BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.WITHOUT_PRORATION)
                            .build())
                    .setProductDetailsParamsList(arrKey)
                    .build();
        billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    public boolean checkSubIs(){
        String key = RmSave.getSubType(activity);
        String token = RmSave.getSubToken(activity);
        return !key.isEmpty() && !token.isEmpty();
    }

    public void onDestroy() {
        try {
            if (this.billingClient != null) {
                this.billingClient.endConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.billingClient = null;
    }

}

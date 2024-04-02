package com.remi.remiads.a_pro;

import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.SkuDetails;

import java.util.List;

public interface NewPriceResult {

    void onListPrice(List<ProductDetails> arrPrice);

}

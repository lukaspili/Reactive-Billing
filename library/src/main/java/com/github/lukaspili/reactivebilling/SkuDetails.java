//package com.github.lukaspili.reactivebilling;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class SkuDetails implements Parcelable {
//
//    public final String productId;
//
//    public final String title;
//
//    public final String description;
//
//    public final boolean isSubscription;
//
//    public final String currency;
//
//    public final Double priceValue;
//
//    /**
//     * Use this value to return the raw price from the product.
//     * This allows math to be performed without needing to worry about errors
//     * caused by floating point representations of the product's price.
//     *
//     * This is in micros from the Play Store.
//     */
//    public final long priceLong;
//
//    public final String priceText;
//
//    public SkuDetails(JSONObject source) throws JSONException {
//        String responseType = source.optString(Constants.RESPONSE_TYPE);
//        if (responseType == null)
//            responseType = Constants.PRODUCT_TYPE_MANAGED;
//        productId = source.optString(Constants.RESPONSE_PRODUCT_ID);
//        title = source.optString(Constants.RESPONSE_TITLE);
//        description = source.optString(Constants.RESPONSE_DESCRIPTION);
//        isSubscription = responseType.equalsIgnoreCase(Constants.PRODUCT_TYPE_SUBSCRIPTION);
//        currency = source.optString(Constants.RESPONSE_PRICE_CURRENCY);
//        priceLong = source.optLong(Constants.RESPONSE_PRICE_MICROS);
//        priceValue = priceLong / 1000000d;
//        priceText = source.optString(Constants.RESPONSE_PRICE);
//    }
//
//    @Override
//    public String toString() {
//        return String.format("%s: %s(%s) %f in %s (%s)", productId, title, description, priceValue, currency, priceText);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        SkuDetails that = (SkuDetails) o;
//
//        if (isSubscription != that.isSubscription) return false;
//        return !(productId != null ? !productId.equals(that.productId) : that.productId != null);
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = productId != null ? productId.hashCode() : 0;
//        result = 31 * result + (isSubscription ? 1 : 0);
//        return result;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.productId);
//        dest.writeString(this.title);
//        dest.writeString(this.description);
//        dest.writeByte(isSubscription ? (byte) 1 : (byte) 0);
//        dest.writeString(this.currency);
//        dest.writeDouble(this.priceValue);
//        dest.writeLong(this.priceLong);
//        dest.writeString(this.priceText);
//    }
//
//    protected SkuDetails(Parcel in) {
//        this.productId = in.readString();
//        this.title = in.readString();
//        this.description = in.readString();
//        this.isSubscription = in.readByte() != 0;
//        this.currency = in.readString();
//        this.priceValue = in.readDouble();
//        this.priceLong = in.readLong();
//        this.priceText = in.readString();
//    }
//
//    public static final Creator<SkuDetails> CREATOR = new Creator<SkuDetails>() {
//        public SkuDetails createFromParcel(Parcel source) {
//            return new SkuDetails(source);
//        }
//
//        public SkuDetails[] newArray(int size) {
//            return new SkuDetails[size];
//        }
//    };
//}

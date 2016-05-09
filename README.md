# Reactive Billing for Android

Cut the hassle when implementing the In App Billing on Android.  
Reactive Billing is a lightweight reactive wrapper around In App Billing API v3 for Android.


## Features

* **Reactive:** Exposes the In App Billing service methods as `Observable`, allowing to implement easy asynchrounous callbacks and other Rx-related fun stuff.

* **No configuration:** Doesn't require a single line of configuration. It makes it super easy to implement in any architectures (activities/fragments, single activity, etc).

* **Lightweight:** Only does what it's supposed to do, and nothing more. It doesn't implement any logic related to the billing: purchase verification, storage for offline usage, etc. It is all up to you.

* **Convenient:** Returns objects rather than bundles.


## Version

Reactive Billing supports **In App Billing API v3** only.
The current version (0.1) doesn't support subscriptions yet.


## How does it work?

The Reactive Billing API is accessible through the singleton instance.

```java
ReactiveBilling.getInstance(context)
```

The exposed methods are matching the methods of the AIDL billing service `IInAppBillingService`. With the difference that they return `Observable` objects, also taking care of connecting to the AIDL service.


### Response

Each call to the billing service will return a response object.
The response will match the structure of the original `Bundle`, containing at least a response code.

You can check the responses code in the documentation: [In App Billing reference](http://developer.android.com/google/play/billing/billing_reference.html)


### onNext / onError

The subscriber will always receive `onNext` if the request to the billing service was executed successfully. It doesn't mean though that the operation was successful. You need to check the response code.

You can find all the responses code and their meaning in the [official documentation.](http://developer.android.com/google/play/billing/billing_reference.html#billing-codes)

The subscriber can also receive `onError` if an exception is thrown during the connection to the AIDL billing service (`RemoteException`). Reactive Billing is not doing any logic to catch the exception and the latter will be propagated to the subscriber.


### Threading

Depending on which call and on the current play store cache, the billing service can trigger a synchronous network request. It is then recommended to implement the asynchronous reactive model.

```java
ReactiveBilling.getInstance(getContext())
	.getPurchases(PurchaseType.PRODUCT, null)
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
	.subscribe(...)
```


## Methods

### Is Billing Supported

```java
ReactiveBilling.getInstance(getContext()).isBillingSupported(PurchaseType.PRODUCT)
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
	.subscribe(new Action1<Response>() {
		@Override
		public void call(Response response) {
		   if(response.isSuccess()) {
		   // in app billing is supported
			}
		}
	});
```


###  Get sku details

```java
ReactiveBilling.getInstance(getContext())
	.getSkuDetails(PurchaseType.PRODUCT, "coffee", "beer")
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
	.subscribe(new Action1<GetSkuDetails>() {
	    @Override
	    public void call(GetSkuDetails getSkuDetails) {
			if (getSkuDetails.isSuccess()) {
	            getSkuDetails.getData() // list of items
	        }
	    }
	}, new Action1<Throwable>() {
	    @Override
	    public void call(Throwable throwable) {

	    }
	});
```


### Get purchases

```java
ReactiveBilling.getInstance(getContext())
	.getPurchases(PurchaseType.PRODUCT, null)
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
	.subscribe(new Action1<GetPurchases>() {
	    @Override
	    public void call(GetPurchases getPurchases) {
	        if(getPurchases.isSuccess()) {
		        getPurchases.getItems() // items
		    }
	    }
	}, new Action1<Throwable>() {
	    @Override
	    public void call(Throwable throwable) {

	    }
	});
```


### Buy product

Buying a product is a little bit different because it will start a purchase flow, meaning it will show the Play store purchasing dialog.

#### Subscribe to purchase flow

Because of the Android lifecycle, your activity can be destroyed and recreated while the purchase flow is visible (from configuration changes for instance). Therefore the Reactive Billing library needs to be detached/attached when the activity is recreated.

Reactive Billing requires to subscribe for the purchase flow events during the initialisation, which is usually represented by the following methods:

* `onCreate()` for activities
* `onActivityCreated()` for fragments
* `onAttachedToWindow()` for views

```java
ReactiveBilling.getInstance(this).purchaseFlow()
    .subscribe(new Action1<DidBuy>() {
        @Override
        public void call(DidBuy didBuy) {
            if (didBuy.isSuccess()) {
                didBuy.getPurchase(); // the purchased product
            }
        }
    });
```

#### Start the purchase flow

Once your class is subscribed to the purchase flow, you can start the purchase flow for a product


```java
ReactiveBilling.getInstance(this).buy("your_product_id", PurchaseType.PRODUCT, "your dev payload");
```


#### Full example

```java
public class BuyActivity extends Activity {

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subscription = ReactiveBilling.getInstance(this).purchaseFlow()
            .subscribe(new Action1<DidBuy>() {
                @Override
                public void call(DidBuy didBuy) {
                    // receives the result of the purchase flow
                    if (didBuy.isSuccess()) {
                        didBuy.getPurchase(); // the purchased product
                    }
                }
            });
    }

    public void onProductClick(String productId) {
        // start the purchase flow
        ReactiveBilling.getInstance(this).buy(productId, PurchaseType.PRODUCT, "your dev payload");
    }

    @Override
    protected void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }

        super.onDestroy();
    }
}
```


### Consume purchase

```java
ReactiveBilling.getInstance(getContext())
    .consumePurchase("purchase token")
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<Response>() {
        @Override
        public void call(Response response) {
            if(response.isSuccess()) {
	            // successfully consumed
	        }
        }
    }, new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {

        }
    });
```


## Reactive Billing Example app

You can find the Reactive Billing Example app on the play store:
[https://play.google.com/store/apps/details?id=com.github.lukaspili.reactivebilling.sample](https://play.google.com/store/apps/details?id=com.github.lukaspili.reactivebilling.sample)

The source code is located in the current project, under `sample/`.


## Gradle

Reactive Billing is available on maven central.

```groovy
dependencies {
	compile 'com.github.lukaspili.reactive-billing:reactive-billing:0.1'

	// you would also probably need rxjava and rxandroid
	compile 'io.reactivex:rxjava:1.1.5'
	compile 'io.reactivex:rxandroid:1.2.0'
}
```


## Acknowledgements

* [Reactive Location](https://github.com/mcharmas/Android-ReactiveLocation)
* [RxPermissions](https://github.com/tbruyelle/RxPermissions)


## Author

* Lukasz Piliszczuk ([@lukaspili](https://twitter.com/lukaspili))


## License

Reactive Billing is released under the MIT license. See the LICENSE file for details.

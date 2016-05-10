# Reactive Billing for Android

Cut the hassle when implementing the In App Billing on Android.  
Reactive Billing is a lightweight reactive wrapper around In App Billing API v3 for Android.


## Features

* **Reactive:** Exposes the In App Billing service methods as `Observable`, allowing to implement easy asynchrounous callbacks and other Rx-related fun stuff.

* **No configuration:** Doesn't require to implement activities `onActivityResult()`. It makes it super easy to implement in any architectures (activities/fragments, single activity, etc).

* **Lightweight:** Only does what it's supposed to do, nothing more. It doesn't implement any logic related to the billing: purchase verification, storage for offline usage, etc. It is all up to you.

* **Convenient:** Returns objects rather than bundles.


## Version

Reactive Billing supports **In App Billing API v3** only.  
The current version (0.2) doesn't support subscriptions yet.


## How does it work?

The Reactive Billing API is accessible through the singleton instance.

```java
ReactiveBilling.getInstance(context);
```

The exposed methods are matching the methods of the AIDL billing service `IInAppBillingService`. With the difference that they return `Observable` objects, also taking care of connecting to the AIDL billing service.


### Response

Each call to the billing service will return a response object.  
The response will match the structure of the original `Bundle`, containing at least a response code.

You can check the response codes in the documentation: [In App Billing reference](http://developer.android.com/google/play/billing/billing_reference.html)


### onNext / onError

The subscriber will always receive `onNext` if the request to the billing service is executed successfully. But it doesn't mean that the response of the request is a success. You need to check the returned response code.

You can find all the response codes and their meaning in the documentation: [In App Billing reference](http://developer.android.com/google/play/billing/billing_reference.html#billing-codes)

The subscriber can also receive `onError` if an exception is thrown during the connection to the AIDL billing service (`RemoteException`). Reactive Billing is not doing any logic to catch the exception and the latter will be propagated to the subscriber.


### Threading

Depending on which call and on the current Play Store cache, the billing service can trigger a synchronous network request. It is then recommended to implement the asynchronous reactive model when interacting with the service.

```java
ReactiveBilling.getInstance(getContext())
	.getPurchases(PurchaseType.PRODUCT, null)
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
	.subscribe(...)
```


## Methods

### Is Billing Supported

The Rx version of `IInAppBillingService.isBillingSupported()`


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
	}, new Action1<Throwable>() {
		@Override
		public void call(Throwable throwable) {
		    
		}
	});
```


###  Get sku details

The Rx version of [`IInAppBillingService.getSkuDetails()`](http://developer.android.com/google/play/billing/billing_reference.html#getSkuDetails)


```java
ReactiveBilling.getInstance(getContext())
	.getSkuDetails(PurchaseType.PRODUCT, "coffee", "beer")
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
	.subscribe(new Action1<GetSkuDetailsResponse>() {
	    @Override
	    public void call(GetSkuDetailsResponse response) {
			if (response.isSuccess()) {
	            response.getList() // list of sku details
	        }
	    }
	}, new Action1<Throwable>() {
	    @Override
	    public void call(Throwable throwable) {

	    }
	});
```


### Get purchases

The Rx version of [`IInAppBillingService.getPurchases()`](http://developer.android.com/google/play/billing/billing_reference.html#getPurchases)


```java
ReactiveBilling.getInstance(getContext())
	.getPurchases(PurchaseType.PRODUCT, null)
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
	.subscribe(new Action1<GetPurchasesResponse>() {
	    @Override
	    public void call(GetPurchasesResponse response) {
	        if(response.isSuccess()) {
		        response.getList() // list of purchases
		    }
	    }
	}, new Action1<Throwable>() {
	    @Override
	    public void call(Throwable throwable) {

	    }
	});
```


### Buy product

Buying a product is a little bit different because it's a two step process.

* Start the purchase flow (show the Play store purchasing dialog)
* Receive the purchase flow result (receive the result from previous dialog)


#### Start the purchase flow

The Rx version of [`IInAppBillingService.getBuyIntent()`](http://developer.android.com/google/play/billing/billing_reference.html#getBuyIntent)

In addition, if the request is successful, Reactive Billing will start the purchase flow automatically.

```java
ReactiveBilling.getInstance(getContext())
	.startPurchase(skuDetails.getProductId(), skuDetails.getPurchaseType(), null, null)
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())
	.subscribe(new Action1<Response>() {
	    @Override
	    public void call(Response response) {
	        if (response.isSuccess()) {
	            // purchase flow was started successfully, nothing to do here
	        } else {
	            // handle cannot start purchase flow
	        }
	    }
	}, new Action1<Throwable>() {
	    @Override
	    public void call(Throwable throwable) {
	        
	    }
	});
```

#### Receive purchase flow results

Because of the Android lifecycle, your activity can be destroyed and recreated while the purchase flow is visible. Therefore the subscriber for the purchase flow events needs to be unsubscribed and subscribed again when the activity is recreated.

Reactive Billing requires to subscribe for the purchase flow events during the initialisation, which is usually represented by the following methods:

* `onCreate()` for activities
* `onActivityCreated()` for fragments
* `onAttachedToWindow()` for views

```java
ReactiveBilling.getInstance(this).purchaseFlow()
    .subscribe(new Action1<PurchaseResponse>() {
        @Override
        public void call(PurchaseResponse response) {
            if (response.isSuccess()) {
                response.getPurchase(); // the purchased product
            }
        }
    });
```

You would also want to check if the purchase flow was cancelled.

```java
if (response.isSuccess()) {
    response.getPurchase(); // the purchased product
} else if(response.isCancelled()) {
	// purchase flow cancelled
} else {
	response.getResponseCode(); // purchase flow failed, handle the response code
}
```


#### Extras

In order to be able to differentiate properly the events receiving in the purchase flow observable, you can provide an "extras" bundle when starting the purchase flow. 


#### Full example

```java
public class BuyActivity extends Activity {

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subscription = ReactiveBilling.getInstance(this).purchaseFlow()
            .subscribe(new Action1<PurchaseResponse>() {
                @Override
                public void call(PurchaseResponse response) {
                    // receives the result of the purchase flow
                    if (response.isSuccess()) {
                        response.getPurchase(); // the purchased product
                    } else {
                    	// handle
                    }
                }
            });
    }

    public void onProductClick(String productId) {
        // start the purchase flow
        ReactiveBilling.getInstance(getContext())
			.startPurchase(productId, PurchaseType.PRODUCT, null, null)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Action1<Response>() {
			    @Override
			    public void call(Response response) {
			        if (response.isSuccess()) {
			            // purchase flow was started successfully, nothing to do here
			        } else {
			            // handle cannot start purchase flow
			        }
			    }
			}, new Action1<Throwable>() {
			    @Override
			    public void call(Throwable throwable) {
			        // handle
			    }
			});
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

The Rx version of `IInAppBillingService.consumePurchase()`


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

Reactive Billing is available on Maven Central.

```groovy
dependencies {
	compile 'com.github.lukaspili.reactive-billing:reactive-billing:0.2'

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

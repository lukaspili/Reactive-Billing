package com.github.lukaspili.reactivebilling.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private LinearLayout containerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);

        setSupportActionBar(toolbar);

        viewPager.setAdapter(new AppPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);


//        progressBar = (ProgressBar) findViewById(R.id.progress);
//        containerView = (LinearLayout) findViewById(R.id.container);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        refreshProducts();
    }

//    private void refreshProducts() {
//        Log.d(getClass().getName(), "Refresh products");
//
//        progressBar.setVisibility(View.VISIBLE);
//        containerView.setVisibility(View.GONE);
//
//        ReactiveBilling.getInstance(this)
//                .getPurchases(PurchaseType.PRODUCT, null)
//                .subscribe(new Action1<GetPurchases>() {
//                    @Override
//                    public void call(GetPurchases getPurchases) {
//                        didSucceedGetPurchases(getPurchases);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        didFailGetPurchases(throwable);
//                    }
//                });
//    }
//
//    private void didSucceedGetPurchases(GetPurchases getPurchases) {
//        Log.d(getClass().getName(), "Did succeed get purchases");
//        Log.d(getClass().getName(), String.format("Purchases count: %d", 0));
//
//        progressBar.setVisibility(View.GONE);
//        containerView.setVisibility(View.VISIBLE);
//    }
//
//    private void didFailGetPurchases(Throwable t) {
//        Log.d(getClass().getName(), "Did fail get purchases", t);
//    }

//    private void didClickBuyBeer() {
//
//    }
//
//    private void didClickBuyCoffee() {
//
//    }
}

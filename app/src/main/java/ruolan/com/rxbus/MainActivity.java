package ruolan.com.rxbus;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ruolan.com.rxbus.helper.RxSubscriptions;
import ruolan.com.rxbus.rxbus.RxBus;
import ruolan.com.rxbus.rxbus.RxBusSubscriber;
import rx.Subscription;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView mIvAvator;
    private TextView mTvName,mTvEmail;
    private LinearLayout mLlLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initView(navigationView);
        initListener();

    }

    private void initView(NavigationView navigationView) {
        mIvAvator = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_avator);
        mTvName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        mTvEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        mLlLogin = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.ll_login);

        subscribeEvent();
    }

    private void initListener() {

        mLlLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }

    private Subscription mRxSub;

    private void subscribeEvent(){
        RxSubscriptions.remove(mRxSub);
        mRxSub = RxBus.getDefault().toObservable(UserEvent.class)
                .map(new Func1<UserEvent, UserEvent>() {
                    @Override
                    public UserEvent call(UserEvent userEvent) {
                        return userEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<UserEvent>() {
                    @Override
                    public void onEvent(UserEvent userEvent) {
                        if (userEvent != null){
                            mTvEmail.setText(userEvent.mUserEmail);
                            mTvName.setText(userEvent.mUserName);
                            Glide.with(MainActivity.this)
                                    .load(userEvent.mImageUrl).asBitmap()
                            .into(mIvAvator);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void initView() {
        mIvAvator = (ImageView) findViewById(R.id.iv_avator);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
        mLlLogin = (LinearLayout) findViewById(R.id.ll_login);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

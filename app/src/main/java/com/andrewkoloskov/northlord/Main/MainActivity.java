package com.andrewkoloskov.northlord.Main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.andrewkoloskov.northlord.Profile.profileFragment;
import com.andrewkoloskov.northlord.R;
import com.andrewkoloskov.northlord.RentWorker.LastRentsFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public View mMainView;
    public View mProgressView;
    public String Login;
    public String pas;
    public Fragment last;
    public Fragment add;
    public Fragment mainfrag;
    public Fragment profile;
    public FloatingActionButton fab;
    public int id;
    public boolean hidemenu=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Login=getIntent().getExtras().getString("login");
        pas=getIntent().getExtras().getString("pass");
        mProgressView=findViewById(R.id.login_progress);
        mMainView=findViewById(R.id.MainFrame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment d=new AddCarDialog();
                d.show(getSupportFragmentManager(),"");
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mainfrag=new main_page_fragment();
        profile=new profileFragment();
        last=new LastRentsFragment();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ShowFragment(R.id.nav_main_page);
    }
    public void ShowFragment(int itemId) {
        Fragment fragment = null;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        switch (itemId) {
            case R.id.nav_main_page:
                fragment = mainfrag;
                ((main_page_fragment)mainfrag).showMain();
                hidemenu=true;
                this.invalidateOptionsMenu();
                break;
            case R.id.nav_profile:
                fragment =  profile;
                fab.hide();
                ((main_page_fragment)mainfrag).showProfile();
                hidemenu=true;
                this.invalidateOptionsMenu();
                break;
            case R.id.fab:
                fragment = add;
                fab.hide();
                hidemenu=true;
                this.invalidateOptionsMenu();
                break;
            case R.id.nav_lastrents:
                fragment = mainfrag;
                ((main_page_fragment)mainfrag).showRents();
                fab.hide();
                hidemenu=true;
                this.invalidateOptionsMenu();
                break;
        }
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.MainFrame, fragment);
            fragmentTransaction.commit();
            ((main_page_fragment)mainfrag).update();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (hidemenu) {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ShowFragment(R.id.nav_main_page);
//            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main_page) {
            ShowFragment(R.id.nav_main_page);
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            ShowFragment(R.id.nav_profile);
//
        }else if(id==R.id.nav_lastrents){
            ShowFragment(R.id.nav_lastrents);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.MenuDeleteItem) {
            ((main_page_fragment)mainfrag).delete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

package com.andrewkoloskov.northlord.LoginWorker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import com.andrewkoloskov.northlord.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    int flag = 1;
    LoginMainFragment loginMainFragment=new LoginMainFragment();
    LoginSignFragment loginSignFragment=new LoginSignFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        showFragment(1);
    }

    public void showFragment(int i) {
        Fragment fragment = null;
        switch (i) {
            case 1:
                fragment = loginMainFragment;
                flag=1;
                break;
            case 2:
                fragment = loginSignFragment;
                flag=2;
                break;
            default:
                fragment = loginMainFragment;
                flag=1;
                break;
        }
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.LoginFrame, fragment);
            fragmentTransaction.commit();
        }

    }

    @Override
    public void onBackPressed() {

        if (flag != 1) {
            showFragment(1);
        } else {
            super.onBackPressed();
        }
    }


}


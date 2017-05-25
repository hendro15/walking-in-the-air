
package hendro.com.accelero.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import hendro.com.accelero.R;
import hendro.com.accelero.commons.Dialog;
import hendro.com.accelero.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener {

    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setLayout(savedInstanceState);
        intentNavigation();
        dialog = new Dialog(this);
    }

    private void intentNavigation() {
        Intent intent = getIntent();
        String goto_item = intent.getStringExtra("goto_item");
        if (goto_item != null && !goto_item.isEmpty()) {
            if (goto_item != null && goto_item.equals("home")) {
                Fragment fragment = null;
                Class fragmentClass = HomeFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack("satu").commit();
            }
        }
    }

    private void setLayout(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = HomeFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack("satu").commit();
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void switchFragment(Fragment fragment, String title, String subTitle) {
        setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .addToBackStack("satu")
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}


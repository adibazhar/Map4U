package map2u.com.mapu;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    TextView tvInfo;
    private FragmentPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fab button

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //tab id





        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                    new BasemapFragment()).commit();
            navigationView.setCheckedItem(R.id.item_a);
        }
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
       // String itemName = (String) menuItem.getTitle();
       // int itemName = menuItem.getItemId();
        //tvInfo.setText(itemName);


        /*if(itemName==R.id.item_a){
            Toast.makeText(this,"Basemap selected", Toast.LENGTH_SHORT).show();
        }
        if(itemName==R.id.item_b){
            startActivity(new Intent(MainActivity.this,SecondActivity.class));
        }*/

        switch (menuItem.getItemId()){
            case R.id.item_a:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                        new BasemapFragment()).commit();
                break;

            case R.id.item_b:
                Intent map = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(map);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;

        }
        closeDrawer();
        return true;




    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            closeDrawer();
        }
       else super.onBackPressed();
    }

    /*private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_45));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_165));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }*/


}

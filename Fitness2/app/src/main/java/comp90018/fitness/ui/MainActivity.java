package comp90018.fitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import comp90018.fitness.R;
import comp90018.fitness.databinding.ActivityMainBinding;
import comp90018.fitness.ui.moments.AddMomentActivity;

import static comp90018.fitness.ui.moments.placeholder.PlaceholderContent.getFirebaseData;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private String TAG = "First Demo";
    public static String MESSAGE = "Message";
    public static int MESSAGE_RECEIVE = 1;

    /* end of exercise changes */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* retrive firebase data */
        getFirebaseData();

        /* setting bindings for the activity */
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.Moments, R.id.navigation_home, R.id.map)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        /* add moment button function */
        Button button = findViewById(R.id.addMoment);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 给bnt1添加点击响应事件
                Intent intent = new Intent(MainActivity.this, AddMomentActivity.class);
                //启动
                startActivity(intent);
            }
        });


    }





}
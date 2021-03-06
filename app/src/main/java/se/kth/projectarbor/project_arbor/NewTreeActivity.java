package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;

/*
* Created by Project Arbor
*
* This activity launches with the application, it determines if a tree is alive
* if the tree is alive the game will start. If this is the first time the user
* launches the game a "new tree" button will be presented
*
* This activity resumes the game if it can, if not it will create all the
* necessary objects. It will also start the game logic with an alarm.
*
 */

public class NewTreeActivity extends AppCompatActivity {

    private final static String TAG = "ARBOR_NEW_TREE";
    private Button newTreeBtn;
    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Controls if a tree exist or not, will go to main activity if the tree exist.
        // If a tree dose not exist it will set up the tree view.
        sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("FIRST_TREE", false)) {
            startService(new Intent(NewTreeActivity.this, MainService.class)
            .putExtra("MESSAGE_TYPE", MainService.MSG_TREE_GAME));
        }

        setContentView(R.layout.activity_new_tree);

        // If the user press this button we will save a new game state to the installation
        // folder and start the game logic and go to the main activity view.
        newTreeBtn = (Button) findViewById(R.id.new_tree_btn);
        newTreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.saveState(getApplicationContext(), MainService.filename,
                        new Tree(), new Environment.Forecast[]{}, new Double(0));
                sharedPreferences.edit().putBoolean("FIRST_TREE", true).commit();
                Log.d(TAG, "new save state");

                Intent intent = new Intent(NewTreeActivity.this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_NEED);
                PendingIntent pendingIntent = PendingIntent.getService(NewTreeActivity.this, 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (MainService.ALARM_HOUR * 1000), pendingIntent);

                Intent updateIntent = new Intent(NewTreeActivity.this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_TREE_GAME);
                startService(updateIntent);
            }
        });
    }

}

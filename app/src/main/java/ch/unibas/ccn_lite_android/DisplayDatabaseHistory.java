package ch.unibas.ccn_lite_android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;




public class DisplayDatabaseHistory extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_database_history);

        Intent intent = getIntent();
        String historyValue = intent.getStringExtra("sensorHistory");
        int countOfItemsInDatabase = intent.getIntExtra("countOfItems", 0);

        TextView historyLocation = (TextView) findViewById(R.id.historyShowTextView);
        historyLocation.setMovementMethod(new ScrollingMovementMethod());
        historyLocation.setText("Number of Items: " + countOfItemsInDatabase + "\n" + historyValue , TextView.BufferType.EDITABLE);



    }
}

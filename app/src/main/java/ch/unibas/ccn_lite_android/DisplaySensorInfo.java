package ch.unibas.ccn_lite_android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class DisplaySensorInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensor_info);


        Intent intent = getIntent();
        String infoValue = intent.getStringExtra("sensorInfo");

        TextView infoLocation = (TextView) findViewById(R.id.sensorInfoShowTextView);
        infoLocation.setMovementMethod(new ScrollingMovementMethod());
        infoLocation.setText(infoValue , TextView.BufferType.EDITABLE);

    }
}

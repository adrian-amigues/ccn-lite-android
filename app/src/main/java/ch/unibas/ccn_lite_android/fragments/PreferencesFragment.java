package ch.unibas.ccn_lite_android.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ch.unibas.ccn_lite_android.CcnLiteAndroid;
import ch.unibas.ccn_lite_android.R;


/**
 * Created by maria on 2016-10-05.
 */

public class PreferencesFragment extends Fragment {
    String bodyString;
    String portString;
    String contentString;
    View rootView;

    public PreferencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_preferences, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button b = (Button) rootView.findViewById(R.id.sendButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText body = (EditText) rootView.findViewById(R.id.IPEditText);
                bodyString = body.getText().toString();
                EditText port = (EditText) rootView.findViewById(R.id.portEditText);
                portString = port.getText().toString();
                int portInt = Integer.parseInt(portString);
                EditText content = (EditText) rootView.findViewById(R.id.contentEditText);
                contentString = content.getText().toString();
                String resultValue ="";
                //TODO: change content to uri
                androidMkc("ccnx2015", "127.0.0.1", 9999, contentString, bodyString);

                TextView result = (TextView) rootView.findViewById(R.id.resultTextView);
                result.setMovementMethod(new ScrollingMovementMethod());
                result.setText(resultValue, TextView.BufferType.EDITABLE);
            }
        });
    }

    public native String androidMkc(String suiteString, String ipString, int portInt, String uriString, String bodyString);
}

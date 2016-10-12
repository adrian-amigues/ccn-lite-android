package ch.unibas.ccn_lite_android.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ch.unibas.ccn_lite_android.CcnLiteAndroid;
import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.RelayService;

import static ch.unibas.ccn_lite_android.R.id.resultTextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    RelayService mService;
    boolean mBound = false;


    View rootView;
    String ipString;
    String portString;
    String contentString;
    TextView resultv;
    String resultValue;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to RelayService, cast the IBinder and get RelayService instance
            RelayService.LocalBinder binder = (RelayService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mBound) {
            mService.startRely();
        }


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        resultv = (TextView) rootView.findViewById(R.id.resultTextView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String arraySpinner[] = new String[] {
                "CCNx2015", "NDN2013", "CCNB", "IOT2014", "LOCALRPC", "LOCALRPC"
        };
        Spinner s = (Spinner) rootView.findViewById(R.id.formatSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);

        s.setAdapter(adapter);

        Button b = (Button) rootView.findViewById(R.id.sendButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText ip = (EditText) rootView.findViewById(R.id.IPEditText);
                ipString = ip.getText().toString();
                EditText port = (EditText) rootView.findViewById(R.id.portEditText);
                portString = port.getText().toString();
                int portInt = Integer.parseInt(portString);
                EditText content = (EditText) rootView.findViewById(R.id.contentEditText);
                contentString = content.getText().toString();
                String resultValue ="";
                //CcnLiteAndroid ccnLiteAndroid = new CcnLiteAndroid();
                new AndroidPeek().execute(ipString, Integer.toString(portInt), contentString);
                //result = (TextView) rootView.findViewById(R.id.resultTextView);
                //result.setMovementMethod(new ScrollingMovementMethod());
                //result.setText(resultValue, TextView.BufferType.EDITABLE);
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        // Bind to RelayService
        Intent intent = new Intent(getActivity(), RelayService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(getActivity(), "mBound = " + mBound, Toast.LENGTH_SHORT).show();


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private class AndroidPeek extends AsyncTask<String, Void, String> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected String doInBackground(String... params) {
            String ipString = params[0];
            int portInt = Integer.parseInt(params[1]);
            String contentString = params[2];
            return mService.startAndroidPeek(ipString, portInt, contentString);
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(String result) {
            resultv.setMovementMethod(new ScrollingMovementMethod());
            resultv.append(result);
        }
    }
}

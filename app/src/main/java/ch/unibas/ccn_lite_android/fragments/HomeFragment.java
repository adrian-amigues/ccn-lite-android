package ch.unibas.ccn_lite_android.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ch.unibas.ccn_lite_android.CcnLiteAndroid;
import ch.unibas.ccn_lite_android.R;


public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View rootView;
    String ipString;
    String portString;
    String contentString;
    private String mParam1;
    private String mParam2;
    EditText ipEditText;
    TextView resultTextView;
    EditText portEditText;
    EditText contentEditText;
    Spinner formatSpinner;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ipEditText = (EditText) rootView.findViewById(R.id.IPEditText);
        portEditText = (EditText) rootView.findViewById(R.id.portEditText);
        contentEditText = (EditText) rootView.findViewById(R.id.contentEditText);
        resultTextView = (TextView) rootView.findViewById(R.id.resultTextView);
        formatSpinner = (Spinner) rootView.findViewById(R.id.formatSpinner);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        String arraySpinner[] = new String[] {
                "CCNx2015", "NDN2013", "CCNB", "IOT2014", "LOCALRPC", "LOCALRPC"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);

        formatSpinner.setAdapter(adapter);

        super.onActivityCreated(savedInstanceState);
        Button b = (Button) rootView.findViewById(R.id.sendButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ipString = ipEditText.getText().toString();
                portString = portEditText.getText().toString();
                String formatString = formatSpinner.getSelectedItem().toString();
                int portInt = Integer.parseInt(portString);
                contentString = contentEditText.getText().toString();
                new AndroidPeek().execute(ipString, Integer.toString(portInt), contentString, formatString);
            }
        });


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
            String formatString = params[3];
            return androidPeek(ipString, portInt, contentString, formatString);
        }

        public native String androidPeek(String ipString, int portString, String contentString, String formatString);

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(String result) {
            resultTextView.setMovementMethod(new ScrollingMovementMethod());
            resultTextView.append(result);
        }
    }
}

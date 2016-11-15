package ch.unibas.ccn_lite_android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ch.unibas.ccn_lite_android.R;

/**
 *
 * Created by adrian on 2016-11-04.
 */

public class RelayOptionsFragment extends DialogFragment {
    private boolean useServiceRelay;
    private String externalIp;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(RelayOptionsFragment dialog);
//        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //TODO: move settings to preferences
        int selectedRadio = 0;
        useServiceRelay = true;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            externalIp = bundle.getString("externalIp");
            useServiceRelay = bundle.getBoolean("useServiceRelay");
            selectedRadio = useServiceRelay ? 0 : 1;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_relay_options, null);
        final EditText ipField = (EditText) dialogView.findViewById(R.id.dialog_ip_address);
        ipField.setText(externalIp);
        if (useServiceRelay) {
            ipField.setEnabled(false);
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(R.array.dialog_relay_options_radios, selectedRadio, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                useServiceRelay = true;
                                ipField.setEnabled(false);
                                break;
                            case 1:
                                useServiceRelay = false;
                                ipField.setEnabled(true);
                                break;
                        }
                    }
                })
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        externalIp = ipField.getText().toString();
                        mListener.onDialogPositiveClick(RelayOptionsFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        return builder.create();
    }

    public boolean getUseServiceRelay() {
        return useServiceRelay;
    }

    public String getExternalIp() {
        return externalIp;
    }
}

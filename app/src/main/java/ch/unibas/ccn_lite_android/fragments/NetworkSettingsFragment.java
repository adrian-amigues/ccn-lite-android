package ch.unibas.ccn_lite_android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import ch.unibas.ccn_lite_android.R;

/**
 *  Created by adrian on 2016-11-17.
 */

public class NetworkSettingsFragment extends DialogFragment {
    private boolean useServiceRelay;
    private boolean useAutoRefresh;
    private String externalIp;
    private String ccnSuite;

    public interface NoticeDialogListener {
        void onNetworkSettingsDialogPositiveClick(NetworkSettingsFragment dialog);
//        void onDialogNegativeClick(DialogFragment dialog);
    }

    NetworkSettingsFragment.NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NetworkSettingsFragment.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int selectedRadioRelay = 0;
        int selectedRadioSuite = 0;
        ccnSuite = getString(R.string.default_ccn_suite);
        useServiceRelay = true;
        useAutoRefresh = false;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            externalIp = bundle.getString(getString(R.string.bundle_name_externalIp));
            ccnSuite = bundle.getString(getString(R.string.bundle_name_ccnSuite));
            useServiceRelay = bundle.getBoolean(getString(R.string.bundle_name_useServiceRelay));
            useAutoRefresh = bundle.getBoolean(getString(R.string.bundle_name_useAutoRefresh));
            selectedRadioRelay = useServiceRelay ? 0 : 1;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_network_settings, null);

        // Suite radios
        RadioGroup suiteRg = (RadioGroup) dialogView.findViewById(R.id.dialog_suite_radio_group);
        switch (ccnSuite) {
            case "ccnx2015":
                suiteRg.check(R.id.dialog_suite_radio_ccnx);
                break;
            case "ndn2013":
                suiteRg.check(R.id.dialog_suite_radio_ndn);
                break;
        }
        suiteRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.dialog_suite_radio_ccnx:
                        ccnSuite = "ccnx2015";
                        break;
                    case R.id.dialog_suite_radio_ndn:
                        ccnSuite = "ndn2013";
                        break;
                }
            }
        });

        // Auto refresh checkbox
        CheckBox cb = (CheckBox) dialogView.findViewById(R.id.dialog_suite_checkbox_auto_refresh);
        cb.setChecked(useAutoRefresh);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useAutoRefresh = isChecked;
            }
        });

        // IP address field
        final EditText ipField = (EditText) dialogView.findViewById(R.id.dialog_ip_address);
        ipField.setText(externalIp);
        ipField.setEnabled(!useServiceRelay);

        // Relay radios
        RadioGroup relayRg = (RadioGroup) dialogView.findViewById(R.id.dialog_relay_radio_group);
        if (useServiceRelay) {
            relayRg.check(R.id.dialog_relay_radio_service_relay);
        } else {
            relayRg.check(R.id.dialog_relay_radio_ip_address);
        }
        relayRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.dialog_relay_radio_service_relay:
                        useServiceRelay = true;
                        ipField.setEnabled(false);
                        break;
                    case R.id.dialog_relay_radio_ip_address:
                        useServiceRelay = false;
                        ipField.setEnabled(true);
                        break;
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Network Settings")
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        externalIp = ipField.getText().toString();
                        mListener.onNetworkSettingsDialogPositiveClick(NetworkSettingsFragment.this);
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

    public boolean getUseAutoRefresh() {
        return useAutoRefresh;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public String getCcnSuite() {
        return ccnSuite;
    }
}

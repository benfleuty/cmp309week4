package uk.ac.abertay.telephonysms;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_READ_PHONE_STATE = 0;
    private static final int REQUEST_CODE_READ_CALL_LOG = 1;
    public static final int REQUEST_CODE_RECEIVE_SMS = 2;
    private static final int REQUEST_CODE_STARTUP = 3;

    private final BroadcastReceiver INCOMING_CALL_RECEIVER = new CallReceiver();
    private final BroadcastReceiver OUTGOING_CALL_RECEIVER = new CallHijacker();
    private static final HashMap<Integer, String[]> permissionRequests = new HashMap<Integer, String[]>() {{
        put(REQUEST_CODE_READ_PHONE_STATE, new String[]{Manifest.permission.READ_PHONE_STATE});
        put(REQUEST_CODE_READ_CALL_LOG, new String[]{Manifest.permission.READ_CALL_LOG});
        put(REQUEST_CODE_RECEIVE_SMS, new String[]{Manifest.permission.RECEIVE_SMS});
        put(REQUEST_CODE_STARTUP, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.RECEIVE_SMS});
    }};
    private final BroadcastReceiver INCOMING_SMS_RECEIVER = new IncomingSmsReceiver();
    HashMap<String, Boolean> userIsUneducatedAboutPermission = new HashMap<String, Boolean>() {{
        put(Manifest.permission.READ_PHONE_STATE, true);
        put(Manifest.permission.READ_CALL_LOG, true);
        put(Manifest.permission.RECEIVE_SMS, true);
    }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setPhoneStateListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceivers();
        checkBackgroundPermissions();
    }

    private void checkBackgroundPermissions() {
        requestPermissions(permissionRequests.get(REQUEST_CODE_STARTUP),REQUEST_CODE_STARTUP);
    }

    private void checkReadCallLogPermission() {
        getPermissionState(Manifest.permission.READ_CALL_LOG, REQUEST_CODE_READ_CALL_LOG);
    }

    private void checkSmsReceivePermission() {
        getPermissionState(Manifest.permission.RECEIVE_SMS, REQUEST_CODE_RECEIVE_SMS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(INCOMING_CALL_RECEIVER, filter);

        filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(OUTGOING_CALL_RECEIVER, filter);

        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(INCOMING_SMS_RECEIVER, filter);
    }

    private void unregisterReceivers() {
        unregisterReceiver(INCOMING_CALL_RECEIVER);
        unregisterReceiver(OUTGOING_CALL_RECEIVER);
        unregisterReceiver(INCOMING_SMS_RECEIVER);
    }

    private void setPhoneStateListener() {
        getPermissionState(Manifest.permission.READ_CALL_LOG, REQUEST_CODE_READ_CALL_LOG);

        boolean resultOK = checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;

        if (!resultOK) return;

        String TAG = "PhoneStateListener";
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.i(TAG, "Call state is idle");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.i(TAG,"Call state is off hook");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.i(TAG,"Call state is ringing");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + state);
                }
            }
        };

        telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void updatePhoneDetails() {
        if (getPermissionState(Manifest.permission.READ_PHONE_STATE, REQUEST_CODE_READ_PHONE_STATE))
            getPhoneDetails();
    }

    public void btnEnableNetworkInformation_onClick(View view) {
        // false if don't ask again
        String permission = Manifest.permission.READ_PHONE_STATE;
        if (!shouldShowRequestPermissionRationale(permission)) {
            showRationale("User Intervention Required",
                    "You have previously selected 'Do not ask again' for phone permissions. Please enable this permission in settings to enable this application to gather information about your mobile network",
                    permission,
                    -1);
        } else {
            updatePhoneDetails();
        }
    }

    public void btnGetPhoneDetails_onClick(View view) {
        updatePhoneDetails();
    }

    private boolean getPermissionState(final String permission, final int requestCode) {
        switch (checkSelfPermission(permission)) {
            case PackageManager.PERMISSION_DENIED:
                requestPermissions(new String[]{permission}, requestCode);
                return false;
            case PackageManager.PERMISSION_GRANTED:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0 || grantResults.length == 0) {
            requestPermissions(permissionRequests.get(requestCode), requestCode);
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            boolean isGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

            switch (permissions[i]) {
                case Manifest.permission.READ_PHONE_STATE:
                    if (isGranted) readPhoneStateGranted();
                    else readPhoneStateDenied(permissions[i]);
                    break;
                case Manifest.permission.READ_CALL_LOG:
                    if (isGranted) readCallLogGranted();
                    else readCallLogDenied(permissions[i]);
                    break;
                case Manifest.permission.RECEIVE_SMS:
                    if (isGranted) receiveSmsGranted();
                    else receiveSmsDenied(permissions[i]);
                    break;
                default:
                    Log.wtf("MainActivity/onRequestPermissionsResult", "Permission result for " + permissions[i] + " not implemented!");
            }
        }
    }

    private void receiveSmsDenied(String permission) {
        if (shouldShowRequestPermissionRationale(permission) && userIsUneducatedAboutPermission.get(permission)) {
            showRationale("Receive SMS Permission Needed",
                    "This permission is needed to detect when an SMS message is received.",
                    permission,
                    REQUEST_CODE_RECEIVE_SMS);
            userIsUneducatedAboutPermission.put(permission, false);
        } else {
            Log.d("DEBUG", "no rationale needed");
        }
    }

    private void receiveSmsGranted() {

    }

    private void readCallLogGranted() {
    }

    private void readCallLogDenied(String permission) {
        if (shouldShowRequestPermissionRationale(permission) && userIsUneducatedAboutPermission.get(permission)) {
            showRationale("Read Call Log Permission Needed",
                    "This permission is needed to obtain the phone number you are calling / is calling you.",
                    permission,
                    REQUEST_CODE_READ_CALL_LOG);
            userIsUneducatedAboutPermission.put(permission, false);
        } else {
            Log.d("DEBUG",
                    "no rationale needed");
        }
    }

    private void readPhoneStateDenied(String permission) {
        if (shouldShowRequestPermissionRationale(permission) && userIsUneducatedAboutPermission.get(permission)) {
            showRationale("Read Phone State Permission Needed",
                    "This permission is needed to gather information about your mobile network.",
                    permission,
                    REQUEST_CODE_READ_PHONE_STATE);
            userIsUneducatedAboutPermission.put(permission, false);
        } else {
            disableNetworkOperatorInformation();
        }
    }

    private void disableNetworkOperatorInformation() {
        findViewById(R.id.llgroupNetworkDisplayInformation).setVisibility(View.GONE);
        findViewById(R.id.llgroupEnableInformation).setVisibility(View.VISIBLE);
    }

    private void enableNetworkOperatorInformation() {
        findViewById(R.id.llgroupNetworkDisplayInformation).setVisibility(View.VISIBLE);
        findViewById(R.id.llgroupEnableInformation).setVisibility(View.GONE);
    }

    private void showRationale(String title, String message, String permission, int requestCode) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (requestCode == -1) return;
                        requestPermissions(new String[]{permission}, requestCode);
                    }
                })
                .create()
                .show();
    }

    private void readPhoneStateGranted() {
        getPhoneDetails();
    }

    private void getPhoneDetails() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        boolean resultOK = checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;

        if (resultOK) {
            String operatorId = telephonyManager.getNetworkOperator();
            String countryCode = telephonyManager.getNetworkCountryIso();
            String networkName = telephonyManager.getNetworkOperatorName();
            String phoneNumber = null;
            try {
                phoneNumber = telephonyManager.getLine1Number();
            } catch (Exception e) {
                phoneNumber = "Unknown";
            }
            TextView textCC = findViewById(R.id.textCountryCode);
            TextView textOid = findViewById(R.id.textOperatorID);
            TextView textNetName = findViewById(R.id.textNetworkName);
            TextView textPhone = findViewById(R.id.textCurrentPhoneNumber);

            textCC.setText(countryCode);
            textOid.setText(operatorId);
            textNetName.setText(networkName);
            textPhone.setText(phoneNumber);

            enableNetworkOperatorInformation();
        }
    }

}
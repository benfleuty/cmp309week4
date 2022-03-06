package uk.ac.abertay.telephonysms;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
    }

    private void checkPermissions() {
        checkPhoneStatePermission();
    }

    private void checkPhoneStatePermission() {
        int checkResult = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        switch (checkResult) {
            case android.content.pm.PackageManager.PERMISSION_DENIED:
                permissionPhoneStateDenied();
                break;
            case android.content.pm.PackageManager.PERMISSION_GRANTED:
                permissionPhoneStateGranted();
                break;
        }
    }

    private boolean notAlreadyAsked = true;

    private void permissionPhoneStateDenied() {
        if (!notAlreadyAsked)return;
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
            notAlreadyAsked = false;
            showExplanation("Permission Request", "We need this permission to display information about your network operator", Manifest.permission.READ_PHONE_STATE, REQUEST_CODE_READ_PHONE_STATE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READ_PHONE_STATE);
        }
    }

    // from https://stackoverflow.com/questions/35484767/activitycompat-requestpermissions-not-showing-dialog-box/35486162#35486162
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions(new String[]{permission}, permissionRequestCode);
                    }
                });
        builder.create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_PHONE_STATE:
                handlePhoneStatePermissionChanged(grantResults);
                break;
        }
    }

    private void handlePhoneStatePermissionChanged(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionPhoneStateGranted();
        } else {
            permissionPhoneStateDenied();
        }
    }

    private void permissionPhoneStateGranted() {
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        boolean resultOK = checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;

        if (resultOK) {
            String operatorId = telephonyManager.getNetworkOperator();
            String countryCode = telephonyManager.getNetworkCountryIso();
            String networkName = telephonyManager.getNetworkOperatorName();
            String phoneNumber = telephonyManager.getLine1Number();

            TextView textCC = findViewById(R.id.textCountryCode);
            TextView textOid = findViewById(R.id.textOperatorID);
            TextView textNetName = findViewById(R.id.textNetworkName);
            TextView textPhone = findViewById(R.id.textCurrentPhoneNumber);

            textCC.setText(countryCode);
            textOid.setText(operatorId);
            textNetName.setText(networkName);
            textPhone.setText(phoneNumber);
        }
    }
}
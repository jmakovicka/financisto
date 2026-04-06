package ru.orangesoftware.financisto.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

import ru.orangesoftware.financisto.R;
import ru.orangesoftware.financisto.utils.MyPreferences;

public class RequestPermissionActivity extends FragmentActivity {

    SwitchMaterial toggleGetAccounts;
    SwitchMaterial toggleCamera;
    SwitchMaterial toggleSms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permissions);

        toggleGetAccounts = findViewById(R.id.toggleGetAccounts);
        toggleCamera = findViewById(R.id.toggleCamera);
        toggleSms = findViewById(R.id.toggleSms);

        checkPermissions();

        findViewById(R.id.toggleGetAccounts).setOnClickListener(
                v -> requestPermission(Manifest.permission.GET_ACCOUNTS, toggleGetAccounts));
        findViewById(R.id.toggleCamera).setOnClickListener(
                v -> requestPermission(Manifest.permission.CAMERA, toggleCamera));
        findViewById(R.id.toggleSms).setOnClickListener(
                v -> requestPermission(Manifest.permission.RECEIVE_SMS, toggleSms));

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MyPreferences.switchLocale(base));
    }

    private void checkPermissions() {
        disableToggleIfGranted(Manifest.permission.GET_ACCOUNTS, toggleGetAccounts);
        disableToggleIfGranted(Manifest.permission.CAMERA, toggleCamera);
        disableToggleIfGranted(Manifest.permission.RECEIVE_SMS, toggleSms);
    }

    private void disableToggleIfGranted(String permission, CompoundButton toggleButton) {
        if (isGranted(permission)) {
            toggleButton.setChecked(true);
            toggleButton.setEnabled(false);
        }
    }

    private void requestPermission(String permission, CompoundButton toggleButton) {
        toggleButton.setChecked(false);
        ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
    }

    private boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions();
    }

}

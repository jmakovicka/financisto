package ru.orangesoftware.financisto.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import ru.orangesoftware.financisto.R;
import ru.orangesoftware.financisto.utils.MyPreferences;

@EActivity(R.layout.activity_request_permissions)
public class RequestPermissionActivity extends Activity {

    @Extra("requestedPermission")
    String requestedPermission;

    @ViewById(R.id.toggleGetAccounts)
    SwitchMaterial toggleGetAccounts;

    @ViewById(R.id.toggleCamera)
    SwitchMaterial toggleCamera;

    @ViewById(R.id.toggleSms)
    SwitchMaterial toggleSms;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MyPreferences.switchLocale(base));
    }

    @AfterViews
    public void initViews() {
        checkPermissions();
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

    @Click(R.id.toggleGetAccounts)
    public void onGrantGetAccounts() {
        requestPermission(Manifest.permission.GET_ACCOUNTS, toggleGetAccounts);
    }

    @Click(R.id.toggleCamera)
    public void onGrantCamera() {
        requestPermission(Manifest.permission.CAMERA, toggleCamera);
    }

    @Click(R.id.toggleSms)
    public void onGrantSms() {
        requestPermission(Manifest.permission.RECEIVE_SMS, toggleSms);
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
        checkPermissions();
    }

}

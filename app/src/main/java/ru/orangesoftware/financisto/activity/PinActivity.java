/*******************************************************************************
 * Copyright (c) 2010 Denis Solonenko.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     Denis Solonenko - initial API and implementation
 ******************************************************************************/
package ru.orangesoftware.financisto.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

import ru.orangesoftware.financisto.R;
import ru.orangesoftware.financisto.utils.MyPreferences;
import ru.orangesoftware.financisto.utils.PinProtection;
import ru.orangesoftware.financisto.view.PinView;

public class PinActivity extends FragmentActivity implements PinView.PinListener {

    public static final String SUCCESS = "PIN_SUCCESS";

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private final Handler handler = new Handler();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MyPreferences.switchLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pin = MyPreferences.getPin(this);
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED)
                    return;

                setFingerprintStatus(R.string.fingerprint_error, R.drawable.ic_error_black_48dp, R.color.material_orange);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                setFingerprintStatus(R.string.fingerprint_auth_success, R.drawable.ic_check_circle_black_48dp, R.color.material_teal);
                handler.postDelayed(() -> onSuccess(null), 200);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                setFingerprintStatus(R.string.fingerprint_auth_failed, R.drawable.ic_error_black_48dp, R.color.material_orange);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Locked")
                .setSubtitle("Unlock with biometric")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        if (pin == null) {
            onSuccess(null);
        } else if (canAuth == BiometricManager.BIOMETRIC_SUCCESS && MyPreferences.isPinLockUseFingerprint(this)) {
            setContentView(R.layout.lock_fingerprint);
            askForFingerprint();
        } else {
            usePinLock();
        }
    }

    private void usePinLock() {
        String pin = MyPreferences.getPin(this);
        PinView v = new PinView(this, this, pin, R.layout.lock);
        setContentView(v.getView());
    }

    private void askForFingerprint() {
        View usePinButton = findViewById(R.id.use_pin);
        if (MyPreferences.isUseFingerprintFallbackToPinEnabled(this)) {
            usePinButton.setOnClickListener(v -> usePinLock());
        } else {
            usePinButton.setVisibility(View.GONE);
        }

        biometricPrompt.authenticate(promptInfo);
    }

    private void setFingerprintStatus(int messageResId, int iconResId, int colorResId) {
        TextView status = findViewById(R.id.fingerprint_status);
        ImageView icon = findViewById(R.id.fingerprint_icon);
        int color = ContextCompat.getColor(this, colorResId);
        status.setText(messageResId);
        status.setTextColor(color);
        icon.setImageResource(iconResId);
        icon.setColorFilter(color);
    }

    @Override
    public void onConfirm(String pinBase64) {
    }

    @Override
    public void onSuccess(String pinBase64) {
        PinProtection.pinUnlock(this);
        Intent data = new Intent();
        data.putExtra(SUCCESS, true);
        setResult(RESULT_OK, data);
        finish();
    }
}

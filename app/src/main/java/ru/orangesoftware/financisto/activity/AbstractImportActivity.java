/*
 * Copyright (c) 2011 Denis Solonenko.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package ru.orangesoftware.financisto.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ru.orangesoftware.financisto.R;
import ru.orangesoftware.financisto.utils.MyPreferences;
import ru.orangesoftware.financisto.utils.PinProtection;

public abstract class AbstractImportActivity extends Activity {

    public static final int IMPORT_FILENAME_REQUESTCODE = 0xff;

    private final int layoutId;
    protected ImageButton bBrowse;
    protected TextView edFilename;
    protected Uri fileUri;
    protected String mimeType;

    public AbstractImportActivity(int layoutId) {
        this.layoutId = layoutId;
        this.mimeType = "*/*";
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MyPreferences.switchLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        bBrowse = findViewById(R.id.btn_browse);
        bBrowse.setOnClickListener(v -> openFile());
        edFilename = findViewById(R.id.edFilename);

        internalOnCreate();
    }

    protected void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setDataAndType(fileUri, mimeType);

        try {
            startActivityForResult(intent, IMPORT_FILENAME_REQUESTCODE);
        } catch (ActivityNotFoundException e) {
            // No compatible file manager was found.
            Toast.makeText(this, R.string.no_filemanager_installed, Toast.LENGTH_SHORT).show();
        }

    }

    protected void uriToDisplay() {
        try (Cursor fileCursor = getContentResolver().query(fileUri, null, null, null, null)) {
            assert fileCursor != null;
            int nameIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            fileCursor.moveToFirst();
            String filePath = fileCursor.getString(nameIndex);
            if (filePath != null) {
                edFilename.setText(filePath);
            }
        }
    }

    protected abstract void internalOnCreate();

    protected abstract void updateResultIntentFromUi(Intent data);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMPORT_FILENAME_REQUESTCODE) {
            if (resultCode == RESULT_OK && data != null) {
                fileUri = data.getData();
                if (fileUri != null) {
                    uriToDisplay();
                    savePreferences();
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        PinProtection.lock(this);
        savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PinProtection.unlock(this);
        restorePreferences();
    }

    protected abstract void savePreferences();

    protected abstract void restorePreferences();

}

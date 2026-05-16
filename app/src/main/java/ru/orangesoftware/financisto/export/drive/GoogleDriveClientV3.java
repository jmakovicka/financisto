package ru.orangesoftware.financisto.export.drive;

import android.content.Context;

import java.io.File;

import ru.orangesoftware.financisto.db.DatabaseAdapter;

public class GoogleDriveClientV3 {

    private final Context mContext;

    DatabaseAdapter db;

    public GoogleDriveClientV3(Context context) {
        mContext = context.getApplicationContext();
    }

    public void uploadFile(File f) {
    }

    public void doBackup(DoDriveBackupEvent event) {
    }

    public void listFiles(DoDriveListFilesEvent event) {
    }

    public void doRestore(DoDriveRestoreEvent event) {
    }
}

package ru.orangesoftware.financisto.export.drive;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void doBackup(DoDriveBackupEvent event) { }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void listFiles(DoDriveListFilesEvent event) { }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void doRestore(DoDriveRestoreEvent event) { }
}

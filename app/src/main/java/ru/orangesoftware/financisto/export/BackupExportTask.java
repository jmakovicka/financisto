package ru.orangesoftware.financisto.export;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.OutputStream;

import ru.orangesoftware.financisto.backup.DatabaseExport;
import ru.orangesoftware.financisto.db.DatabaseAdapter;

public class BackupExportTask extends ImportExportAsyncTask {

    public final Uri fileUri;

    public BackupExportTask(Activity context, ProgressDialog dialog) {
        super(context, dialog);
        this.fileUri = null;
    }

    public BackupExportTask(Activity context, ProgressDialog dialog, Uri fileUri) {
        super(context, dialog);
        this.fileUri = fileUri;
    }

    @Override
    protected Object work(Context context, DatabaseAdapter db, String... params) throws Exception {
        DatabaseExport export = new DatabaseExport(context, db.db(), true);
        String backupFileName;
        if (fileUri == null) {
            backupFileName = export.export();
        } else {
            OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri);
            export.export(outputStream);
            try (Cursor fileCursor = context.getContentResolver().query(fileUri, null, null, null, null)) {
                assert fileCursor != null;
                int nameIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileCursor.moveToFirst();
                backupFileName = fileCursor.getString(nameIndex);
            }
        }
        return backupFileName;
    }

    @Override
    protected String getSuccessMessage(Object result) {
        return String.valueOf(result);
    }

}
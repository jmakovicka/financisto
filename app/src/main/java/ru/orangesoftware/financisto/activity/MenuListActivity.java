/*******************************************************************************
 * Copyright (c) 2010 Denis Solonenko.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * Contributors:
 * Denis Solonenko - initial API and implementation
 * Abdsandryk Souza - implementing 2D chart reports
 ******************************************************************************/
package ru.orangesoftware.financisto.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import ru.orangesoftware.financisto.R;
import ru.orangesoftware.financisto.adapter.SummaryEntityListAdapter;
import ru.orangesoftware.financisto.export.BackupExportTask;
import ru.orangesoftware.financisto.export.BackupImportTask;
import ru.orangesoftware.financisto.export.csv.CsvExportOptions;
import ru.orangesoftware.financisto.export.csv.CsvImportOptions;
import ru.orangesoftware.financisto.export.qif.QifExportOptions;
import ru.orangesoftware.financisto.export.qif.QifImportOptions;

import static ru.orangesoftware.financisto.service.DailyAutoBackupScheduler.scheduleNextAutoBackup;

import ru.orangesoftware.financisto.utils.MyPreferences;
import ru.orangesoftware.financisto.utils.PinProtection;

public class MenuListActivity extends ListActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MyPreferences.switchLocale(base));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new SummaryEntityListAdapter(this, MenuListItem.values()));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MenuListItem.values()[position].call(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MenuListItem.ACTIVITY_CSV_EXPORT) {
                CsvExportOptions options = CsvExportOptions.fromIntent(data);
                MenuListItem.doCsvExport(this, options);
            } else if (requestCode == MenuListItem.ACTIVITY_QIF_EXPORT) {
                QifExportOptions options = QifExportOptions.fromIntent(data);
                MenuListItem.doQifExport(this, options);
            } else if (requestCode == MenuListItem.ACTIVITY_CSV_IMPORT) {
                CsvImportOptions options = CsvImportOptions.fromIntent(data);
                MenuListItem.doCsvImport(this, options);
            } else if (requestCode == MenuListItem.ACTIVITY_QIF_IMPORT) {
                QifImportOptions options = QifImportOptions.fromIntent(data);
                MenuListItem.doQifImport(this, options);
            } else if (requestCode == MenuListItem.ACTIVITY_DB_IMPORT) {
                if (data != null) {
                    Uri fileUri = data.getData();
                    if (fileUri != null) {
                        ProgressDialog d = ProgressDialog.show(this, null, getString(R.string.restore_database_inprogress), true);
                        new BackupImportTask(this, d).execute(fileUri.toString());
                    }
                }
            } else if (requestCode == MenuListItem.ACTIVITY_EXPORT_FILENAME) {
                if (data != null) {
                    Uri fileUri = data.getData();
                    ProgressDialog d = ProgressDialog.show(this, null, getString(R.string.backup_database_inprogress), true);
                    final BackupExportTask t = new BackupExportTask(this, d, fileUri);
                    t.execute((String[]) null);
                }
            }
        }

        if (requestCode == MenuListItem.ACTIVITY_CHANGE_PREFERENCES) {
            scheduleNextAutoBackup(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PinProtection.lock(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PinProtection.unlock(this);
    }

    ProgressDialog progressDialog;

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}

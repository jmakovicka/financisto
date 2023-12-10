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
import android.view.View;
import android.widget.ListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import ru.orangesoftware.financisto.R;
import ru.orangesoftware.financisto.adapter.SummaryEntityListAdapter;
import ru.orangesoftware.financisto.bus.GreenRobotBus;
import ru.orangesoftware.financisto.export.BackupImportTask;
import ru.orangesoftware.financisto.export.csv.CsvExportOptions;
import ru.orangesoftware.financisto.export.csv.CsvImportOptions;
import ru.orangesoftware.financisto.export.dropbox.DropboxBackupTask;
import ru.orangesoftware.financisto.export.qif.QifExportOptions;
import ru.orangesoftware.financisto.export.qif.QifImportOptions;
import static ru.orangesoftware.financisto.service.DailyAutoBackupScheduler.scheduleNextAutoBackup;
import ru.orangesoftware.financisto.utils.MyPreferences;
import ru.orangesoftware.financisto.utils.PinProtection;

@EActivity(R.layout.activity_menu_list)
public class MenuListActivity extends ListActivity {

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;

    @Bean
    GreenRobotBus bus;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MyPreferences.switchLocale(base));
    }

    @AfterViews
    protected void init() {
        setListAdapter(new SummaryEntityListAdapter(this, MenuListItem.values()));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MenuListItem.values()[position].call(this);
    }

    @OnActivityResult(MenuListItem.ACTIVITY_CSV_EXPORT)
    public void onCsvExportResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            CsvExportOptions options = CsvExportOptions.fromIntent(data);
            MenuListItem.doCsvExport(this, options);
        }
    }

    @OnActivityResult(MenuListItem.ACTIVITY_QIF_EXPORT)
    public void onQifExportResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            QifExportOptions options = QifExportOptions.fromIntent(data);
            MenuListItem.doQifExport(this, options);
        }
    }

    @OnActivityResult(MenuListItem.ACTIVITY_CSV_IMPORT)
    public void onCsvImportResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            CsvImportOptions options = CsvImportOptions.fromIntent(data);
            MenuListItem.doCsvImport(this, options);
        }
    }

    @OnActivityResult(MenuListItem.ACTIVITY_QIF_IMPORT)
    public void onQifImportResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            QifImportOptions options = QifImportOptions.fromIntent(data);
            MenuListItem.doQifImport(this, options);
        }
    }

    @OnActivityResult(MenuListItem.ACTIVITY_CHANGE_PREFERENCES)
    public void onChangePreferences() {
        scheduleNextAutoBackup(this);
    }

    @OnActivityResult(MenuListItem.ACTIVITY_DB_IMPORT)
    public void onDbImport(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                ProgressDialog d = ProgressDialog.show(this, null, getString(R.string.restore_database_inprogress), true);
                new BackupImportTask(this, d).execute(fileUri.toString());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PinProtection.lock(this);
        bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PinProtection.unlock(this);
        bus.register(this);
    }

    ProgressDialog progressDialog;

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doDropboxBackup(StartDropboxBackup e) {
        ProgressDialog d = ProgressDialog.show(this, null, this.getString(R.string.backup_database_dropbox_inprogress), true);
        new DropboxBackupTask(this, d).execute();
    }

    public static class StartDropboxBackup {
    }

    public static class StartDriveBackup {
    }

}

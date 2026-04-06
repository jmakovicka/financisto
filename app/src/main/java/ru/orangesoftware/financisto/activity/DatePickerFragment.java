package ru.orangesoftware.financisto.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String YEAR = "YEAR";
    public static final String MONTH = "MONTH";
    public static final String DAY_OF_MONTH = "DAY_OF_MONTH";

    private final DatePickerDialog.OnDateSetListener callback;

    public DatePickerFragment(@NonNull DatePickerDialog.OnDateSetListener callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        int year, month, day;

        final Calendar c = Calendar.getInstance();
        // Use the current date as the default date in the picker.
        if (args != null) {
            year = args.getInt(YEAR, c.get(Calendar.YEAR));
            month = args.getInt(MONTH, c.get(Calendar.MONTH));
            day = args.getInt(DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        } else {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        callback.onDateSet(view, year, month, day);
    }
}

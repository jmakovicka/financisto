package ru.orangesoftware.financisto.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public static final String HOUR_OF_DAY = "HOUR_OF_DAY";
    public static final String MINUTE = "MINUTE";

    private final TimePickerDialog.OnTimeSetListener callback;

    public TimePickerFragment(@NonNull TimePickerDialog.OnTimeSetListener callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        int hour, minute;

        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();
        if (args != null) {
            hour = args.getInt(HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
            minute = args.getInt(MINUTE, c.get(Calendar.MINUTE));
        } else {
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        return new TimePickerDialog(getActivity(), this, hour, minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        callback.onTimeSet(view, hourOfDay, minute);
    }
}

package ru.orangesoftware.financisto.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

import ru.orangesoftware.financisto.R;
import ru.orangesoftware.financisto.utils.MyPreferences;
import ru.orangesoftware.financisto.utils.StringUtil;
import ru.orangesoftware.financisto.utils.Utils;

public class CalculatorInput extends DialogFragment {

    public final static String AMOUNT_ARG = "amount";

    protected TextView tvResult;

    protected TextView tvOp;

    protected Vibrator vibrator;

    private final Stack<String> stack = new Stack<>();
    private String result = "0";
    private boolean isRestart = true;
    private boolean isInEquals = false;
    private char lastOp = '\0';
    private AmountListener listener;

    public void setListener(AmountListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculator, container, false);

        final int[] calcButtons = {R.id.b0, R.id.b1, R.id.b2, R.id.b3,
                R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8, R.id.b9, R.id.bAdd,
                R.id.bSubtract, R.id.bDivide, R.id.bMultiply, R.id.bPercent,
                R.id.bPlusMinus, R.id.bDot, R.id.bResult, R.id.bClear, R.id.bDelete};

        for (int id : calcButtons) {
            View bView = view.findViewById(id);
            bView.setOnClickListener(v -> {
                Button b = (Button) v;
                char c = b.getText().charAt(0);
                onButtonClick(c);
            });
        }

        View bOk = view.findViewById(R.id.bOK);
        bOk.setOnClickListener(v -> {
            if (!isInEquals) {
                doEqualsChar();
            }
            listener.onAmountChanged(result);
            dismiss();
        });

        View bCancel = view.findViewById(R.id.bCancel);
        bCancel.setOnClickListener(v -> dismiss());

        tvResult = view.findViewById(R.id.result);
        tvOp = view.findViewById(R.id.op);

        Bundle args = getArguments();
        if (args != null) {
            setDisplay(args.getString(AMOUNT_ARG, "0"));
        } else {
            setDisplay("0");
        }

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        return view;
    }

    private void setDisplay(String s) {
        if (Utils.isNotEmpty(s)) {
            s = s.replaceAll(",", ".");
            result = s;
            tvResult.setText(s);
        }
    }

    private void onButtonClick(char c) {
        if (vibrator != null && MyPreferences.isPinHapticFeedbackEnabled(getActivity())) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
        switch (c) {
            case 'C':
                resetAll();
                break;
            case '<':
                doBackspace();
                break;
            default:
                doButton(c);
                break;
        }
    }

    private void resetAll() {
        setDisplay("0");
        tvOp.setText("");
        lastOp = '\0';
        isRestart = true;
        stack.clear();
    }

    private void doBackspace() {
        String s = tvResult.getText().toString();
        if ("0".equals(s) || isRestart) {
            return;
        }
        String newDisplay = s.length() > 1 ? s.substring(0, s.length() - 1) : "0";
        if ("-".equals(newDisplay)) {
            newDisplay = "0";
        }
        setDisplay(newDisplay);
    }

    private void doButton(char c) {
        if (Character.isDigit(c) || c == '.') {
            addChar(c);
        } else {
            switch (c) {
                case '+':
                case '-':
                case '/':
                case '*':
                    doOpChar(c);
                    break;
                case '%':
                    doPercentChar();
                    break;
                case '=':
                case '\r':
                    doEqualsChar();
                    break;
                case '±':
                    setDisplay(new BigDecimal(result).negate().toPlainString());
                    break;
            }
        }
    }

    private void addChar(char c) {
        String s = tvResult.getText().toString();
        if (c == '.' && s.indexOf('.') != -1 && !isRestart) {
            return;
        }
        if ("0".equals(s)) {
            s = String.valueOf(c);
        } else {
            s += c;
        }
        setDisplay(s);
        if (isRestart) {
            setDisplay(String.valueOf(c));
            isRestart = false;
        }
    }

    private void doOpChar(char op) {
        if (isInEquals) {
            stack.clear();
            isInEquals = false;
        }
        stack.push(result);
        doLastOp();
        lastOp = op;
        tvOp.setText(String.valueOf(lastOp));
    }

    private void doLastOp() {
        isRestart = true;
        if (lastOp == '\0' || stack.size() == 1) {
            return;
        }

        String valTwo = stack.pop();
        String valOne = stack.pop();
        switch (lastOp) {
            case '+':
                stack.push(asNumber(valOne).add(asNumber(valTwo)).toPlainString());
                break;
            case '-':
                stack.push(asNumber(valOne).subtract(asNumber(valTwo)).toPlainString());
                break;
            case '*':
                stack.push(asNumber(valOne).multiply(asNumber(valTwo)).toPlainString());
                break;
            case '/':
                BigDecimal d2 = asNumber(valTwo);
                if (d2.intValue() == 0) {
                    stack.push("0.0");
                } else {
                    stack.push(asNumber(valOne).divide(d2, 2, RoundingMode.HALF_UP).toPlainString());
                }
                break;
            default:
                break;
        }
        setDisplay(stack.peek());
        if (isInEquals) {
            stack.push(valTwo);
        }
    }

    private BigDecimal asNumber(String s) {
        if (StringUtil.isEmpty(s)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s);
    }

    private void doPercentChar() {
        if (stack.size() == 0)
            return;
        setDisplay(new BigDecimal(result).divide(Utils.HUNDRED, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(stack.peek())).toPlainString());
        tvOp.setText("");
    }

    private void doEqualsChar() {
        if (lastOp == '\0') {
            return;
        }
        if (!isInEquals) {
            isInEquals = true;
            stack.push(result);
        }
        doLastOp();
        tvOp.setText("");
    }

}

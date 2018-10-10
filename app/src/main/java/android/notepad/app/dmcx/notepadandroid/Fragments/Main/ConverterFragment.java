package android.notepad.app.dmcx.notepadandroid.Fragments.Main;


import android.app.AlertDialog;
import android.notepad.app.dmcx.notepadandroid.Activities.MainActivity;
import android.notepad.app.dmcx.notepadandroid.Activities.Variables.Vars;
import android.notepad.app.dmcx.notepadandroid.Fragments.Main.Converter.ConverterController;
import android.notepad.app.dmcx.notepadandroid.Interface.IActionCallback;
import android.notepad.app.dmcx.notepadandroid.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class ConverterFragment extends Fragment {

    public static final String TAG = "CURRENCY CONVERTER TAG";

    private EditText enterValueEditText;
    private Button convertButton;
    private Spinner currencySpinnerFrom;
    private Spinner currencySpinnerTo;
    private TextView resultTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency, container, false);

        enterValueEditText = view.findViewById(R.id.enterValueEditText);
        convertButton = view.findViewById(R.id.convertButton);
        currencySpinnerFrom = view.findViewById(R.id.currencySpinnerFrom);
        currencySpinnerTo = view.findViewById(R.id.currencySpinnerTo);
        resultTextView = view.findViewById(R.id.resultTextView);

        Locale[] locales = Locale.getAvailableLocales();
        List<String> countries = new ArrayList<>();
        final ArrayList<String> currencies = new ArrayList<>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }

            try {
                Currency.getInstance(locale).getDisplayName();
                String currency = Currency.getInstance(locale).getCurrencyCode();
                if (currency.length() > 0 && !currencies.contains(currency)) {
                    currencies.add(currency);
                }

            } catch (Exception e) {
                // pass
            }

        }

        Collections.sort(countries);
        Collections.sort(currencies);

        List<String> cFrom = new ArrayList<>(currencies);
        List<String> cTo = new ArrayList<>(currencies);
        cFrom.add(0, "From");
        cTo.add(0, "To");

        currencySpinnerFrom.setAdapter(new ArrayAdapter<>(MainActivity.instance, R.layout.style_spnner_text_view, cFrom));
        currencySpinnerTo.setAdapter(new ArrayAdapter<>(MainActivity.instance, R.layout.style_spnner_text_view, cTo));

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Vars.IsOnline) {
                    Toast.makeText(MainActivity.instance, "Internet connection needed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (enterValueEditText.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.instance, "Enter some value.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currencySpinnerFrom.getSelectedItem().toString().equals("From") ||
                        currencySpinnerTo.getSelectedItem().toString().equals("To") ) {
                    Toast.makeText(MainActivity.instance, "Select a 'From' and a 'To'.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final float value = Float.valueOf(enterValueEditText.getText().toString());

                final AlertDialog dialog = new SpotsDialog(MainActivity.instance, "Calculating...");
                dialog.show();

                ConverterController.Currency(currencySpinnerFrom.getSelectedItem().toString() + "_" + currencySpinnerTo.getSelectedItem().toString(), new IActionCallback() {
                    @Override
                    public void onCallback(Object object) {
                        dialog.dismiss();

                        if (object instanceof Float) {
                            float apiValue = Float.valueOf(String.valueOf(object));
                            apiValue *= value;

                            resultTextView.setText(String.valueOf(apiValue));
                        } else {
                            String errorCode = String.valueOf(object);
                            Toast.makeText(MainActivity.instance, errorCode, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}

package cu.lenier.cashrpido.bottomsheet;


import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import cu.lenier.cashrpido.R;

public class BottomSheetAddCard extends BottomSheetDialogFragment {

    private static final int MAX_DIGITS = 16;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_bottom_sheet_add_card, container, false);

        // Referencias a vistas
        TextInputEditText etCardNumber = view.findViewById(R.id.et_card_number);
        TextInputEditText etCardName = view.findViewById(R.id.et_card_name);
        AutoCompleteTextView spinnerCurrency = view.findViewById(R.id.spinner_currency);
        MaterialButton btnAddCard = view.findViewById(R.id.btn_add_card);



        // InputFilter para contar solo dígitos (sin guiones)
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                // Texto actual sin guiones
                String current = dest.toString().replaceAll("-", "");
                int currentLength = current.length();

                // Texto que se va a agregar sin guiones
                String added = source.subSequence(start, end).toString().replaceAll("-", "");
                if (currentLength + added.length() > MAX_DIGITS) {
                    return "";
                }
                return null;
            }
        };
        // Aplica el filtro al campo (elimina maxLength en XML)
        etCardNumber.setFilters(new InputFilter[]{filter});

        // TextWatcher para formatear el número en grupos de 4 dígitos con guiones.
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se requiere acción
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se requiere acción
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;
                // Eliminar guiones existentes
                String digitsOnly = s.toString().replaceAll("-", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digitsOnly.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append("-");
                    }
                    formatted.append(digitsOnly.charAt(i));
                }
                etCardNumber.setText(formatted.toString());
                etCardNumber.setSelection(formatted.length());
                isFormatting = false;
            }
        });

        // Acción del botón: Guardar tarjeta
        btnAddCard.setOnClickListener(v -> {
            // Obtener el número de tarjeta sin guiones
            String cardNumber = etCardNumber.getText() != null
                    ? etCardNumber.getText().toString().replaceAll("-", "")
                    : "";
            String cardName = etCardName.getText() != null ? etCardName.getText().toString() : "";


            // Procesa o guarda los datos según necesites

            dismiss(); // Cierra el BottomSheet
        });


        return view;
    }
}

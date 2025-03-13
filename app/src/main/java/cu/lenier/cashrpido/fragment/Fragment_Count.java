package cu.lenier.cashrpido.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cu.lenier.cashrpido.R;
import cu.lenier.cashrpido.bottomsheet.BottomSheetAddCard;

public class Fragment_Count extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_count, container, false);

        // Cambiar el título del Toolbar
        if (getActivity() != null) {
            getActivity().setTitle(R.string.title_card);
        }


        Button btn = view.findViewById(R.id.btn_add_card);

        btn.setOnClickListener(v -> {
            BottomSheetAddCard bottomSheet = new BottomSheetAddCard();
            bottomSheet.show(getParentFragmentManager(), "BottomSheetAddCard");
        });



        return view;
    }
}
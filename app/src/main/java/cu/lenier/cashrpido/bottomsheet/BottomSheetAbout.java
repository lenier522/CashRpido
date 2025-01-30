package cu.lenier.cashrpido.bottomsheet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import com.google.android.material.switchmaterial.SwitchMaterial;

import cu.lenier.cashrpido.R;

public class BottomSheetAbout extends BottomSheetDialogFragment {

    ImageView play,face,what;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_about, container, false);

        TextView aboutTitle = view.findViewById(R.id.aboutTitle);
        TextView aboutDescription = view.findViewById(R.id.aboutDescription);

    
        aboutTitle.setText(R.string.acerca_de);
        aboutDescription.setText(R.string.esta_es_una_aplicaci_n_de_finanzas_dise_ada_para_ayudarte);

        play = view.findViewById(R.id.googlePlayLogo);
        face = view.findViewById(R.id.facebookLogo);
        what = view.findViewById(R.id.whatssapLogo);

        play.setOnClickListener(v -> {
            String url = "https://play.google.com/store/apps/details?id=cu.lenier.cashrpido";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        what.setOnClickListener(v -> {
            String url = "https://chat.whatsapp.com/KcbXwUB8gZH6GISMuuBky4";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}

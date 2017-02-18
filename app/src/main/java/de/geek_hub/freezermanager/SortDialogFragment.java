package de.geek_hub.freezermanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SortDialogFragment extends DialogFragment {
    private SortDialogListener sortDialogListener;

    public interface SortDialogListener {
        void onSortSelect(int position);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.main_pick_sort)
                .setItems(R.array.sort_options, (dialog, which) -> sortDialogListener.onSortSelect(which))
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sortDialogListener = (SortDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SortDialogListener");
        }
    }
}
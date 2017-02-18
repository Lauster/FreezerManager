package de.geek_hub.freezermanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;

class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private final ItemList itemList;
    private final Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final LinearLayout view;
        ViewHolder(LinearLayout view) {
            super(view);
            this.view = view;
        }
    }

    ItemListAdapter(ItemList itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.two_line_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView text1 = (TextView) holder.view.findViewById(R.id.text1);
        TextView text2 = (TextView) holder.view.findViewById(R.id.text2);

        Item item = itemList.getItem(position);
        if (item.getSize() == -1) {
            text1.setText(String.format(Locale.getDefault(), "%1$s", item.getName()));
        } else {
            String[] unitIds = this.context.getResources().getStringArray(R.array.unit_ids);
            String[] units = this.context.getResources().getStringArray(R.array.units);

            text1.setText(String.format(Locale.getDefault(),
                    "%1$s (%2$s %3$s)",
                    item.getName(),
                    new DecimalFormat("##########.##########").format(item.getSize()),
                    units[Arrays.asList(unitIds).indexOf(item.getUnit())]));
        }

        java.text.DateFormat dateFormat = DateFormat.getDateFormat(this.context);
        if (item.getExpDate() == null) {
            text2.setText(String.format("%1$s", dateFormat.format(item.getFreezeDate())));
        } else {
            text2.setText(String.format("%1$s  |  %2$s",
                    dateFormat.format(item.getFreezeDate()),
                    dateFormat.format(item.getExpDate())));
        }

    }

    @Override
    public int getItemCount() {
        return itemList.length();
    }
}

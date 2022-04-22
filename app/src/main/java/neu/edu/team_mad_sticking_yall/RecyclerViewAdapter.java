package neu.edu.team_mad_sticking_yall;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private final ArrayList<ItemCard> itemList;
    private ItemCardListener listener;

    //Constructor
    public RecyclerViewAdapter(ArrayList<ItemCard> itemList) {
        this.itemList = itemList;
    }

    public void setOnItemClickListener(ItemCardListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new RecyclerViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        ItemCard currentItem = itemList.get(position);

        holder.itemIcon.setImageResource(currentItem.getImageSource());
        holder.itemName.setText(currentItem.getItemName());
        holder.itemDesc.setText(currentItem.getItemDesc());
        holder.checkBox.setChecked(currentItem.getStatus());

        if (currentItem.isOnline) {
            holder.onlineStatus.setText(R.string.itemOnLine);
            holder.onlineStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            holder.onlineStatus.setText(R.string.itemOffLine);
            holder.onlineStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

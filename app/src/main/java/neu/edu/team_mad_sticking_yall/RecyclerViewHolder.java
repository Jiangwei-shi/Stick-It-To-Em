package neu.edu.team_mad_sticking_yall;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView itemIcon;
    public TextView itemName;
    public TextView itemDesc;
    public TextView onlineStatus;
    public CheckBox checkBox;

    public RecyclerViewHolder(View itemView, final ItemCardListener listener) {
        super(itemView);

        itemIcon = itemView.findViewById(R.id.item_icon);
        itemName = itemView.findViewById(R.id.item_name);
        itemDesc = itemView.findViewById(R.id.item_desc);
        checkBox = itemView.findViewById(R.id.checkbox);
        onlineStatus = itemView.findViewById(R.id.onlineStatus);

        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        listener.onItemClick(position);
                    }
                }
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCheckBoxClick(position);
                    }
                }
            }
        });
    }
}

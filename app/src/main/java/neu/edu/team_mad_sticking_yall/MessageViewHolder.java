package neu.edu.team_mad_sticking_yall;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public CardView messageCardView;
    public RelativeLayout recipientLayout;
    public ImageView recipientStickIcon;
    public TextView recipientStickName;
    public TextView recipientTime;

    public RelativeLayout senderLayout;
    public ImageView senderStickIcon;
    public TextView senderStickName;
    public TextView senderTime;

    public MessageViewHolder(View msgView, final MessageCardListener listener) {
        super(msgView);

        messageCardView = msgView.findViewById(R.id.messageCardView);

        recipientLayout = msgView.findViewById(R.id.recipientLayout);
        recipientLayout.setVisibility(View.INVISIBLE);
        recipientStickIcon = msgView.findViewById(R.id.recipientStickIcon);
        recipientStickName = msgView.findViewById(R.id.recipientStickName);
        recipientTime = msgView.findViewById(R.id.recipientTime);

        senderLayout = msgView.findViewById(R.id.senderLayout);
        senderLayout.setVisibility(View.INVISIBLE);
        senderStickIcon = msgView.findViewById(R.id.senderStickIcon);
        senderStickName = msgView.findViewById(R.id.senderStickName);
        senderTime = msgView.findViewById(R.id.senderTime);

        msgView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
//                    int position = getLayoutPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//
//                        listener.onItemClick(position);
//                    }
                }
            }
        });

    }
}

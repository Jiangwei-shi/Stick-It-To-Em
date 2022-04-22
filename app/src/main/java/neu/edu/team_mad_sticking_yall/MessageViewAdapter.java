package neu.edu.team_mad_sticking_yall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private final ArrayList<MessageCard> messageList;
    private MessageCardListener listener;

    //Constructor
    public MessageViewAdapter(ArrayList<MessageCard> itemList) {
        this.messageList = itemList;
    }

    public void setOnItemClickListener(MessageCardListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card, parent, false);
        return new MessageViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageCard messageCard = messageList.get(position);

        if (messageCard.getGravity() == MessageGravity.LEFT) {
            holder.recipientLayout.setVisibility(View.VISIBLE);
            holder.senderLayout.setVisibility(View.INVISIBLE);
            holder.recipientStickIcon.setImageResource(messageCard.getImageSource());
            holder.recipientStickName.setText(messageCard.getStickName() + "[" + messageCard.getSenderName() + "]");
            holder.recipientTime.setText(messageCard.getMessageTime());
        } else if (messageCard.getGravity() == MessageGravity.RIGHT) {
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.ALIGN_RIGHT);
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            holder.messageCardView.setLayoutParams(params);

            holder.recipientLayout.setVisibility(View.INVISIBLE);
            holder.senderLayout.setVisibility(View.VISIBLE);
            holder.senderStickIcon.setImageResource(messageCard.getImageSource());
            holder.senderStickName.setText(messageCard.getStickName() + "[" + messageCard.getSenderName() + "]");
            holder.senderTime.setText(messageCard.getMessageTime());
        }




    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

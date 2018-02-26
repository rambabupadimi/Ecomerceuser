package c.com.ecomerceuser;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import c.com.ecomerceuser.helper.DateTimeUtilities;
import c.com.ecomerceuser.models.ChatMessage;


/**
 * Created by user on 07-05-2017.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatMessage> chatMessageList;
    String senderId;
    private SparseBooleanArray selectedItems;
    private static int currentSelectedIndex = -1;
    public static int itemPositionInAdapter=0;
    private ChatListAdapterListener listener;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messageLeft,messageRight,messageLeftTime,messageRightTime;
        LinearLayout leftLayout,rightLayout;
        LinearLayout dateAndTimeLayout;
        TextView dateAndTimeTextview;
        public MyViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.message_layout_left);
            rightLayout = (LinearLayout) view.findViewById(R.id.message_layout_right);
            messageLeft =  (TextView) view.findViewById(R.id.message_left);
            messageLeftTime =  (TextView) view.findViewById(R.id.send_time_left);
            messageRight =  (TextView) view.findViewById(R.id.message_right);
            messageRightTime =  (TextView) view.findViewById(R.id.send_time_right);


            dateAndTimeLayout   = (LinearLayout) itemView.findViewById(R.id.date_and_time_chat_layout);
            dateAndTimeTextview = (TextView) itemView.findViewById(R.id.date_and_time_bubble);
            dateAndTimeLayout.setEnabled(false);
            dateAndTimeLayout.setClickable(false);



        }
    }


    public ChatAdapter(List<ChatMessage> chatMessageList, String senderId, ChatListAdapterListener listener) {
        this.chatMessageList = chatMessageList;
        this.senderId = senderId;
        this.listener = listener;
        selectedItems       =   new SparseBooleanArray();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        itemPositionInAdapter = position;
        ChatMessage chatMessage =chatMessageList.get(position);
       try {
           if (chatMessage.getSenderId().toString().equalsIgnoreCase(senderId)) {
               // visible right
               holder.rightLayout.setVisibility(View.VISIBLE);
               holder.leftLayout.setVisibility(View.GONE);
               holder.messageRight.setText(chatMessage.getMessage());
               holder.messageRightTime.setText(chatMessage.getSent_time().split(" ")[1] + chatMessage.getSent_time().split(" ")[2]);

           } else {
               holder.rightLayout.setVisibility(View.GONE);
               holder.leftLayout.setVisibility(View.VISIBLE);
               holder.messageLeft.setText(chatMessage.getMessage());
               holder.messageLeftTime.setText(chatMessage.getSent_time().split(" ")[1] + chatMessage.getSent_time().split(" ")[2]);

           }
       }catch (Exception e)
       {
           e.printStackTrace();
       }
        showDateAndTimeLayout(holder,chatMessageList,position);


        holder.itemView.setActivated(selectedItems.get(position, false));

        holder.rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.rightLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);

                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;

            }
        });

    }

    @Override
    public int getItemCount() {
        return (chatMessageList!=null)?chatMessageList.size():0;
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);

        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }


    private void showDateAndTimeLayout(MyViewHolder holder,List<ChatMessage> chatMessageEntityList,int position)
    {
        if(chatMessageEntityList!=null && chatMessageEntityList.size()>0)
        {
            if(position==0)
            {


                String prevTime = chatMessageEntityList.get(position).getSent_time();
                String prevDate = prevTime.split(" ")[0];
                holder.dateAndTimeLayout.setVisibility(View.VISIBLE);
                holder.dateAndTimeLayout.setActivated(false);
                if(prevDate.equalsIgnoreCase(DateTimeUtilities.getCurrentOnlyDate()))
                {
                    holder.dateAndTimeTextview.setText("TODAY");
                }
/*
                else if(prevDate.equalsIgnoreCase(DateTimeUtilities.getYesterdayDate()))
                {
                    holder.dateAndTimeTextview.setText(" YESTERDAY ");
                }
*/
                else {
                    holder.dateAndTimeTextview.setText(DateTimeUtilities.getConvertedTime(prevDate));
                }
            }
            if(position>0)
            {
                String prevTime = chatMessageEntityList.get(position-1).getSent_time();
                String prevDate =prevTime.split(" ")[0];

                String currentTime = chatMessageEntityList.get(position).getSent_time();
                String currentDate  = currentTime.split(" ")[0];
                if(!prevDate.toString().equalsIgnoreCase(currentDate))
                {
                    holder.dateAndTimeLayout.setVisibility(View.VISIBLE);
                    if(currentDate.equalsIgnoreCase(DateTimeUtilities.getCurrentOnlyDate()))
                    {
                        holder.dateAndTimeTextview.setText("TODAY");
                    }
/*
                    else if(currentDate.equalsIgnoreCase(DateTimeUtilities.getYesterdayDate()))
                    {
                        holder.dateAndTimeTextview.setText("YESTERDAY");
                    }
*/
                    else {
                        holder.dateAndTimeTextview.setText(DateTimeUtilities.getConvertedTime(currentDate));
                    }
                }
                else
                {
                    holder.dateAndTimeLayout.setVisibility(View.GONE);
                }
            }

        }
    }





    public  ChatMessage getItemScrolledPosition()
    {
        return chatMessageList.get(itemPositionInAdapter);
    }



    public interface ChatListAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }


}

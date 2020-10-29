package com.code4rox.videoplayer.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.code4rox.videoplayer.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScrollingWidgetsAdapter extends RecyclerView.Adapter<ScrollingWidgetsAdapter.ScrollingWidgetHolder> {
    private ArrayList<Integer> widgetslist;
    private boolean timerTriggered;
    private long millisUntilFinished;
    private int deviceWidth, totalitemwidth, marginright;
    private static int totalitemsize;
    private int widgetid;

    public ScrollingWidgetsAdapter(ArrayList<Integer> widgets) {

        this.widgetslist = widgets;
        totalitemsize = widgets.size();
    }

    @NonNull
    @Override
    public ScrollingWidgetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlefloatcontrolwidget, parent, false);
        return new ScrollingWidgetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollingWidgetHolder holder, int position) {
        int widgetid = widgetslist.get(position);
        holder.widget.setImageResource(widgetid);
        if (widgetid == R.drawable.ic_time_ic) {
            if (timerTriggered) {
                holder.timer.setText("" + String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                holder.timer.setVisibility(View.VISIBLE);
            } else {
                holder.timer.setVisibility(View.GONE);
            }
        } else {
            holder.timer.setVisibility(View.GONE);
        }
        holder.widget.setOnClickListener((v) -> {
//            int i = 0;
            EventBus.getDefault().post(new MessageEvent(position, widgetid));

        });


    }


    public static int sizeoflist(int size) {
        totalitemsize = size;
        return totalitemsize;
    }

    @Override
    public int getItemCount() {
        return totalitemsize;
    }

    public static int totalitemcount() {
        return totalitemsize;
    }

    public void changeitemviewImage(int position, int imagereference) {
        widgetslist.set(position, imagereference);
//        notifyItemChanged(position);
//        notifyItemChanged(position, imagereference);
    }

    public void changeTimerView(int position, long millisUntilFinished, boolean timerTriggered) {
        this.millisUntilFinished = millisUntilFinished;
        this.timerTriggered = timerTriggered;
        Log.d("timerstatus", "insideviewchangedfunction");
        notifyItemChanged(position);
    }

    public class ScrollingWidgetHolder extends RecyclerView.ViewHolder {
        public ImageButton widget;
        //        public RelativeLayout widgetview;
        public TextView timer;

        public ScrollingWidgetHolder(@NonNull View itemView) {
            super(itemView);
            this.widget = itemView.findViewById(R.id.demowidget);
//            this.widgetview = itemView.findViewById(R.id.widgetview);
            this.timer = itemView.findViewById(R.id.timer);
            /*        if (position == getItemCount() - 2) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            int x= ScreenUtils.Companion.getScreenWidth(holder.itemView.getContext());
            marginright = ScreenUtils.Companion.getScreenWidth(holder.itemView.getContext()) - (3 * ScreenUtils.Companion.dpToPx(holder.itemView.getContext(), 35));
            layoutParams.rightMargin = marginright;
            holder.itemView.setLayoutParams(layoutParams);
        }*/


        }

    }


    public class MessageEvent {

        public int position, imageReference;

        public MessageEvent(int position, int drawable) {
            this.position = position;
            this.imageReference = drawable;


        }
    }
}

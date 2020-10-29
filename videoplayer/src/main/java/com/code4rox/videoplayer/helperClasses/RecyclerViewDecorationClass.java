package com.code4rox.videoplayer.helperClasses;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.util.Log;

public class RecyclerViewDecorationClass extends RecyclerView.ItemDecoration {
    private int deviceWidth;
    private int marginright;
    private Context mcontext;
    private Drawable mDivider;

    public RecyclerViewDecorationClass() {

    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        if (parent.getContext().getResources().getConfiguration().orientation == parent.getContext().getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            deviceWidth = getScreenWidth(parent.getContext());
            int itemswidth = 2 * ScreenUtils.Companion.dpToPx(parent.getContext(), 35);
            marginright = (deviceWidth) - (itemswidth+30);

        } else {
            deviceWidth = getScreenWidth(parent.getContext());
            int itemswidth = 3 * ScreenUtils.Companion.dpToPx(parent.getContext(), 35);
            marginright = deviceWidth - (itemswidth+40);
        }
        Log.d("recyclerview", "reyclerwidth" + deviceWidth);
        Log.d("recyclerview", "margin" + marginright);
        try {
            if ((parent.getChildLayoutPosition(view) == parent.getAdapter().getItemCount() - 1)) {
                outRect.right = marginright;


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }
        Point size = new Point();
        if (display != null) {
            display.getSize(size);
        }

        return size.x;
    }
}

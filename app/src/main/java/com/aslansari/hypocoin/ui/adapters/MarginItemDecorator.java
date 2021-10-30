package com.aslansari.hypocoin.ui.adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private final int marginVertical;
    private final int marginHorizontal;

    public MarginItemDecorator(int marginVertical, int marginHorizontal) {
        this.marginVertical = marginVertical;
        this.marginHorizontal = marginHorizontal;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = marginVertical;
        }
        outRect.left = marginHorizontal;
        outRect.right = marginHorizontal;
        outRect.bottom = marginVertical;
    }
}

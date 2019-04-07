package com.example.grindingmachine;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class DigitListView extends ListView {
    public DigitListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DigitListView(Context context) {
        super(context);
    }

    public DigitListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int witdhSpec = MeasureSpec.makeMeasureSpec(400,
                MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(400,
                MeasureSpec.AT_MOST);
        super.onMeasure(witdhSpec, heightSpec);
    }
}

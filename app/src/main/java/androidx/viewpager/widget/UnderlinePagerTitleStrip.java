package androidx.viewpager.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.cypressworks.mensaplan.R;

/**
 * Created by Kirill on 29.10.2014.
 */
public class UnderlinePagerTitleStrip extends PagerTitleStrip {

    private final Paint mTabPaint = new Paint();
    private int mTabPadding;
    private int mIndicatorHeight;
    private int mTabAlpha = 0xFF;
    private int mIndicatorColor;

    public UnderlinePagerTitleStrip(final Context context) {
        super(context);
        init();
    }

    public UnderlinePagerTitleStrip(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Resources res = getResources();
        mTabPadding = (int) res.getDimension(R.dimen.tab_padding);
        mIndicatorHeight = (int) res.getDimension(R.dimen.indicator_height);
        mIndicatorColor = res.getColor(R.color.accent);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        final int bottom = getHeight();
        final int left = mCurrText.getLeft() - mTabPadding;
        final int right = mCurrText.getRight() + mTabPadding;
        final int top = bottom - mIndicatorHeight;
        mTabPaint.setColor(mTabAlpha << 24 | (mIndicatorColor & 0xFFFFFF));
        canvas.drawRect(left, top, right, bottom, mTabPaint);
    }

    @Override
    void updateTextPositions(int position, float positionOffset, boolean force) {
        super.updateTextPositions(position, positionOffset, force);
        mTabAlpha = (int) (Math.abs(positionOffset - 0.5f) * 2 * 0xFF);
    }
}

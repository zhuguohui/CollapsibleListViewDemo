package com.zgh.collapsiblelistview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by yuelin on 2016/6/3.
 */
public class CollapsibleListView extends LinearLayout implements View.OnClickListener {
    TextView tv_title;
    boolean isOpen = false;
    ListView listview;
    boolean isAnimatting = false;
    ValueAnimator animatorOpen, animatorClose;
    private String mTitle;
    private BaseAdapter mAdapter;
    private int openHight;
    private int openDuration;
    private static final int DEFAULT_HEIGHT = 150;//dp
    private static final int DEFAULT_DURATION = 500;//ms
    private static final String DEFAULT_TITLE = "标题";
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#E14C60");
    ;
    private static final int DEFAULT_CONTENT_COLOR = Color.parseColor("#C76C78");
    private static final int DEFAULT_TITLE_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_CORNER_RADIUS = 10;
    private Drawable openDrawable, closeDrawable;
    private int titleColor;
    private int contentColor;
    private int cornerRadius = 5;
    private int titleTextColor;

    public CollapsibleListView(Context context) {
        this(context, null);
    }

    public CollapsibleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_collapsible_listview, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        listview = (ListView) findViewById(R.id.list_view);
        tv_title.setOnClickListener(this);
        resolveAttrs(context, attrs);
        initDrawable();
        initAnimator();
        initBackground();
        tv_title.setText(mTitle);
        tv_title.setTextColor(titleTextColor);
    }

    private void initBackground() {
        // 外部矩形弧度
        float[] outerR = new float[8];
        for (int i = 0; i < outerR.length; i++) {
            outerR[i] = cornerRadius;
        }
        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable background = new ShapeDrawable(rr);
        //指定填充颜色
        background.getPaint().setColor(contentColor);
        // 指定填充模式
        background.getPaint().setStyle(Paint.Style.FILL);
        findViewById(R.id.ll_back).setBackgroundDrawable(background);
        ShapeDrawable title = new ShapeDrawable(rr);
        title.getPaint().setColor(titleColor);
        title.getPaint().setStyle(Paint.Style.FILL);
        tv_title.setBackgroundDrawable(title);

    }


    private void initDrawable() {
        openDrawable = createDrawable(R.drawable.ic_jiantou_up);
        closeDrawable = createDrawable(R.drawable.ic_jiantou_down);
    }

    private Drawable createDrawable(int rid) {
        Drawable drawable = getResources().getDrawable(
                rid);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        return drawable;
    }

    private void resolveAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CollapsibleListView);
        String title = array.getString(R.styleable.CollapsibleListView_CollapsibleTitle);
        if (title == null) {
            title = DEFAULT_TITLE;
        }
        mTitle = title;
        openHight = (int) array.getDimension(R.styleable.CollapsibleListView_CollapsibleHight, dip2px(getContext(), DEFAULT_HEIGHT));
        openDuration = array.getInt(R.styleable.CollapsibleListView_CollapsibleDuration, DEFAULT_DURATION);
        if (openDuration < 0) {
            openDuration = DEFAULT_DURATION;
        }
        titleColor = array.getColor(R.styleable.CollapsibleListView_CollapsibleTitleColor, DEFAULT_TITLE_COLOR);
        contentColor = array.getColor(R.styleable.CollapsibleListView_CollapsibleContentColor, DEFAULT_CONTENT_COLOR);
        cornerRadius = (int) array.getDimension(R.styleable.CollapsibleListView_CollapsibleCornerRadius, DEFAULT_CORNER_RADIUS);
        titleTextColor = array.getColor(R.styleable.CollapsibleListView_CollapsibleTitleTextColor, DEFAULT_TITLE_TEXT_COLOR);
        array.recycle();
    }

    private void initAnimator() {
        animatorClose = creatAnimator(openHight, 0);
        animatorOpen = creatAnimator(0, openHight);
    }

    public void setAdapter(@NonNull BaseAdapter adapter) {
        mAdapter = adapter;
        listview.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_title) {
            toggle();
        }
    }

    private void toggle() {
        if (isAnimatting) {
            return;
        }

        ValueAnimator animator = isOpen ? animatorClose : animatorOpen;
        animator.start();
    }

    private ValueAnimator creatAnimator(int from, int hight) {
        ValueAnimator animator = ValueAnimator.ofInt(from, hight);
        animator.setDuration(openDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = listview.getLayoutParams();
                params.height = h;
                listview.setLayoutParams(params);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimatting = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isOpen = !isOpen;
                Drawable drawable = isOpen ? openDrawable : closeDrawable;
                tv_title.setCompoundDrawables(null, null, drawable, null);
                isAnimatting = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isAnimatting = true;
            }
        });
        return animator;
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

# CollapsibleListViewDemo

#效果

![这里写图片描述](http://img.blog.csdn.net/20160607094526837)

#使用
##1.添加依赖
```
dependencies {
    compile 'com.zgh.collapsiblelistview:collapsiblelistview:1.0.1'
}
```
##2.xml布局

```
    <com.zgh.collapsiblelistview.CollapsibleListView
        android:id="@+id/clv_3"
        android:layout_width="220dp"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="20dp"
        app:CollapsibleContentColor="#7CFBE1"
        app:CollapsibleDuration="250"
        app:CollapsibleHight="150dp"
        app:CollapsibleTitle="列表三"
        app:CollapsibleCornerRadius="10dp"
        app:CollapsibleTitleTextColor="#000000"
        app:CollapsibleTitleColor="#A4D2D2" />

```
##3.设置adapter与点击事件

```
 ArrayAdapter<String> adapter;
    CollapsibleListView clv_1,clv_2,clv_3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter=new ArrayAdapter<String>(this,R.layout.simple_list_item,new String[]{"Item1","Item2","Item3","Item4","Item5","Item6","Item7"});
        clv_1= (CollapsibleListView) this.findViewById(R.id.clv_1);
        clv_1.setAdapter(adapter);
        clv_1.getListview().setOnItemClickListener(new MyOnItemClickListener("first"));
     
    }

    public  class MyOnItemClickListener implements AdapterView.OnItemClickListener{
        String title;
        public MyOnItemClickListener(String title){
            this.title=title;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this,title+" "+parent.getAdapter().getItem(position)+" click",Toast.LENGTH_SHORT).show();
        }
    }
```

#属性

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="CollapsibleListView">
        <!--标题-->
        <attr name="CollapsibleTitle" format="string" />
        <!--展开listview的高度-->
        <attr name="CollapsibleHight" format="dimension" />
        <!--展开时间-->
        <attr name="CollapsibleDuration" format="integer" />
        <!--标题布局颜色-->
        <attr name="CollapsibleTitleColor" format="color" />
        <!--内容布局颜色-->
        <attr name="CollapsibleContentColor" format="color" />
        <!--圆角-->
        <attr name="CollapsibleCornerRadius" format="dimension" />
        <!--标题文字颜色-->
        <attr name="CollapsibleTitleTextColor" format="color" />
    </declare-styleable>
</resources>
```
#实现
实现的部分没有什么难度，主要讲两点，一个是折叠效果的实现，还有一个是背景的生成
##1.折叠效果实现,

通过使用ValueAnimator在动画执行的时候，修改ListView的高度。

```
 ValueAnimator animatorOpen, animatorClose;
    
 private void initAnimator() {
        animatorClose = creatAnimator(openHight, 0);
        animatorOpen = creatAnimator(0, openHight);
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
```
##2.背景生成
由于需要可定制圆角大小，所以不能单纯的直接设置布局的背景色，此处采用的是在解析完属性以后根据属性动态生成ShapeDrawable做完布局的背景

```
	private static final int DEFAULT_HEIGHT = 150;//dp
    private static final int DEFAULT_DURATION = 500;//ms
    private static final String DEFAULT_TITLE = "标题";
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#E14C60");
    private static final int DEFAULT_CONTENT_COLOR = Color.parseColor("#C76C78");
    private static final int DEFAULT_TITLE_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_CORNER_RADIUS = 10;
    private Drawable openDrawable, closeDrawable;
    private int titleColor;
    private int contentColor;
    private int cornerRadius = 5;
    private int titleTextColor;

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

```

#GitHub
https://github.com/zhuguohui/CollapsibleListViewDemo

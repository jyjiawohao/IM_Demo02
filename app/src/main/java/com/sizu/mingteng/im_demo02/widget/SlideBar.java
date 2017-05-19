package com.sizu.mingteng.im_demo02.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.util.DensityUtil;
import com.sizu.mingteng.im_demo02.R;
import com.sizu.mingteng.im_demo02.utils.StringUtils;

import java.util.List;

/**
 * Created by lenovo on 2017/5/14.
 */

public class SlideBar extends View {
    private static final String[] SECTIONS = {"搜", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"
            , "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private int mAvgWidth;
    private int mAvgHeight;
    private Paint mPaint;
    private TextView mTvFloat;
    private RecyclerView mRecyclerView;

    public SlideBar(Context context) {
        super(context, null);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec); //测量宽高
     /*   MeasureSpec.AT_MOST; //wrap_content 的时候用
        MeasureSpec.EXACTLY; //match_parent
        MeasureSpec.UNSPECIFIED;*/

        int measuredHeight = getMeasuredHeight(); //获取高
        int measuredWidth = getMeasuredWidth();//获取宽
        mAvgWidth = measuredWidth / 2;
        mAvgHeight = measuredHeight / SECTIONS.length; //高度除以 总字母的长度 每一个文本的评价高度
    }
    /*  给ViewGroup 用的
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }*/

    /**
     * 创建画笔
     */
    private void init() {
        //创建画笔  传递一个参数:  ANTI_ALIAS_FLAG 不要有锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        /**
         * 单位是像素  像素是屏幕的密度有关系
         */
        mPaint.setTextSize(DensityUtil.sp2px(getContext(), 10)); //参数是像素值 把sp 转换为像传递进去 DensityUtil环信里面的转换util
        mPaint.setTextAlign(Paint.Align.CENTER); //设置文本对齐 设置中间值 中心点对齐
        // mPaint.setColor(getResources().getColor(R.color.inActive)); //设置画笔的颜色
        mPaint.setColor(Color.parseColor("#9c9c9c"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //按下
                /**
                 * 1.设置背景为灰色 矩形 圆角
                 * 2. 显示点赞的section文本
                 * 3. 判断通讯录中正好有当前section字母开头的用户,定位recyclerview的位置,让用户看见
                 */
                break;
            case MotionEvent.ACTION_MOVE: //移动
                /**
                 * 1.设置背景为灰色 矩形 圆角
                 * 2. move到哪个文本Section就把floatTitle 修改掉
                 * 3. 判断通讯录中正好有当前Section字母开头的用户，则定位RecyclerView的位置，让用户看见
                 */
                setBackgroundResource(R.drawable.slidebar_bk);
                showFloatAndScrollRecyclerView(event.getY());
                break;
            case MotionEvent.ACTION_UP: //抬起 手指离开
                /**
                 * 1. 隐藏背景(把背景变成全透明的)，隐藏floatTitle
                 */
                setBackgroundColor(Color.TRANSPARENT);
                if (mTvFloat != null) {
                    mTvFloat.setVisibility(GONE);
                    //mTvFloat.setTextColor(getResources().getColor(R.color.transparent));
                }
                break;
        }

        return true; //让自己消费掉
    }

    /**
     * 显示 float
     *
     * @param y
     */
    private void showFloatAndScrollRecyclerView(float y) {
        /**
         * 根据y坐标计算点中的文本
         */
        int index = (int) (y / mAvgHeight);
        if (index < 0) { //y 的最小是0
            index = 0;
        } else if (index > SECTIONS.length - 1) { //最大 不能大于 文本长度的角标
            index = SECTIONS.length - 1;
        }
        String section = SECTIONS[index];
        /**
         * 获取FloatTitle(先让SlideBar找父控件，然后让父控件找FloatTitle)，然后设置section
         */
        if (mTvFloat == null) { //不要频繁找 ID
            ViewGroup parent = (ViewGroup) getParent();//在子类里面找到父控件
            mTvFloat = (TextView) parent.findViewById(R.id.tv_float); //频繁查找 比较好性能
            mRecyclerView = (RecyclerView) parent.findViewById(R.id.recyclerView);
        }
        mTvFloat.setVisibility(VISIBLE);
        // mTvFloat.setTextColor(getResources().getColor(R.color.white));
        mTvFloat.setText(section);
        /**
         * 拿到section后去判断这个section在RecyclerView中的所有数据中的脚标（也可能不存在）
         *
         *  通过RecyclerView获取到Adapter，通过Adapter获取到联系人数据
         */

        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        //可以通过强转 到我们自己的Adapter 拿到数据  但是这样不好用 以后再写别的项目就不好复用了
        /*ContactAdapter   adapter1 = (ContactAdapter) mRecyclerView.getAdapter();
        List<String> data = adapter1.getData();*/
        if (adapter instanceof IContactAdapter) { //实例
            IContactAdapter contactAdapter = (IContactAdapter) adapter;
            List<String> data = contactAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                String contact = data.get(i);
                if (section.equals(StringUtils.getInitial(contact))) {
                    mRecyclerView.smoothScrollToPosition(i);
                    return;
                }
            }
            mRecyclerView.smoothScrollToPosition(1);//RecyclerView 的平滑移动
        } else {
            throw new RuntimeException("使用SlideBar时绑定的Adapter必须实现IContactAdapter接口");
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 这个方法会频繁调用 ,所以不要在这个方法了吗创建对象
        for (int i = 0; i < SECTIONS.length; i++) {
            /**
             * 参数1:需要绘制的文本, 2:宽 3:高  4:画笔
             */
            canvas.drawText(SECTIONS[i], mAvgWidth, mAvgHeight * (i + 1), mPaint);
        }
    }
}

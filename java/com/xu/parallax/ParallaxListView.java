package com.xu.parallax;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

public class ParallaxListView extends ListView{

	//实现构造方法
	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParallaxListView(Context context) {
		super(context);
	}
	//需要设置图片的最大的高度，图片高度完全显示就可以了
	private int maxHeight;
	//listview高度变大，要拿到图片的引用才能操纵图片
	private ImageView imageView;
	private int orignalHeight;//ImageView最初的高度
	//设置视差的图片，提供的一个方法，把图片放进去
	public void setParallaxImageView( final ImageView imageView){
		this.imageView = imageView;
		
		//设定最大高度
		imageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				//可能还没有完全获取到高度，所有用这个方法，全局的布局监听不用布局测量
				//这个方法在imgview执行完之后在执行的，下面在获取高度就没有问题了
				imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				//控件的高度
				orignalHeight = imageView.getHeight();
				Log.e("tag", "orignalHeight: "+orignalHeight);
				//获取图片的高度
				int drawableHeight = imageView.getDrawable().getIntrinsicHeight();
				//因为图片的长宽是不一样的，需要优化一下
				//如果控件的高度大于图片的高度。最大高度就设定为乘以2，否者图片高度大就用图片的高度
				maxHeight = orignalHeight>drawableHeight?orignalHeight*2:drawableHeight;
			}
		});
		
	}
	
	/**下拉刷新的时候就可以用这个了
	 * 在listview滑动到头的时候执行，可以获取到继续滑动的距离和方向
	 * deltaX：继续滑动x方向的距离
	 * deltaY：继续滑动y方向的距离     负：表示顶部到头   正：表示底部到头
	 * maxOverScrollX:x方向最大可以滚动的距离
	 * maxOverScrollY：y方向最大可以滚动的距离
	 * isTouchEvent: true: 是手指拖动滑动     false:表示fling靠惯性滑动;
	 */
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,int scrollY,int scrollRangeX,
								   int scrollRangeY,int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		
//		Log.e("tag", "deltaY: "+deltaY   +   "  isTouchEvent:"+isTouchEvent);
		//图片变大，发生的情况是，判断顶部到头并且在手动往下拖的话
		if(deltaY<0 && isTouchEvent){
			//表示顶部到头，并且是手动拖动到头的情况
			//我们需要不断的增加ImageView的高度，需要变高
			if(imageView!=null){
				//新的高度等于当前控件的高度减去deltaY（高度增加，因为是负值，所以要减去）
				int newHeight = imageView.getHeight()-deltaY/3;
				if(newHeight>maxHeight)newHeight = maxHeight;
				//新的高度重写赋值给控件
				imageView.getLayoutParams().height = newHeight;
				//使ImageView的布局参数生效
				imageView.requestLayout();
			}
		}
		//所有可以滚动的方法，都有这个方法可以设置
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	//松开的时候需要恢复图片
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction()==MotionEvent.ACTION_UP){
			//这只是一个值的动画器，没有具体的动画，所有要添加动画更新的监听
			//需要将ImageView的高度缓慢恢复到最初高度。用到了展开动画，就是view高度变高
			//高度变化是int值的变化，参数：现在的高度变成最初的高度
			ValueAnimator animator = ValueAnimator.ofInt(imageView.getHeight(),orignalHeight);
			//添加动画更新的监听
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					//获取动画的值，设置给imageview，直接用整数来接收，这个值就是高度
					int animatedValue = (Integer) animator.getAnimatedValue();

					//把高度给他，
					imageView.getLayoutParams().height = animatedValue;
					//在生效一下就可以了
					imageView.requestLayout();//使ImageView的布局参数生效
				}
			});
			//设置了一个弹性的插值器（改变动画运动的轨迹，有很多种类）数值越大弹性最大
			animator.setInterpolator(new OvershootInterpolator(5));
			animator.setDuration(350);
			animator.start();
		}
		return super.onTouchEvent(ev);
	}
}

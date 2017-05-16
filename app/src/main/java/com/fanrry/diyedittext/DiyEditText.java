package com.fanrry.diyedittext;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;



/**
 * <p>
 * 类描述：
 * </p>
 *
 * @author fanrry
 * @date 16/8/2下午2:57
 */
public class DiyEditText extends RelativeLayout {

    private static final float D_TEXT_SIZE = 4.0f;
    private TextView textView;
    private EditText editText;
    private int dY;
    private ValueAnimator animator;
    private String hint;
    private int intPutType;
    private boolean isShowClear;
    private OnAnimatorEndListener listener;
    public ImageView hidePwd;
    private ImageView clearImage;
    private ImageView errImage;
    private boolean mPwdIsShow;
    private LinearLayout diyLayout;
    private boolean isVCode;
    private int marginRight;

    public interface onChangeListener {
        void onChange(String s);
    }

    onChangeListener onChangeListener;

    public DiyEditText(Context context) {
        super(context);
        intView(context, null);
    }

    public DiyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        intView(context, attrs);
    }

    public DiyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DiyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        intView(context, attrs);
    }

    private void intView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DiyEitText);
        hint = a.getString(R.styleable.DiyEitText_hint);
        intPutType = a.getInt(R.styleable.DiyEitText_intPutType, 0);
        marginRight = a.getInt(R.styleable.DiyEitText_marginRight, 0);
        isShowClear = a.getBoolean(R.styleable.DiyEitText_isShowClear, true);
        isVCode = a.getBoolean(R.styleable.DiyEitText_isVCode, false);
        View view = LayoutInflater.from(context).inflate(R.layout.diy_edit_text, this);
        editText = (EditText) view.findViewById(R.id.edit_text);
        textView = (TextView) view.findViewById(R.id.text_view);
        hidePwd = (ImageView) view.findViewById(R.id.diy_edit_eye);
        clearImage = (ImageView) view.findViewById(R.id.diy_edit_delet);
        errImage = (ImageView) view.findViewById(R.id.diy_edit_error);
        diyLayout = (LinearLayout) view.findViewById(R.id.diy_layout);
        clearImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    errImage.setVisibility(INVISIBLE);
                }
                if (hasFocus && editText.getText().length() == 0) {
                    startAnimate();
                } else if (!hasFocus && editText.getText().length() == 0) {
                    if (animator != null) {
                        animator.reverse();
                        animator = null;
                    }
                }

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearImage.setVisibility(s.length() > 0 ? VISIBLE : GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onChangeListener != null)
                    onChangeListener.onChange(s.toString());
            }
        });

        if (hint != null)
            textView.setText(hint);

        editText.setInputType(intPutType);

        hidePwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mPwdIsShow) {

                    hidePwd.setImageResource(R.mipmap.show_pwd);
                    editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editText.setSelection(editText.getText().length());
                    mPwdIsShow = true;
                } else {

                    hidePwd.setImageResource(R.mipmap.hide_pwd);
                    hidePwd();
                    editText.setSelection(editText.getText().length());


                }
            }
        });

        if (intPutType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {//密码框
            hidePwd.setVisibility(VISIBLE);
            hidePwd();
        } else if (isVCode && (intPutType == InputType.TYPE_CLASS_TEXT || intPutType == InputType.TYPE_CLASS_NUMBER)) {//证码框

//            setPadding(Utils.Dp2Px(getContext(), 12), 0, Utils.Dp2Px(getContext(), 140), 0);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(0, 0, 140, 0);
//            diyLayout.setLayoutParams(lp);

            setMargins(diyLayout, 0, 0,dp2Px(getContext(), marginRight), 0);

            hidePwd.setVisibility(GONE);
        } else if (intPutType == InputType.TYPE_CLASS_TEXT) {


        } else {
            hidePwd.setVisibility(GONE);
        }

    }

    private int dp2Px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void hidePwd() {
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mPwdIsShow = false;
    }


    /**
     * 动态设置margin值
     *
     * @param v 要设置margin值的对象
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     */
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void startAnimate() {
        if (animator != null) {
            return;
        }
        dY = 0 - (editText.getHeight() - textView.getHeight()) / 2 - textView.getHeight() / 2;
        animator = ValueAnimator.ofFloat(0, dY);
        animator.setTarget(textView);
        animator.setDuration(200);
//      animator.setInterpolator(value)
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setTranslationY((Float) animation.getAnimatedValue());
                float bit = animation.getAnimatedFraction();
                textView.setTextSize(18 - D_TEXT_SIZE * bit);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null)
                    listener.onEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public String getEidtText() {
        return editText.getText().toString();
    }

    public void setEidtText(String text) {
        editText.setText(text);
    }

    public void setHint(String hint) {
        textView.setText(hint);
    }

    public void setInputType(int inputType) {
        editText.setInputType(inputType);
    }


    /**
     * 密码输入错误时变换图标并失去密码框焦点
     *
     * @param icon
     */
    public void changeIcon(int icon) {
        editText.clearFocus();
        errImage.setVisibility(VISIBLE);
    }

    public void startAnimation() {
        startAnimate();
    }

    public void setAnimatonEndListener(OnAnimatorEndListener listener) {
        this.listener = listener;
    }

    public interface OnAnimatorEndListener {
        void onEnd();
    }

    public void setEidtEnable(boolean enable) {
        editText.setEnabled(enable);
        if (!enable && animator != null) {
            editText.clearFocus();
        }
    }

    public void reverseAnimiton() {
        if (animator != null) {
            animator.reverse();
            animator = null;
        }
    }


    public void setChangeListner(onChangeListener listner) {
        this.onChangeListener = listner;
    }

//    @TargetApi(Build.VERSION_CODES.KITKAT)

    public void showPopupWindow(String errMess) {
        errImage.setVisibility(VISIBLE);
        View contentView = LayoutInflater.from(getContext()).inflate(
                R.layout.pop_window, null);
        TextView upWindowTv = (TextView) contentView.findViewById(R.id.upwindow_tv);
        upWindowTv.setText(errMess);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
//        popupWindow.setFocusable(false);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.upwindow2));

        popupWindow.setOutsideTouchable(true);


        //获取在整个屏幕内的绝对坐标，注意这个值是要从屏幕顶端算起，也就是包括了通知栏的高度。
        int[] location = new int[2];
        errImage.getLocationOnScreen(location);

        //加载PopupWindow的布局
        //测量布局的大小
        contentView.measure(0, 0);
        //将布局大小设置为PopupWindow的宽高
        PopupWindow popWindow = new PopupWindow(contentView, contentView.getMeasuredWidth(), contentView.getMeasuredHeight(), true);
        // 设置好参数之后再show
        popupWindow.showAtLocation(errImage, Gravity.NO_GRAVITY, location[0] - (popWindow.getWidth()
                - errImage.getWidth()) - dp2Px(getContext(), 5) - errImage.getWidth() / 2, location[1] - popWindow.getHeight() / 2 - dp2Px(getContext(), 5));
//        popupWindow.showAsDropDown(errImage,0,0-(getHeight()/2+popupWindow.getHeight()));
//        popupWindow.showAsDropDown(errImage,-(popWindow.getWidth()-errImage.getWidth()),);
//        popupWindow.showAtLocation(errImage, Gravity.NO_GRAVITY,location[0],location[1]);
        //获取需要在其上方显示的控件的位置信息


        //在控件上方显示
//        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.RIGHT, (location[0] + view.getWidth() / 2) - (view.getWidth()) / 2, location[1] - (view.getHeight()));
//        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.RIGHT, location[0] , location[1] );

//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) errImage.getLayoutParams();
//        int topMargin = params.topMargin;


//        popupWindow.showAtLocation(view, Gravity.RIGHT, location[0]+Utils.Dp2Px(getContext(),3), topMargin/2+view.getHeight()/4);
//        popupWindow.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }


    public void setPadding(int left, int top, int right, int bottom) {

        editText.setPadding(left, top, right, bottom);
    }

}

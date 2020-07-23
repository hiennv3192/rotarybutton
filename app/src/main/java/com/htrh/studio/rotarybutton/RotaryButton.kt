package com.htrh.studio.rotarybutton

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import kotlin.math.atan2
import kotlin.math.floor

class RotaryButton : View {

    companion object {
        private const val DEFAULT_MAX_VALUE = 100
    }

    private var mRotateDegrees: Float = 0f
    private var mSweepAngle: Float = 0f

    //    private int mProgress;
    private var mMax = DEFAULT_MAX_VALUE.toFloat()
    private var mCenterCanvasX = 0f
    private var mCenterCanvasY = 0f
    private var mDownDegrees = 0f
    private var mCurrDegrees = 0f
    private var mDegrees = 0f
    private val mIsEnable = true

    private lateinit var mRectF: RectF
    private lateinit var mPaint: Paint
    private lateinit var mPaintFlags: PaintFlagsDrawFilter
    private lateinit var mProgressBgBm: SoftReference<Bitmap>
    private lateinit var mProgressFgBm: SoftReference<Bitmap>
    private lateinit var mButtonFgBm: SoftReference<Bitmap>
    private lateinit var mButtonBgBm: SoftReference<Bitmap>

    private var mListener: OnCircleSeekBarChangeListener? = null

    private var mClickListener: OnClickListener? = null

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     *
     *
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply `R.attr.buttonStyle` for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     * the view. Can be 0 to not look for defaults.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWidthHeight = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(viewWidthHeight, viewWidthHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterCanvasX = width / 2f
        mCenterCanvasY = height / 2f
        setupPaintShaderProgress(w, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mListener != null) {
            mListener!!.onProgressChange(mDegrees.toInt())
        }
        //        mProgress = (int) degrees;
        if (mDegrees > mMax) {
            mDegrees = mMax
        }
        if (mDegrees < 0) {
            mDegrees = 0f
        }
        mSweepAngle = 270 * (mDegrees / mMax)
        canvas.save()
        drawProgress(canvas, mSweepAngle)
        drawButton(canvas, mSweepAngle)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (mClickListener != null) {
            mClickListener!!.onClick(this)
        }
        if (!mIsEnable) {
            return super.onTouchEvent(event)
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (mListener != null) {
                mListener!!.onStartTrackingTouch(this)
            }
            val dx = event.x - mCenterCanvasX
            val dy = event.y - mCenterCanvasY
            val radians = atan2(dy.toDouble(), dx.toDouble())
            mDownDegrees = (radians * 180 / Math.PI).toFloat()
            mDownDegrees -= 90f
            if (mDownDegrees < 0) {
                mDownDegrees += 360f
            }
            mDownDegrees = floor(mDownDegrees / 360 * (mMax + 5).toDouble()).toFloat()
            return true
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            val dx = event.x - mCenterCanvasX
            val dy = event.y - mCenterCanvasY
            val radians = Math.atan2(dy.toDouble(), dx.toDouble())
            mCurrDegrees = (radians * 180 / Math.PI).toFloat()
            mCurrDegrees -= 90f
            if (mCurrDegrees < 0) {
                mCurrDegrees += 360f
            }
            mCurrDegrees = Math.floor(mCurrDegrees / 360 * (mMax + 5).toDouble()).toFloat()
            if (mCurrDegrees / (mMax + 4) > 0.75f && (mDownDegrees - 0) / (mMax + 4) < 0.25f) {
                mDegrees--
                if (mDegrees < 0) {
                    mDegrees = 0f
                }
                mDownDegrees = mCurrDegrees
            } else if (mDownDegrees / (mMax + 4) > 0.75f
                && (mCurrDegrees - 0) / (mMax + 4) < 0.25f
            ) {
                mDegrees++
                if (mDegrees > mMax) {
                    mDegrees = mMax
                }
                mDownDegrees = mCurrDegrees
            } else {
                mDegrees += mCurrDegrees - mDownDegrees
                if (mDegrees > mMax) {
                    mDegrees = mMax
                }
                if (mDegrees < 0) {
                    mDegrees = 0f
                }
                mDownDegrees = mCurrDegrees
            }
            invalidate()
            return true
        }
        if (event.action == MotionEvent.ACTION_UP) {
            if (mListener != null) {
                mListener!!.onStopTrackingTouch(this)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun setOnClickListener(mListener: OnClickListener?) {
        mClickListener = mListener
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

//        isEnable = enabled;
//        if (enabled) {
//            mArcPaintPrimary.setColor(ContextCompat.getColor(getContext(), R.color.color_seekbar_progress));
//        } else {
//            mArcPaintPrimary.setColor(ContextCompat.getColor(getContext(), R.color.color_disable));
//        }
    }

    fun setProgressBgImg(id: Int) {
        mProgressBgBm = SoftReference(BitmapFactory.decodeResource(resources, id))
    }

    fun setProgressFgImg(id: Int) {
        mProgressFgBm = SoftReference(BitmapFactory.decodeResource(resources, id))
    }

    fun setButtonBgImg(id: Int) {
        mButtonBgBm = SoftReference(BitmapFactory.decodeResource(resources, id))
    }

    fun setButtonFgImg(id: Int) {
        mButtonFgBm = SoftReference(BitmapFactory.decodeResource(resources, id))
    }

    fun setMax(max: Int) {
        this.mMax = max.toFloat()
    }

    var progress: Int
        get() = mDegrees.toInt()
        set(progress) {
            mDegrees = progress.toFloat()
            invalidate()
        }

    fun setOnSeekBarChangeListener(mListener: OnCircleSeekBarChangeListener?) {
        this.mListener = mListener
    }

    private fun init(context: Context) {
        mRectF = RectF()
        mPaint = Paint()
        mPaintFlags = PaintFlagsDrawFilter(0, 3)
        mProgressBgBm =
            SoftReference(BitmapFactory.decodeResource(resources, R.drawable.progress_bg))
        mProgressFgBm =
            SoftReference(BitmapFactory.decodeResource(resources, R.drawable.progress_foreground))
        mButtonBgBm =
            SoftReference(BitmapFactory.decodeResource(resources, R.drawable.btn_bg))
        mButtonFgBm =
            SoftReference(
                BitmapFactory.decodeResource(resources, R.drawable.btn_foreground)
            )
    }

    private fun setupPaintShaderProgress(viewWidth: Int, viewHeight: Int) {
        val matrix = Matrix()
        val src = RectF(
            0f,
            0f,
            mProgressFgBm.get()?.width?.toFloat()!!,
            mProgressFgBm.get()?.width?.toFloat()!!
        )
        val dst = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val shader =
            BitmapShader(mProgressFgBm.get()!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER)
        shader.setLocalMatrix(matrix)
        mPaint.shader = shader
        matrix.mapRect(mRectF, src)
    }

    private fun drawProgress(canvas: Canvas, sweepAngle: Float) {
        // create a rectangle frame to contain a progress line
        mRectF.set(
            0 + 0.toFloat(),
            0 + 0.toFloat(),
            width - 0.toFloat(),
            height - 0.toFloat()
        )
        drawProgressBg(canvas)
        drawProgressFg(canvas, sweepAngle)
    }

    private fun drawProgressBg(canvas: Canvas) {
        canvas.drawBitmap(mProgressBgBm.get()!!, null, mRectF, null)
    }

    private fun drawProgressFg(canvas: Canvas, sweepAngle: Float) {
        // set la true thì sẽ vẽ từ tâm ra, false thì chỉ vẽ viền ngoài
        // tham khảo https://thoughtbot.com/blog/android-canvas-drawarc-method-a-visual-guide
        canvas.drawArc(mRectF, 135f, sweepAngle, true, mPaint)
    }

    private fun drawButton(canvas: Canvas, sweepAngle: Float) {
        drawButtonBg(canvas)

        // rotate image, object be draw after call this method will take effect
        mRotateDegrees = 225 + sweepAngle //rotation degree

        canvas.rotate(mRotateDegrees, mCenterCanvasX, mCenterCanvasY)
        drawButtonFg(canvas)
    }

    private fun drawButtonBg(canvas: Canvas) {
        // create a rectangle frame to contain a button background
        mRectF.set(
            0 + 100.toFloat(),
            0 + 100.toFloat(),
            width - 100.toFloat(),
            height - 100.toFloat()
        )
        canvas.drawBitmap(mButtonBgBm.get()!!, null, mRectF, null)
    }

    private fun drawButtonFg(canvas: Canvas) {
        // create a rectangle frame to contain a button
        mRectF.set(
            0 + 180.toFloat(),
            0 + 180.toFloat(),
            width - 180.toFloat(),
            height - 180.toFloat()
        )
        canvas.drawBitmap(mButtonFgBm.get()!!, null, mRectF, null)
    }

    interface OnCircleSeekBarChangeListener {
        fun onProgressChange(progress: Int)
        fun onStartTrackingTouch(rotaryButton: RotaryButton?)
        fun onStopTrackingTouch(rotaryButton: RotaryButton?)
    }
}
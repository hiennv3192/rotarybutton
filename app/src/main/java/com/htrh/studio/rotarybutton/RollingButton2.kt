package com.htrh.studio.rotarybutton

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class RollingButton2 : View {
    private val TAG = "RotaryButton"
    private var progressBgBm: Bitmap? = null
    private var progressFgBm: Bitmap? = null
    private var buttonBm: Bitmap? = null
    private var buttonBgBm2: Bitmap? = null
    private var buttonBgBm: Bitmap? = null
    private var mRectF: RectF? = null
    private var mPaint: Paint? = null
    private lateinit var mPaintFlags: PaintFlagsDrawFilter

    //    private int mProgress;
    private var max = DEFAULT_MAX_VALUE.toFloat()
    private var centerCanvasX = 0f
    private var centerCanvasY = 0f
    private var downDegrees = 0f
    private var currDegrees = 0f
    private var degrees = 0f
    private val isEnable = true
    private var mListener: OnCircleSeekBarChangeListener? = null

    private var sweepAngle = 0f

    private var mHeight = 0
    private var mScale = 0

    interface OnCircleSeekBarChangeListener {
        fun onProgressChange(progress: Int)
        fun onStartTrackingTouch(rollingButton: RollingButton2?)
        fun onStopTrackingTouch(rollingButton: RollingButton2?)
    }

    fun setOnSeekBarChangeListener(mListener: OnCircleSeekBarChangeListener?) {
        this.mListener = mListener
    }

    private var mClickListener: OnClickListener? = null
    override fun setOnClickListener(mListener: OnClickListener?) {
        mClickListener = mListener
    }

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

    private fun init(context: Context) {
        mRectF = RectF()
        mPaint = Paint()
        mPaintFlags = PaintFlagsDrawFilter(0, 3)
        progressBgBm = BitmapFactory.decodeResource(resources, R.drawable.progress_bg)
        progressFgBm = BitmapFactory.decodeResource(resources, R.drawable.progress_foreground)
        buttonBm = BitmapFactory.decodeResource(resources, R.drawable.btn_foreground)
        buttonBgBm = BitmapFactory.decodeResource(resources, R.drawable.btn_bg)
        buttonBgBm2 = BitmapFactory.decodeResource(resources, R.drawable.rolling_btn_bg2)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWidthHeight = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(viewWidthHeight, viewWidthHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mScale = Math.min(width, height) - paddingLeft - paddingRight
        mHeight = (h - paddingTop) - paddingBottom
//        mRectF.set(10f,10f,width-10f,height-10f)
        centerCanvasX = width / 2f
        centerCanvasY = height / 2f
        setupPaintShaderProgress(w, height)
    }

    private fun setupPaintShaderProgress(viewWidth: Int, viewHeight: Int) {
        val matrix = Matrix()
        val src = RectF(0f, 0f, progressFgBm!!.width.toFloat(), progressFgBm!!.width.toFloat())
        val dst = RectF(10f, 10f, viewWidth.toFloat()-10, viewHeight.toFloat()-10)
        val shader = BitmapShader(progressFgBm!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER)
        shader.setLocalMatrix(matrix)
        mPaint!!.shader = shader
        matrix.mapRect(mRectF, src)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mListener != null) {
            mListener!!.onProgressChange(degrees.toInt())
        }
        //        mProgress = (int) degrees;
        if (degrees > max) {
            degrees = max
        }
        if (degrees < 0) {
            degrees = 0f
        }
        sweepAngle = 282f * (degrees / max)
        canvas!!.drawFilter=mPaintFlags
        canvas.save()
        drawProgressCircle(canvas)
        drawRollableButton(canvas)
        canvas.restore()
    }

    private fun drawProgressCircle(canvas: Canvas) {
        //ở đây mRectF set như nào thì dst phần matrix.setRectToRect cũng phải set như thế k thì view progress khi quay sẽ bị lệch
        mRectF?.set(10f, 10f, width - 10f, height - 10f)
        drawProgressBg(canvas)
        drawProgressFg(canvas)
    }

    private fun drawProgressBg(canvas: Canvas) {
        canvas.drawBitmap(progressBgBm!!, null, mRectF!!, null)
    }

    private fun drawProgressFg(canvas: Canvas) {
        canvas.drawArc(mRectF!!, 129f, sweepAngle, true, mPaint!!)
    }

    private fun drawRollableButton(canvas: Canvas) {
        //ở đây +219 hay -141 hay bất kì 1 số khác là do chúng ta cần đưa nấc trên button về đúng vị trí ban đầu vì mỗi design vẽ nấc button ở vị trí khác nhau
//        val degrees = 219 + sweepAngle
        val degrees = sweepAngle - 141
        //vì cái button bên trong bóng đè lên progress nên set lại khoảng cách vẽ
        mRectF?.set(20f, 20f, width - 20f, height - 20f)
        drawBgButton(canvas)
        canvas.rotate(degrees, centerCanvasX, centerCanvasY)
        drawButton(canvas)
    }

    private fun drawBgButton(canvas: Canvas) {
//        canvas.drawBitmap(mBitmapBgButton,mLeft,mTop,null)
//        mRectF.set(20f,20f,width-20f,height-20f)
        mRectF!![0 + 100.toFloat(), 0 + 100.toFloat(), width - 100.toFloat()] =
            height - 100.toFloat()
        canvas.drawBitmap(buttonBgBm!!, null, mRectF!!, null)
    }

    private fun drawButton(canvas: Canvas) {
//        mRectF.set(20f,20f,width-20f,height-20f)
        mRectF!![0 + 100.toFloat(), 0 + 100.toFloat(), width - 100.toFloat()] =
            height - 100.toFloat()
        canvas.drawBitmap(buttonBm!!, null, mRectF!!, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_UP) {
            if (mListener != null) {
                mListener!!.onStopTrackingTouch(this)
            }
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (mListener != null) {
                mListener!!.onStartTrackingTouch(this)
            }
        }

        if (event!!.action == MotionEvent.ACTION_DOWN) {
            val dx: Float = event.x - centerCanvasX
            val dy: Float = event.y - centerCanvasY
            val radians = Math.atan2(dy.toDouble(), dx.toDouble())
            downDegrees = (radians * 180 / Math.PI).toFloat()
            downDegrees -= 90f
            if (downDegrees < 0) {
                downDegrees += 360f
            }
            downDegrees =
                Math.floor(downDegrees / 360 * (max + 5).toDouble()).toFloat()
            return true
            Log.e(TAG, "down")
        }

        if (event!!.action == MotionEvent.ACTION_MOVE) {
            onMoveView(event)
        }
        invalidate()
        return true
    }

    private fun onMoveView(event: MotionEvent) {
        val dx: Float = event.x - centerCanvasX
        val dy: Float = event.y - centerCanvasY
        Log.e(TAG, "x " + event.x + " y " + event.y)
        val radians = Math.atan2(dy.toDouble(), dx.toDouble())
        currDegrees = (radians * 180 / Math.PI).toFloat()
        currDegrees -= 90f
        if (currDegrees < 0) {
            currDegrees += 360f
        }
        currDegrees =
            Math.floor(currDegrees / 360 * (max + 5).toDouble()).toFloat()

        if (currDegrees / (max + 4) > 0.75f && (downDegrees - 0) / (max + 4) < 0.25f) {
            degrees--
            if (degrees < 0) {
                degrees = 0f
            }
            downDegrees = currDegrees
        } else if (downDegrees / (max + 4) > 0.75f && (currDegrees - 0) / (max + 4) < 0.25f) {
            degrees++
            if (degrees > max) {
                degrees = max.toFloat()
            }
            downDegrees = currDegrees
        } else {
            degrees += currDegrees - downDegrees
            if (degrees > max) {
                degrees = max.toFloat()

            }
            if (degrees < 0) {
                degrees = 0f
            }
            downDegrees = currDegrees
        }
    }

    fun setMax(max: Int) {
        this.max = max.toFloat()
    }

    var progress: Int
        get() = degrees.toInt()
        set(progress) {
            degrees = progress.toFloat()
            invalidate()
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

    companion object {
        private const val DEFAULT_MAX_VALUE = 100
    }
}
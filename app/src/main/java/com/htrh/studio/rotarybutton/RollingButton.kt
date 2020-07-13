package com.htrh.studio.rotarybutton

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class RollingButton : View {
    private var progressBgBm: Bitmap? = null
    private var progressFgBm: Bitmap? = null
    private var buttonBm: Bitmap? = null
    private var buttonBgBm2: Bitmap? = null
    private var buttonBgBm: Bitmap? = null
    private var oval: RectF? = null
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

    interface OnCircleSeekBarChangeListener {
        fun onProgressChange(progress: Int)
        fun onStartTrackingTouch(rollingButton: RollingButton?)
        fun onStopTrackingTouch(rollingButton: RollingButton?)
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
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        oval = RectF()
        mPaint = Paint()
        mPaintFlags = PaintFlagsDrawFilter(0, 3)
        progressBgBm = BitmapFactory.decodeResource(resources, R.drawable.progress_bg)
        progressFgBm = BitmapFactory.decodeResource(resources, R.drawable.progress_foreground)
        buttonBm = BitmapFactory.decodeResource(resources, R.drawable.rolling_btn_foreground)
        buttonBgBm = BitmapFactory.decodeResource(resources, R.drawable.rolling_btn_bg)
        buttonBgBm2 = BitmapFactory.decodeResource(resources, R.drawable.rolling_btn_bg2)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWidthHeight = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(viewWidthHeight, viewWidthHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerCanvasX = width / 2f
        centerCanvasY = height / 2f
        setupPaintShaderProgress(w, height)
    }

    private fun setupPaintShaderProgress(viewWidth: Int, viewHeight: Int) {
        val matrix = Matrix()
        val src = RectF(0f, 0f, progressFgBm!!.width.toFloat(), progressFgBm!!.width.toFloat())
        val dst = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val shader = BitmapShader(progressFgBm!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER)
        shader.setLocalMatrix(matrix)
        mPaint!!.shader = shader
        matrix.mapRect(oval, src)
    }

    override fun onDraw(canvas: Canvas) {
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
        val sweepAngle = 270 * (degrees / max)
        canvas.save()
        drawProgressCircle(canvas, sweepAngle)
        drawRollableButton(canvas, sweepAngle)
        canvas.restore()
    }

    private fun drawProgressCircle(canvas: Canvas, sweepAngle: Float) {
        // create a rectangle frame to contain a progress line
        oval!![0 + 0.toFloat(), 0 + 0.toFloat(), width - 0.toFloat()] = height - 0.toFloat()
        drawProgressBg(canvas)
        drawProgressFg(canvas, sweepAngle)
    }

    private fun drawProgressBg(canvas: Canvas) {
        canvas.drawBitmap(progressBgBm!!, null, oval!!, null)
    }

    private fun drawProgressFg(canvas: Canvas, sweepAngle: Float) {
        // set la true thì sẽ vẽ từ tâm ra, false thì chỉ vẽ viền ngoài
        // tham khảo https://thoughtbot.com/blog/android-canvas-drawarc-method-a-visual-guide
        canvas.drawArc(oval!!, 135f, sweepAngle, true, mPaint!!)
    }

    private fun drawRollableButton(canvas: Canvas, sweepAngle: Float) {
        val degrees = 225 + sweepAngle //rotation degree
        drawBackgroundButton(canvas)
        drawBackgroundButton2(canvas)

        // rotate image, object be draw after call this method will take effect
        canvas.rotate(degrees, centerCanvasX, centerCanvasY)
        drawButton(canvas)
    }

    private fun drawButton(canvas: Canvas) {
        // create a rectangle frame to contain a button
        oval!![0 + 180.toFloat(), 0 + 180.toFloat(), width - 180.toFloat()] = height - 180.toFloat()
        canvas.drawBitmap(buttonBm!!, null, oval!!, null)
    }

    private fun drawBackgroundButton2(canvas: Canvas) {
        // create a rectangle frame to contain a button background
        oval!![0 + 120.toFloat(), 0 + 120.toFloat(), width - 120.toFloat()] = height - 120.toFloat()
        canvas.drawBitmap(buttonBgBm2!!, null, oval!!, null)
    }

    private fun drawBackgroundButton(canvas: Canvas) {
        // create a rectangle frame to contain a button background
        oval!![0 + 100.toFloat(), 0 + 100.toFloat(), width - 100.toFloat()] = height - 100.toFloat()
        canvas.drawBitmap(buttonBgBm!!, null, oval!!, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

//        if (Utils.getDistance(event.getX(), event.getY(), midx, midy) > Math.max(mainCircleRadius, Math.max(backCircleRadius, progressRadius))) {
//            return super.onTouchEvent(event);
//        }
        if (mClickListener != null) {
            mClickListener!!.onClick(this)
        }
        if (!isEnable) {
            return super.onTouchEvent(event)
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (mListener != null) {
                mListener!!.onStartTrackingTouch(this)
            }
            val dx = event.x - centerCanvasX
            val dy = event.y - centerCanvasY
            val radians = Math.atan2(dy.toDouble(), dx.toDouble())
            downDegrees = (radians * 180 / Math.PI).toFloat()
            downDegrees -= 90f
            if (downDegrees < 0) {
                downDegrees += 360f
            }
            downDegrees = Math.floor(downDegrees / 360 * (max + 5).toDouble()).toFloat()
            return true
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            val dx = event.x - centerCanvasX
            val dy = event.y - centerCanvasY
            val radians = Math.atan2(dy.toDouble(), dx.toDouble())
            currDegrees = (radians * 180 / Math.PI).toFloat()
            currDegrees -= 90f
            if (currDegrees < 0) {
                currDegrees += 360f
            }
            currDegrees = Math.floor(currDegrees / 360 * (max + 5).toDouble()).toFloat()
            if (currDegrees / (max + 4) > 0.75f && (downDegrees - 0) / (max + 4) < 0.25f) {
                degrees--
                if (degrees < 0) {
                    degrees = 0f
                }
                downDegrees = currDegrees
            } else if (downDegrees / (max + 4) > 0.75f && (currDegrees - 0) / (max + 4) < 0.25f) {
                degrees++
                if (degrees > max) {
                    degrees = max
                }
                downDegrees = currDegrees
            } else {
                degrees += currDegrees - downDegrees
                if (degrees > max) {
                    degrees = max
                }
                if (degrees < 0) {
                    degrees = 0f
                }
                downDegrees = currDegrees
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
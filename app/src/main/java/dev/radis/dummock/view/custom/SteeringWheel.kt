package dev.radis.dummock.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.radis.dummock.R
import kotlin.math.cos
import kotlin.math.sin


class SteeringWheel(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var paint: Paint = Paint()

    private var wheelViewCenter = false
    private var wheelViewSize = 320
    private var wheelColor = Color.BLACK

    private var speed = 50
    private var textColor = Color.BLACK
    private var textSize = 72
    private var textPadding = 64
    private val text: String
        get() = "$speed Km"

    private var wheelOutRadius = 120F
    private var wheelOutWidth = 10f

    private var wheelLineWidth = 8f
    private var wheelLineCount = 3

    init {
        setAttributes(attrs)
    }

    private fun setAttributes(attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SteeringWheel, 0, 0
        ).apply {
            try {
                wheelViewCenter =
                    getBoolean(R.styleable.SteeringWheel_wheel_view_center, false)
                wheelColor = getColor(
                    R.styleable.SteeringWheel_wheel_color,
                    ContextCompat.getColor(context, android.R.color.black)
                )
                speed = getInt(R.styleable.SteeringWheel_default_speed, speed)
                textSize = getInt(R.styleable.SteeringWheel_text_size, textSize)
                textColor = getColor(
                    R.styleable.SteeringWheel_text_color,
                    ContextCompat.getColor(context, android.R.color.black)
                )
                wheelOutRadius =
                    getDimension(R.styleable.SteeringWheel_wheel_out_radius, wheelOutRadius)
                wheelOutWidth =
                    getDimension(R.styleable.SteeringWheel_wheel_out_width, wheelOutWidth)
                wheelLineWidth =
                    getDimension(R.styleable.SteeringWheel_wheel_line_width, wheelLineWidth)
                wheelLineCount =
                    getInt(R.styleable.SteeringWheel_wheel_line_count, wheelLineCount)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = wheelColor
        canvas?.let {
            drawWheelOut(it)
            drawWheelIn(it)
            drawWheelLines(it)
            drawText(it)
        }
    }

    private fun drawWheelOut(canvas: Canvas) {
        paint.strokeWidth = wheelOutWidth
        paint.style = Paint.Style.STROKE

        canvas.drawCircle(wheelViewSize / 2f, wheelViewSize / 2f, wheelOutRadius, paint)
    }

    private fun drawWheelIn(canvas: Canvas) {
        paint.style = Paint.Style.FILL

        canvas.drawCircle(
            wheelViewSize / 2f,
            wheelViewSize / 2f,
            wheelOutRadius / 4f,
            paint
        )
    }

    private fun drawWheelLines(canvas: Canvas) {
        paint.strokeWidth = wheelLineWidth

        var angle = 0f
        val pointAngle: Float = (360 / wheelLineCount).toFloat()
        while (angle < 361) {
            val displacedAngle = angle - 90
            val x: Float =
                wheelViewSize / 2f + ((wheelOutRadius) * cos(Math.toRadians(displacedAngle.toDouble())).toFloat())
            val y: Float =
                wheelViewSize / 2f + ((wheelOutRadius) * sin(Math.toRadians(displacedAngle.toDouble())).toFloat())
            canvas.drawLine(
                wheelViewSize / 2f,
                wheelViewSize / 2f,
                x,
                y,
                paint
            )
            angle += pointAngle
        }
    }

    private fun drawText(canvas: Canvas) {
        paint.color = textColor
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = textSize.toFloat() * resources.displayMetrics.density

        canvas.drawText(
            text,
            (wheelViewSize / 2f) - ((text.length.toFloat() * paint.textSize) / 4),
            (wheelViewSize / 2f) + wheelOutRadius + paint.textSize + textPadding,
            paint
        )

    }

    fun setSpeed(speed: Int) {
        this.speed = speed
        // to update the view appearance
        invalidate()
        requestLayout()
    }

    fun getSpeed(): Int {
        return speed
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        wheelViewSize = measuredWidth.coerceAtMost(measuredHeight)
        setMeasuredDimension(wheelViewSize, wheelViewSize)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putInt("speed", speed)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if (viewState is Bundle) {
            val bundle = viewState
            speed = bundle.getInt("speed")
            viewState = bundle.getParcelable("instanceState")
        }
        super.onRestoreInstanceState(viewState)
    }

}
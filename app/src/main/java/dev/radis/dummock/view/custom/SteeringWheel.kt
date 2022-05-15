package dev.radis.dummock.view.custom

import android.R.attr.radius
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

    private var wheelOutRadius = 120F
    private var wheelOutWidth = 10f

    private var wheelLineWidth = 8f
    private var wheelLineCount = 3

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SteeringWheel, 0, 0
        ).apply {
            try {
                wheelViewCenter =
                    getBoolean(R.styleable.SteeringWheel_wheel_view_center, false)
                wheelColor = getColor(
                    R.styleable.SteeringWheel_wheel_color,
                    ContextCompat.getColor(context, android.R.color.background_dark)
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
        }
    }

    private fun drawWheelOut(canvas: Canvas) {
        //canvas.drawCircle(centerOfX, centerOfY, wheelOutRadius, paint)

        paint.strokeWidth = wheelOutWidth
        paint.style = Paint.Style.STROKE

        canvas.drawCircle(wheelViewSize / 2f, wheelViewSize / 2f, wheelOutRadius / 2f, paint)
    }

    private fun drawWheelIn(canvas: Canvas) {
        paint.style = Paint.Style.FILL

        canvas.drawCircle(
            wheelViewSize / 2f,
            wheelViewSize / 2f,
            wheelOutRadius / 8f,
            paint
        )
    }

    private fun drawWheelLines(canvas: Canvas) {
        paint.strokeWidth = wheelLineWidth

        var angle = 0f
        val pointAngle: Float = (360 / wheelLineCount).toFloat()
        while (angle < 361) {
            //move round the circle to each point
            val displacedAngle = angle - 90
            val x: Float =
                wheelViewSize / 2f + (cos(Math.toRadians(displacedAngle.toDouble())).toFloat() * radius) //convert angle to radians for x and y coordinates
            val y: Float =
                wheelViewSize / 2f + (sin(Math.toRadians(displacedAngle.toDouble())).toFloat() * radius)
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        wheelViewSize = measuredWidth.coerceAtMost(measuredHeight)
        setMeasuredDimension(wheelViewSize, wheelViewSize)
    }

}
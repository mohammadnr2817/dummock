package dev.radis.dummock.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.radis.dummock.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class SteeringWheel(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        private const val TRIANGLE_TOP_POINT_FACTOR = 0.9F
        private const val TRIANGLE_SIDE_POINT_FACTOR = 5
    }

    private var paint: Paint = Paint()

    private var wheelColor = Color.BLACK

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
                wheelColor = getColor(
                    R.styleable.SteeringWheel_wheel_color,
                    ContextCompat.getColor(context, android.R.color.black)
                )
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
            drawWheelTriangles(it)
        }
    }

    private fun drawWheelOut(canvas: Canvas) {
        paint.strokeWidth = wheelOutWidth
        paint.style = Paint.Style.STROKE

        canvas.drawCircle(getMeasuredSize() / 2f, getMeasuredSize() / 2f, getRadius(), paint)
    }

    private fun drawWheelIn(canvas: Canvas) {
        paint.style = Paint.Style.FILL

        canvas.drawCircle(
            getMeasuredSize() / 2f,
            getMeasuredSize() / 2f,
            getRadius() / 4f,
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
                getMeasuredSize() / 2f + ((getRadius()) * cos(Math.toRadians(displacedAngle.toDouble())).toFloat())
            val y: Float =
                getMeasuredSize() / 2f + ((getRadius()) * sin(Math.toRadians(displacedAngle.toDouble())).toFloat())
            canvas.drawLine(
                getMeasuredSize() / 2f,
                getMeasuredSize() / 2f,
                x,
                y,
                paint
            )
            angle += pointAngle
        }
    }

    private fun drawWheelTriangles(canvas: Canvas) {
        paint.strokeWidth = wheelLineWidth
        paint.style = Paint.Style.FILL

        var angle = 0f
        val pointAngle: Float = (360 / wheelLineCount).toFloat()
        while (angle < 361) {
            val displacedAngle = angle - 90
            val xTopPoint: Float =
                getMeasuredSize() / 2f + ((getRadius() * TRIANGLE_TOP_POINT_FACTOR) * cos(
                    Math.toRadians(
                        displacedAngle.toDouble()
                    )
                ).toFloat())
            val yTopPoint: Float =
                getMeasuredSize() / 2f + ((getRadius() * TRIANGLE_TOP_POINT_FACTOR) * sin(
                    Math.toRadians(
                        displacedAngle.toDouble()
                    )
                ).toFloat())

            val xRightPoint: Float =
                getMeasuredSize() / 2f + ((getRadius()) * cos(Math.toRadians(displacedAngle.toDouble() + TRIANGLE_SIDE_POINT_FACTOR)).toFloat())
            val yRightPoint: Float =
                getMeasuredSize() / 2f + ((getRadius()) * sin(Math.toRadians(displacedAngle.toDouble() + TRIANGLE_SIDE_POINT_FACTOR)).toFloat())

            val xLeftPoint: Float =
                getMeasuredSize() / 2f + ((getRadius()) * cos(Math.toRadians(displacedAngle.toDouble() - TRIANGLE_SIDE_POINT_FACTOR)).toFloat())
            val yLeftPoint: Float =
                getMeasuredSize() / 2f + ((getRadius()) * sin(Math.toRadians(displacedAngle.toDouble() - TRIANGLE_SIDE_POINT_FACTOR)).toFloat())

            val path = Path()
            path.moveTo(xTopPoint, yTopPoint)
            path.lineTo(xRightPoint, yRightPoint)
            path.lineTo(xLeftPoint, yLeftPoint)
            path.lineTo(xTopPoint, yTopPoint)

            canvas.drawPath(path, paint)

            angle += pointAngle
        }
    }

    private fun getRadius(): Float {
        return getMeasuredSize() / 2 - (wheelOutWidth / 2)
    }

    private fun getMeasuredSize(): Float {
        return min(measuredWidth, measuredHeight).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            min(measuredWidth, measuredHeight),
            min(measuredWidth, measuredHeight)
        )
    }

}
package dev.radis.dummock.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import dev.radis.dummock.R
import dev.radis.dummock.utils.Logit
import kotlin.math.atan2
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

    private var touchX1: Float = 0F
    private var touchY1: Float = 0F

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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX1 = event.x - getMeasuredSize() / 2
                touchY1 = event.y - getMeasuredSize() / 2
            }
            MotionEvent.ACTION_MOVE -> {
                val x2 = event.x - getMeasuredSize() / 2
                val y2 = event.y - getMeasuredSize() / 2

//                val anglePoint1 = getAngleFromArcTan(atan(touchY1 / touchX1), touchX1, touchY1)
//                val anglePoint2 = getAngleFromArcTan(atan(y2 / x2), x2, y2)

                // --- val time1 = SystemClock.elapsedRealtime()

                val anglePoint1 = getAngleFromArcTan2(atan2(touchY1, touchX1))
                val anglePoint2 = getAngleFromArcTan2(atan2(y2, x2))

                Logit.d(
                    "1: ${Math.toDegrees(anglePoint1.toDouble())}, 2: ${
                        Math.toDegrees(
                            anglePoint2.toDouble()
                        )
                    } -> res:${Math.toDegrees((anglePoint2 - anglePoint1).toDouble())}"
                )
                rotation =
                    (rotation + Math.toDegrees((anglePoint2 - anglePoint1).toDouble())).toFloat()

                touchX1 = x2
                touchY1 = y2
            }
        }
        return true
    }

    private fun getAngleFromArcTan(aTan: Float, x: Float, y: Float): Float {
        return if (x >= 0 && y >= 0) aTan
        else if (x >= 0 && y < 0) {
            (aTan + Math.PI).toFloat()
        } else if (x < 0 && y >= 0) {
            (aTan + Math.PI / 2).toFloat()
        } else {
            (aTan + (3 / 2 * Math.PI)).toFloat()
        }
    }

    private fun getAngleFromArcTan2(aTan: Float): Float {
        return (aTan + Math.PI).toFloat()
    }

}
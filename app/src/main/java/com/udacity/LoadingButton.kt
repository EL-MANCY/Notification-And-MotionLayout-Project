package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var font = 50f
    private var widthSize = 0
    private var heightSize = 0
    private var Bar = 0f
    private var valueAnimator = ValueAnimator()

    private var buttonRect = Rect(0, 0, 0, 0)
    private var buttonAnimateRect = Rect(0, 0, 0, 0)

    private var loadText = resources.getString(R.string.button_loading)
    private var text = resources.getString(R.string.download)
    private var buttonColor = resources.getColor(R.color.colorPrimary, null)
    private var animationColor = resources.getColor(R.color.colorPrimaryDark, null)
    private var textColor = resources.getColor(R.color.white, null)
    private var iconColor = resources.getColor(R.color.colorAccent, null)


    private val textRect = Rect()
    private val circleRect = RectF(0f, 0f, 0f, 0f)
    private var circleRadius = 0f


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
        textSize = font
    }
    //flag
    var isAnimated = false

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }



    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {

                getString(R.styleable.LoadingButton_text)?.let { text = it }
                getString(R.styleable.LoadingButton_loadingText)?.let { loadText = it }
                getColor(R.styleable.LoadingButton_loadingColor,animationColor).let { animationColor=it }
                getColor(R.styleable.LoadingButton_loadingIconColor, iconColor).let { iconColor=it }
                getColor(R.styleable.LoadingButton_textColor, textColor).let { textColor=it }
                getColor(R.styleable.LoadingButton_buttonColor, buttonColor).let { buttonColor=it }
                getFloat(R.styleable.LoadingButton_fontSize, font).let { font=it }
                getFloat(R.styleable.LoadingButton_progress, Bar).let { Bar=it }
            } catch (e:Exception){
                Toast.makeText(context,"LOST CONNECTION",Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        const val standardAnimTime = 3000L
    }
    fun start() {
        //to start the animation each click
        valueAnimator.cancel()
        //flag
        isAnimated = true
        valueAnimator = ValueAnimator.ofFloat(Bar, 1f).apply {
            duration = standardAnimTime
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                Bar = it.animatedValue as Float
                invalidate()
            }
            doOnEnd {
                Bar = 0f
                isAnimated = false
            }
            start()
        }
    }
    fun stop(){
        isAnimated = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = width
        heightSize = height
        buttonRect = Rect(0, 0, widthSize, heightSize)
        buttonAnimateRect = Rect(0, 0, widthSize, heightSize)
        circleRadius = height / 4f
        circleRect.top = heightSize / 4f - circleRadius
        circleRect.bottom = heightSize / 4f + circleRadius
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            if (isAnimated) {
                paint.color = buttonColor
                drawRect(buttonRect, paint)
                paint.color = animationColor
                buttonAnimateRect.right = (widthSize * Bar).toInt()
                drawRect(buttonAnimateRect, paint)

                paint.color = textColor
                paint.getTextBounds(loadText, 0, loadText.length, textRect)
                drawText(
                    loadText,
                    widthSize / 2f,
                    heightSize / 2 - textRect.exactCenterY(),
                    paint
                )

                val Angle = 360 * Bar
                paint.color = iconColor
                circleRect.left = widthSize / 2f + textRect.right / 2f + 4
                circleRect.right = circleRect.left + (2 * circleRadius)
                drawArc(circleRect, 0f, Angle, true, paint)
            } else {
                paint.color = buttonColor
                drawRect(buttonRect, paint)

                paint.color = textColor
                paint.getTextBounds(text, 0, text.length, textRect)
                drawText(
                    text,
                    widthSize / 2f,
                    heightSize / 2 - textRect.exactCenterY(),
                    paint
                )
            }
        }
    }
}
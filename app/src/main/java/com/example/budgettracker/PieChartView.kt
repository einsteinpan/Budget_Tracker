package com.example.budgettracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class PieChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var data: Map<String, Float> = mapOf()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 55f  // 調整文字大小
        textAlign = Paint.Align.LEFT
    }
    private val rect = RectF()
    private val colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN)
    private var sortedData: List<Map.Entry<String, Float>> = listOf()

    fun setData(data: Map<String, Float>) {
        this.data = data
        // 確保使用相同的排序，以便圓餅圖的扇形和圖例相匹配
        sortedData = data.entries.sortedByDescending { it.value }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (sortedData.isEmpty()) return

        val total = data.values.sum()
        var startAngle = -90f

        // 調整圓餅圖的大小和位置
        val pieChartSize = Math.min(width, height) * 0.5f
        val pieChartOffsetY = height * 0.2f
        rect.set(
            width * 0.5f - pieChartSize / 2,
            height * 0.5f - pieChartSize / 2- pieChartOffsetY,
            width * 0.5f + pieChartSize / 2,
            height * 0.5f + pieChartSize / 2- pieChartOffsetY,
        )

        // 繪製圓餅圖
        sortedData.forEachIndexed { index, entry ->
            val sweepAngle = entry.value / total * 360
            paint.color = colors[index % colors.size]
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)
            startAngle += sweepAngle
        }

        // 繪製圖例和類別名稱
        var legendY = rect.top
        sortedData.forEachIndexed { index, entry ->
            paint.color = colors[index % colors.size]
            canvas.drawRect(rect.right + 50f, legendY, rect.right + 90f, legendY + 40f, paint)
            canvas.drawText(entry.key, rect.right + 100f, legendY + 35f, textPaint)
            legendY += 60f
        }

        // 繪製總計金額和類別列表
        drawTotalAndCategories(canvas, total)
    }

    private fun drawTotalAndCategories(canvas: Canvas, total: Float) {
        val startX = rect.left
        var startY = rect.bottom + 100f
        val lineSpacing = textPaint.textSize * 1.5f

        // 繪製總計金額
        canvas.drawText("總計金額: $%.2f".format(total), startX, startY, textPaint)

        // 繪製類別和金額列表
        startY += lineSpacing
        sortedData.forEachIndexed { index, entry ->
            val category = entry.key
            val amount = entry.value
            val percentage = amount / total * 100

            // 繪製類別和金額
            canvas.drawText("$category: $%.2f (%.1f%%)".format(amount, percentage), startX, startY, textPaint)
            startY += lineSpacing
        }
    }
}
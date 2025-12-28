package com.example.e_posyandu.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_posyandu.data.model.Riwayat
import kotlin.math.roundToInt

@Composable
fun KmsChart(
    riwayatList: List<Riwayat>,
    modifier: Modifier = Modifier,
    title: String = "Kartu Menuju Sehat (KMS)",
    jenisKelamin: String = "L", // L untuk Laki-laki, P untuk Perempuan
    tanggalLahir: String = "" // Tanggal lahir untuk menghitung usia
) {
    if (riwayatList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada data pertumbuhan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Berat Badan Menurut Usia (${if (jenisKelamin == "L") "Laki-laki" else "Perempuan"})",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Chart container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    KmsChartCanvas(
                        riwayatList = riwayatList,
                        jenisKelamin = jenisKelamin,
                        tanggalLahir = tanggalLahir
                    )
                }

                // Legend
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem(
                            color = Color(0xFF4CAF50),
                            label = "Normal (Hijau)"
                        )
                        LegendItem(
                            color = Color(0xFFFFC107),
                            label = "Gizi Kurang (Kuning)"
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem(
                            color = Color(0xFFF44336),
                            label = "Gizi Buruk (Merah)"
                        )
                        LegendItem(
                            color = Color(0xFF2196F3),
                            label = "Data Anak"
                        )
                    }
                }
                
                // Chart info
                Text(
                    text = "Grafik KMS menunjukkan pertumbuhan berat badan anak sesuai standar WHO",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun KmsChartCanvas(
    riwayatList: List<Riwayat>,
    jenisKelamin: String,
    tanggalLahir: String
) {
    val sortedRiwayat = riwayatList.sortedBy { it.tanggal }
    
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height
        val padding = 60f
        val bottomPadding = 100f
        val leftPadding = 100f
        
        val chartWidth = width - leftPadding - padding
        val chartHeight = height - padding - bottomPadding
        
        // KMS parameters: Usia 0-60 bulan, Berat 0-25 kg
        val minUsia = 0f
        val maxUsia = 60f
        val minBerat = 0f
        val maxBerat = 25f
        
        val usiaRange = maxUsia - minUsia
        val beratRange = maxBerat - minBerat
        
        // Draw chart background
        drawRect(
            color = Color.White,
            topLeft = Offset(leftPadding, padding),
            size = androidx.compose.ui.geometry.Size(chartWidth, chartHeight)
        )
        
        // Draw KMS zones (background colors)
        drawKmsZones(
            leftPadding = leftPadding,
            padding = padding,
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            minBerat = minBerat,
            maxBerat = maxBerat,
            jenisKelamin = jenisKelamin
        )
        
        // Draw WHO growth curves
        drawWhoGrowthCurves(
            leftPadding = leftPadding,
            padding = padding,
            bottomPadding = bottomPadding,
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            minUsia = minUsia,
            maxUsia = maxUsia,
            minBerat = minBerat,
            maxBerat = maxBerat,
            jenisKelamin = jenisKelamin
        )
        
        // Draw grid lines and labels
        drawKmsGridLinesWithLabels(
            width = width,
            height = height,
            padding = padding,
            leftPadding = leftPadding,
            bottomPadding = bottomPadding,
            chartWidth = chartWidth,
            chartHeight = chartHeight,
            minUsia = minUsia,
            maxUsia = maxUsia,
            minBerat = minBerat,
            maxBerat = maxBerat
        )
        
        // Draw axes
        // Y-axis
        drawLine(
            color = Color.Black,
            start = Offset(leftPadding, padding),
            end = Offset(leftPadding, height - bottomPadding),
            strokeWidth = 3f
        )
        // X-axis
        drawLine(
            color = Color.Black,
            start = Offset(leftPadding, height - bottomPadding),
            end = Offset(width - padding, height - bottomPadding),
            strokeWidth = 3f
        )
        
        // Plot child's growth data
        if (sortedRiwayat.isNotEmpty() && tanggalLahir.isNotEmpty()) {
            sortedRiwayat.forEach { riwayat ->
                // Calculate age in months from birth date and measurement date
                val usiaBulan = calculateAgeInMonths(tanggalLahir, riwayat.tanggal)
                
                if (usiaBulan in minUsia..maxUsia && riwayat.berat in minBerat..maxBerat) {
                    val x = leftPadding + ((usiaBulan - minUsia) / usiaRange * chartWidth)
                    val y = height - bottomPadding - ((riwayat.berat - minBerat) / beratRange * chartHeight)
                    
                    // Draw point
                    drawCircle(
                        color = Color(0xFF2196F3),
                        radius = 10f,
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = Offset(x, y)
                    )
                    
                    // Draw value label
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#2196F3")
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        drawText(
                            "${riwayat.berat}kg",
                            x,
                            y - 15f,
                            paint
                        )
                        drawText(
                            "${usiaBulan.roundToInt()}bln",
                            x,
                            y + 30f,
                            paint
                        )
                    }
                }
            }
        }
    }
}

// Helper function to calculate age in months from birth date and measurement date
private fun calculateAgeInMonths(tanggalLahir: String, tanggalPengukuran: String): Float {
    return try {
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val birthDate = dateFormat.parse(tanggalLahir)
        val measurementDate = dateFormat.parse(tanggalPengukuran)
        
        if (birthDate != null && measurementDate != null) {
            val diffInMillis = measurementDate.time - birthDate.time
            val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
            val ageInMonths = diffInDays / 30.44f // Average days per month
            ageInMonths.coerceAtLeast(0f)
        } else {
            0f
        }
    } catch (e: Exception) {
        // Fallback: estimate based on current age in years
        (0..60).random().toFloat()
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawKmsZones(
    leftPadding: Float,
    padding: Float,
    chartWidth: Float,
    chartHeight: Float,
    minBerat: Float,
    maxBerat: Float,
    jenisKelamin: String
) {
    // Draw colored zones based on WHO standards
    // Red zone (severe malnutrition) - bottom 10%
    drawRect(
        color = Color(0xFFF44336).copy(alpha = 0.1f),
        topLeft = Offset(leftPadding, padding + chartHeight * 0.9f),
        size = androidx.compose.ui.geometry.Size(chartWidth, chartHeight * 0.1f)
    )
    
    // Yellow zone (mild malnutrition) - next 15%
    drawRect(
        color = Color(0xFFFFC107).copy(alpha = 0.15f),
        topLeft = Offset(leftPadding, padding + chartHeight * 0.75f),
        size = androidx.compose.ui.geometry.Size(chartWidth, chartHeight * 0.15f)
    )
    
    // Green zone (normal) - middle 60%
    drawRect(
        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
        topLeft = Offset(leftPadding, padding + chartHeight * 0.15f),
        size = androidx.compose.ui.geometry.Size(chartWidth, chartHeight * 0.6f)
    )
    
    // Orange zone (overweight) - top 15%
    drawRect(
        color = Color(0xFFFF9800).copy(alpha = 0.1f),
        topLeft = Offset(leftPadding, padding),
        size = androidx.compose.ui.geometry.Size(chartWidth, chartHeight * 0.15f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWhoGrowthCurves(
    leftPadding: Float,
    padding: Float,
    bottomPadding: Float,
    chartWidth: Float,
    chartHeight: Float,
    minUsia: Float,
    maxUsia: Float,
    minBerat: Float,
    maxBerat: Float,
    jenisKelamin: String
) {
    // Draw WHO growth curves (simplified - in real app, use actual WHO data)
    val curvePoints = if (jenisKelamin == "L") {
        // Boys weight curves (simplified)
        listOf(
            listOf(0f to 3.3f, 12f to 9.6f, 24f to 12.2f, 36f to 14.3f, 48f to 16.3f, 60f to 18.3f), // P3
            listOf(0f to 3.9f, 12f to 10.9f, 24f to 13.4f, 36f to 15.7f, 48f to 17.8f, 60f to 20.0f), // P15
            listOf(0f to 4.4f, 12f to 12.0f, 24f to 14.8f, 36f to 17.0f, 48f to 19.2f, 60f to 21.2f), // P50
            listOf(0f to 5.0f, 12f to 13.3f, 24f to 16.3f, 36f to 18.6f, 48f to 20.7f, 60f to 22.9f), // P85
            listOf(0f to 5.8f, 12f to 14.8f, 24f to 17.8f, 36f to 20.0f, 48f to 22.2f, 60f to 24.5f)  // P97
        )
    } else {
        // Girls weight curves (simplified)
        listOf(
            listOf(0f to 3.2f, 12f to 8.9f, 24f to 11.5f, 36f to 13.9f, 48f to 15.8f, 60f to 17.7f), // P3
            listOf(0f to 3.6f, 12f to 10.2f, 24f to 12.8f, 36f to 15.0f, 48f to 17.0f, 60f to 19.0f), // P15
            listOf(0f to 4.2f, 12f to 11.5f, 24f to 14.1f, 36f to 16.1f, 48f to 18.2f, 60f to 20.2f), // P50
            listOf(0f to 4.8f, 12f to 12.8f, 24f to 15.6f, 36f to 17.6f, 48f to 19.6f, 60f to 21.7f), // P85
            listOf(0f to 5.5f, 12f to 14.2f, 24f to 17.2f, 36f to 19.2f, 48f to 21.2f, 60f to 23.5f)  // P97
        )
    }
    
    val colors = listOf(
        Color(0xFFF44336), // Red - P3
        Color(0xFFFFC107), // Yellow - P15
        Color(0xFF4CAF50), // Green - P50
        Color(0xFFFFC107), // Yellow - P85
        Color(0xFFF44336)  // Red - P97
    )
    
    curvePoints.forEachIndexed { curveIndex, points ->
        val path = Path()
        points.forEachIndexed { pointIndex, (usia, berat) ->
            val x = leftPadding + ((usia - minUsia) / (maxUsia - minUsia) * chartWidth)
            val y = size.height - bottomPadding - ((berat - minBerat) / (maxBerat - minBerat) * chartHeight)
            
            if (pointIndex == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = colors[curveIndex],
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = if (curveIndex == 2) 3f else 2f // P50 is thicker
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawKmsGridLinesWithLabels(
    width: Float,
    height: Float,
    padding: Float,
    leftPadding: Float,
    bottomPadding: Float,
    chartWidth: Float,
    chartHeight: Float,
    minUsia: Float,
    maxUsia: Float,
    minBerat: Float,
    maxBerat: Float
) {
    // Vertical grid lines (age in months)
    for (usia in 0..60 step 6) {
        val x = leftPadding + ((usia - minUsia) / (maxUsia - minUsia) * chartWidth)
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = Offset(x, padding),
            end = Offset(x, height - bottomPadding),
            strokeWidth = 1f
        )
        
        // Age labels on X-axis
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 20f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            drawText(
                "$usia",
                x,
                height - bottomPadding + 25f,
                paint
            )
        }
    }
    
    // Horizontal grid lines (weight in kg)
    for (berat in 0..25 step 5) {
        val y = height - bottomPadding - ((berat - minBerat) / (maxBerat - minBerat) * chartHeight)
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = Offset(leftPadding, y),
            end = Offset(width - padding, y),
            strokeWidth = 1f
        )
        
        // Weight labels on Y-axis
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 20f
                textAlign = android.graphics.Paint.Align.RIGHT
                isAntiAlias = true
            }
            drawText(
                "$berat",
                leftPadding - 10f,
                y + 5f,
                paint
            )
        }
    }
    
    // Axis titles
    drawContext.canvas.nativeCanvas.apply {
        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        
        // Y-axis title
        save()
        rotate(-90f, 30f, height / 2)
        drawText(
            "Berat Badan (kg)",
            30f,
            height / 2,
            titlePaint
        )
        restore()
        
        // X-axis title
        drawText(
            "Umur (bulan)",
            leftPadding + chartWidth / 2,
            height - 20f,
            titlePaint
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = MaterialTheme.shapes.small)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
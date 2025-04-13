package com.napnap.testoapp.ui.screens.quiz

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val questionMark: ImageVector
	get() {
		if (_questionMark != null) {
			return _questionMark!!
		}
		_questionMark = ImageVector.Builder(
            name = "Question_mark",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
			path(
    			fill = SolidColor(Color.Black),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(424f, 640f)
				quadToRelative(0f, -81f, 14.5f, -116.5f)
				reflectiveQuadTo(500f, 446f)
				quadToRelative(41f, -36f, 62.5f, -62.5f)
				reflectiveQuadTo(584f, 323f)
				quadToRelative(0f, -41f, -27.5f, -68f)
				reflectiveQuadTo(480f, 228f)
				quadToRelative(-51f, 0f, -77.5f, 31f)
				reflectiveQuadTo(365f, 322f)
				lineToRelative(-103f, -44f)
				quadToRelative(21f, -64f, 77f, -111f)
				reflectiveQuadToRelative(141f, -47f)
				quadToRelative(105f, 0f, 161.5f, 58.5f)
				reflectiveQuadTo(698f, 319f)
				quadToRelative(0f, 50f, -21.5f, 85.5f)
				reflectiveQuadTo(609f, 485f)
				quadToRelative(-49f, 47f, -59.5f, 71.5f)
				reflectiveQuadTo(539f, 640f)
				close()
				moveToRelative(56f, 240f)
				quadToRelative(-33f, 0f, -56.5f, -23.5f)
				reflectiveQuadTo(400f, 800f)
				reflectiveQuadToRelative(23.5f, -56.5f)
				reflectiveQuadTo(480f, 720f)
				reflectiveQuadToRelative(56.5f, 23.5f)
				reflectiveQuadTo(560f, 800f)
				reflectiveQuadToRelative(-23.5f, 56.5f)
				reflectiveQuadTo(480f, 880f)
			}
		}.build()
		return _questionMark!!
	}

private var _questionMark: ImageVector? = null

package appcup.uom.polaris.core.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Robot: ImageVector
    get() {
        if (RobotImpl != null) return RobotImpl!!

        RobotImpl = ImageVector.Builder(
            name = "Robot",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-400f)
                quadToRelative(0f, -100f, 70f, -170f)
                reflectiveQuadToRelative(170f, -70f)
                horizontalLineToRelative(240f)
                quadToRelative(100f, 0f, 170f, 70f)
                reflectiveQuadToRelative(70f, 170f)
                verticalLineToRelative(400f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(-400f)
                quadToRelative(0f, -66f, -47f, -113f)
                reflectiveQuadToRelative(-113f, -47f)
                horizontalLineTo(360f)
                quadToRelative(-66f, 0f, -113f, 47f)
                reflectiveQuadToRelative(-47f, 113f)
                close()
                moveToRelative(160f, -280f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(280f, 400f)
                reflectiveQuadToRelative(23.5f, -56.5f)
                reflectiveQuadTo(360f, 320f)
                reflectiveQuadToRelative(56.5f, 23.5f)
                reflectiveQuadTo(440f, 400f)
                reflectiveQuadToRelative(-23.5f, 56.5f)
                reflectiveQuadTo(360f, 480f)
                moveToRelative(240f, 0f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(520f, 400f)
                reflectiveQuadToRelative(23.5f, -56.5f)
                reflectiveQuadTo(600f, 320f)
                reflectiveQuadToRelative(56.5f, 23.5f)
                reflectiveQuadTo(680f, 400f)
                reflectiveQuadToRelative(-23.5f, 56.5f)
                reflectiveQuadTo(600f, 480f)
                moveTo(280f, 760f)
                verticalLineToRelative(-80f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(360f, 600f)
                horizontalLineToRelative(240f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(680f, 680f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(-80f, 0f)
                horizontalLineToRelative(560f)
                close()
            }
        }.build()

        return RobotImpl!!
    }

private var RobotImpl: ImageVector? = null


val FilterFocus: ImageVector
    get() {
        if (_Filter_center_focus != null) return _Filter_center_focus!!

        _Filter_center_focus = ImageVector.Builder(
            name = "Filter_center_focus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(480f, 600f)
                quadToRelative(-50f, 0f, -85f, -35f)
                reflectiveQuadToRelative(-35f, -85f)
                reflectiveQuadToRelative(35f, -85f)
                reflectiveQuadToRelative(85f, -35f)
                reflectiveQuadToRelative(85f, 35f)
                reflectiveQuadToRelative(35f, 85f)
                reflectiveQuadToRelative(-35f, 85f)
                reflectiveQuadToRelative(-85f, 35f)
                moveToRelative(0f, -80f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(520f, 480f)
                reflectiveQuadToRelative(-11.5f, -28.5f)
                reflectiveQuadTo(480f, 440f)
                reflectiveQuadToRelative(-28.5f, 11.5f)
                reflectiveQuadTo(440f, 480f)
                reflectiveQuadToRelative(11.5f, 28.5f)
                reflectiveQuadTo(480f, 520f)
                moveTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(400f, 0f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                close()
                moveTo(120f, 360f)
                verticalLineToRelative(-160f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(200f, 120f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(80f)
                horizontalLineTo(200f)
                verticalLineToRelative(160f)
                close()
                moveToRelative(640f, 0f)
                verticalLineToRelative(-160f)
                horizontalLineTo(600f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(160f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(840f, 200f)
                verticalLineToRelative(160f)
                close()
            }
        }.build()

        return _Filter_center_focus!!
    }

private var _Filter_center_focus: ImageVector? = null




val PhotoPrints: ImageVector
    get() {
        if (_Photo_prints != null) return _Photo_prints!!

        _Photo_prints = ImageVector.Builder(
            name = "Photo_prints",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(581f, 560f)
                quadToRelative(68f, 0f, 115.5f, -47f)
                reflectiveQuadTo(749f, 400f)
                quadToRelative(-68f, 0f, -116.5f, 47f)
                reflectiveQuadTo(581f, 560f)
                moveToRelative(0f, 0f)
                quadToRelative(-3f, -66f, -51.5f, -113f)
                reflectiveQuadTo(413f, 400f)
                quadToRelative(5f, 66f, 52.5f, 113f)
                reflectiveQuadTo(581f, 560f)
                moveToRelative(0f, -120f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(621f, 400f)
                verticalLineToRelative(-10f)
                lineToRelative(10f, 4f)
                quadToRelative(15f, 6f, 30.5f, 3f)
                reflectiveQuadToRelative(23.5f, -17f)
                quadToRelative(9f, -15f, 6f, -32f)
                reflectiveQuadToRelative(-20f, -24f)
                lineToRelative(-10f, -4f)
                lineToRelative(10f, -4f)
                quadToRelative(17f, -7f, 19.5f, -24.5f)
                reflectiveQuadTo(685f, 260f)
                quadToRelative(-9f, -15f, -24f, -17.5f)
                reflectiveQuadToRelative(-30f, 3.5f)
                lineToRelative(-10f, 4f)
                verticalLineToRelative(-10f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(581f, 200f)
                reflectiveQuadToRelative(-28.5f, 11.5f)
                reflectiveQuadTo(541f, 240f)
                verticalLineToRelative(10f)
                lineToRelative(-10f, -4f)
                quadToRelative(-15f, -6f, -30f, -3.5f)
                reflectiveQuadTo(477f, 260f)
                quadToRelative(-8f, 14f, -5.5f, 31.5f)
                reflectiveQuadTo(491f, 316f)
                lineToRelative(10f, 4f)
                lineToRelative(-10f, 4f)
                quadToRelative(-17f, 7f, -20f, 24f)
                reflectiveQuadToRelative(6f, 32f)
                quadToRelative(8f, 14f, 23.5f, 17f)
                reflectiveQuadToRelative(30.5f, -3f)
                lineToRelative(10f, -4f)
                verticalLineToRelative(10f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(581f, 440f)
                moveToRelative(0f, -80f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(541f, 320f)
                reflectiveQuadToRelative(11.5f, -28.5f)
                reflectiveQuadTo(581f, 280f)
                reflectiveQuadToRelative(28.5f, 11.5f)
                reflectiveQuadTo(621f, 320f)
                reflectiveQuadToRelative(-11.5f, 28.5f)
                reflectiveQuadTo(581f, 360f)
                moveToRelative(-68f, 400f)
                horizontalLineToRelative(219f)
                quadToRelative(-6f, 24f, -24f, 41.5f)
                reflectiveQuadTo(664f, 822f)
                lineTo(228f, 875f)
                quadToRelative(-33f, 4f, -59.5f, -16f)
                reflectiveQuadTo(138f, 806f)
                lineTo(86f, 368f)
                quadToRelative(-4f, -33f, 16.5f, -59f)
                reflectiveQuadToRelative(53.5f, -30f)
                lineToRelative(45f, -5f)
                verticalLineToRelative(80f)
                lineToRelative(-36f, 4f)
                lineToRelative(54f, 438f)
                close()
                moveToRelative(-152f, -80f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(281f, 600f)
                verticalLineToRelative(-440f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(361f, 80f)
                horizontalLineToRelative(440f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(881f, 160f)
                verticalLineToRelative(440f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(801f, 680f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(440f)
                verticalLineToRelative(-440f)
                horizontalLineTo(361f)
                close()
                moveToRelative(220f, -220f)
            }
        }.build()

        return _Photo_prints!!
    }

private var _Photo_prints: ImageVector? = null


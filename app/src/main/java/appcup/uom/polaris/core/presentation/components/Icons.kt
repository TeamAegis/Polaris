package appcup.uom.polaris.core.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
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

val AudioWaves: ImageVector
    get() {
        if (_audio_waves != null) return _audio_waves!!

        _audio_waves = ImageVector.Builder(
            name = "Robot",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(8.80195f, 18.1f)
                verticalLineTo(4.89998f)
                curveTo(8.80195f, 4.45928f, 9.1593f, 4.10196f, 9.6f, 4.10193f)
                curveTo(10.0407f, 4.10193f, 10.398f, 4.45925f, 10.398f, 4.89998f)
                verticalLineTo(18.1f)
                curveTo(10.398f, 18.5407f, 10.0407f, 18.898f, 9.6f, 18.898f)
                curveTo(9.1593f, 18.898f, 8.80195f, 18.5407f, 8.80195f, 18.1f)
                close()
                moveTo(13.602f, 15.2571f)
                verticalLineTo(8.14959f)
                curveTo(13.602f, 7.70888f, 13.9592f, 7.35155f, 14.4f, 7.35154f)
                curveTo(14.8408f, 7.35154f, 15.198f, 7.70887f, 15.198f, 8.14959f)
                verticalLineTo(15.2571f)
                curveTo(15.198f, 15.6977f, 14.8406f, 16.0551f, 14.4f, 16.0551f)
                curveTo(13.9594f, 16.0551f, 13.602f, 15.6977f, 13.602f, 15.2571f)
                close()
                moveTo(4.00195f, 13.1242f)
                verticalLineTo(10.0785f)
                curveTo(4.00195f, 9.63777f, 4.35928f, 9.28045f, 4.8f, 9.28045f)
                curveTo(5.24073f, 9.28045f, 5.59805f, 9.63777f, 5.59805f, 10.0785f)
                verticalLineTo(13.1242f)
                curveTo(5.59805f, 13.5649f, 5.24073f, 13.9223f, 4.8f, 13.9223f)
                curveTo(4.35928f, 13.9223f, 4.00195f, 13.5649f, 4.00195f, 13.1242f)
                close()
                moveTo(18.402f, 13.1242f)
                verticalLineTo(10.0785f)
                curveTo(18.402f, 9.63777f, 18.7592f, 9.28045f, 19.2f, 9.28045f)
                curveTo(19.6408f, 9.28045f, 19.998f, 9.63777f, 19.998f, 10.0785f)
                verticalLineTo(13.1242f)
                curveTo(19.998f, 13.5649f, 19.6408f, 13.9223f, 19.2f, 13.9223f)
                curveTo(18.7592f, 13.9223f, 18.402f, 13.5649f, 18.402f, 13.1242f)
                close()
            }
        }.build()
        return _audio_waves!!
    }
private var _audio_waves: ImageVector? = null;

val Journals: ImageVector
    get() {
        if (_Journals != null) return _Journals!!

        _Journals = ImageVector.Builder(
            name = "Journals",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(5f, 0f)
                horizontalLineToRelative(8f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
                verticalLineToRelative(10f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                horizontalLineTo(3f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
                horizontalLineToRelative(1f)
                arcToRelative(1f, 1f, 0f, false, false, 1f, 1f)
                horizontalLineToRelative(8f)
                arcToRelative(1f, 1f, 0f, false, false, 1f, -1f)
                verticalLineTo(4f)
                arcToRelative(1f, 1f, 0f, false, false, -1f, -1f)
                horizontalLineTo(3f)
                arcToRelative(1f, 1f, 0f, false, false, -1f, 1f)
                horizontalLineTo(1f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
                horizontalLineToRelative(8f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
                verticalLineToRelative(9f)
                arcToRelative(1f, 1f, 0f, false, false, 1f, -1f)
                verticalLineTo(2f)
                arcToRelative(1f, 1f, 0f, false, false, -1f, -1f)
                horizontalLineTo(5f)
                arcToRelative(1f, 1f, 0f, false, false, -1f, 1f)
                horizontalLineTo(3f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(1f, 6f)
                verticalLineToRelative(-0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 1f, 0f)
                verticalLineTo(6f)
                horizontalLineToRelative(0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 1f)
                horizontalLineToRelative(-2f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, -1f)
                close()
                moveToRelative(0f, 3f)
                verticalLineToRelative(-0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 1f, 0f)
                verticalLineTo(9f)
                horizontalLineToRelative(0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 1f)
                horizontalLineToRelative(-2f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, -1f)
                close()
                moveToRelative(0f, 2.5f)
                verticalLineToRelative(0.5f)
                horizontalLineTo(0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, false, 0f, 1f)
                horizontalLineToRelative(2f)
                arcToRelative(0.5f, 0.5f, 0f, false, false, 0f, -1f)
                horizontalLineTo(2f)
                verticalLineToRelative(-0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, false, -1f, 0f)
            }
        }.build()

        return _Journals!!
    }

private var _Journals: ImageVector? = null



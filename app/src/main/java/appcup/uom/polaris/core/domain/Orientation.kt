package appcup.uom.polaris.core.domain

/**
 * Represents the orientation of a device in 3D space.
 *
 * @property azimuth The rotation around the Z axis, typically representing the compass direction.
 * Values range from 0 to 360 degrees, where 0 is North, 90 is East, 180 is South, and 270 is West.
 * @property pitch The rotation around the X axis, representing the up/down tilt of the device.
 * Values typically range from -90 degrees (pointing straight down) to +90 degrees (pointing straight up).
 * @property roll The rotation around the Y axis, representing the tilt of the device to the left or right.
 * Values typically range from -180 degrees to +180 degrees.
 */
data class Orientation(
    val azimuth: Float,
    val pitch: Float,
    val roll: Float
)


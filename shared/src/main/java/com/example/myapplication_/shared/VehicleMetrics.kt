package com.example.myapplication_.shared

/**
 * 차량 실시간 메트릭 데이터 (Pleos / AAOS VHAL 데이터)
 */
data class VehicleMetrics(
    val speedKmh: Float = 0f,
    val gear: GearSelection = GearSelection.PARK,
    val totalDrivingDurationSeconds: Long = 0,
    val continuousDrivingSeconds: Long = 0,
    val suddenAccelerationCount: Int = 0,
    val suddenBrakingCount: Int = 0
)

enum class GearSelection(val rawCode: Int, val label: String) {
    PARK(4, "P"),
    REVERSE(2, "R"),
    NEUTRAL(1, "N"),
    DRIVE(8, "D");

    companion object {
        fun fromRawCode(code: Int): GearSelection {
            return entries.find { it.rawCode == code } ?: PARK
        }
    }
}

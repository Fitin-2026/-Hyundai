package com.example.myapplication_.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 차량 실시간 센서 데이터 및 안부 상태 싱글톤 리포지토리
 */
object CarDataRepository {

    private val _metrics = MutableStateFlow(VehicleMetrics())
    val metrics: StateFlow<VehicleMetrics> = _metrics.asStateFlow()

    private val _drivingState = MutableStateFlow(DrivingState.PARKED)
    val drivingState: StateFlow<DrivingState> = _drivingState.asStateFlow()

    private val _wellnessSummary = MutableStateFlow(WellnessSummary())
    val wellnessSummary: StateFlow<WellnessSummary> = _wellnessSummary.asStateFlow()

    private var driverName: String = "부모님"

    fun setDriverName(name: String) {
        driverName = name
        updateSummary()
    }

    /**
     * VHAL / 센서 또는 시뮬레이터로부터 들어온 속도 및 기어 업데이트
     */
    fun updateVehicleData(speedKmh: Float, gear: GearSelection) {
        val current = _metrics.value
        val isMoving = speedKmh > 0.5f

        val newState = when {
            gear == GearSelection.PARK -> DrivingState.PARKED
            isMoving -> DrivingState.DRIVING
            else -> DrivingState.STOPPED
        }

        val updatedMetrics = current.copy(
            speedKmh = speedKmh,
            gear = gear,
            totalDrivingDurationSeconds = if (newState != DrivingState.PARKED) current.totalDrivingDurationSeconds + 1 else current.totalDrivingDurationSeconds,
            continuousDrivingSeconds = if (newState == DrivingState.DRIVING) current.continuousDrivingSeconds + 1 else current.continuousDrivingSeconds
        )

        _metrics.value = updatedMetrics
        _drivingState.value = newState
        updateSummary()
    }

    /**
     * 급제동 / 급가속 감지 주입 (에뮬레이터 및 데모용)
     */
    fun triggerSuddenEvent(isBraking: Boolean) {
        val current = _metrics.value
        val updated = if (isBraking) {
            current.copy(suddenBrakingCount = current.suddenBrakingCount + 1)
        } else {
            current.copy(suddenAccelerationCount = current.suddenAccelerationCount + 1)
        }
        _metrics.value = updated
        updateSummary()
    }

    /**
     * 주행 리셋 (새 주행 시작시)
     */
    fun resetTrip() {
        _metrics.value = VehicleMetrics()
        _drivingState.value = DrivingState.PARKED
        updateSummary()
    }

    private fun updateSummary() {
        val summary = WellnessEngine.calculateWellness(
            metrics = _metrics.value,
            drivingState = _drivingState.value,
            driverName = driverName
        )
        _wellnessSummary.value = summary
        FirebaseManager.publishSummary(summary)
    }
}

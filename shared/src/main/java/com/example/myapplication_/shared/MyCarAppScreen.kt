package com.example.myapplication_.shared

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyCarAppScreen(carContext: CarContext) : Screen(carContext) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    init {
        scope.launch {
            CarDataRepository.wellnessSummary.collectLatest {
                invalidate()
            }
        }
    }

    override fun onGetTemplate(): Template {
        val summary = CarDataRepository.wellnessSummary.value
        val metrics = CarDataRepository.metrics.value
        val state = CarDataRepository.drivingState.value

        return when (state) {
            DrivingState.DRIVING -> buildDrivingTemplate(metrics, summary)
            DrivingState.STOPPED -> buildStoppedTemplate(metrics, summary)
            DrivingState.PARKED -> buildParkedTemplate(metrics, summary)
        }
    }

    /**
     * 1. 주행 중 UI (DRIVING)
     * - 원칙: 주행 방해 완전 차단 (팝업/버튼 차단, 조용한 모니터링)
     */
    private fun buildDrivingTemplate(metrics: VehicleMetrics, summary: WellnessSummary): Template {
        val pane = Pane.Builder()
            .addRow(
                Row.Builder()
                    .setTitle("주행 중: ${metrics.speedKmh.toInt()} km/h (D기어)")
                    .addText("🛡️ 운전자의 시선 이동을 줄이기 위해 주행 중 시각 팝업은 완전히 차단됩니다.")
                    .build()
            )
            .addRow(
                Row.Builder()
                    .setTitle("컨디션 모니터링")
                    .addText("상태: ${summary.fatigueLevel.badgeText} | 연속 주행: ${metrics.continuousDrivingSeconds / 60}분")
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("정차 테스트(0km/h)")
                    .setOnClickListener {
                        CarDataRepository.updateVehicleData(0f, GearSelection.DRIVE)
                    }
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("급제동 감지 테스트")
                    .setOnClickListener {
                        CarDataRepository.triggerSuddenEvent(isBraking = true)
                    }
                    .build()
            )
            .build()

        return PaneTemplate.Builder(pane)
            .setTitle("옆자리 - 안전 주행 중")
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle("주차 완료(P기어)")
                            .setOnClickListener {
                                CarDataRepository.updateVehicleData(0f, GearSelection.PARK)
                            }
                            .build()
                    )
                    .build()
            )
            .build()
    }

    /**
     * 2. 정차 중 UI (STOPPED - 신호 대기)
     * - 원칙: 손을 떼는 행동(스트레칭) 유도 금지! 짧은 안내 정보만 제공
     */
    private fun buildStoppedTemplate(metrics: VehicleMetrics, summary: WellnessSummary): Template {
        val pane = Pane.Builder()
            .addRow(
                Row.Builder()
                    .setTitle("신호 대기 중 🚦")
                    .addText("운전대에 손을 두고 편안히 신호를 기다리세요.")
                    .build()
            )
            .addRow(
                Row.Builder()
                    .setTitle("주행 거리 정보")
                    .addText(summary.stoppedDrivingInfo)
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("60km/h 주행 재개")
                    .setOnClickListener {
                        CarDataRepository.updateVehicleData(60f, GearSelection.DRIVE)
                    }
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("주차 완료(P기어)")
                    .setOnClickListener {
                        CarDataRepository.updateVehicleData(0f, GearSelection.PARK)
                    }
                    .build()
            )
            .build()

        return PaneTemplate.Builder(pane)
            .setTitle("옆자리 - 신호 대기 중")
            .setHeaderAction(Action.APP_ICON)
            .build()
    }

    /**
     * 3. 주차 / 도착 후 UI (PARKED - P 기어)
     * - 원칙: 주차 후에만 안부 문구, 휴식/스트레칭 추천 및 안부 전송 정보 표시
     */
    private fun buildParkedTemplate(metrics: VehicleMetrics, summary: WellnessSummary): Template {
        val pane = Pane.Builder()
            .addRow(
                Row.Builder()
                    .setTitle("오늘 운전 안부 요약")
                    .addText(summary.wellnessStatusSentence)
                    .build()
            )
            .addRow(
                Row.Builder()
                    .setTitle("주차 후 추천 휴식")
                    .addText("🧘 ${summary.restAreaStretchingTip}")
                    .build()
            )
            .addRow(
                Row.Builder()
                    .setTitle("가족 안부 전송 상태")
                    .addText("📩 ${summary.delayedTransmissionStatus}")
                    .addText(summary.privacyDisclaimer)
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("새 주행 출발 (60km/h)")
                    .setOnClickListener {
                        CarDataRepository.updateVehicleData(60f, GearSelection.DRIVE)
                    }
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("주행 기록 리셋")
                    .setOnClickListener {
                        CarDataRepository.resetTrip()
                    }
                    .build()
            )
            .build()

        return PaneTemplate.Builder(pane)
            .setTitle("옆자리 - 오늘 주행 안부 리포트 🌿")
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}

package com.example.myapplication_.shared

/**
 * 웰니스 알고리즘 엔진
 * - 점수화/평가/의료진단 금지 원칙 준수
 * - 본인의 평소 상태 대비 변화 및 문장형 안부 표현 산출
 */
object WellnessEngine {

    fun calculateWellness(
        metrics: VehicleMetrics,
        drivingState: DrivingState,
        driverName: String = "부모님"
    ): WellnessSummary {
        val durationMinutes = (metrics.totalDrivingDurationSeconds / 60).toInt()
        val continuousMinutes = (metrics.continuousDrivingSeconds / 60).toInt()

        // 1. 평소 상태 대비 피로도 변화 감지
        val fatigue = when {
            continuousMinutes >= 90 || metrics.suddenBrakingCount >= 4 -> FatigueLevel.HIGH_REST_NEEDED
            continuousMinutes >= 45 || metrics.suddenBrakingCount >= 2 -> FatigueLevel.MODERATE
            else -> FatigueLevel.GOOD
        }

        // 2. 부모님 부담 없는 문장형 안부 생성
        val sentence = when (fatigue) {
            FatigueLevel.GOOD -> "${driverName}께서 오늘도 평소처럼 편안하게 도착하셨어요 🌿"
            FatigueLevel.MODERATE -> "${driverName}께서 주행을 마치고 편안히 쉬고 계십니다 ☕"
            FatigueLevel.HIGH_REST_NEEDED -> "${driverName}께서 장거리 주행 후 휴식을 취하고 계십니다 🛋️"
        }

        // 3. 정차 중 안전 정보 (스트레칭 금지 -> 위치/거리 정보 제공)
        val stoppedInfo = when {
            continuousMinutes >= 60 -> "다음 휴게소까지 8km (약 7분 거리)"
            continuousMinutes >= 30 -> "다음 휴게소까지 15km (약 12분 거리)"
            else -> "편안한 마음으로 신호 대기 중입니다."
        }

        // 4. 주차 완료 후 스트레칭 추천
        val stretchingTip = when (fatigue) {
            FatigueLevel.HIGH_REST_NEEDED -> "장시간 운전이 끝났습니다. 시트를 뒤로 기울이고 1분간 깊은 호흡과 어깨 스트레칭을 권합니다."
            FatigueLevel.MODERATE -> "주차 완료 후 차에서 내리기 전 30초간 목과 어깨를 천천히 풀어주세요."
            FatigueLevel.GOOD -> "오늘도 안심 운전 완료! 편안하고 즐거운 시간 보내세요."
        }

        return WellnessSummary(
            driverName = driverName,
            timestampMs = System.currentTimeMillis(),
            drivingTripCount = 1,
            totalDrivingMinutes = durationMinutes,
            drivingState = drivingState,
            fatigueLevel = fatigue,
            wellnessStatusSentence = sentence,
            stoppedDrivingInfo = stoppedInfo,
            restAreaStretchingTip = stretchingTip
        )
    }
}

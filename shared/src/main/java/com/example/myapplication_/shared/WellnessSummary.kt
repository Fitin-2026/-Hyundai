package com.example.myapplication_.shared

/**
 * 평소 컨디션 대비 변화 및 안부 수준 (평가/진단/점수화 금지)
 */
enum class FatigueLevel(val description: String, val badgeText: String) {
    GOOD("평소와 다름없이 양호 🌿", "🟢 평소처럼 양호"),
    MODERATE("약간의 피로 감지 ☕", "🟡 피로 감지"),
    HIGH_REST_NEEDED("휴식 필요 권유 🧘", "🟠 휴식 필요")
}

/**
 * 운전자 안부 요약 데이터 (가족 공유용 - 목적지, 경로, 실시간 위치 정보 완전 제외)
 */
data class WellnessSummary(
    val driverName: String = "부모님",
    val timestampMs: Long = System.currentTimeMillis(),
    val drivingTripCount: Int = 1,
    val totalDrivingMinutes: Int = 0,
    val drivingState: DrivingState = DrivingState.PARKED,
    val fatigueLevel: FatigueLevel = FatigueLevel.GOOD,
    val wellnessStatusSentence: String = "오늘도 평소처럼 편안하게 도착하셨어요 🌿",
    val stoppedDrivingInfo: String = "다음 휴게소까지 12km (약 10분 거리)",
    val restAreaStretchingTip: String = "주차 후 차량 내부에서 1분간 어깨와 목을 천천히 풀어주세요.",
    val isSharingActive: Boolean = true,
    val privacyDisclaimer: String = "🔒 목적지·경로·위치 정보는 일절 전송되지 않습니다.",
    val delayedTransmissionStatus: String = "주차 완료 후 요약 카드 전달 완료",
    val sharedFields: SharedFields = SharedFields()
)

data class SharedFields(
    val shareFatigueLevel: Boolean = true,
    val shareWellnessSentence: Boolean = true,
    val shareTripCount: Boolean = true,
    val shareDrivingDuration: Boolean = true
)

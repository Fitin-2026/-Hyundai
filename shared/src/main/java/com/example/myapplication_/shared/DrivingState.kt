package com.example.myapplication_.shared

/**
 * 차량의 주행 맥락 상태
 * - DRIVING: 주행 중 (속도 > 0 km/h) -> 시각적 알림 완전 차단
 * - STOPPED: 정차 중 (속도 = 0 km/h, D/N 기어) -> 짧은 웰니스 추천 카드 팝업 후 자동 닫힘
 * - PARKED: 주차/도착 (P 기어) -> '오늘 운전 리포트' 카드 제공
 */
enum class DrivingState(val displayName: String) {
    DRIVING("주행 중"),
    STOPPED("정차 중 (신호 대기)"),
    PARKED("주차 완료 (도착)")
}

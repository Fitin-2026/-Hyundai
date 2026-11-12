package com.example.myapplication_.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 실시간 안부 데이터 싱크 매니저 (Firebase / 서버 동기화 래퍼)
 * - 실제 Firestore 연결 시에는 FirebaseFirestore.getInstance() 노드로 전달
 * - 1차 MVP & 에뮬레이터 데모에서는 메모리 싱크 및 Flow로 앱 간 실시간 연결 보장
 */
object FirebaseManager {

    private val _sharedSummary = MutableStateFlow(WellnessSummary())
    val sharedSummary: StateFlow<WellnessSummary> = _sharedSummary.asStateFlow()

    fun publishSummary(summary: WellnessSummary) {
        if (!summary.isSharingActive) return
        _sharedSummary.value = summary
        // TODO: Firestore integration point
        // FirebaseFirestore.getInstance().collection("users").document("driver1").set(summary)
    }

    fun updateSharingSettings(isActive: Boolean, fields: SharedFields) {
        val current = _sharedSummary.value
        val updated = current.copy(
            isSharingActive = isActive,
            sharedFields = fields
        )
        _sharedSummary.value = updated
    }
}

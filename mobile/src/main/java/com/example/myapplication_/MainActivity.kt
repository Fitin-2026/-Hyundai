package com.example.myapplication_

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication_.shared.CarDataRepository
import com.example.myapplication_.shared.DrivingState
import com.example.myapplication_.shared.FatigueLevel
import com.example.myapplication_.shared.FirebaseManager
import com.example.myapplication_.shared.GearSelection
import com.example.myapplication_.shared.SharedFields
import com.example.myapplication_.shared.WellnessSummary
import com.example.myapplication_.ui.theme.MyApplication_옆자리Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication_옆자리Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        onShowToast = { msg ->
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, onShowToast: (String) -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val sharedSummaryState = FirebaseManager.sharedSummary.collectAsState()
    val sharedSummary = sharedSummaryState.value

    val metricsState = CarDataRepository.metrics.collectAsState()
    val currentMetrics = metricsState.value

    val stateState = CarDataRepository.drivingState.collectAsState()
    val drivingState = stateState.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
    ) {
        // App Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Text(
                    text = "옆자리 YeopJari",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "차량 특화 웰니스 & 가족 안부 공유 서비스",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Tab Row (Family View vs Driver Settings)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("가족 화면 (안부 카드)", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("운전자 설정 & 시뮬레이터", fontWeight = FontWeight.Bold) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (selectedTabIndex == 0) {
                FamilyAnbooTab(summary = sharedSummary, onShowToast = onShowToast)
            } else {
                DriverSettingsTab(
                    summary = sharedSummary,
                    drivingState = drivingState,
                    speedKmh = currentMetrics.speedKmh,
                    onShowToast = onShowToast
                )
            }
        }
    }
}

/**
 * 1. 가족 화면 Tab (위치/경로 정보 없이 따뜻한 문장형 안부 카드 제공)
 */
@Composable
fun FamilyAnbooTab(summary: WellnessSummary, onShowToast: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🚗", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${summary.driverName}의 오늘 주행 안부",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                val badgeColor = when (summary.fatigueLevel) {
                    FatigueLevel.GOOD -> Color(0xFF10B981)
                    FatigueLevel.MODERATE -> Color(0xFFF59E0B)
                    FatigueLevel.HIGH_REST_NEEDED -> Color(0xFFEF4444)
                }

                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = summary.fatigueLevel.badgeText,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(16.dp))

            if (!summary.isSharingActive) {
                Text(
                    text = "🔒 운전자가 안부 공유를 일시 정지했습니다.",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            } else {
                // 문장형 안부 카드 메인 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0FDF4), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "💌 오늘 안부 요약",
                            color = Color(0xFF166534),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = summary.wellnessStatusSentence,
                            color = Color(0xFF14532D),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (summary.sharedFields.shareFatigueLevel) {
                    AnbooDetailRow(label = "컨디션 상태", value = summary.fatigueLevel.description)
                }
                if (summary.sharedFields.shareDrivingDuration) {
                    AnbooDetailRow(label = "총 운전 시간", value = "${summary.totalDrivingMinutes} 분")
                }
                if (summary.sharedFields.shareTripCount) {
                    AnbooDetailRow(label = "오늘 운전 횟수", value = "${summary.drivingTripCount} 회 (안전 종료)")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📩 ${summary.delayedTransmissionStatus}",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onShowToast("🔔 [안부 알림] ${summary.wellnessStatusSentence}")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("가족 안부 푸시 알림 수신 데모", color = Color.White)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Privacy Guarantee Banner (강조)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🛡️", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "사생활 보호 보장 방침",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E3A8A)
                )
                Text(
                    text = "🔒 목적지 · 경로 · 실시간 위치 정보는 일절 전송되지 않습니다.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2563EB)
                )
            }
        }
    }
}

@Composable
fun AnbooDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF64748B), fontSize = 14.sp)
        Text(text = value, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

/**
 * 2. 운전자 설정 & 데모 시뮬레이터 Tab
 */
@Composable
fun DriverSettingsTab(
    summary: WellnessSummary,
    drivingState: DrivingState,
    speedKmh: Float,
    onShowToast: (String) -> Unit
) {
    var isSharingOn by remember { mutableStateOf(summary.isSharingActive) }
    var shareFatigue by remember { mutableStateOf(summary.sharedFields.shareFatigueLevel) }
    var shareSentence by remember { mutableStateOf(summary.sharedFields.shareWellnessSentence) }
    var shareDuration by remember { mutableStateOf(summary.sharedFields.shareDrivingDuration) }

    // Sharing Settings Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "⚙️ 안부 공유 설정 (운전자 Opt-in)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("가족에게 안부 전체 공유", fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = isSharingOn,
                    onCheckedChange = { checked ->
                        isSharingOn = checked
                        FirebaseManager.updateSharingSettings(
                            checked,
                            SharedFields(shareFatigueLevel = shareFatigue, shareWellnessSentence = shareSentence, shareTripCount = true, shareDrivingDuration = shareDuration)
                        )
                        onShowToast(if (checked) "안부 공유가 활성화되었습니다." else "안부 공유가 일시정지되었습니다.")
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

            Text("공유 항목 세부 선택", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("피로도 / 컨디션 상태")
                Switch(
                    checked = shareFatigue,
                    enabled = isSharingOn,
                    onCheckedChange = {
                        shareFatigue = it
                        FirebaseManager.updateSharingSettings(
                            isSharingOn,
                            SharedFields(shareFatigueLevel = it, shareWellnessSentence = shareSentence, shareTripCount = true, shareDrivingDuration = shareDuration)
                        )
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("문장형 안부 표현")
                Switch(
                    checked = shareSentence,
                    enabled = isSharingOn,
                    onCheckedChange = {
                        shareSentence = it
                        FirebaseManager.updateSharingSettings(
                            isSharingOn,
                            SharedFields(shareFatigueLevel = shareFatigue, shareWellnessSentence = it, shareTripCount = true, shareDrivingDuration = shareDuration)
                        )
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("총 운전 시간 요약")
                Switch(
                    checked = shareDuration,
                    enabled = isSharingOn,
                    onCheckedChange = {
                        shareDuration = it
                        FirebaseManager.updateSharingSettings(
                            isSharingOn,
                            SharedFields(shareFatigueLevel = shareFatigue, shareWellnessSentence = shareSentence, shareTripCount = true, shareDrivingDuration = it)
                        )
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Wearable Integration Mock
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "⌚ 웨어러블 연동 (Health Connect)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("안정 심박수: 72 bpm | 지난밤 수면: 7시간 30분 (🟢 컨디션 양호)", fontSize = 13.sp, color = Color(0xFF475569))
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = { onShowToast("Health Connect 최신 건강 데이터가 동기화되었습니다.") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("건강 데이터 동기화 리프레시")
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Demo Vehicle Simulator Controls
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "🎛️ 차량 상태 시뮬레이터 (데모 영상용)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "현재 상태: ${drivingState.displayName} | 속도: ${speedKmh.toInt()} km/h",
                fontSize = 13.sp,
                color = Color(0xFF38BDF8)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { CarDataRepository.updateVehicleData(60f, GearSelection.DRIVE) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("60km/h 주행", fontSize = 12.sp)
                }
                Button(
                    onClick = { CarDataRepository.updateVehicleData(0f, GearSelection.DRIVE) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308))
                ) {
                    Text("정차(신호)", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { CarDataRepository.triggerSuddenEvent(isBraking = true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("급제동 감지", fontSize = 12.sp)
                }
                Button(
                    onClick = { CarDataRepository.updateVehicleData(0f, GearSelection.PARK) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("도착/주차(P)", fontSize = 12.sp)
                }
            }
        }
    }
}

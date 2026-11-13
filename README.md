# 옆자리

출발하면 옆자리가 조용히 함께해요 — 운전자의 주행 요약을 위치 정보 없이 가족에게 전하는 차량용·모바일 앱

## 실행 방법
1. Android Studio에서 이 저장소를 열고 Gradle Sync를 완료합니다.
2. 실행 구성에서 `automotive`를 선택하고 Pleos Connect 에뮬레이터에서 실행합니다.
3. 모바일 앱은 `mobile`을 선택해 일반 Android 에뮬레이터 또는 기기에서 실행합니다.
4. APK로 바로 설치하려면 `apk/` 폴더의 APK를 사용하세요.

## 테스트 환경 및 필요한 설정
- Android Studio: [버전]
- JDK 11 이상, minSdk 29, compileSdk/targetSdk 37
- 외부 서버·API 키가 필요 없습니다. 안부 공유는 앱 내 메모리 방식으로 동작하는 데모입니다.

## 개발 환경 (Pleos Connect 에뮬레이터)
- 에뮬레이터 버전: Pleos Connect v2.1.0
- 화면 규격: [가로 x 세로 해상도, 예: 1920x1080]

## APK 위치
- `apk/옆자리_release.apk`

## 구조
- `automotive` 차량용 앱
- `mobile` 모바일 앱
- `shared` 공통 로직

# commit_frontend

## 🛠 기술 스택
- 언어: Kotlin
- 서버 연결: Retrofit
- 알림: FCM(Firebase Cloud Messaging)
- 결제: WebView
- 채팅: Socket.IO client
- 홈화면: ViewPager2+TabLayout, SearchView, RecyclerView
- 마이페이지: SharedPreferences, Glide

 ## 화면 구현방식(Compose+XML)
 - Jetpack Compose : 채팅, 결제, 신청함, 신청서, 후기, 게시글, 포인트충전, 검색화면
 - XML(ConstraintLayout) : 홈화면, 마이페이지, 로그인, 북마크, 알림화면

## 🧑‍💻 Git 브랜치 전략
- 기능 단위 브랜치로 분리
- 브랜치 네이밍 규칙: `type/#이슈번호-기능`
  - feature	: 기능 개발
  - fix	: 일반 버그 수정
  - hotfix : 긴급 버그 수정
  - release	: 릴리즈 준비
  - develop	: 통합 개발 브랜치
  - main	: 최종 배포 브랜치

## 💬 Commit Convention
- [타입] #이슈번호: 작업 내용 (ex: [Fix] #11: 로그인 오류 해결)
  - Feature	: 새로운 기능
  - Fix	: 버그 수정
  - UI : UI 관련 작업
  - Refactor :	리팩토링
  - Add : 부수적 코드/파일 추가
  - Chore :	설정/변수명 변경 등
  - Docs :	문서 수정
  - Hotfix :	긴급 버그 수정
  - Delete :	코드/파일 삭제
  - Move :	파일 이동
  - Rename :	이름 변경
  - Merge :	브랜치 병합

## 💡 Code Convention
- 함수는 최대 100줄 이내
- 들여쓰기 4칸
- 하나의 클래스는 하나의 책임 원칙(SRP) 지향
- 기능별로 패키지 정리
- 중괄호: 여는 중괄호는 코드 블록 시작과 같은 줄에, 닫는 중괄호는 다음 줄에 작성

## 🧾 Issue & PR 규칙
- [Refactor / Feature/ Bug / Fix/ Style] 이슈 제목
- [타입/#이슈번호] 작업 요약

## targetSDK와 minSDK 버전
- targetSDK : 35 / Android 15.0
- minSDK : 24
- IDE 내 Emulator로 테스트


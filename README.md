# commit_frontend
<img width="1424" height="797" alt="image" src="https://github.com/user-attachments/assets/e4fa5ccd-9d61-40e3-9671-92031a21b0f1" />

## 📌 프로젝트 소개  
**커미션 중개 서비스 플랫폼 어플리케이션**  
사용자(의뢰인)와 작가(아티스트)를 연결하여 커미션을 의뢰·진행할 수 있는 플랫폼 앱입니다.  
작품 등록, 채팅, 결제, 후기, 알림, 북마크 등 커미션 거래 전 과정을 지원합니다.  

## 👥 Team & Role
| 이승은 | 전유나(팀장) | 최예윤 |
|:---:|:---:|:---:|
| <img src="https://avatars.githubusercontent.com/sseungeun" alt="이승은" width="150"> | <img src="https://avatars.githubusercontent.com/Erna23" alt="전유나" width="150"> | <img src="https://avatars.githubusercontent.com/yeyun0423" alt="최예윤" width="150"> |
| 담당 역할 | 담당 역할 | 담당 역할 |
| [GitHub](https://github.com/sseungeun) | [GitHub](https://github.com/Erna23) | [GitHub](https://github.com/yeyun0423) |
| <li>신청서 화면</li><li>후기 화면</li><li>채팅 화면</li> | <li>로그인/회원가입 화면</li><li>홈화면</li><li>알림 화면</li><li>북마크 화면</li><li>마이페이지 화면</li><li>작가_프로필 화면</li> | <li>게시글 화면</li><li>신청함 화면</li><li>검색창 화면</li><li>포인트 충전 화면</li> |

## 🛠 기술 스택
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Socket.IO](https://img.shields.io/badge/Socket.IO-010101?style=for-the-badge&logo=socket.io&logoColor=white)


 ## 🖥️ 화면 구현
| 방식 | 적용 화면 |
|------|-----------|
| 🎨 **Jetpack Compose** | 💬 채팅 · 💳 결제 · 📥 신청함/신청서 · 📝 후기/게시글 · ⚡ 포인트 충전 · 🔍 검색 |
| 🏗️ **XML (ConstraintLayout)** | 🏠 홈화면 · 🙋 마이페이지 · 🔑 로그인 · 📑 북마크 · 🔔 알림 · 👩‍🎨 작가 프로필 |

## 🧑‍💻 Git 브랜치 전략
- 기능 단위 브랜치로 분리
- 브랜치 네이밍 규칙: `type/#이슈번호-기능`
- **feature**(기능 개발) | **fix**(일반 버그 수정) | **develop**(통합 개발 브랜치) | **main**(최종 배포 브랜치)

## 💬 Commit Convention
- [타입] #이슈번호: 작업 내용 (ex: [Fix] #11: 로그인 오류 해결)
- **Feature**(기능) | **Fix**(버그) | **UI**(화면) | **Refactor**(리팩토링) | **Add**(추가) | **Rename**(이름변경) | **Merge**(병합)

## 🧾 Issue & PR 규칙
- [Refactor / Feature/ Bug / Fix/ Style] 이슈 제목
- [타입/#이슈번호] 작업 요약

## targetSDK와 minSDK 버전
- targetSDK : 35 / Android 15.0
- minSDK : 24


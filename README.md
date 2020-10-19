# FLO_MusicPlayer

# 뮤직 플레이어 앱 만들기
때는 2014년 11월! FLO 앱의 기획이 끝나고 본격적으로 개발 단계에 착수해야 하는 상황이 되었습니다. 런칭 목표 일자는 2015년 1월로 모든 팀원들이 전력 질주하는 단계죠. 얼마 전 팀에 앱 개발자로 합류한 당신은 아래의 요구사항에 따라 FLO 앱을 만들어야 합니다. 자세한 내용을 확인해보세요!

## 참고사항
따로 디자인 가이드를 드리지 않습니다. 앱의 상세한 동작, UX/UI, 버튼의 동작 방식 등은 FLO 앱을 통해서 직접 확인하고 참고해주시면 됩니다. [FLO 앱 확인하러 가기 (클릭)→](https://www.music-flo.com/)

## 환경 요구사항
- 언어: Java, Kotlin 중 택 1
- minSdkVersion: 21
- targetSdkVersion: 27 이상
- 라이브러리 사용: 필요시 외부 라이브러리 사용이 가능하며, 사용 시 출처(링크)를 주석으로 포함해주세요. 단, AndroidX, Google 라이브러리는 출처를 적지 않아도 됩니다.

## 화면 구성 요소
앱 구성 화면은 다음과 같습니다.

- 스플래시 스크린
- 음악 재생 화면
  - 재생 중인 음악 정보(제목, 가수, 앨범 커버 이미지, 앨범명)
  - 현재 재생 중인 부분의 가사 하이라이팅
  - Seekbar
  - Play/Stop 버튼
- 전체 가사 보기 화면
  - 특정 가사로 이동할 수 있는 토글 버튼
  - 전체 가사 화면 닫기 버튼
  - Seekbar
  - Play/Stop 버튼

## 기능 요구 사항
각 화면에는 다음과 같은 기능들이 구현되어야 합니다. 앱을 런칭한 뒤에는 요구사항이나 개선사항들이 엄청 많아질 것이 자명하니, 확장성을 고려하며 개발하는 것이 무척 중요합니다. 그리고 FLO를 처음 써보는 유저들이 금세 친숙함을 느낄 수 있는 UI/UX를 고민하는 것도 중요하겠죠?

스플래시 스크린
제공되는 [이미지](https://grepp-cloudfront.s3.ap-northeast-2.amazonaws.com/programmers_imgs/competition-imgs/2020-Flo-challenge/FLO_Splash-Img3x(1242x2688).png)를 2초간 노출 후 음악 재생 화면으로 전환시킵니다.

음악 재생 화면
- 주어진 노래의 재생 화면이 노출됩니다.
  - 앨범 커버 이미지, 앨범명, 아티스트명, 곡명이 함께 보여야 합니다.
- 재생 버튼을 누르면 음악이 재생됩니다. (1개의 음악 파일을 제공할 예정)
  - 재생 시 현재 재생되고 있는 구간대의 가사가 실시간으로 표시됩니다.
- 정지 버튼을 누르면 재생 중이던 음악이 멈춥니다.
- seekbar를 조작하여 재생 시작 시점을 이동시킬 수 있습니다.

전체 가사 보기 화면
- 전체 가사가 띄워진 화면이 있으며, 특정 가사 부분으로 이동할 수 있는 토글 버튼이 존재합니다.
  - 토글 버튼 on: 특정 가사 터치 시 해당 구간부터 재생
  - 토글 버튼 off: 특정 가사 터치 시 전체 가사 화면 닫기
- 전체 가사 화면 닫기 버튼이 있습니다.
- 현재 재생 중인 부분의 가사가 하이라이팅 됩니다.

곡 정보 데이터
- 다음 url에서 제공하는 하나의 곡만 재생하면 됩니다.
  - https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json
- 제공하는 json 파일의 내용이 변경되면 앱에서는 이를 반영하여 새로운 곡을 표시해 주어야 합니다.
- json파일에는 다음과 같은 필드가 있습니다.
  - singer: 아티스트명
  - album: 앨범명
  - title: 곡명
  - image: 앨범 커버 이미지
  - file: mp3 파일의 링크
  - lyrics: 시간으로 구분 된 가사. 가사의 각 줄 마다 해당 가사가 표시되기 시작되어야 할 시간이 분:초:밀리초 단위로 적혀 있으며, 이를 활용하여 현재 재생되는 곡의 가사를 표시해야 합니다.

*저작권 정보:
[CC BY](https://creativecommons.org/licenses/by/4.0/deed.ko) 라이선스로 [한국저작권위원회](https://gongu.copyright.or.kr/gongu/wrt/wrt/view.do?wrtSn=13238027&menuNo=200020)에서 제공한 곡을 활용했습니다.

============================================================================
# 20-10-01 프로그래머스 FLO 앱 개발 과제 후기
안드로이드 공부를 본격적으로 시작한지 1개월 정도 되어서 코드 작성, 코드 관리, 최적화 등 내가 스스로 만족할만하다고 느끼지는 않았지만 초보자인만큼 기능 구현, 예외 처리에 힘쓰자는 마음으로 도전했고 완성하게 되었습니다. 앱을 만들고 든 생각은 단순히 안드로이드 공부만 하는것보다 프로그래머스에서 내준 과제처럼 내가 스스로 생각하며 필요한 기능을 찾아서 앱을 만드는 것이 훨씬 흥미있고 역량 증가에 도움이 된다고 생각합니다!!

<img src="https://user-images.githubusercontent.com/52240990/94803712-ae079b80-0424-11eb-8887-e7e5c975e976.png" width="20%">

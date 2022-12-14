# CustomKeyboard


# 키보드 입력 영상
https://user-images.githubusercontent.com/50452867/207613467-6c1355ef-0a66-4545-a5d0-6812c0e1bc5d.mp4

### 키보드 입력 구현 방법
- ViewGroup(KoreanKeypadView.kt)에 child(KeypadLetterView.kt)를 배치하는 방식으로 뷰 구성
  - 초기에는 LinearLayout으로 구성하는 방안을 고민했지만, 성능을 고려해 커스텀뷰 방식으로 선택
-  한글입력(KoreanAutomata.kt)
  - 4가지의 State값으로 나누어 개발
      - 0 -> 아무것도 입력되지 않은 상태
      - 1 -> 초성이 입력된 상태
      - 2 -> 초성과 중성이 입력된 상태
      - 3 -> 초성과 중성과 종성이 입력된 상태

  참고코드: https://github.com/godsangin/Keyboard-Sample
  godsangin님의 키보드 예제 코드를 참고하여 개발하였습니다.


# 클립보드 기능 영상
https://user-images.githubusercontent.com/50452867/207613488-f55e766f-66a8-43a5-a8de-c13cbd4f64f8.mp4

### 클립보드 기능 구현 방법
- 엡 내 db: DataStore 사용하여 구현, 클립보드에 변화가 있을 때마다 저장
- 앱 내 db에 있는 리스트에 접근하여 RecyclerView로 리스트 구현
- 클립보드 버튼 클릭시 키패드 뷰, 클립보드 뷰의 visibilty를 변경하는 방식으로 구현


## 단축키 기능 (구현에 실패했습니다.)
- 단축키 팝업 레이아웃은 ConstraintLayout을 통해 레이아웃 깊이를 최대한 얕게 구현
- 단축키를 꾹 눌렀을 때, popupWindow를 띄워 기능을 구현하려 했지만, popupWindow가 띄워지면 키보드의 동작이 이상해지는 버그 발생 (키보드가 꺼지거나, 계속 깜빡인다)
- 다른 좋은 대안을 찾지 못해 시간 안에 구현하지 못했습니다.

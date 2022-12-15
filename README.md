# CustomKeyboard


## 사용한 기술
- DataStore (클립모드 리스트 앱 내부영역 저장에 사용)
- dagger-hilt


# 키보드 입력 영상
https://user-images.githubusercontent.com/50452867/207613467-6c1355ef-0a66-4545-a5d0-6812c0e1bc5d.mp4

### 키보드 입력 구현 방법
- ViewGroup(KoreanKeypadView.kt)에 child(KeypadLetterView.kt)를 배치하는 방식으로 뷰를 구성하였습니다.
  - 초기에는 LinearLayout으로 구성하는 방안을 고민했지만 View의 깊이가 깊어질 수 있고, 절대적인 뷰의 개수가 늘어날 수 있어, 성능을 고려해 커스텀뷰 방식으로 선택
- Shift 버튼 클릭시 쌍자음에 해당하는 KeypadLetterView의 text를 바꾸고 invalidate() 하여 구현

- TouchDelegate를 이용해 터치 영역을 넓혀 뷰의 사이사이를 클릭해도 입력이 가능합니다.

-  한글입력/조합(KoreanAutomata.kt)
    - 4가지의 State값으로 나누어 개발했습니다.
        - 0 -> 아무것도 입력되지 않은 상태
        - 1 -> 초성이 입력된 상태
        - 2 -> 초성과 중성이 입력된 상태
        - 3 -> 초성과 중성과 종성이 입력된 상태

    - 참고코드: https://github.com/godsangin/Keyboard-Sample godsangin님의 키보드 예제 코드를 참고하여 개발하였습니다.


# 클립보드 기능 영상
https://user-images.githubusercontent.com/50452867/207613488-f55e766f-66a8-43a5-a8de-c13cbd4f64f8.mp4

### 클립보드 기능 구현 방법
- DataStore에 저장된 리스트에 접근하여 RecyclerView로 리스트 구현하였습니다.
- 클립보드 버튼 클릭시 키패드 뷰, 클립보드 뷰의 visibilty를 변경하는 방식으로 구현하였습니다.


## 단축키 기능 (구현에 실패했습니다.)
- 단축키 팝업 레이아웃은 ConstraintLayout을 통해 레이아웃 깊이를 최대한 얕게 구현하려했습니다.
- 단축키를 꾹 눌렀을 때, popupWindow를 띄워 기능을 구현하려 했지만, popupWindow가 띄워지면 키보드의 동작이 이상해지는 버그가 발생했습니다. (키보드가 꺼지거나, 계속 깜빡인다)
- 다른 좋은 대안을 찾지 못해 시간 안에 구현하지 못했습니다.


## 아쉬운 점
- kotlin coroutine을 제대로 활용하지 못한 점이 아쉽습니다. IMEService의 LifeCycle을 고려해 coroutine을 제대로 활용해보고 싶습니다.


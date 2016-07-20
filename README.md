CONNECT 3 BLOCKS
---
**Scheduled**
- Kick-off project : 몰라.....적지 않았어.
- Create new project and start coding : `2016-05-23`
 - 한달동안 아무것도 안했어.--> 이제 두달이야
- 프로젝트에 집중하는 시간이 적으니 속도가 더 느려지고 있어.

**Toggle button**
Lib 에 토글 버튼 클래스 생성해서 사용할 것.
- TiledSprite on Rectangle

**Level View Button**
- Color configuration 에 대해서는 해당 클래스 안에 정의 할 것.
- Color
 - LevelViewOnly
   - background color
   - font color
 - LevelViewSel
   - Default background
   - Default font
   - Default gage
 - SelPage
 - Blank

**Memo**
- 다음부터는 프로젝트를 만들때 부터 ReadMe.md 파일을 만들어서 내용을 정리 할 것.
 - 그림은 굳이 필요 없음. 어짜피 계속해서 수정되는 내용이고 svg 파일로 정리하는 것이 가장 명확
 - 파일이 계속해서 분산되니 불필요한 일이 반복된다.
- 오래 쉬면 하기 싫어져....... 웬지 긴 프로젝트 여정이 예상됨. 
- Notification 을 굳이 Service에서 해야 할 지 고민해 볼 것.
- 네모버튼안에 스프라이트 집어 넣기
 - 굳이 클래스를 다시 만들거나 extends 하지 말고 RectangleButton 클래스로 버튼 객체를 만들고 해당객체.attachChild 로 스프라이트를 집어 넣는 것이 효율적일 듯.
- 나 혼자 보는 메모나 문서는 최대한 보통 사용하는 언어를 사용해야 한다. 문어체는 공유시에는 효율적일지 모르나 혼자 볼때에는 오히려 이해도를 떨어뜨린다. 유머도 없고. 쳇.

**ToDo@20 July,2016**
1. roundButtons 
   (1) focus 색상 적용
   (2) Homescene에서 버튼 선택 적용
    - Select and focus button has LevelSelButton property among buttons has same property.


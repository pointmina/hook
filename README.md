2024 04 04 -> 5th commit
-

1. 네비게이션바 홈 추가 clear☑️
    
2. 홈 상세 뷰 구현
- 클릭하면 -> 상세 뷰 구현 clear☑️


3. 태그뷰 태그 모음 tag_fragment  clear ☑️
- 그리드 뷰 사용

4. 태그뷰 선택 태그 selected_tag_fragment clear☑️
- 태그 수정? : 바텀 시트 or fragment, 커스텀 알림 😵

2024 04 05 -> 6th commit
-

1. Dummy Data 만들고 home에 리사이클 뷰 뿌리기☑️
- 레이아웃 매니저
- recyclerView Divider : https://youtu.be/esLQ7oEvfOs?si=kN0JCEpFoEIILt9W

2. 리사이클 클릭스 상세 activity 뿌리기

3. selected_tag fragment에서 수정 클릭스 -> 태그 수정 fragment 


2024 04 07 -> 7th commit
-

1. kakao login -> google login ☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️☑️ 하..
- https://visualandroidblog.blogspot.com/2023/04/google-sign-in-firebase-android-kotlin-tutorial.html
- https://developers.google.com/android/guides/setup?hl=ko
- https://www.youtube.com/watch?v=H_maapn4Q3Q
- https://firebase.google.com/docs/auth/android/start?hl=ko&_gl=1*tfveyn*_up*MQ..*_ga*Nzk2MDYxODgwLjE3MTI0MjI2ODI.*_ga_CW55HF8NVT*MTcxMjQyMjY4Mi4xLjAuMTcxMjQyMjY4Mi4wLjAuMA..
- https://console.firebase.google.com/u/0/project/hook-882c5/settings/general/android:com.hanto.Hook?hl=ko


2. 설정 버튼 누르면☑️
- 로그아웃 버튼
- 유저 정보 나오게

https://github.com/pointmina/Hook/assets/68779817/14745caf-0b3f-4546-bfee-3a9b5a55f393

2024 04 08 -> 8th commit
-

0. 레이아웃 만들기, 함수 만들기 ☑️

2. ![image](https://github.com/pointmina/Hook/assets/68779817/8252ed3e-6185-425f-b675-a1a82247cad5)
- 리사이클러뷰에 리사이클러뷰 삽입.. 에휴
- https://velog.io/@simsubeen/Android-Kotlin-RecyclerView-%EA%B0%80%EB%A1%9C-%EC%A0%95%EB%A0%AC-GridLayoutManager
- https://notepad96.tistory.com/201
- https://jinsangjin.tistory.com/25
- https://developer.android.com/develop/ui/views/layout/recyclerview?hl=ko <---------------------------------------


2. ![image](https://github.com/pointmina/Hook/assets/68779817/6ea96baf-456a-4a62-ad78-0b48cf506f49)
- 글자길이 수에따라 가로로 길어져야함.. 에흌ㅋㅋㅋ
- 겉에 껍데기 가로 길이를 wrap_content로 만들고 text를 외부 데이터에서 받아오는 방식

해결 못함..
그럼 아예 따로 만들어야하나..? 하 멘붕 ㅜ

고민거리
-

1. 앱실행시 어플 아이콘 안보이고 바로 스플래시로 넘어가기
- main icon은 무슨색 할거임?

2. 로그인 페이지 구현
- 카카오 로그인 : https://developers.kakao.com/docs/latest/ko/kakaologin/design-guide
- https://jdroid.tistory.com/10#google_vignette
    
3. 리사이클 뷰 삽입..
- search view
- https://velog.io/@simsubeen/Android-Kotlin-SearchView%EC%99%80-Filter%EB%A1%9C-RecyclerView-%EA%B2%80%EC%83%89%ED%95%98%EA%B8%B0

5. 바텀 네비게이션 뷰에서 선택하면 폰트가 적용안됨..ㅠ

6. 모두 보기 클릭시
- fragment 띄우기? vs 목록으로 나오기..?
- 이건 크게 상관 없을 듯;

7. web share
- https://wormwlrm.github.io/2020/05/09/Web-Share-API.html
- https://boxfoxs.tistory.com/390

8. 상세페이지

추가 기능 
-

1. 모션으로 바텀 네비게이션 넘기기
2. 모두보기 fragment띄울때 스무스하게 띄우기
3.  
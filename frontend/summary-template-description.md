summary template 조회 API를 호출해 요약글 작성하기 페이지를 구성한다.

GET http://localhost:8080/api/summary-templates
Cookie: JSESSIONID={{sessionId}}

summary template의 구조는 아래와 같다.

```json
{
  "templates": {
    "핵심 파악": {
      "order": 1,
      "type": "STATIC",
      "element": [
        {
          "id": 1,
          "section": "BIG_PICTURE",
          "title": "이 글을 한 문장으로 요약하자면?",
          "description": "이 글이 소개하고자 했던 개념, 해결하고자 했던 문제 등을 하나의 문장으로 정리해보세요.",
          "createdAt": "2025-10-04T06:03:49",
          "updatedAt": "2025-10-04T06:03:49"
        }
      ]
    },
    "세부 내용 정리": {
      "order": 2,
      "type": "SELECT",
      "element": [
        {
          "id": 2,
          "section": "DETAILS",
          "title": "문제-해결-결론형",
          "description": null,
          "createdAt": "2025-10-04T06:03:49",
          "updatedAt": "2025-10-04T06:03:49"
        },
        {
          "id": 3,
          "section": "DETAILS",
          "title": "왜-무엇을-어떻게형",
          "description": null,
          "createdAt": "2025-10-04T06:03:49",
          "updatedAt": "2025-10-04T06:03:49"
        },
        {
          "id": 4,
          "section": "DETAILS",
          "title": "자유형",
          "description": null,
          "createdAt": "2025-10-04T06:03:49",
          "updatedAt": "2025-10-04T06:03:49"
        }
      ]
    },
    "사고 확장": {
      "order": 3,
      "type": "MULTI_SELECT",
      "element": [
        {
          "id": 5,
          "section": "EXPANSION",
          "title": "새롭게 알게 된 사실",
          "description": null,
          "createdAt": "2025-10-04T06:03:49",
          "updatedAt": "2025-10-04T06:03:49"
        },
        {
          "id": 6,
          "section": "EXPANSION",
          "title": "나의 현재 상황에 적용하기",
          "description": null,
          "createdAt": "2025-10-04T06:03:49",
          "updatedAt": "2025-10-04T06:03:49"
        },
        {
          "id": 7,
          "section": "EXPANSION",
          "title": "기존에 알고 있던 지식과 비교/대조하기",
          "description": null,
          "createdAt": "2025-10-04T06:03:49",
          "updatedAt": "2025-10-04T06:03:49"
        },
        {
          "id": 8,
          "section": "EXPANSION",
          "title": "추가적으로 알아 볼 내용",
          "description": null,
          "createdAt": "2025-10-04T06:03:49",
          "updatedAt": "2025-10-04T06:03:49"
        }
      ]
    }
  }
}
```

template json 내 각 key는 h2 태그로 각 section의 제목을 나타낸다.

각 section의 type은 아래와 같은 의미를 갖는다.
- STATIC: 단 한 개의 sub-section 만 가지며 반드시 화면에 노출한다.
- SELECT: 여러 개 sub-section 중 하나를 선택할 수 있게 하며 이것만 화면에 노출한다.
- MULTI_SELECT: 여러 개 sub-section 중 여러 개를 선택할 수 있게 하며 선택한 것들을 하나씩 덧붙이는 형태로 모두 화면에 노출한다. '이 섹션 제거하기' 버튼을 눌러 삭제할 수 있도록 한다.

각 section 별로 여러 개 sub-section이 존재할 수 있으며, 각 sub-section의 제목은 section의 element 내 title 값으로 h3 태그로 나타낸다.

각 sub-section에는 text box가 있어 사용자가 요약문을 작성할 수 있다.

최종적으로 작성한 요약문은 아래 요약 생성 API를 호출해 저장한다.

POST http://localhost:8080/api/summaries
Content-Type: application/json
Cookie: JSESSIONID={{sessionId}}

{
"bookmarkId": 4,
"content": "이것은 요약 내용입니다."
}

content에는 요약문 전문을 markdown 문법으로 저장할 것이며, 위에서 명시한 h2 태그와 h3 태그는 ## 와 ###로 변경하여 나타낸다.

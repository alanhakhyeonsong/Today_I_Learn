# Vim 에디터 단축키 모음

![](https://velog.velcdn.com/images/songs4805/post/0b1029a9-6785-438c-b242-e68bbd874f8c/image.png)

# Vim 모드

![](https://velog.velcdn.com/images/songs4805/post/ab129852-7a57-4cc8-af19-c5c346c86688/image.png)

|       Mode        |                      Description                       |
| :---------------: | :----------------------------------------------------: |
|    Normal mode    |                   vim 시작시의 모드                    |
|    Insert mode    |       `i`, `a`, `o` 등의 명령어로 삽입 모드 진입       |
|    Visual mode    |       visual line/block을 지정해서 동작하는 모드       |
|    Select mode    |        선택 영역을 바로 수정(거의 쓰이지 않음)         |
| Command-line mode |            `:`, `/`, `?`, `!`으로 명령 수행            |
|      Ex mode      |                        Ex mode                         |
| Terminal-job mode | `:terminal` 명령으로 vim 창에서 터미널 실행 (예: bash) |

# Vim 설정

자신의 홈 디렉토리에서 `.vimrc` 파일을 생성

```bash
$ cd ~
$ vim .vimrc (or nano .vimrc)
```

- `.vimrc` : vim 활용 시에 활용되는 설정값들을 저장함

![](https://velog.velcdn.com/images/songs4805/post/1644b165-bfc6-4d06-bd0b-c9a27e49c2b5/image.png)

# Vim 이동

- h, j, k, l : 좌, 하, 상, 우
- w : 단어 단위 앞으로 이동(단어 시작)
- e : 단어 단위 앞으로 이동(단어 끝)
- b : 단어 단위 뒤로 이동
- **0 : 라인 시작으로 이동**
- **$ : 라인 끝으로 이동**
- **f(CHAR) : 앞쪽으로 가장 먼저 발견되는 CHAR로 이동**
- **F(CHAR) : 뒤쪽으로 가장 먼저 발견되는 CHAR로 이동**
- } : 앞쪽으로 단락 단위 이동
- { : 뒤쪽으로 단락 단위 이동
- **G : 파일의 마지막 줄로 이동**
- **gg : 파일의 첫 줄로 이동**
- H, M, L : 현재 화면 기준 최상단/중간/최하단으로 이동

![](https://velog.velcdn.com/images/songs4805/post/84629123-f466-455b-a586-406059bcb470/image.png)

# Vim 편집

## 입력 모드

- **i : 현재 위치에서 입력 모드 시작**
- I : 현재 라인의 시작에서 입력 모드 시작
- a : 현재 위치 다음 칸에서 입력 모드 시작
- A : 현재 라인의 끝에서 입력 모드 시작
- o : 현재 라인의 다음 줄에서 입력 모드 시작
- O : 현재 라인을 다음 줄로 밀고 입력 모드 시작
- cc : 현재 라인 삭제 후 입력 모드 시작
- C : 현재 위치부터 라인 끝까지 삭제 후 입력 모드 시작
- r : 한 글자만 바꿀 때 활용함
- R : 바꾸기 모드가 되면서 원래 있던 내용들을 덮어 쓰면서 입력함

## 되돌리기/반복

- u : undo
- **CTRL + r : redo**
- .(dot) : 마지막 행동 반복

## 복사

- yy : 현재 라인을 복사함
- yw : 현재 word의 끝까지 복사함

## 붙여넣기

- p : 현재 위치 다음에 붙여넣기
- P : 현재 위치에 붙여넣기

## 잘라내기

- **x : 현재 캐릭터를 잘라내기**
- dd : 현재 라인을 잘라내기
- dw : 현재 word의 끝까지 잘라내기
- **J : 아래행 합치기**

## 파일 열기/저장 닫기

- `:e <filename>` : 파일 편집
- `:w` : 저장
- `:w!` : 강제로 저장
- `:q` : 나가기
- `:q!` : 강제로 나가기 (저장 x)
- `:wq` : 저장하고 나가기 (== `:x`)
- `:wq!` : 강제로 저장하고 나가기
- **`ZZ` : 저장 후 종료**
- **`Ctrl + 2` : 마지막 입력 한번 더 입력 뒤 편집모드 종료**

# Vim 검색 및 치환

## String 검색

- **`/keyword` : 아래쪽으로 검색함**
- **`?keyword` : 위쪽으로 검색함**
- `n` : 정방향 검색을 반복함
- `N` : 역방향 검색을 반복함
- `/abc\|xyz` : abc 혹은 xyz를 검색함
- `:nohl` : 하이라이트 없앰

## String 치환

- `:%s/str/replace/g` : `str`을 `replace`로 치환함
- `:%s/str/replace/gc` : 치환 시 사용자에게 묻기
- `:%s/$/replace/gc` : 제일 뒤에 `<replace>` 추가

## 라인 지정 기호

- `.` : 현재 라인
- `1` : 첫번째 라인
- `$` : 마지막 라인

## 라인 지정 String 치환

- `1,10s/str/replace/gc` : 라인 1~10까지만 치환을 적용함
- `1,.s/str/replace/gc` : 라인 1에서 현재 라인까지만 치환을 적용함

# Vim에서의 멀티 윈도우

- `:split` : 가로 분할
- `:vsplit` : 세로 분할
- `:quit` : 해당 윈도우 닫기
- `:only` : 해당 윈도우만 남기기

# Vim에서의 비주얼 모드

- v : 일반 비주얼 모드
- V : 라인 비주얼 모드 (Shift + v)
  - gg → shift + v + g는 뭘까? (== gg → V + G)
- Ctrl + v : 비주얼 블록모드(Visual Block Mode)
- d : 삭제
- y : 복사
- p : 붙여넣기
- ~ : 대소문자 변환
- u : 소문자 변환
- U : 대문자 변환
- **J : 공백 제거 및 아래 줄을 붙임**
- `>, <` : 들여쓰기 (인덴트)
- `:norm i#` : 제일 처음에 #을 입력함 (주석처리)
- `:norm x` : 제일 처음 문자를 삭제함

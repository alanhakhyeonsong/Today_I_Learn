# Git

## Contents

Ref: Open Source SW introduction - Spring Semester, 2022

- [git 시작하기](#git-시작하기)
- [git stage와 commit 이해하기](#git-stage와-commit-이해하기)
  - [작업 트리](#작업-트리)
  - [스테이지](#스테이지)
  - [저장소](#저장소)
- [git으로 버전을 관리하는 과정](#git으로-버전을-관리하는-과정)
  - [git status](#git-status)
  - [git log](#git-log)
  - [git add, commit](#git-add,-commit)
  - [git diff](#git-diff)
  - [`git checkout <commit>`](#`git-checkout-<commit>`)
- [.gitignore 파일 추가하기](#.gitignore-파일-추가하기)
- [git branch](#git-branch)
  - [브랜치가 필요한 이유?](#브랜치가-필요한-이유?)
  - [branch의 기능](#branch의-기능)
  - [HEAD → master의 의미](#HEAD-→-master의-의미)
  - [branch 병합(merge)하기](<#branch-병합(merge)하기>)
  - [branch 강제 삭제하기](#branch-강제-삭제하기)
  - [branch 그래프](#branch-그래프)

## git 시작하기

```bash
$ git init
```

`.git`이라는 숨김 디렉토리가 생성된다.

## git stage와 commit 이해하기

![](https://velog.velcdn.com/images/songs4805/post/610b487e-feb1-4ed9-92d1-d36fd3fb28e9/image.png)

### 작업 트리

- 파일 수정, 저장 등의 작업을 하는 디렉토리
- working directory
- 우리 눈에 보이는 디렉터리가 바로 작업 트리

### 스테이지

- 버전을 만들 파일이 대기하는 곳
- 스테이징 영역
- 눈에 보이지 않음

### 저장소

- 스테이지에 있는 대기 파일을 버전으로 만들어 저장하는 곳
- repository
- 눈에 보이지 않음

## git으로 버전을 관리하는 과정

![](https://velog.velcdn.com/images/songs4805/post/20e73add-6d9c-4bab-9330-2bb4eca4e0b9/image.png)

1. 작업 트리에서 파일을 수정하고 저장
2. 버전을 만들고 싶다면 스테이지에 넣음
3. 스테이지에 있던 파일을 저장소에 버전으로 저장

### git status

저장소의 상태를 확인한다.

```bash
$ git status
```

### git log

지금까지의 저장소의 히스토리를 확인한다.

```bash
# n개의 히스토리만을 출력함
$ git log -n

# 각 commit의 diff 결과를 출력함
$ git log -p

# 최근 2개의 diff 결과를 출력함
$ git log -p -2

# 모든 히스토리를 보여줌
$ git log --all
```

### git add, commit

```bash
$ nano main.py
> print("hello world!")

$ cat main.py

# 또는 git add .
$ git add main.py

$ git status

$ git commit -m "{message}"
```

- 버전을 만드는 행위를 **커밋**이라 함
- 커밋할 경우에 메시지를 함께 기록함

### git diff

- 현재 working directory의 마지막 commit과의 차이점을 비교
- `$ git diff <commit#1> <commit#2>` : 두 commit의 차이점을 비교

### `git checkout <commit>`

- 해당 버전으로 파일을 되돌릴 수 있음
- `$ git log -all`을 통해 이후 버전 확인 가능

```bash
$ git init
$ git add <file#1> <file#2> ...
# git add .
$ git commit -m "message"
# git commit -am "message" : add와 commit을 동시에 수행
$ git status
$ git log
```

![](https://velog.velcdn.com/images/songs4805/post/b6edd66e-5e32-4df8-8cf4-74b5290b74cf/image.png)

## .gitignore 파일 추가하기

`.gitignore`

- **설정한 파일들에 대하여 git track 하지 않도록 설정하는 역할**
- 개인 정보와 관련된 파일
- 수시로 변경되는 파일
- 코드와는 관련이 없는 파일
- dummy 파일
- **원격 저장소(github)와 연동할 때 많이 활용함**

## git branch

### 브랜치가 필요한 이유?

- 여러 가지 코드 버전이 생겨날 때
- 지금 버전의 내용을 기억하고 다른 버전으로 코드를 작성할 때
- 고객사(혹은 나라) 별로 규격이나 사양이 다르다면?

### branch의 기능

- master: 깃에서 자동으로 만드는 기본 브랜치
- 분기(branch): master 브랜치에서 새 브랜치를 만듦
- 병합(merge): 새 브랜치에 있던 파일을 master 브랜치에 합침

![](https://velog.velcdn.com/images/songs4805/post/652ffff7-85dc-4ffc-97aa-22d3744197c8/image.png)

### HEAD → master의 의미

![](https://velog.velcdn.com/images/songs4805/post/e1602704-b735-4c32-8e06-9c390e383390/image.png)

**master 브랜치가 가장 최신 커밋을 가리키고 있고, HEAD가 master 브랜치를 가리킴. HEAD는 여러 브랜치 중에서 현재 작업 중인 브랜치를 가리킴.**

```bash
$ git branch
$ git branch <branch name>
$ git checkout <branch name>
$ git branch
```

### branch 병합(merge)하기

- master 브랜치로 체크아웃
- `$ git merge <branch name>`
- 자동 병합이 될 수도 있고, 충돌이 발생할 수도 있다.
- 자동 병합: 다른 파일의 수정, 같은 파일이라도 다른 위치 수정
  - `$ git branch -d <branch name>`
  - 해당 브랜치 삭제하면 끝
- 충돌 발생: 같은 파일의 같은 위치 수정
  - 충돌 문서를 직접 수정하여 충돌을 해결해야 함
  - 수정하였으면 해결한 파일을 스테이지에 올리고 커밋
  - `$ git branch -d <branch name>`

### branch 강제 삭제하기

`$ git branch -D <branch name>`

![](https://velog.velcdn.com/images/songs4805/post/41ad9eed-979f-4ef4-80ee-b076bd3eed87/image.png)

### branch 그래프

`$ git log --oneline --branches --graph`

- `--oneline`: log의 내용을 한 줄로 간략하게 보여줌
- `--branches`: 모든 branch에 대한 log 내용을 보여줌
- `--graph`: 간략한 그래프 형태로 표시해줌

![](https://velog.velcdn.com/images/songs4805/post/320181c0-e8fb-48dd-8fc6-34e700bb0b24/image.png)

- `$ git log <branch#1> .. <branch#2>`: 두 브랜치의 차이를 보여줌

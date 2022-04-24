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
  - [`git checkout <commit>`](#git-checkout-<commit>)
- [.gitignore 파일 추가하기](#.gitignore-파일-추가하기)
- [git branch](#git-branch)
  - [브랜치가 필요한 이유?](#브랜치가-필요한-이유?)
  - [branch의 기능](#branch의-기능)
  - [HEAD → master의 의미](#HEAD--master의-의미)
  - [branch 병합(merge)하기](#branch-병합하기)
  - [branch 강제 삭제하기](#branch-강제-삭제하기)
  - [branch 그래프](#branch-그래프)
- [Git Cycle](#Git-Cycle)
- [git 복귀](#git-복귀)
  - [checkout](#checkout)
  - [reset](#reset)
  - [revert](#revert)
- [git reset 옵션](#git-reset-옵션)
- [git revert](#git-revert)
- [git checkout](#git-checkout)
- [git reflog](#git-reflog)

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

### branch 병합하기

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

## Git Cycle

![](https://velog.velcdn.com/images/songs4805/post/91fed5fd-86ef-48a6-a337-827146e77289/image.png)

- untracked: git에서 파일을 추적하지 않는 상태
- unmodified: git에서 파일을 추적중이며, 수정된 내용이 없는 상태
- modified: git에서 파일을 추적중이며, 수정된 내용이 있는 상태
- staged: git에서 commit에 적용될 파일의 상태

## git 복귀

- 코드를 예전 버전으로 돌리고 싶을 때
- 몇 줄 고치지 않았다면, 굳이 버전을 돌릴 필요는 없음
- 하지만, 굉장히 방대한 양을 코딩했고, 심각한 오류를 발견했을 경우는? → 오류를 고치는 것보다 때론 이전 버전으로 돌리는게 더 좋을 수도 있음

### checkout

- HEAD가 가리키는 commit을 변경함
- 복귀라고 보기는 어려울 것 같다.

### **reset**

- 기록한 commit을 취소하는 형태
- soft, mixed, hard 옵션 존재함

### **revert**

- 기록한 commit을 남겨두고 **취소에 대한 새로운 커밋을 생성**

```bash
$ git checkout

$ git reset
# git reset --soft
# git reset --mixed (default)
# git reset --hard

$ git revert
```

## git reset 옵션

- `$ git reset --soft`

  - 변경 이력은 모두 삭제하지만 변경 내용은 남아 있음
  - **staged 상태**
    > commit 순서에 따른 되돌리기 정도 (3만 되돌리기)
    >
    > 1. 파일 수정
    > 2. 수정된 파일 add
    > 3. **add된 파일들을 commit**

- `$ git reset --mixed`

  - 변경 이력은 모두 삭제하지만 변경 내용은 남아 있음
  - **modified 상태**
    > commit 순서에 따른 되돌리기 정도 (2, 3만 되돌리기)
    >
    > 1. 파일 수정
    > 2. **수정된 파일 add**
    > 3. **add된 파일들을 commit**

- `$ git reset --hard`

  - 변경 이력은 모두 삭제하고, 변경 내용도 삭제함
  - **이전 commit의 unmodified 상태**
    > commit 순서에 따른 되돌리기 정도 (1~3 모두 되돌리기)
    >
    > 1. **파일 수정**
    > 2. **수정된 파일 add**
    > 3. **add된 파일들을 commit**

- 원격 저장소(github)와 연결되어 있을 경우?
  - reset → commit → push → 여러 error 발생 가능
  - `--force` 옵션을 통해 강제로 overwrite 가능

**다른 사람들과 협업하는 공간에선 reset을 할 경우 굉장히 많은 문제들이 발생 가능하다. 원격 저장소를 통한 협업 repo에선 reset을 자제하자!**

| `git reset [옵션] eea5` | working directory      | staging area           | repository                  |
| ----------------------- | ---------------------- | ---------------------- | --------------------------- |
| `--soft`                | 안 바뀜                | 안 바뀜                | **HEAD가 eea5 커밋 가리킴** |
| `--mixed`               | 안 바뀜                | **eea5 커밋처럼 바뀜** | **HEAD가 eea5 커밋 가리킴** |
| `--hard`                | **eea5 커밋처럼 바뀜** | **eea5 커밋처럼 바뀜** | **HEAD가 eea5 커밋 가리킴** |

## git revert

- **특정 커밋을 취소하는 커밋을 생성함**
- **원격 저장소(github)를 활용할 경우에는 reset보단 revert를 활용하자**

```bash
$ git revert HEAD
```

- `--no-edit`: commit 메시지 작성 X
- 지금 HEAD의 커밋을 취소하는 커밋을 생성함

```bash
$ git revert <commit#1>...<commit#2>
```

- `--no-edit`: commit 메시지 작성 X
- <commit#1>에서 ...와 <commit#2>까지를 취소하는 커밋을 생성함

## git checkout

수정사항의 되돌리기가 가능함

```bash
# 특정 <file>의 수정사항을 commit 상태로 되돌리기
$ git checkout <file>

# 모든 파일의 수정사항을 commit 상태로 되돌리기
$ git checkout .

# HEAD가 가리키는 <commit>을 변경함
# 사용자의 시점이 이동한다고 볼 수 있음. 어느 파일도 삭제가 되지 않음
# git log --all 로 확인 가능함
$ git checkout <commit ID>
```

## git reflog

- git의 모든 이력을 다 볼 수 있는 reflog
- `git reset --hard <commit>`도 복구가 가능함
- 삭제한 branch 또한 복구가 가능함
- **거의 모든 상황에서 복구가 가능하지만, 항상 가능한 것은 아니다.**

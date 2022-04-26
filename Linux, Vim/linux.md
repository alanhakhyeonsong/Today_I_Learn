# Linux 명령어 모음

# 기본 명령어

## ls(리스트)

- 현재 디렉터리에 있는 파일과 그 하위 디렉터리들의 이름이 나열
- `$ ls -l` : 파일명뿐만 아니라 접근 권한, 소유자, 그룹, 파일 크기, 최종 수정 날짜도 출력(`ll`과 비슷)
- `$ ls -l /var` : `/var`와 같이 특정 디렉터리를 지정하면 해당 디렉터리의 내용이 출력됨
- `$ ls -lh` : 킬로바이트나 메가바이트 또는 기가바이트 단위처럼 읽기 쉬운 형식으로 출력
- `$ ls -R` : 현재 디렉터리 아래에 어떤 디렉터리가 있는지 확인

## pwd(현재 작업 디렉토리)

- `$ pwd` : 현재 작업 디렉토리를 화면에 출력

## cd(디렉토리 변경)

- 현재 작업 디렉토리를 지정한 디렉토리로 바꿈
- `$ cd ..` : 상위 디렉토리로 이동
- `$ cd /home/<사용자 계정>` : 현재 디렉토리에서 아주 멀리 떨어져 있는 곳으로 가려면 절대 경로를 사용하는 편이 좋다
- 탭(Tab) 키를 잘 활용하자

## cat(파일 내용 출력)

- 연결한다는 뜻의 concatenate에서 유래, 파일 내용을 터미널에 출력함(편집 불가)
- `$ cat /etc/fstab` : etc 디렉터리에 있는 fstab 파일 출력(이처럼 짧은 파일을 볼 때 좋음)
- `$ cat -n <파일명>` : **라인 앞에 줄번호를 같이 출력함**

## less(파일 내용 표시)

- 파일 내용을 조금씩 보여주어 텍스트 내용을 읽기 쉽게 함
- `$ less /etc/services` : less 명령 뒤에 읽으려는 파일의 이름을 지정
- 방향키: e, y(한줄) / d, u(반 페이지) / f, b(한 페이지)

## touch(빈 파일 생성)

- **기존 파일에 touch 명령을 실행하면 파일 내용은 그대로 두고 타임스탬프만 갱신**
- `$ touch -c <파일명>` : <파일명>이 없으면 파일이 생성되지 않는다.
- `$ touch f1 f2 f3` : 여러 파일을 동시에 생성함

## stat(파일의 다양한 정보 확인)

- `$ stat <파일명>` : 파일에 대한 다양한 정보를 확인함
- 접근/변경/수정 시각 정보 등을 알려줌

## mkdir(디렉토리 생성)

- `$ mkdir <디렉토리명>` : 특정 디렉토리명 생성
- 동일한 디렉토리명이 있다면 새로운 디렉토리는 생성하지 못함

## rmdir(디렉토리 삭제)

- `$ rmdir <디렉토리명>` : 특정 디렉토리명 삭제
- 비어있는 디렉토리만 삭제가 가능함
- 디렉토리 안에 빈 디렉토리가 존재하더라도 삭제가 불가능함

## rm(파일/디렉토리 삭제)

- `$ rm <파일명>` : 특정 파일을 삭제함
- `$ rm *.dat` : `.dat` 확장자 파일을 모두 삭제함
- `$ rm *` : 모든 파일을 삭제함
- `$ rm -r <디렉토리명>` : **해당 디렉토리를 삭제함(파일이 들어있어도 삭제 가능)**
- `$ rm -rf <디렉토리명>` : **특정 경고 없이 모두 강제(force)로 삭제함**

## su(Switch User)

- `$ su 계정명` : 다른 사용자의 계정으로 전환하는 명령어(환경변수 유지)
- `$ su - 계정명` : 다른 사용자의 계정으로 변환(환경변수도 변환)

## sudo(SuperUser Do)

- 관리자 권한은 필요할 때 명령 앞에 sudo를 붙임으로써 얻을 수 있음
- ex) `/etc/shadow` 파일은 sudo 권한 없이는 들여다볼 수 없음

```bash
$ cat /etc/shadow
cat: /etc/shadow: Permission denied
$ sudo cat /etc/shadow
[sudo] password for ubuntu:
```

## --help 옵션

- 모든 명령어에는 `--help`가 가능함
- `$ <명령어> --help`

## man(Manual)

- 명령줄에서 man 뒤에 알고 싶은 명령을 입력하면 해당 명령의 맨 페이지(man page)를 보여줌
- `$ man <명령어>`
- 방향키: e, y(한줄) / d, u(반 페이지) / f, b(한 페이지)
- 종료: q

## chmod(change mode)

rwx | rwx | rwx : 7 | 7 | 7  
// (4,2,1)에 해당

- `chmod <파일권한> <파일명>`
- `chmod a+w bash.sh`
- `chmod g-w bash.sh`
- `chmod 755 bash.sh` : rwx | r-x | r-x

![](https://velog.velcdn.com/images/songs4805/post/3ddb9c65-724b-4691-88c5-41499829ae99/image.png)

# 텍스트 관련 명령어

## echo(에코)

- `echo [option] [string]`
- 텍스트나 문자열을 출력함
- `$ echo "Hello World!"` : "Hello World!"를 출력함
- `$ echo -e <string>` : 이스케이프를 해석 가능함

```bash
$ echo -e "Hello\nWorld!"
Hello
World!
```

## head

- **문서 내용의 앞부분만 출력함(기본 10줄)**
- `$ head <파일명>` : 파일명의 앞부분만 출력함
- `$ head -n [NUM] <파일명>` : 파일명의 앞부분 [NUM] 줄까지 출력함
- `$ head -c [NUM] <파일명>` : 파일명의 앞부분 [NUM] 바이트까지 출력함

## tail

- **문서 내용의 뒷부분만 출력함(기본 10줄)**
- `$ tail <파일명>` : 파일명의 뒷부분만 출력함
- `$ tail -n [NUM] <파일명>` : 파일명의 뒷부분 [NUM] 줄까지 출력함
- `$ tail -c [NUM] <파일명>` : 파일명의 뒷부분 [NUM] 바이트까지 출력함
- **`$ tail -f <파일명>` : 추가되는 내용을 대기하면서 모니터링함. append 되는 내용을 계속해서 출력함**

## > & >> (부등호 표시, write/overwrite & append)

- `>` : 명령어 뒤에 나오는 파일에 write or overwrite
- `>>` : 명령어 뒤에 나오는 파일에 append
- `$ echo "Hello World!" > output.txt`
- `$ echo "Hello World!" >> output.txt`
- `1>` : 표준 출력, `2>` : 표준 에러 출력
- `1>>` : 표준 출력(append), `2>>` : 표준 에러 출력(append)

## ; (세미콜론)

- 명령어 2개를 한 줄에 연속적으로 입력할 경우에 활용
- `$ mkdir hello ; mkdir hello2` : hello와 hello2 디렉토리를 생성함

## | (pipe, 파이프)

- 여러 개의 명령어가 파이프라인을 구성하여 실행되어짐
- 이전 명령어의 출력이 다음 명령어의 입력 값으로 넣는 형태로 진행함
- `$ cat --help | head`
- 파이프의 개수 제한은 없음

## wc (word count)

- `line/word/byte count` 출력함
- `$ wc <파일명>`
- `$ wc -l <파일명>` : 라인수만 출력함
- `$ cat <파일명> | wc -l` : 라인수만 출력함
- `$ wc -l *.py` : 여러 파이썬 파일들의 라인수를 출력함

## nl (number line)

- 파일 내용을 넘버와 함께 출력함
- `cat -n`과 거의 동일함 (공백에 따른 차이가 있을 수 있음)
- `$ nl <파일명>`
- `$ nl -v [num] <파일명>` : 특정 [num]부터 행번호가 시작함

## sort (소트)

- 내용을 정렬한 뒤에 출력함. 파이프와 주로 함께 쓰임
- `$ sort <파일명>`
- `$ ls -l | sort`
- `$ -n` : --numeric-sort
- `$ -k` : key에 의한 정렬 수행
- `$ -r` : reverse

## uniq (유니크)

- 중복된 내용은 제거하고 출력함
- `$ -d` : 중복된 내용만 출력
- `$ -u` : 중복되지 않은 내용만 출력
- `$ -i` : 대소문자를 무시하고 중복된 내용을 제거함
- `$ sort <파일명> | nl`
- `$ sort <파일명> | uniq | nl`
- `$ sort <파일명> | uniq -i | nl`
- `$ sort <파일명> | uniq -d | nl`
- `$ sort <파일명> | uniq -u | nl`

# 검색, 비교, 처리 명령어

## cut

- 특정 컬럼의 내용을 잘라냄
- `$ ... | cut -b` : byte 만큼 잘라냄
- `$ ... | cut -f` : 필드 선택
- `$ ... | cut -d` : tab을 대신할 delimiter 지정
- `$ ... | cut --output-delimiter` : 출력으로 사용할 delimiter 지정
- `$ head /etc/passwd | cut -d ':' -f 1-3` : ':'을 기준으로 1~3 번째
- `$ head /etc/passwd | cut -d ':' -f 1-3 --output-delimiter='='` : 딜리미터 바꾸기
- `$ wc -l *.py | cut -b 2-5`

## grep

- **파일 안에 있는 특정 문자열을 검색**
- `$ grep <검색 문자열> <파일명>`
- `$ -n` : 줄 번호 함께 표시
- `$ -r` : 서브 디렉토리 안에 있는 파일도 검색함
- `$ grep cuda *.py`
- `$ grep cuda . -r`
- `$ ls -help | grep FILE`
- `$ grep a.*z *.py` : a로 시작해서 z로 끝나는 문자 검색

## find

- **특정 조건에 맞는 파일을 검색함**
- `$ find . -name "*.py"` : py인 파일을 작업 디렉토리 기준으로 검색
- `$ find . | grep <str>` : **`<str>`이 들어가 있는 파일을 검색**
- `$ find . -empty` : **빈 디렉토리 혹은 빈 파일 검색**
- `$ find . -size -N (or +N)` : 사이즈로 검색함 (이하/이상)  
  `$ find . -size -1000c` : 1000바이트 이하를 검색함
- `$ find . -regex ".*test.*.py$"` : test가 들어가는 파이썬 파일 검색
- 액션(Action)
  - `$ <...> -ls` : 파일 정보를 같이 표시함
  - `$ <...> -delete` : 찾은 파일들 삭제함
  - `$ <...> -exec command {} \;` : 주어진 명령 수행

## diff

- 두 파일의 차이점을 비교함
- `$ diff <file1> <file2> ` : 두 파일의 차이를 비교함
- `$ -r` : 서브 디렉토리까지 비교함
- `$ -q` : **파일 불일치 여부만을 표시**
- `$ -u` : **통합 형식으로 출력함**
- `$ -w` : 모든 공백 문자 차이를 무시함
- `$ -B` : 빈 줄의 개수 차이를 무시함

## sed

- 스트림 편집을 위하여 사용 (stream editor)
- `cat <file> | sed -n '2,5p'` : 2번째 줄에서 5번째 줄을 출력함
- `cat <file> | sed '3,6d'` : 3번째 줄에서 6번째 줄을 제외하고 출력함
- `cat <file> | sed -N '/import/p'` : import가 들어가는 줄을 출력함
- `cat <file> | sed 's/import/IMPORT/g'` : **import를 IMPORT로 변경하고 출력함**
- `cat <file> | sed '/import/,+3p'` : import가 들어가 있는 줄부터 3줄을 더 출력함

## awk

- Aho + Weinberger + Kernighan → AWK
- 레코드 기반으로 데이터 처리 명령어
- `$ cat <file> | awk '/import/{print}'`
- `$ cat <file> | awk '{ print $1, $2 }'`
- `$ cat <file> | awk '{ print NR, $1, $2 }'`
- `$ cat <file> | awk '{ print NR, ":", $1, $2 }'`
- `$ cat <file> | awk '/import/ { print NR, ":", $1, $2 }'` : 형태 바꿈
- `$ cat <file> | awk -F ":" '/import/ { print NR, ":", $1, $2 }'`

![](https://velog.velcdn.com/images/songs4805/post/fea28db4-48dd-4f16-8d62-67c62b900240/image.png)

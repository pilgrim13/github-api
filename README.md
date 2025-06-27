# GitHub 분석 서비스 API

특정 GitHub 사용자, 저장소, 조직의 활동 내역을 분석하고 요약된 정보를 제공하는 RESTful API 서비스

## ✨ 주요 Endpoint

1.  **사용자 프로필 분석 API**: 특정 사용자의 기본 정보와 함께, 해당 사용자가 소유한 모든 공개 저장소의 프로그래밍 언어 사용 분포를 계산하여 제공
2.  **저장소 활동 분석 API**: 특정 저장소의 기본 정보, 이슈 및 Pull Request 통계(평균 처리 시간, 상위 기여자 등)를 요약하여 제공
3.  **인기 저장소 조회 API**: 특정 조직(Organization)이 소유한 공개 저장소들을 스타(Star) 개수 순으로 정렬하여 상위 N개의 목록을 제공

## 🛠️ 기술 스택

* **언어**: Java 17
* **프레임워크**: Spring Boot 3.2.5
* **의존성**:
    * Spring Web
    * Spring Cloud OpenFeign
    * Lombok
    * Springdoc-openapi-ui (Swagger)
* **빌드 도구**: Gradle 8.10
* **CI/CD**: GitHub Actions, Docker, Docker Compose

## 🚀 로컬 환경 설정 및 실행

### 사전 준비

1.  **Java 17**
2.  **GitHub 개인용 액세스 토큰 (PAT) 생성 및 설정**: GitHub API 요청 인증을 위해 PAT가 필요합니다.

    **a. 토큰 발급 받기**
    * [GitHub 토큰 발급 페이지(Fine-grained tokens)](https://github.com/settings/tokens?type=beta)로 이동하여 `Generate new token` 버튼을 클릭합니다.
    * **Repository access** 항목에서 **`All repositories`** 를 선택합니다.
    * **Permissions** 항목 > **Repository permissions** 에서 아래 3가지 권한의 Access를 **`Read-only`** 로 설정합니다: `Contents`, `Issues`, `Pull requests`
    * `Generate token` 버튼을 눌러 토큰을 생성하고, 생성된 값을 즉시 복사해 둡니다.

    **b. 설정 파일 생성**
    * 프로젝트의 `src/main/resources/` 디렉토리 안에 `application-secret.yml` 파일을 새로 만듭니다.
    * 아래 내용을 파일에 작성하고, 자신의 토큰 값으로 교체합니다.

    ```yaml
    github:
      token: "ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" # 여기에 발급받은 GitHub PAT를 붙여넣으세요
    ```
    *이 파일은 `.gitignore`에 등록되어 있어 Git 저장소에 포함되지 않습니다.*

### 설치 및 실행

#### 방법 1: IntelliJ IDEA 사용 (권장)

1.  **프로젝트 열기**: IntelliJ에서 `File > Open`을 통해 프로젝트 폴더를 엽니다.
2.  **프로젝트 빌드**: 우측 `Gradle` 탭에서 `Tasks > build > build`를 더블 클릭하여 프로젝트를 빌드합니다.
3.  **애플리케이션 실행**: `src/main/java` 아래의 메인 애플리케이션 파일(`GithubAPIApplication.java`)을 열고, `main` 메소드 옆의 녹색 '실행' 아이콘(▶)을 클릭하여 실행합니다.

---

## 🔁 CI/CD 파이프라인

이 프로젝트는 **GitHub Actions**와 **Docker**를 사용하여 CI/CD 파이프라인을 구축했습니다.

### 파이프라인 흐름

1.  **트리거**: `master` 브랜치에 코드가 `push`되거나 `pull request`가 생성되면 파이프라인이 자동으로 실행됩니다. 또한, Actions 탭에서 수동으로 실행할 수도 있습니다.
2.  **실행 환경**: 과제 요구사항에 따라, 미리 구성된 **Self-hosted runner**에서 모든 작업이 실행됩니다.
3.  **작업 순서**:
    * 소스 코드를 체크아웃합니다.
    * `./gradlew build` 명령어로 애플리케이션을 빌드하고 모든 단위 테스트를 실행합니다.
    * `Dockerfile`을 사용하여 애플리케이션을 **Docker 이미지**로 빌드합니다.
    * `docker compose up -d` 명령어로 최신 이미지를 사용하여 컨테이너를 **자동으로 배포**합니다.
4.  **민감 정보 관리**: `DOCKERHUB_TOKEN`, `GH_PAT`와 같은 민감 정보는 **GitHub Secrets**를 통해 안전하게 관리되며, 워크플로우 실행 시에만 주입됩니다.

---

## 📖 API 사용 방법

애플리케이션이 실행 중일 때, 아래 주소로 접속하면 Swagger UI를 통해 모든 API의 명세를 확인하고 직접 테스트해 볼 수 있습니다.

* **Swagger UI 주소**: `http://localhost:8080/swagger-ui.html`

### 엔드포인트 목록

| Method | URL | 설명 |
| :--- | :--- | :--- |
| `GET` | `/api/users/{username}/profile-summary` | 사용자 프로필 요약 조회 |
| `GET` | `/api/repos/{owner}/{repo}/summary` | 저장소 활동 요약 조회 |
| `GET` | `/api/popular-repo` | 조직의 인기 저장소 목록 조회 |

## ❗ 에러 처리

이 API는 발생 가능한 오류 상황에 대해 다음과 같은 표준 에러 코드를 반환합니다.

| 상태 코드 | 에러 코드 (내부) | 설명 |
| :---: | :--- | :--- |
| `400` | `common.01` | 잘못된 타입의 파라미터가 입력되었을 때 |
| `403` | `common.03` | GitHub API 요청 횟수 제한을 초과했을 때 |
| `403` | `common.04` | 해당 리소스에 접근할 권한이 없을 때 |
| `404` | `user.01` | 존재하지 않는 사용자를 조회했을 때 |
| `404` | `repo.01` | 존재하지 않는 저장소를 조회했을 때 |
| `404` | `org.01` | 존재하지 않는 조직을 조회했을 때 |
| `503` | `external.01` | 외부 API(GitHub) 서버가 불안정할 때 |
| `500` | `common.99` | 위에서 처리하지 못한 모든 서버 내부 오류 |

# 빌드 단계
# Base Image Gradle 8.5.0/JDK 17
FROM gradle:8.5.0-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /build

# build.gradle 파일만 먼저 복사하여 의존성을 캐싱
COPY build.gradle settings.gradle ./
# 의존성 다운로드 (소스 코드가 바뀌지 않으면 이 단계는 캐시를 사용해 빠르게 넘어감)
RUN gradle build --no-daemon || return 0

# 전체 소스 코드 복사
COPY . .

# 애플리케이션 빌드 (테스트는 생략하여 빌드 속도 향상)
RUN gradle build -x test --no-daemon

# 실행 단계
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# GitHub PAT를 환경 변수로 주입받을 수 있도록 ARG 선언
ARG GITHUB_TOKEN

# ARG로 받은 값을 실제 환경 변수로 설정
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

# 빌드 단계(builder)에서 생성된 JAR 파일을 복사
COPY --from=builder /build/build/libs/*.jar ./app.jar

# 애플리케이션 실행 명령어
# java -jar -Dspring.profiles.active=secret app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=secret", "app.jar"]
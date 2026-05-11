# Safepetit CI/CD 배포 가이드

## GitHub Secrets

GitHub 저장소의 `Settings > Secrets and variables > Actions`에 다음 값 등록 필요.

| 이름 | 설명 |
| --- | --- |
| `DOCKERHUB_USERNAME` | Docker Hub 계정명 |
| `DOCKERHUB_TOKEN` | Docker Hub Access Token |
| `EC2_HOST` | EC2 Public IP 또는 도메인 |
| `EC2_USERNAME` | EC2 SSH 사용자명 |
| `EC2_SSH_KEY` | EC2 접속용 private key 전문 |

선택 값.

| 이름 | 기본값 | 설명 |
| --- | --- | --- |
| `EC2_SSH_PORT` | `22` | SSH 포트 |
| `APP_PORT` | `8080` | EC2 외부 노출 포트 |
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring 활성 프로필 |

## EC2 사전 준비

EC2에 Docker 및 Docker Compose Plugin 설치 필요.

```bash
sudo yum update -y
sudo yum install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
```

Amazon Linux 2023 기준 Docker Compose Plugin 설치 필요.

```bash
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/download/v2.27.1/docker-compose-linux-x86_64 -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
docker compose version
```

권한 반영을 위한 SSH 재접속 필요.

## 배포 흐름

1. `main` 브랜치 push 발생.
2. GitHub Actions에서 JDK 21 설정.
3. `./gradlew clean build` 실행.
4. Docker 이미지 빌드 및 Docker Hub push.
5. EC2에 `docker-compose.yml` 복사.
6. EC2에서 최신 이미지 pull.
7. 기존 컨테이너 종료 후 백그라운드 재실행.
8. 미사용 Docker 이미지 prune 수행.

## 배포 확인

EC2 접속 후 다음 명령으로 상태 확인 가능.

```bash
cd ~/safepetit
docker compose ps
docker compose logs -f app
```

애플리케이션 기본 접근 주소.

```text
http://<EC2_PUBLIC_IP>:8080
```

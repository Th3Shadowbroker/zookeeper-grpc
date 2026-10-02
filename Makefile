all: proto jar docker

proto:
	rm -rf client-go
	mkdir -p client-go
	(cd client-go && go mod init github.com/telekom/zookeeper-grpc)
	protoc --go_out=client-go --go-grpc_out=client-go src/main/proto/*.proto
	(cd client-go && go mod tidy)

jar:
	./gradlew bootJar

docker:
	docker build --tag zookeeper-grpc .

up:
	docker compose up -d

down:
	docker compose down
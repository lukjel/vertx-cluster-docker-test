mvn package && docker build -t vertx-docker-test .

docker kill $(docker ps -q -a)
docker rm $(docker ps -q -a)

docker run -d vertx-docker-test
docker run -d vertx-docker-test
docker run -d vertx-docker-test

#docker service create --replicas 3 --name vertx-docker-test vertx-docker-test
#docker service update --image vertx-docker-test:latest vertx-docker-test

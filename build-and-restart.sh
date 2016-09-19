mvn package && docker build -t vertx-docker-test .

docker kill $(docker ps -q -a)
docker rm $(docker ps -q -a)

docker run -d --name t1 vertx-docker-test
sleep 1s
docker run -d --name t2 vertx-docker-test
sleep 1s
docker run -d --name t3 vertx-docker-test
sleep 1s
docker run -d --name t4 vertx-docker-test
sleep 1s

#docker service create --replicas 3 --name vertx-docker-test vertx-docker-test
#docker service update --image vertx-docker-test:latest vertx-docker-test

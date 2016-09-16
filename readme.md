#Description

Simple project for testing Docker and Vertex with Cluster.

#Requirements

I tested this on Google Cloud but it should work "everywhere".
I used OSX with docker  1.12.1-beta26.1 (build: 12100).
You need:
- docker localy
- mavenlocaly
- java localy
- for GoogleCloud - gcloud installed and initilaized/configured

#Setup

1. Provisioning test machine and base setup (docker): 
``docker-machine create --driver google --google-project ENTER-YOUR-PROJECT-ID --google-machine-type n1-highcpu-4 vm1``

2. Connecting to remote docker
``eval $(docker-machine env vm1)``

3. Build, make images, run (also kill old ones when needed)
``./build-and-restart.sh``

4. You can check:
``docker stats`` shows "live" info about images
run ``docker network inspect bridge`` and find ip of any node. Should be in containers... In my case it is 172.17.0.3 (4 and 5). 
``docker ps`` shows running images
``docker logs IMAGE-ID`` shows console log of given image. 

5. In second terminal: connect to created machine:
``gcloud compute ssh vm1``

6. Run there (maybe you have to change IP):
``curl http://172.17.03:8080/OK``
sohuld response: OK

7. Then try:
``curl http://172.17.0.3:8080/ping``
and wait for result. It is wise to run it few times (at least 5) - on the beginning results are changing... 

Try to deploy more / less containers and retest - check it out how fast is eventBus...

#Problems

Whe you kill a image / node - it takes huge time (minutes) to reorganize cluster. Till this moment the test is not working. **Terrible! Has to be fixed!**

Swarm Mode... it looks greate (from the lectures) but I can not run it. Mostly because of no support for multicast in swarm network. **In progress** 

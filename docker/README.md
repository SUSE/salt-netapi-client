# SaltStack Dockerfiles

## Introduction

This repository contains two **Dockerfile**s of [*SaltStack*](https://http://saltstack.com) for [Docker](https://www.docker.com/)'s automated build published to the public [Docker Hub Registry](https://registry.hub.docker.com/).

In particular, this repository contains two Docker images:

* [**saltstack-master**](https://registry.hub.docker.com/u/mbologna/saltstack-master): a SaltStack master container image with netapi module (cherrypy) enabled. 
This container works with `supervisord` to automatically launch `salt-master` and `salt-api` daemons.  
* [**saltstack-minion**](https://registry.hub.docker.com/u/mbologna/saltstack-minion): a SaltStack minion container image.

Both of the images *expect* that you declare a volume in which you pass config files and keys (see `etc_*` directories and usage).
In this way, the two images are standard and can be customized by specifing a volume with config files.

## Base Docker image

* ubuntu/14.04

## Installation

1. Install [Docker](https://www.docker.com/).

2. Download [SaltStack master automated build](https://registry.hub.docker.com/u/mbologna/saltstack-master) from public [Docker Hub Registry](https://registry.hub.docker.com/): `docker pull mbologna/saltstack-master`

   (alternatively, you can build an image from Dockerfile: `docker build -t="mbologna/saltstack-master" github.com/mbologna/saltstack-master`)
   
3. Download [SaltStack minion automated build](https://registry.hub.docker.com/u/mbologna/saltstack-minion) from public [Docker Hub Registry](https://registry.hub.docker.com/): `docker pull mbologna/saltstack-master`

   (alternatively, you can build an image from Dockerfile: `docker build -t="mbologna/saltstack-minion" github.com/mbologna/saltstack-minion`)

## Usage

### run saltstack-master

	% docker run -d --name saltmaster \
	-v `pwd`/etc_master/salt:/etc/salt \
	-p 8000:8000 \
	-ti mbologna/saltstack-master

### run saltstack-minion1

	% docker run -d --name saltminion1 \
	--link saltmaster \
	-v `pwd`/etc_minion1/salt:/etc/salt mbologna/saltstack-minion

### run saltstack-minion2

	% docker run -d --name saltminion2 \
	--link saltmaster \ 
	-v `pwd`/etc_minion2/salt:/etc/salt mbologna/saltstack-minion

### have fun!

Now you can access `saltstack-master` either via NetAPI:
 

	% curl -sS http://localhost:8000/login \
              -c ~/cookies.txt \
              -H 'Accept: application/json' \
              -d username=saltdev \
              -d password=saltdev \
              -d eauth=pam
	{"return": [{"perms": [".*"], "start": 1446379166.406894, "token": "4072d45939ad1a33ffbe0565ec7d15d0cf2e24c2", "expire": 1446422366.406895, "user": "saltdev", "eauth": "pam"}]}%
	% curl -sS http://localhost:8000 \
              -b ~/cookies.txt \
              -H 'Accept: application/json' \
              -d client=local \
              -d tgt='*' \
              -d fun=test.ping
	{"return": [{"minion1": true, "minion2": true}]}%
 	

or via command line:

	% docker exec saltmaster /bin/sh -c "salt '*' test.ping"                                                                                                                                                    
	minion1:
	    True
	minion2:
	    True

### (optional) configure additional minions
 
I'm sure you grasp the main concept now:

1. Generate keys and certs and place them in `/etc/salt`
2. Launch a saltstack-minion mapping `/etc/salt/` the correct volume and link the container to `saltmaster` container
3. Remember to accept minion keys on saltstack-master
4. Conquer the world

## Caveats and security

1. `saltstack-master` exposes port 8000/tcp (**NO SSL**) in order to consume `salt-api` via its HTTP interface. 
**WARNING**: your credentials travel in plain-text.

2. Please note that the provided keys and certs are intended to use **ONLY** in test environments. **DO NOT** use them in production.
 
3. I couldn't get `saltstack-master` to work with EAUTO auth module. 
Workaround: I had to manually add a `saltdev` user (password: `saltdev`) to the container (see `Dockerfile-master`) and use PAM authentication. Again, use these containers only in test environments.

## Credits

Credits goes to [@UtahDave](https://github.com/UtahDave/salt-vagrant-demo/): I copied certs and keys from its `salt-vagrant-demo` repository.

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

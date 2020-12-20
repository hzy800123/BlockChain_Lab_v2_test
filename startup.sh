#!/bin/bash
echo "4.Startup CA Services, Peers and Orderer in Network"
COMPOSE_FILE_CA=docker/docker-compose-ca.yaml
COMPOSE_FILE_BASE=docker/docker-compose-ABC.yaml
COMPOSE_FILE_COUCH=docker/docker-compose-couch.yaml
CA_IMAGE_TAG=${CA_VERSION} IMAGE_TAG=${FABRIC_VERSION} BASE_TAG=${OTHER_VERSION} DB_IMAGE_TAG=${OTHER_VERSION} docker-compose -f ${COMPOSE_FILE_CA} -f ${COMPOSE_FILE_BASE} -f ${COMPOSE_FILE_COUCH} up -d
echo

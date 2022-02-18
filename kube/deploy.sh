#!/bin/bash
set -euo pipefail

export KUBE_NAMESPACE=${ENVIRONMENT}
export KUBE_SERVER=${KUBE_SERVER}
export KUBE_TOKEN=${KUBE_TOKEN}
export VERSION=${VERSION}

export MIN_REPLICAS="1"
export MAX_REPLICAS="1"

if [[ ${KUBE_NAMESPACE} == *prod ]]
then
    export UPTIME_PERIOD="Mon-Sun 05:00-23:00 Europe/London"
    export CLUSTER_NAME="acp-prod"
else
    export UPTIME_PERIOD="Mon-Fri 08:00-18:00 Europe/London"
    export CLUSTER_NAME="acp-notprod"
fi

export KUBE_CERTIFICATE_AUTHORITY="https://raw.githubusercontent.com/UKHomeOffice/acp-ca/master/${CLUSTER_NAME}.crt"

cd kd

kd \
   --timeout 10m \
    -f deployment.yaml \
    -f service.yaml \
    -f autoscale.yaml

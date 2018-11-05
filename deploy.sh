#!/bin/bash

export KUBE_NAMESPACE=${KUBE_NAMESPACE}
export KUBE_SERVER=${KUBE_SERVER}

if [[ -z ${VERSION} ]] ; then
    export VERSION=${IMAGE_VERSION}
fi

if [[ ${ENVIRONMENT} == "prod" ]] ; then
    echo "deploy ${VERSION} to PROD namespace, using HOCS_CASEWORK_PROD drone secret"
    export KUBE_TOKEN=${HOCS_CASEWORK_PROD}
    export REPLICAS="2"
else
    if [[ ${ENVIRONMENT} == "qa" ]] ; then
        echo "deploy ${VERSION} to QA namespace, using HOCS_CASEWORK_QA drone secret"
        export KUBE_TOKEN=${HOCS_CASEWORK_QA}
        export REPLICAS="2"
    elif [[ ${ENVIRONMENT} == "demo" ]] ; then
        echo "deploy ${VERSION} to DEMO namespace, using HOCS_CASEWORK_DEMO drone secret"
        export KUBE_TOKEN=${HOCS_CASEWORK_DEMO}
        export REPLICAS="1"
    elif [[ ${ENVIRONMENT} == "dev" ]] ; then
        echo "deploy ${VERSION} to DEV namespace, using HOCS_CASEWORK_DEV drone secret"
        export KUBE_TOKEN=${HOCS_CASEWORK_DEV}
        export REPLICAS="1"        
    else
        echo "Unable to find environment: ${ENVIRONMENT}"
    fi
fi

if [[ -z ${KUBE_TOKEN} ]] ; then
    echo "Failed to find a value for KUBE_TOKEN - exiting"
    exit -1
fi

cd kd

kd --insecure-skip-tls-verify \
   --timeout 10m \
    -f deployment.yaml \
    -f service.yaml

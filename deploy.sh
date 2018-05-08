#!/bin/bash

export KUBE_NAMESPACE=${KUBE_NAMESPACE}
export KUBE_SERVER=${KUBE_SERVER}

if [[ -z ${VERSION} ]] ; then
    export VERSION=${IMAGE_VERSION}
fi

if [[ ${ENVIRONMENT} == "pr" ]] ; then
    echo "deploy ${VERSION} to pr namespace, using hocs-casework_pr drone secret"
    export KUBE_TOKEN=${hocs-casework_pr}
else
    if [[ ${ENVIRONMENT} == "test" ]] ; then
        echo "deploy ${VERSION} to test namespace, using hocs-casework_qa drone secret"
        export KUBE_TOKEN=${hocs-casework_qa}
    else
        echo "deploy ${VERSION} to dev namespace, using hocs-casework_dev drone secret"
        export KUBE_TOKEN=${hocs-casework_dev}
    fi
fi

if [[ -z ${KUBE_TOKEN} ]] ; then
    echo "Failed to find a value for KUBE_TOKEN - exiting"
    exit -1
fi

cd kd

kd --insecure-skip-tls-verify \
    -f networkPolicy.yaml \
    -f deployment.yaml \
    -f service.yaml
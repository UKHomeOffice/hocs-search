#!/bin/bash
set -euo

export AWS_ACCESS_KEY_ID=UNSET
export AWS_SECRET_ACCESS_KEY=UNSET
export AWS_DEFAULT_REGION=eu-west-2

SCRIPT_LOCATION=$(dirname "$0")
ELASTIC_MAPPING_PATH="${SCRIPT_LOCATION}/elastic_mapping.json"

if ! [[ -f "${ELASTIC_MAPPING_PATH}" ]]
then
    echo "${ELASTIC_MAPPING_PATH} was not found."
    exit 1
fi

## make sure that localstack is running in the pipeline
until curl http://localstack:4566/health --silent | grep -q "running"; do
   sleep 5
   echo "Waiting for LocalStack to be ready..."
done

until curl http://localstack:4571 --silent | grep -q "elasticsearch"; do
   sleep 10
   echo "Waiting for ElasticSearch to be ready..."
done

curl -X PUT http://localstack:4571/local-case --silent -H "Content-Type: application/json" -d "@${ELASTIC_MAPPING_PATH}"

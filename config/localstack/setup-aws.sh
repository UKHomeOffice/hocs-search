#!/bin/bash
set -euo

export AWS_ACCESS_KEY_ID=UNSET
export AWS_SECRET_ACCESS_KEY=UNSET
export AWS_DEFAULT_REGION=eu-west-2

## make sure that localstack is running in the pipeline
until curl http://localstack:4566/health --silent | grep -q "running"; do
   sleep 5
   echo "Waiting for LocalStack to be ready..."
done

aws --endpoint-url=http://localstack:4578 es create-elasticsearch-domain --domain-name decs --elasticsearch-version 6.7

until curl http://localstack:4571 --silent | grep -q "elasticsearch"; do
   sleep 10
   echo "Waiting for ES 6.7.0 to be ready..."
done

curl -X PUT http://localstack:4571/local-case --silent -H "Content-Type: application/json" -d @/docker-entrypoint-initaws.d/elastic_mapping.json

aws --endpoint-url=http://localstack:4566 sqs create-queue --queue-name search-queue-dlq
aws --endpoint-url=http://localstack:4566 sqs create-queue --queue-name search-queue --attributes '{"RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:search-queue-dlq\",\"maxReceiveCount\":1}"}'

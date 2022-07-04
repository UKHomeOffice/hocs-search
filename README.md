# hocs-search

[![CodeQL](https://github.com/UKHomeOffice/hocs-search/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/UKHomeOffice/hocs-search/actions/workflows/codeql-analysis.yml)

This is the Home Office Correspondence Systems (HOCS) search service. The service serves the search functionality within HOCS through the use of `elasticsearch`. 

## Getting Started


### Prerequisites

* ```Java 17```
* ```Docker```
* ```LocalStack```

### Preparation

In order to run the service locally, LocalStack is required. We have provided an [docker-compose.yml](docker-compose.yml) file to support this. 

To start LocalStack through Docker, run the following command from the root of the project:

```shell
docker-compose up
```

This brings up the LocalStack docker image and creates the necessary AWS resources to run the project. This is done through mounting the [localstack configuration folder](config/localstack) into the docker image.

This configuration folder contains 3 shell scripts that each handle a seperate part of the AWS creation.

1. [1-setup-queues.sh](config/localstack/1-setup-queue.sh)  
This creates both the `search-queue` and `search-queue-dlq` used within the service and adds the required association between them. The dead-letter queue currently specified a `maxReceiveCount` of 2 that mimics the production values.
2. [2-setup-elastic.sh](config/localstack/2-setup-elastic.sh)  
Since [Localstack 0.11.1](https://newreleases.io/project/github/localstack/localstack/release/v0.11.1) non main-line elasticsearch indexes are lazily loaded, this handles the creation.
3. [3-setup-index.sh](config/localstack/3-setup-index.sh)  
This creates the index from the associated [elastic index mapping](/config/localstack/elastic_mapping.json). This script requires that the elasticsearch domain exists. If you are receiving an error when running this, please run script 2 first.
To run this on OSX `coreutils` is required which can be installed using brew `brew install coreutils`.

At present our elastic index mapping only supports version `7.X.X` of elasticsearch.

To stop the running containers, run the following:

```shell
docker-compose down
```

## Using the Service

### Versioning

For versioning this project uses SemVer.

### Authors

This project is authored by the Home Office.

### License

This project is licensed under the MIT license. For details please see License

This project contains public sector information licensed under the Open Government Licence v3.0. (http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/)

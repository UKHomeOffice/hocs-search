# hocs-search

[![CodeQL](https://github.com/UKHomeOffice/hocs-search/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/UKHomeOffice/hocs-search/actions/workflows/codeql-analysis.yml)

This is the Home Office Correspondence Systems (HOCS) search service. The service serves the search functionality within HOCS through the use of `elasticsearch`. 

## Getting Started

### Prerequisites

* ```Java 17```
* ```Docker```
* ```LocalStack```

### Submodules

This project contains a 'ci' submodule with a docker-compose and infrastructure scripts in it.
Most modern IDEs will handle pulling this automatically for you, but if not

```console
$ git submodule update --init --recursive
```

## Docker Compose

This repository contains a [Docker Compose](https://docs.docker.com/compose/)
file.

### Start localstack (sqs, sns, s3, es)
From the project root run:
```console
$ docker-compose -f ./ci/docker-compose.yml -f ./ci/docker-compose.elastic.yml up -d localstack 
```

>With Docker using 4 GB of memory, this takes approximately 5 minutes to startup.

### Stop the services
From the project root run:
```console
$ docker-compose -f ./ci/docker-compose.yml stop
```
> This will retain data in the local database and other volumes.

### Preparation

In order to run the service locally, LocalStack is required. 
We have provided a docker-compose file to support this. 

View the readme in [hocs repo](https://github.com/UKHomeOffice/hocs/blob/main/README.md) for more details.

## Using the Service

### Versioning

For versioning this project uses SemVer.

### Authors

This project is authored by the Home Office.

### License

This project is licensed under the MIT license. For details please see License

This project contains public sector information licensed under the Open Government Licence v3.0. (http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/)

# Contributing to the ScanR application

### Table of Contents

* [How to Contribute](#how-to-contribute)
  * [Submit a Pull Request](#submit-a-pull-request)
  * [Contribution guidelines](#contribution-guidelines)
    * [General guidelines for contribution](#general-guidelines-for-contribution)
    * [Source Code Style](#source-code-style)
* [Technical environment](#technical-environment)
* [Build from source](#build-from-source)
  * [Prerequisite](#prerequisite)
  * [Step by step](#step-by-step)
  * [With Docker](#with-docker)
* [Running Unit Tests](#running-unit-tests)
* [To go further](#to-go-further)

## How to Contribute

### Submit a Pull Request

If you have improvements to ScanR, send us your pull requests! For those
just getting started, Github has a [howto](https://help.github.com/articles/using-pull-requests/).
ScanR team members will be assigned to review your pull requests. Once the pull requests are approved, we will merge the pull requests.

Before sending your pull requests, make sure you followed this list.

- Read [contributing guidelines](CONTRIBUTING.md).
- Check if my changes are consistent with the guidelines.
- Changes are consistent with the Coding Style
- Run Unit Tests

### Contribution guidelines

Before sending your pull request, make sure your changes are consistent with the guidelines and follow the ScanR coding style.

##### General guidelines for contribution

* Include unit tests when you contribute new features, as they help to
  a) prove that your code works correctly, and b) guard against future breaking
  changes to lower the maintenance cost.
* Bug fixes also generally require unit tests, because the presence of bugs
  usually indicates insufficient test coverage.
* Keep API compatibility in mind when you change code in ScanR. Reviewers of your
  pull request will comment on any API compatibility issues.
* When you contribute a new feature to ScanR, the maintenance burden is transferred to the ScanR team. This means that benefit of the contribution must be compared against the cost of maintaining the feature.

##### Source code style

The code style of ScanR application is based on the Google Code Style references
* [Google Python Style Guide](https://github.com/google/styleguide/blob/gh-pages/pyguide.md)
* [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
* [Google JavaScript Style Guide](https://google.github.io/styleguide/jsguide.html)
* [Google Shell Style Guide](https://google.github.io/styleguide/shell.xml)

## Technical environment

### Programming languages

ScanR uses the following languages
- Java 8 for the data persistance, the workflow orchestor and API provider
- Python 3.5.3 for the most of plug-in, especially for the website crawler, ... A framework has been developed for this usage
- JavaScript for the frontend application

### Frameworks

For each language, the main frameworks used are

| Language        |   Framworks    |
| -------------   | -------------  |
|  **Typescript (javascript)**  | angularjs, angular-material, d3js
|  **Python**                   | panda, scipy, numpy, pyquery
|  **Java**                     | spring core, data, mvc, security, messaging

### Databases

ScanR uses the following databases
- [Cassandra](http://cassandra.apache.org/) (version 2.x) for raw crawl data
- [Mongodb](https://www.mongodb.com/) (version 3.0.9) for structured data
- [Elasticsearch](https://www.elastic.co/) (version 6.5.4) for the search engine

## Build from source

### Prerequisite

- Linux environment
- 16Go RAM
- 400Go of disk size  

### Step by step

* Install java, maven, python, npm
* Install Cassandra, Mongodb, Elasticsearch and RabbitMQ (version 3.6.1)
* Get ScanR sources using Git : `git clone git@github.com:MinistereSupRecherche/scanR.git`
* Compile java modules with maven

``` bash
# First, install the workflow module
cd java/companies-queue
mvn install

# Then, generate jar files for the backend application
cd java/scanr-backend
mvn package

# Launch ScanR backend (app.jar is in java/scanr-backend/app/target/)
cd java/scanr-backend/app/target/
java -jar app.jar

# Lauche ScanR backend admin
cd java/companies-queue/workflow/target/
java -jar workflow.jar

# You can check the API at http://localhost:8080/api
```

* Install python modules

``` bash
cd python

# For each modules, install dependencies in the following order :
# - companies-plugin
# - entities-extractor
# - fastmatch
# - cstore_api
# - textmining
# - webmining

tar xzf {dependance}.tar.gz
cd {dependance}
source tools/setup_venv.sh
./tools/deps.sh
./tools/install.sh

# Launch plugins
cd {plugin}
source tools/setup_venv.sh
./tools/deps.sh
python {plugin}/main.py --conf {config} --proc {number-of-processes}
```

* Install and start frontend modules

``` bash
cd front

# For both modules (frontend and frontend-admin)
npm start
````

### With Docker

For each modules, a Dockerfile is available ! Make it easier to install the application
``` bash
# Install docker
# Go to deploy/prod/<module>
# Check and update (if needed) the config file
# Execute./run.sh
# Profit !
```

## Running unit tests

##### Python
Run `./tools/test.sh`

##### Java
Run `maven test`

##### JavaScript
Run `npm test` to run the karma unit tests

## To go further

A full technical documentation with explanation of the ScanR algorithms is available following this link

# eLMIS to EMR Mediator

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2ca14ac18d374af8938a3d6a5cc65052)](https://app.codacy.com/gh/SoftmedTanzania/emr-mediator-elmis?utm_source=github.com&utm_medium=referral&utm_content=SoftmedTanzania/emr-mediator-elmis&utm_campaign=Badge_Grade_Settings)
[![Java CI Badge](https://github.com/SoftmedTanzania/emr-mediator-elmis/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/SoftmedTanzania/emr-mediator-elmis/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Coverage Status](https://coveralls.io/repos/github/SoftmedTanzania/emr-mediator-elmis/badge.svg?branch=development)](https://coveralls.io/github/SoftmedTanzania/emr-mediator-elmis?branch=development)

An [OpenHIM](http://openhim.org/) mediator for handling system integration between eLMIS and EMR systems.

## 1. Dev Requirements

1. Java 1.8
2. IntelliJ or Visual Studio Code
3. Maven 3.6.3

### 2 Configuration Parameters

The configuration parameters specific to the mediator and destination system can be found at

`src/main/resources/mediator.properties`

```
# Mediator Properties
mediator.name=eLMIS-to-EMR-Mediator
mediator.host=localhost
mediator.port=3106
mediator.timeout=60000
mediator.heartbeats=true

core.host=localhost
core.api.port=8080
core.api.user=root@openhim.org
core.api.password=openhim-password
```

The configuration parameters specific to the mediator and the mediator's metadata can be found at

`src/main/resources/mediator-registration-info.json`

```
{
  "urn": "urn:uuid:0d32b3e0-45d5-11ec-ad0a-3bccdeca8e69",
  "version": "0.1.0",
  "name": "eLMIS to EMR Mediator",
  "description": "A mediator for handling system integration between EMR and eLMIS",
  "endpoints": [
    {
      "name": "eLMIS to EMR Mediator Route",
      "host": "localhost",
      "port": "3106",
      "path": "/rnr-status",
      "type": "http"
    }
  ],
  "defaultChannelConfig": [
    {
      "name": "eLMIS to EMR Mediator",
      "urlPattern": "^/rnr-status$",
      "type": "http",
      "allow": ["elmis-role"],
      "routes": [
        {
          "name": "eLMIS to EMR Mediator Route",
          "host": "localhost",
          "port": "3106",
          "path": "/rnr-status",
          "type": "http",
          "primary": "true"
        }
      ]
    }
  ]
}
```

## 4. Deployment

To build and run the mediator after performing the above configurations, run the following

```
  mvn clean package -DskipTests=true -e source:jar javadoc:jar
  java -jar target/emr-mediator-elmis-<version>-jar-with-dependencies.jar
```

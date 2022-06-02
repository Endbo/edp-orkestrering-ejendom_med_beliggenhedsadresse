# edp-ork-ejendom-med-beliggenhedsadresse

This archetype creates, builds and deploys a Java based REST API orchestrated with Maven. The documentation below is for
usage of the project template and should be updated with information relevant for your project.

## Basics

* The project uses a parent pom, which contains necessary plugins, properties, etc. See section ```pom.xml``` below
* The project is a Spring boot application with sample code
* The Jkube maven plugin (https://eclipse.org/jkube) is used to build and deploy a docker image.
* The docker image can be deployed to UFST Openshift platform
* The base image can send log files to UFST cls solution, and has instrumentation for Application Performance
  Management (apm)
  using Elastic APM

## Prerequisites

You should have access to:

* An Openshift project and service user credentials
* A docker registry in Artifactory.ccta.dk
* A CLS log stream configured (see below)

These can be obtained by contacting Devops through MitIT (https://skat-myit.onbmc.com/). See next section for naming
conventions.

## The ```${organizationname}``` parameter

This is your organization, which prefixes eg. your artifactory repo. It sets the following defaults:

* Openshift:
    * The resulting project will be ```https://ocpt.ccta.dk:8443/console/project/${organizationname}/overview``` - set
      in ```Cleanupfile```
    * The service user for deploying to Openshift will be ```svc-jenkinsdeployer-${organizationname}``` - set in
* Artifactory docker repo: The resulting registry will be ```${organizationname}-docker-local.artifactory.ccta.dk```
  Ie. the Devops office has a ```devops-docker-local.artifactory.ccta.dk``` repo where artifacts are stored.
* Maven: Used as ```<project.organization.name>``` in the pom file

Note: Should you wish to change these after creating the project with the archetype, you may do so.

## pom.xml

This archetype inherits most functionality from its parent, which has the following GA(V) coordinates:

```
<groupId>dk.ufst.devops.maven</groupId>
<artifactId>spring-boot-project</artifactId>
```

It adds a Jenkinsfile and a build-config.yaml + working code to get started writing a REST API using Spring Boot. If you
wish to do so, you may extend the functionality from the parent pom in the project's```pom.xml``` file. You can add
dependencies, properties, plugins, etc. as you please.

## Environments

The project defines the following environments (through JKube) for Openshift deployment.

* dev
* test
* prod

The environment-specific configuration files are found in ```src/main/jkube```.

## CLS and APM

The project can send log files to UFST central logging solution CLS. The CLS URL is the following for APM:
https://apm.sheplog.ccta.dk/apm-\${organization.name}
The log stream URL may be different, so this may require additional configuration. Ask Devops, eg. in the Slack channel
og through MitIT.

## Running your application

After creating the project with the archetype, you should be able to run it locally with
```mvn spring-boot:run```

Deploying it to the Openshift cluster (in the preconfigured "dev" enviromnent) should be achieved by the following
command:
```mvn deploy -Pdev``` after logging in to the cluster using the command line.

The code will be automatically built on Jenkins using the ```Jenkinsfile``` and ```Cleanupfile``` configuration files.
Make sure that the Openshift namespaces are created, their names can be seen in pom.xml, using the parameters <
openshift.deployment.namespace.${env}>.
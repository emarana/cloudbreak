# Cloudbreak

[![Build Status][circle-ci-badge]][circle-ci]
[![Maintainability](https://api.codeclimate.com/v1/badges/566493a63aaaf0c61bd4/maintainability)](https://codeclimate.com/github/hortonworks/cloudbreak/maintainability)
![Build Status][docker-io]

[circle-ci-badge]: https://img.shields.io/circleci/project/github/hortonworks/cloudbreak.svg
[circle-ci]: https://img.shields.io/circleci/project/github/hortonworks/cloudbreak/master.svg
[docker-io]: https://img.shields.io/docker/pulls/hortonworks/cloudbreak.svg

* Website: https://hortonworks.com/open-source/cloudbreak/
* Documentation: http://hortonworks.github.io/cloudbreak-docs/latest/

# Local Development Setup
As of now this document is focusing on setting up your development environment on OSX. You'll need brew to install certain components in case you don't have them already. To get brew please follow the install instructions on the brew homepage: https://brew.sh

As a prerequisite you need to have Java installed. You can get it with the following command:
```
brew cask install java8
```

You'll need a Hypervisor too. Cloudbreak-Deployer has built-in xhyve setup option, but some of us use VirtualBox instead. Cloudbreak-Deployer works with both, it's up to you which one you prefer.

To set xhyve up:
```
brew install docker-machine-driver-xhyve
```

For VirtualBox usage:
```
brew cask install virtualbox
```

## Cloudbreak Deployer

The simplest way to setup the working environment to be able to start Cloudbreak on your local machine is to use the [Cloudbreak Deployer](https://github.com/hortonworks/cloudbreak-deployer).

First you need to create a sandbox directory which will store the necessary configuration files and dependencies of [Cloudbreak Deployer](https://github.com/hortonworks/cloudbreak-deployer). This directory must be created outside of the cloned Cloudbreak git repository:
```
mkdir cbd-local
cd cbd-local
```

The next step is to download the cloudbreak-deployer onto your machine:
```
curl -s https://raw.githubusercontent.com/hortonworks/cloudbreak-deployer/master/install-dev | sh [-s branch] && cbd --version
```
Use the -s branch option for sh here in case you'd like to checkout another branch than master.

The next step is to setup your docker-machine. The `cbd machine create` command initializes xhyve for you, you can setup you docker machine with virtualbox as well if you prefer. 
To configure docker machine with xhyve:

xhyve:
```
cbd machine create
eval $(docker-machine env cbd)
```
To configure docker machine with VirtualBox:

VirtualBox:
```
docker-machine create --driver virtualbox --virtualbox-disk-size "50000" cbd
eval $(docker-machine env cbd)
```

IP settings are based on your docker-machine configuration. The 'docker-machine ip cbd' command prints the ip address of your docker machine. This is the address you should use in multiple places below. Let's refer to this address as YOUR_IP throughout this document:
```
YOUR_IP=$(docker-machine ip cbd)
```

Add the following to the file named `Profile` under the cbd-local directory you have just created. Please note, when a `cbd` command is executed you should go to the deployment's directory where your `Profile` file could be found (`cbd-local` in our example). The CB_SCHEMA_SCRIPTS_LOCATION environment variable configures the location of SQL scripts that are in the 'core/src/main/resources/schema' directory in the cloned Cloudbreak git repository. CB_LOCAL_DEV_BIND_ADDR is the address of the network interface (`vboxnet0` for VirtualBox or `bridge100` for xhyve, one can check it with `ifconfig`).  
Please note that the full path needs to be configured and env variables like $USER cannot be used. You also have to set a password for your local Cloudbreak in UAA_DEFAULT_USER_PW:

xhyve:
```
export PRIVATE_IP=$PUBLIC_IP
export ULU_SUBSCRIBE_TO_NOTIFICATIONS=true
export CB_INSTANCE_UUID=$(uuidgen | tr '[:upper:]' '[:lower:]')
# NOTE: Some files could disappear when xhyve volume mounts are used, so CB_SCHEMA_SCRIPTS_LOCATION
# is commented out by default. Remove the comment before `cbd util local-dev` is used.
# export CB_SCHEMA_SCRIPTS_LOCATION=/Users/YOUR_USERNAME/YOUR_PROJECT_DIR/cloudbreak/core/src/main/resources/schema
export UAA_DEFAULT_USER_PW=YOUR_PASSWORD
```
VirtualBox:
```
export PUBLIC_IP=$(docker-machine ip cbd)
export DOCKER_MACHINE=cbd
export PRIVATE_IP=$PUBLIC_IP
export ULU_SUBSCRIBE_TO_NOTIFICATIONS=true
export CB_INSTANCE_UUID=$(uuidgen | tr '[:upper:]' '[:lower:]')
export CB_SCHEMA_SCRIPTS_LOCATION=/Users/YOUR_USERNAME/YOUR_PROJECT_DIR/cloudbreak/core/src/main/resources/schema
export UAA_DEFAULT_USER_PW=YOUR_PASSWORD
export CB_LOCAL_DEV_BIND_ADDR=192.168.99.1
```

Then run these commands:
```
cbd start
cbd logs cloudbreak
```

In case you see org.apache.ibatis.migration.MigrationException at the end of the logs run these commands to fix the DB and the re-run the previous section(cbd start and logs):
```
cbd migrate cbdb up
cbd migrate cbdb pending
```

If everything went well then Cloudbreak will be available on http://YOUR_IP. For more details and config parameters please check the documentation of [Cloudbreak Deployer](https://github.com/hortonworks/cloudbreak-deployer).

The deployer has generated a `certs` directory under `cbd-local` directory which will be needed later on to set up IDEA properly.

In order to kill Cloudbreak and Periscope containers running in docker/boot2docker and redirect the Cloudbreak and Periscope related traffic to the Cloudbreak running in IDEA, use the following command:

```
cbd util local-dev
```

## IDEA

### Check out the Cloudbreak repository

Go to https://github.com/hortonworks/cloudbreak, Either clone or download the repository, use SSH which is described here: https://help.github.com/articles/connecting-to-github-with-ssh/

### Project settings in IDEA

In IDEA set your SDK to your Java version under:
Configure -> Project Defaults -> Project Structure -> Project SDK

Cloudbreak can be imported into IDEA as gradle project by specifying the cloudbreak repo root under Import Project. Once it is done, you need to import the proper code formatter by using the __File -> Import Settings...__ menu and selecting the `idea_settings.jar` located in the `config/idea` directory in Cloudbreak git repository.

To launch the Cloudbreak application execute the `com.sequenceiq.cloudbreak.CloudbreakApplication` class  (Set 'Use classpath of module' to `core_main`) with the following VM options:
```
-Dcb.cert.dir=FULL_PATH_OF_THE_CERTS_DIR_GENERATED_BY_CBD
-Dcb.client.id=cloudbreak
-Dcb.client.secret=CB_SECRET_GENERATED_BY_CBD
-Dcb.db.port.5432.tcp.addr=YOUR_IP
-Dcb.db.port.5432.tcp.port=5432
-Dcb.identity.server.url=http://YOUR_IP:8089
-Dserver.port=9091
-Dcb.schema.migration.auto=true
```

The `-Dcb.cert.dir=FULL_PATH_OF_THE_CERTS_DIR_GENERATED_BY_CBD` value above needs to be replaced with the full path of `certs` directory generated by the Cloudbreak-Deployer e.g. `/Users/YOUR_USERNAME/YOUR_PROJECT_DIR/cbd-local/certs`.

The `-Dcb.client.secret=CB_SECRET_GENERATED_BY_CBD` value has to be replaced with the value of UAA_DEFAULT_SECRET from the cdb-local/Profile file.
The database migration scripts run automatically by Cloudbreak by default, this migration can be turned off with the `-Dcb.schema.migration.auto=false` VM option.

### Configure Before launch task

In order to be able to determine the local Cloudbreak version automatically, a `Before launch` task has to be configured for the project in IntelliJ Idea. The required steps are the following:

1. Open `Run/Debug Configurations` for the project
2. Select your project's application
3. Click on `Add` in the `Before launch` panel
4. Select `Run Gradle Task` with the following parameters
    1. `Gradle project`: cloudbreak:core
    2. `Tasks`: buildInfo
5. Confirm and restart the application
    
## Command line

To run Cloudbreak from command line, you have to list the JVM parameters from above for gradle:

```
./gradlew :core:buildInfo :core:bootRun -PjvmArgs="-Dcb.cert.dir=FULL_PATH_OF_THE_CERTS_DIR_GENERATED_BY_CBD \
-Dcb.client.id=cloudbreak \
-Dcb.client.secret=CB_SECRET_GENERATED_BY_CBD \
-Dcb.db.port.5432.tcp.addr=YOUR_IP \
-Dcb.db.port.5432.tcp.port=5432 \
-Dcb.identity.server.url=http://YOUR_IP:8089 \
-Dserver.port=9091"
```

The `-Dcb.cert.dir=FULL_PATH_OF_THE_CERTS_DIR_GENERATED_BY_CBD` value above needs to be replaced with the full path of `certs` directory generated by the Cloudbreak-Deployer e.g. `/Users/YOUR_USERNAME/YOUR_PROJECT_DIR/cbd-local/certs`.

The `-Dcb.client.secret=CB_SECRET_GENERATED_BY_CBD` value has to be replaced with the value of UAA_DEFAULT_SECRET from the cdb-local/Profile file.
The database migration scripts run automatically by Cloudbreak by default, this migration can be turned off with the `-Dcb.schema.migration.auto=false` VM option.

## Database development

If any schema change is required in Cloudbreak database (cbdb), then the developer needs to write SQL scripts to migrate the database accordingly. The schema migration is managed by [MYBATIS Migrations](https://github.com/mybatis/migrations) in Cloudbreak and the cbd tool provides an easy-to-use wrapper for it. The syntax for using the migration commands is `cbd migrate <database name> <command> [parameters]` e.g. `cbd migrate migrate status`.

Create a SQL template for schema changes:
```
cbd migrate cbdb new "CLOUD-123 schema change for new feature"
```
As as result of the above command an SQL file template is generated under the path specified in `CB_SCHEMA_SCRIPTS_LOCATION` environment variable, which is defined in Profile. The structure of the generated SQL template looks like the following:
```
-- // CLOUD-123 schema change for new feature
-- Migration SQL that makes the change goes here.



-- //@UNDO
-- SQL to undo the change goes here.
```
Once you have implemented your SQLs then you can execute them with:
```
cbd migrate cbdb up
```
Make sure pending SQLs to run as well:
```
cbd migrate cbdb pending
```
If you would like to rollback the last SQL file, then just use the down command:
```
cbd migrate cbdb down
```
On order to check the status of database
```
cbd migrate cbdb status

#Every script that has not been executed will be marked as ...pending... in the output of status command:

------------------------------------------------------------------------
-- MyBatis Migrations - status
------------------------------------------------------------------------
ID             Applied At          Description
================================================================================
20150421140021 2015-07-08 10:04:28 create changelog
20150421150000 2015-07-08 10:04:28 CLOUD-607 create baseline schema
20150507121756 2015-07-08 10:04:28 CLOUD-576 change instancegrouptype hostgroup to core
20151008090632    ...pending...    CLOUD-123 schema change for new feature

------------------------------------------------------------------------
```

## Building

Gradle is used for build and dependency management. Gradle wrapper is added to Cloudbreak git repository, therefore building can be done with:
```
./gradlew clean build
```

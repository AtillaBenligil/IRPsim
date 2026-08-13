# IRPsim backend

[![Build IRPsim](https://github.com/IRPsim/backend/actions/workflows/buildBackend.yml/badge.svg)](https://github.com/IRPsim/backend/actions/workflows/buildBackend.yml)

This repository provides the backend for the IRPsim infrastructure, which enables input and output data management for models in the energy domain, model execution management and model coupling.

## Security Sprint Execution Support

For the resit LDAP and RBAC implementation work, use these repository-local artifacts:

* `irpsim-security-implementation-plan.md`
* `resit-release-checklist.md`
* `resit-progress.md`

### Authentication Scaffold (Sprint 1)

Current scaffold endpoints:

* `POST /simulation/auth/login`
* `POST /simulation/auth/logout`
* `POST /simulation/auth/change-password`

LDAP configuration environment variables:

* `IRPSIM_LDAP_URL`
* `IRPSIM_LDAP_BASE_DN`
* `IRPSIM_LDAP_BIND_DN`
* `IRPSIM_LDAP_BIND_PASSWORD`
* `IRPSIM_LDAP_USER_DN_PATTERN` (default: `uid=%s`)

# Starting

To start the backend, please execute the following steps:

* Build the projects using `mvn clean package -DskipTests=true`. It is required that you got the project `com.gams:gamsjavaapi:jar:24.7.1` installed in your local maven repository.
* Start MariaDB: `cd backend-server/target/backend-production && source startEnvironmenManually.sh`. This starts MariaDB with the default password 1rps1m. If there is a problem with creation of the database, please run `docker exec -it $container mysql -u root -pb4s3l -e "CREATE DATABASE irpsim"`
* Optional: Check the values of the environment variables `$IRPSIM_MYSQL_URL`, `$IRPSIM_PORT`
* Start the server: Call `./startup.sh`. The server will be available under `localhost:$IRPSIM_PORT`

## Execution with GAMS

If you need GAMS instance for your model, please execute the following commands before starting the backend:

```
wget https://d37drm4t2jghv5.cloudfront.net/distributions/24.7.1/linux/linux_x64_64_sfx.exe
chmod 755 linux_x64_64_sfx.exe
./linux_x64_64_sfx.exe
gamsinst -a
export LD_LIBRARY_PATH=$(pwd)
```

Please note that without a suitable GAMS license, you will only be able to run minimal models.

## Configuration

If you want to configure IRPsim yourself (instead using the startup script), please set the following variables:
* `IRPSIM_MYSQL_USER`: MySQL user
* `IRPSIM_MYSQL_PASSWORD`: MySQL password
* `IRPSIM_MYSQL_URL`: URL of the database
* `IRPSIM_PORT`: Port of the application
* `IRPSIM_MYSQL_SAVEPATH` and `IRPSIM_MYSQL_LOADPATH` for the path for saving and loading temporary csv files for sql (the loadpath might be inside of a container and therefore different than the savepath)

# Licensing

This project is licensed under GPLv3. 

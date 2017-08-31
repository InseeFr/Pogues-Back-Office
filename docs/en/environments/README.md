# Environments & Configuration

Application settings may vary depending on selected environment.

Available environments are listed in src/main/resources/env:

```
resources
  ├─env
     ├─ dev
     ├─ dv
     ├─ qa
     ├─ prod
     └─ qf
```

Each environment directory contains a set of files used to configure:

 - Backend connection parameters (env/${env}/pogues-bo.properties)
   - Persistence backend with postgresql LDAP
 - External services URIs
 - Logging config (env/${env}/log4j2.xml)

## Define environment on startup:

```bash
export CATALINA_OPTS="-Dfr.insee.pogues.env=qa"
${CATALINA_BASE}/bin/startup.sh
```

This will start a tomcat instance using properties defined in src/main/resources/env/qa/pogues-bo.properties 

## Create a custom environment

Subsequently, defining a new "foo" environment can be done by:

 - Creating a foo subdirectory in src/main/resources/env
 - Copying files from another environment and applying changes that suit the foo environment
 - Start tomcat with ```-Dfr.insee.pogues.env=foo``` as a VM argument
 
**If no value has been defined server will start using dv as an environment**



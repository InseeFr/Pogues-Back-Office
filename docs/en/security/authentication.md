# Authentification

Authentication configuration is made using the Spring Security API in a dedicated SecurityContext class placed in the config package.
Connexion to the identity provider (an Open LDAP directory) is made in this class using parameters retrieved from a property file depending on which [environnement](../environments/README.md) we are running on..

### SecurityContext.java

[include](../../../src/main/java/fr/insee/pogues/configuration/SecurityContext.java)


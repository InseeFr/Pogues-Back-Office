# Acces Control and Security

*Authentication process and Access Control rules are very likely to be modified due to the introduction of a Federated Identity mecanism and an active discussion about security policy. Consider the following lines as a draft*

In development we will stick to the following rules:

 - Full access to *most* of the resources is granted to user after authentication (for exceptions refer to the [permissions](./permissions.md) part of this documentation)

 - Identity provisioning and credentials validation are made against a LDAP directory

 - Authentication layer implementation is based on [Spring Security](https://projects.spring.io/spring-security/).

 - Access control operations are implemented using [Jersey filters](./permissions.md)
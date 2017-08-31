# Contrôle d'Accès et Sécurité

*Les règles de contrôle d'accès et de sécurité sont fortement suceptibles en raison de l'introduction d'une fédération d'identités et des discussions en cours sur la politique de sécurité à mettre en place*

Pour le moment les règles appliquées sont les suivantes:

 - La plupart des ressources de l'application sont accessibles sans contrôle de permission après authentification de l'utilisateur (Les exceptions sont décrites dans la partie [permissions](./permissions.md) de cette documentation)

 - La vérification de mot de passe et l'approvisionnement du compte utilisateur d'éffectue auprès d'un annuaire LDAP

 - L'implémentations de la couche d'authentification est réalisée par l'intermédiaire de  [Spring Security](https://projects.spring.io/spring-security/).

 - Le contrôle d'accès est effectué par l'intermédiaire de  [filtres Jersey](./permissions.md)
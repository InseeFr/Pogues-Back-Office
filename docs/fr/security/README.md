# Contrôle d'Accès et Sécurité

*Les règles de contrôle d'accès et de sécurité sont fortement suceptibles d'évoluer, un mécanisme de SSO couplé à une fédération d'identité étant voué à remplacer le système d'authentification par formulaire actuellement en place sur l'intégration.*

Globalement on peut noter que:

 - La plupart des ressources de l'application sont accessibles sans contrôle de permission après authentification de l'utilisateur

 - Les utilisateurs sont authentifiés auprès d'un annuaire LDAP.

 - L'authentification est réalisée grâce à Spring Security.
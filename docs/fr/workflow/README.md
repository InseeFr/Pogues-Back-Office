# Intégration

Cette section décrit les outils mis en place sur le projet pour l'écriture et l'exécution des tests, les fonctionnalités testées ou à tester et la configuration de la plateforme d'intégration continue utilisée (Travis)

## Vue d'ensemble du workflow d'intégration

## Sur le fork du projet

{% sequence %}
task_branch->>zenika_dev@zenika: Pull Request
zenika_dev@zenika->>travis@zenika: Notify
Note over zenika_dev@zenika: Code Review
Note over  travis@zenika: Build
zenika_dev@zenika->>task_branch:[CR KO] Notify
Note over task_branch: FIX
task_branch->>zenika_dev@zenika: [Fixed] Pull Request
Note over zenika_dev@zenika: Accept
zenika_dev@zenika->>travis@zenika: notify
Note over travis@zenika: Build
Note over travis@zenika: Publish doc
{% endsequence %}

## Intégration I.N.S.E.E.

{% sequence %}
zenika_dev@zenika->>zenika_dev@insee: Pull Request
Note over zenika_dev@insee: Accept
zenika_dev@insee->>travis@insee: notify
Note over travis@insee: Build
Note over travis@insee: Publish doc.
{% endsequence %}
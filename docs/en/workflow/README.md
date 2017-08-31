# Integration Process

This section describes testing frameworks and tools used in development stage, actual test coverage and Travis CI configuration.

## Workflow Overview

## Project Fork

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

## I.N.S.E.E. repository

{% sequence %}
zenika_dev@zenika->>zenika_dev@insee: Pull Request
Note over zenika_dev@insee: Accept
zenika_dev@insee->>travis@insee: notify
Note over travis@insee: Build
Note over travis@insee: Publish doc.
{% endsequence %}
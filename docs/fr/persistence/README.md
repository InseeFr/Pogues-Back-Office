# Persistence des données de l'application Pogues

Les données persistentes de l'application sont stockées dans une base de données postgresql au format JSON

La base de données comprend une seule et unique table:

```
 Colonne | Type  | Modificateurs 
---------+-------+---------------
 id      | text  | 
 data    | jsonb | 
```


#!/bin/bash

ldapmodify -x -D "cn=admin,cn=config" -w config -f database.ldif
ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f ou.ldif
ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f groups.ldif
ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f users.ldif

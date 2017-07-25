#!/bin/bash

sleep 2

ldapmodify -x -D "cn=admin,cn=config" -w config -f /db/org.ldif
ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f /db/ou.ldif
ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f /db/groups.ldif
ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f /db/people.ldif

tail -f /dev/null
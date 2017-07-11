FROM osixia/openldap

MAINTAINER a-cordier

#ENV LDAP_ORGANISATION I.N.S.E.E.
#ENV LDAP_DOMAIN insee.fr
#ENV LDAP_BASE_DN dc=insee,dc=fr

# define ldap config
ADD db/database.ldif /data/database.ldif
ADD db/ou.ldif /data/ou.ldif
ADD db/groups.ldif /data/groups.ldif
ADD db/users.ldif /data/users.ldif

#RUN ldapmodify -x -D "cn=admin,cn=config" -w config -f /data/database.ldif
#RUN ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f /data/ou.ldif
#RUN ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f /data/groups.ldif
#RUN ldapadd -x -D "cn=admin,o=insee,c=fr" -w admin -f /data/users.ldif



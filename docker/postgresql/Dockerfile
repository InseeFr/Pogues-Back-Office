FROM postgres:9.4

MAINTAINER a-cordier

#ENV PGDATA /var/lib/postgresql/data
ENV POSTGRES_USER pogues
ENV POSTGRES_PASSWORD pogues
ENV POSTGRES_DB pogues

COPY data.sql /docker-entrypoint-initdb.d/

#ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["postgres"]
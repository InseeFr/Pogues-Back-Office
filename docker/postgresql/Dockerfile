FROM postgres:9.5

MAINTAINER a-cordier

ENV POSTGRES_USER pogues
ENV POSTGRES_PASSWORD pogues
ENV POSTGRES_DB pogues

COPY db/data.sql /docker-entrypoint-initdb.d/

CMD ["postgres"]
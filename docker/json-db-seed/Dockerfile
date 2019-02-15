FROM python:2

ADD app /opt/json-db-seed/app
ADD json /opt/json-db-seed/json

RUN pip install requests --proxy="$HTTP_PROXY"

CMD python opt/json-db-seed/app/main.py

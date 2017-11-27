FROM python:2

ADD app /opt/exist/app
ADD db /opt/exist/db
ADD system /opt/exist/system

RUN pip install requests --proxy="$HTTP_PROXY"

CMD python opt/exist/app/main.py

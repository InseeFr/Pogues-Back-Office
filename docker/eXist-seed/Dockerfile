FROM python:2

ADD app /app
ADD db /db

RUN pip install requests --proxy="$HTTP_PROXY"

CMD python /app/main.py

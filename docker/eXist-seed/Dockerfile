FROM python:2

ADD app /app
ADD db /db

RUN pip install requests --proxy=proxy-orange.http.insee.fr:8080

CMD python /app/main.py

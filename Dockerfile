FROM python:3.7-rc
MAINTAINER dmanevd@mail.ru

ADD /greetings_app/ /greetings_app/
RUN pip install -r /greetings_app/requirements.txt

ENV DB_URL=sqlite:///foo.db

ENTRYPOINT ["python"]
CMD ["/greetings_app/app.py"]

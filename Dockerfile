FROM gradle:8.3.0-jdk20

WORKDIR /app

COPY app/ /app/

RUN gradle installDist

CMD ./build/install/app/bin/app

FROM gradle:8.3.0-jdk20

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN gradle installDist

CMD ./build/install/app/bin/app

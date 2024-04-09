FROM gradle:8.3.0-jdk20

WORKDIR /app

COPY app/ /app/

RUN gradle installDist

ENV JDBC_DATABASE_URL=jdbc:postgresql://dpg-cmuok6acn0vc73akdjfg-a.oregon-postgres.render.com/new_postgresql_for_javalin
ENV DB_USERNAME=new_postgresql_for_javalin_user
ENV DB_PASSWORD=GvGwspqIZhAYD3HDJjbP9QP51RSh5yf9

CMD ./build/install/app/bin/app

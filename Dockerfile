FROM openjdk
ARG VERSION
RUN mkdir /app
COPY target/feed-${VERSION}-fat.jar /app
RUN mv /app/feed-${VERSION}-fat.jar /app/feed-SNAPSHOT.jar
CMD java -jar /app/feed-SNAPSHOT.jar

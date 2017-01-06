FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/my-money.jar /my-money/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/my-money/app.jar"]

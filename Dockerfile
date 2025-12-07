# Build image

FROM clojure:temurin-11-tools-deps-1.12.3.1577-trixie-slim@sha256:b21661fb322528242b0ef604e8892367ef675d856a8e665c670face7941ba571 AS builder

# Install node.js
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - &&\
    apt-get install -y nodejs
# set working directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# add app
COPY . /usr/src/app

# Build the app
RUN npm ci
RUN npm run build && clojure -Spom && clojure -A:uberjar:prod


# Production image
FROM eclipse-temurin:21-jre-alpine-3.22@sha256:d52fed95b86b9374fcd6f289f022b7d5ef4de6fdbe1e49a59cf7551496027090

# copy artifact build from the 'build environment'
COPY --from=builder /usr/src/app/my-money.jar /my-money/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/my-money/app.jar"]

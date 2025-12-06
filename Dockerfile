# Build image

FROM clojure:temurin-11-tools-deps-1.12.3.1577-trixie-slim as builder

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
FROM --platform=linux/amd64 adoptopenjdk/openjdk11:jre-11.0.11_9-alpine

# copy artifact build from the 'build environment'
COPY --from=builder /usr/src/app/my-money.jar /my-money/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/my-money/app.jar"]

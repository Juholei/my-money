# Build image

FROM clojure:openjdk-11-tools-deps-1.11.1.1105-buster as builder

# Install node.js
RUN curl -sL https://deb.nodesource.com/setup_16.x | sh && apt-get install -y nodejs
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

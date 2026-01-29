# Build image

FROM clojure:temurin-21-tools-deps-trixie-slim@sha256:0e6451872ef48322c6a8f4047ed02fcfe42a6ca9535391f7d012fec4e1d95ab6 AS builder

# Install node.js
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_24.x | bash - &&\
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
FROM eclipse-temurin:25.0.1_8-jre-alpine-3.22@sha256:9c65fe190cb22ba92f50b8d29a749d0f1cfb2437e09fe5fbf9ff927c45fc6e99

# copy artifact build from the 'build environment'
COPY --from=builder /usr/src/app/my-money.jar /my-money/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/my-money/app.jar"]

# Build image

FROM clojure:temurin-25-tools-deps-trixie-slim@sha256:591d0994eebbf22d8181dc14eb24eeb483baee5f8d2b8f5e509aa24d1e062660 AS builder

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
FROM eclipse-temurin:25.0.2_10-jre-alpine-3.22@sha256:2bf0db425c0f387e91530a223e18525de5f76b468335b00c78099f4055121efd

# copy artifact build from the 'build environment'
COPY --from=builder /usr/src/app/my-money.jar /my-money/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/my-money/app.jar"]

# Build image

FROM clojure:openjdk-8-tools-deps as builder

# Install node.js
RUN curl -sL https://deb.nodesource.com/setup_14.x | sh && apt-get install -y nodejs
# set working directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# add app
COPY . /usr/src/app

# Build the app
RUN npm ci
RUN npm run build
RUN clojure -A:uberjar:prod


# Production image
FROM java:8-alpine

# copy artifact build from the 'build environment'
COPY --from=builder /usr/src/app/my-money.jar /my-money/app.jar

EXPOSE 3000

RUN ls -lh /my-money/app.jar

CMD ["java", "-jar", "/my-money/app.jar"]

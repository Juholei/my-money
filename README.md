# my-money

[![my-money CI](https://github.com/Juholei/my-money/actions/workflows/ci.yml/badge.svg)](https://github.com/Juholei/my-money/actions/workflows/ci.yml)

This will display information about your money usage based on web bank exports (CSV from OP or S-Pankki).

Initially generated using Luminus version "2.9.11.22".

FIXME

## Prerequisites

You will need clj CLI tools installed.

## Developing

Launch dev repl with command

	clj -A:dev

Or with nREPL:

	clj -A:dev:nrepl

To start the application run `(start)`. To reload after changes, use `(restart)`.

For frontend, install npm dependencies with `npm install`. Then start shadow-cljs dev build with

	npm start


## Running database migrations

	clj -A:migrate:<dev/prod>

## Running tests

	clj -A:test (use --watch for continous test runner after changes)
	npm run test:watch (continuous running in browser)
	npm test (single run using headless Chrome)

## Check if dependencies are out of date

	clj -A:outdated

## Building

To build frontend, use command 

	npm run build

To build an uberjar for production, use command

	clj -T:build uber

Build frontend first so that it gets packaged in the uberjar.

## License

Copyright © 2017-2020 Juho Leinonen

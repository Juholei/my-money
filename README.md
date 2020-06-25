# my-money

[![Build Status](https://travis-ci.org/Juholei/my-money.svg?branch=master)](https://travis-ci.org/Juholei/my-money)

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


## Running database migrations

	clj -A:migrate:<dev/prod>

## Running tests

	clj -A:test (use --watch for continous test runner after changes)
	lein fronttests-once

## Check if dependencies are out of date

	clj -A:outdated

## Building

To build an uberjar for production, use command

	clj -A:uberjar:prod

## License

Copyright © 2017-2020 Juho Leinonen

# Lambdash -- Everything or Anything analyzing dashboard written by Clojure.

## What?

 Lambdash named from "Lambda Dashboard".

## Architecture

require: mongodb and redis

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Setting

You will need setting by re-written following file:

- settings.example.clj -> settings.clj
- dblogic.example.clj -> dblogic.clj
- pagelogic.example.clj -> pagelogic.clj

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2013 Eclipse License

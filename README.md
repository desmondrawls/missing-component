# INFLECTION FOR REFLECTION

When you first clone this repository, run:

```sh
lein setup
```

This will create files for local configuration, and prep your system
for the project.

### Environment

To recreate the bug with component, start with a REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to initiate and start the system (ignore js error).

```clojure
dev=> (go)
:started
```

By default this creates a web server at <http://localhost:3447>.

Run a query.

```
curl 'http://localhost:3447/api/query' -H 'Content-Type: application/transit+json' --data-binary '[]'
```


When you make changes to your source files, use `reset` to reload any
modified files and reset the server. Changes to CSS or ClojureScript
files will be hot-loaded into the browser.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

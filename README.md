# Validator #

This is a very simple proof-of-concept [Scalatra](http://www.scalatra.org/)
XML validation service that uses [Jing](http://www.thaiopensource.com/relaxng/jing.html)
to validate XML documents against RELAX NG schemas. 

## Build & Run ##

```sh
$ cd validation-demo
$ ./sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

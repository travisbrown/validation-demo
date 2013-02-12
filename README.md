# Validator #

This is a very simple proof-of-concept [Scalatra](http://www.scalatra.org/)
XML validation service that uses [Jing](http://www.thaiopensource.com/relaxng/jing.html)
to validate XML documents against RELAX NG schemas. 

## Building ##

```sh
$ cd validation-demo
$ ./sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

## Usage ##

For convenience: 

```sh
$ SCHEMA=https://raw.github.com/travisbrown/validation-demo/master/examples/shelley-godwin-page.rng
```

With a well-formed and valid file, we'll get an empty array:

```sh
curl -X POST -F "document=@examples/ox-ms_abinger_c56-0001.xml" \
  http://localhost:8080/validate?schema=http://dhhub.org/tmp/shelley-godwin-page.rng
```

The following file isn't well-formed:

```sh
curl -X POST -F "document=@examples/ox-ms_abinger_c56-0001-nwf.xml" \
  http://localhost:8080/validate?schema=http://dhhub.org/tmp/shelley-godwin-page.rng
```

And we get this:

```json
[{
  "uri":null,
  "message":"The end-tag for element type \"del\" must end with a '>' delimiter.",
  "line":9,
  "column":45
}]
```

And finally the following file is well-formed, but does not validate against our schema:

```sh
curl -X POST -F "document=@examples/ox-ms_abinger_c56-0001-invalid.xml" \
  http://localhost:8080/validate?schema=http://dhhub.org/tmp/shelley-godwin-page.rng
```

And we get a nice error message:

```json
[{
  "uri":null,
  "message":"element \"nonsense\" not allowed anywhere; expected the element end-tag, text or element \"add\", \"addSpan\", \"anchor\", \"c\", \"damage\", \"damageSpan\", \"del\", \"delSpan\", \"gap\", \"graphic\", \"handShift\", \"hi\", \"line\", \"metamark\", \"milestone\", \"mod\", \"note\", \"retrace\", \"seg\", \"space\", \"unclear\" or \"zone\"",
  "line":15,
  "column":15
}]
```


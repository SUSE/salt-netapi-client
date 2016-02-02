# saltstack-netapi-client-java [![Build Status](https://travis-ci.org/SUSE/saltstack-netapi-client-java.svg?branch=master)](https://travis-ci.org/SUSE/saltstack-netapi-client-java)

Java bindings for the [Salt API] (http://docs.saltstack.com/en/latest/ref/netapi/all/salt.netapi.rest_cherrypy.html#module-salt.netapi.rest_cherrypy.app), please have a look at the Javadoc for [v0.6.0] (http://suse.github.io/saltstack-netapi-client-java/docs/v0.6.0) or [master] (http://suse.github.io/saltstack-netapi-client-java/docs/master).

## How to use it

Add the following dependency to the `pom.xml` file of your project:

```xml
<dependency>
    <groupId>com.suse.saltstack</groupId>
    <artifactId>saltstack-netapi-client</artifactId>
    <version>0.6.0</version>
</dependency>
```

## Code examples

There is some basic [code examples] (https://github.com/SUSE/saltstack-netapi-client-java/tree/master/src/test/java/com/suse/saltstack/netapi/examples) available to help you getting started with the library.

## Contributing

Pull requests are always welcome, please see [issues] (https://github.com/SUSE/saltstack-netapi-client-java/issues) for a list of things to possibly tackle.

### Style guide

* Indentations are represented with 4 spaces
* The maximum line length should not exceed 92
* Wrapped lines should be indented twice (8 spaces)
* Files should end with a new line

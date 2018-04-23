[![Build Status](https://travis-ci.org/SUSE/salt-netapi-client.svg?branch=master)](https://travis-ci.org/SUSE/salt-netapi-client)
[![Maven Central](https://img.shields.io/maven-central/v/com.suse.salt/salt-netapi-client.svg)](https://mvnrepository.com/artifact/com.suse.salt/salt-netapi-client)

# salt-netapi-client

Java bindings for the [Salt API](http://docs.saltstack.com/en/latest/ref/netapi/all/salt.netapi.rest_cherrypy.html#module-salt.netapi.rest_cherrypy.app), please have a look at the Javadoc for [v0.14.0](http://suse.github.io/salt-netapi-client/docs/v0.14.0) or [master](http://suse.github.io/salt-netapi-client/docs/master).

## How to use it

Add the following dependency to the `pom.xml` file of your project:

```xml
<dependency>
    <groupId>com.suse.salt</groupId>
    <artifactId>salt-netapi-client</artifactId>
    <version>0.14.0</version>
</dependency>
```

## Code examples

There is some basic [code examples](https://github.com/SUSE/salt-netapi-client/tree/master/src/test/java/com/suse/salt/netapi/examples) available to help you getting started with the library.

## Contributing

Pull requests are always welcome, please see [issues](https://github.com/SUSE/salt-netapi-client/issues) for a list of things to possibly tackle.

### Style guide

* Indentations are represented with 4 spaces
* The maximum line length should not exceed 120
* Wrapped lines should be indented twice (8 spaces)
* Files should end with a new line

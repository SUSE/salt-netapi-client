[![Build Status](https://github.com/SUSE/salt-netapi-client/actions/workflows/maven.yml/badge.svg)](https://github.com/SUSE/salt-netapi-client/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.suse.salt/salt-netapi-client)](https://mvnrepository.com/artifact/com.suse.salt/salt-netapi-client)

# IMPORTANT
Starting from v1.0.0, salt-netapi-client uses jakarta instead of javax. If you still want to use javax version, please use v0.x.x.

# salt-netapi-client

Java bindings for the [Salt API](http://docs.saltstack.com/en/latest/ref/netapi/all/salt.netapi.rest_cherrypy.html#module-salt.netapi.rest_cherrypy.app), please have a look at the Javadoc for [v1.0.0](http://suse.github.io/salt-netapi-client/docs/v1.0.0) or [master](http://suse.github.io/salt-netapi-client/docs/master).

## How to use it

Add the following dependency to the `pom.xml` file of your project:

```xml
<dependency>
    <groupId>com.suse.salt</groupId>
    <artifactId>salt-netapi-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Code examples

There is some basic [code examples](https://github.com/SUSE/salt-netapi-client/tree/master/src/test/java/com/suse/salt/netapi/examples) available to help you getting started with the library.

## Contributing

Pull requests are always welcome, please see [issues](https://github.com/SUSE/salt-netapi-client/issues) for a list of things to possibly tackle.

If you want to contribute to salt-netapi-client lib which use javax, please create a PR againsts 5.1 branch.

Make sure you have Git commit signing enabled. If you are not doing it already, check out the [GitHub documentation](https://docs.github.com/en/authentication/managing-commit-signature-verification/about-commit-signature-verification).

### Style guide

* Indentations are represented with 4 spaces
* The maximum line length should not exceed 120
* Wrapped lines should be indented twice (8 spaces)
* Files should end with a new line

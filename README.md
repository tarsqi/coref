# JTarsqi

Beginnings of a new Tarsqi Toolkit (TTK) repository.


At the moment this is a hodgepodge of three efforts:

1. Creating a Java version of TTK.
2. Improving TTK interaction with other tools, most notably Stanford CoreNLP and HeidelTime.
3. Adding event coreference.


### Requirements

- Apache Commons CLI (https://mvnrepository.com/artifact/commons-cli/commons-cli/1.3.1)
- Stanford CoreNLP


### Command line use

There is no significant versioning yet and we do not yet have downloads of jar files, but if you have created a jar using your favorite method you should be able to do the following with it:

*&para; Run the sectioner on a TTK file*

```bash
$ java -jar JTarsqi.jar --sectioner src/resources/sectioner.ttk
```

This will now just print section tags to the standard output.

*&para; Run the Stanford Dependency parser on a TTK file*

```bash
$ java -jar JTarsqi.jar --run --tool stanford --input TTK_FILE --output OUTFILE
```

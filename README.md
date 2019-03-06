# JTarsqi

Beginnings of a new Tarsqi Toolkit (TTK) repository.

At the moment this is a hodgepodge of three strains:

1. Creating a Java version of TTK.
2. Improving TTK interaction with other tools, most notably Stanford CoreNLP and HeidelTime.
3. Adding event coreference.

There is no significant versioning yet and we do not yet have downloads of jar files, but if you have created a jar using your favorite means you should be able to do the following with the jar.


**Run a Tarsqi Component**

You can run the sectioner on any file:

```
java -jar dist/JTarsqi.jar --sectioner src/resources/sectioner.ttk
```

This will now just print section tags to the output.

Other components are not yet available from the jar.

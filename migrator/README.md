# Simple migration tool to migratoe from old to new tags

* converts old style `//Start Solution` etc. to the new variant `//cs:remove:start`.

## To build

```sh
cd migrator
mvn install
```

This installs the jar.

To run in any dir use a script, say `migrate-tool` and put it in your path.

```sh
#!/bin/bash
JAR=/home/hom/.m2/repository/io/github/sebivenlo/migrator/0.1/migrator-0.1.jar

java -cp ${JAR} migrator.TagMigrator
```

run it in the directory of your java files that you want to migrate.

`migrate-tool`

If you want it faster, you could use graalvm to speed things up.

Make sure your JAVA_HOME points to a graalvm installation
the build
```sh
mvn -DskipTests -P native package
```

After a minute or so your shoudl be able to find an executable in the target
dir called migrator.

Copy that to your bin and invoke with 
```sh
migrator
```

in the project dir.





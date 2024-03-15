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






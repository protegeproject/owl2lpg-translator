#!/bin/sh
java=java
java_opts="-Xms8g -Xmx16g"
if test -n "$JAVA_HOME"; then
  java="$JAVA_HOME/bin/java"
fi
exec "$java" $java_opts -jar owl2lpg-translation-cli.jar "$@"
exit 1

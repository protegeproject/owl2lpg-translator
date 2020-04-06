#!/bin/sh
java=java
if test -n "$JAVA_HOME"; then
  java="$JAVA_HOME/bin/java"
fi
exec "$java" $java_args -jar owl2lpg-translation-cli.jar "$@"
exit 1

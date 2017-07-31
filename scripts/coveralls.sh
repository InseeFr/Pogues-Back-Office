#!/usr/bin/env bash
#title           :coverealls.sh
#description     :This script submits coverage reports to coveralls.io
#author		     :a-cordier
#==============================================================================

CMD="mvn clean test"
if [ -n "$COVERALLS_TOKEN" ];then
  mvn -DrepoToken=$COVERALLS_TOKEN coveralls:report
  exit 0
fi

echo "Coveralls token is not set, coverage reports won't be published"



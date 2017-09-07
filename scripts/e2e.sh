#!/usr/bin/env bash
#title           :e2e.sh
#description     :This script launches e2e test on pogues frontend
#author		     :a-cordier
#==============================================================================

STATIC_SOURCES="$TRAVIS_BUILD_DIR/frontend-$TRAVIS_BUILD_NUMBER"

function e2e() {
  pushd $STATIC_SOURCES
#  npm run e2e:full-travis
  popd
}

function main() {
  e2e
}

main
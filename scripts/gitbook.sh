#!/usr/bin/env bash

set -e

UPSTREAM="https://$GITHUB_TOKEN@github.com/$TRAVIS_REPO_SLUG.git"

function build(){
  pushd docs
  gitbook install
  gitbook build
  popd
}

function publish(){
  pushd docs/_book
  git init
  git config user.name "$USER"
  git config user.email "antoine.cordier@zenika.com"
  git remote add upstream "$UPSTREAM"
  git fetch -p upstream
  git reset upstream/gh-pages
  touch .
  git add -A
  git commit -m "Rebuild doc, REV $TRAVIS_COMMIT: $TRAVIS_COMMIT_MESSAGE"
  git push -q upstream HEAD:gh-pages
}

function main(){
  build
  publish
}

main
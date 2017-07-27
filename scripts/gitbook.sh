#!/usr/bin/env bash

set -e

UPSTREAM="https://$GITHUB_TOKEN@github.com/$TRAVIS_REPO_SLUG.git"
MESSAGE="Rebuild doc, REV $TRAVIS_COMMIT: $TRAVIS_COMMIT_MESSAGE"
AUTHOR="$USER <>"

function setup(){
  npm install -g gitbook-cli
}

function build(){
  pushd docs
  gitbook install
  gitbook build
  popd
}

function publish(){
  pushd docs/_book
  git init
  git remote add upstream "$UPSTREAM"
  git fetch --prune upstream
  git reset upstream/gh-pages
  git add --all .
  if git commit --message "$MESSAGE" --author "$AUTHOR" ; then
    git push --quiet upstream HEAD:gh-pages
  fi
  popd
}

function main(){
  setup && build && publish
}

main
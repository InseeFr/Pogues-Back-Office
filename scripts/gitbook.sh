#!/usr/bin/env bash

set -e

UPSTREAM="https://$GITHUB_TOKEN@github.com/$TRAVIS_REPO_SLUG.git"

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
  git config user.name "$USER"
  git config user.email "antoine.codier@zenika.com"
  git remote add upstream "$UPSTREAM"
  git fetch --prune upstream
  git reset upstream/gh-pages
  touch .
  git add --all
  git commit --message "Rebuild doc, REV $TRAVIS_COMMIT: $TRAVIS_COMMIT_MESSAGE"
  git push --quiet upstream HEAD:gh-pages
  popd
}

function main(){
  setup && build && publish
}

main
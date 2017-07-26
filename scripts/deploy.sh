#!/usr/bin/env bash

function gitBook(){
  pushd docs
  gitbook install
  gitbook build
  mv _book /tmp/_book
  popd
  git checkout gh-pages
  mv /tmp/_book/* .
  git commit -m "Travis Bot: Deploy docs for commit $TRAVIS_COMMIT"
  git push
}

function main(){
 gitBook
}

main
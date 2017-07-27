#!/usr/bin/env bash

function gitBook(){
  echo "pushd docs"
  pushd docs
  echo "gitbook install"
  gitbook install
  echo "gitbook build"
  gitbook build
  echo "mv _book /tmp/_book"
  mv _book /tmp/_book$
   echo "popd"
  popd
   echo "GIT CHECKOUT ."
  git checkout .
   echo "git checkout gh-pages"
  git checkout gh-pages
    echo "MV /TMP/_BOOK/* ."
  mv /tmp/_book/* .
   echo "GIT ADD ."
  git add .
   echo "git commit"
  git commit -m "Travis Bot: Deploy docs for commit $TRAVIS_COMMIT"
  echo "git push"
  git push
}

function main(){
 gitBook
}

main
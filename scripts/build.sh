#!/usr/bin/env bash

FRONT_URL=""
FRONT_SOURCES=""
FRONT_BUNDLE=""

function get_front_sources(){
    FRONT_SOURCES=$(mktemp -d)
    git clone "$FRONT_URL" "$FRONT_SOURCES"
}

function build_front(){
    target=${FRONT_SOURCES?Please run get_front_sources() before running build_front()}
    pushd $target
    npm install && npm run build
    FRONT_BUNDLE="$PWD/dist"
    popd
}

function build(){
    mvn clean install -DskipTests
}

function update(){
    pushd target


}

function package(){

}

function main(){
    get_front_sources
    build_front
    build
    explode
    package
}

main
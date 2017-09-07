#!/usr/bin/env bash
#title           :build.sh
#description     :Builds and packages pogues backend and frontend in a war
#author		     :a-cordier
#==============================================================================
FINAL_WAR_NAME=${1?Final war name must be passed as first argument}
STATIC_GH_URL="https://github.com/InseeFr/Pogues"
MAIN_BRANCH="zenika-dev"

if [ "$TRAVIS_PULL_REQUEST" != "false" ];then
  echo "Won't deploy on pull request"
  exit 0
fi

if [ "$TRAVIS_BRANCH" != "$MAIN_BRANCH" ];then
  echo "Won't deploy: Not on branch $MAIN_BRANCH"
  exit 0
fi

# Pull front end sources from github
function get_static(){
    STATIC_SOURCES=$(mktemp -d)
    mv "$STATIC_SOURCES" "$TRAVIS_BUILD_DIR/frontend-$TRAVIS_BUILD_NUMBER"
    STATIC_SOURCES="$TRAVIS_BUILD_DIR/frontend-$TRAVIS_BUILD_NUMBER"
    git clone -b "$MAIN_BRANCH" "$STATIC_GH_URL" "$STATIC_SOURCES"
}

# Build frontend bundle
function build_static(){
    target=${STATIC_SOURCES?Please run get_front_sources() before running build_front()}
    pushd ${target}
    npm install && npm run build
    STATIC_BUNDLE="$PWD/dist"
    popd
}

function build(){
    mvn clean install -DskipTests -Dfinal.war.name="$FINAL_WAR_NAME"
}

# Update backend war file with front end assets
function package(){
    static=${STATIC_BUNDLE?Please run build_static() before running update()}
    for asset in $(ls ${static});do
        jar -uvf "target/${FINAL_WAR_NAME}.war" -C ${static} ${asset}
    done
}

function main(){
    build && get_static && build_static && package
}

main
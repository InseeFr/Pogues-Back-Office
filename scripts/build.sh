#!/usr/bin/env bash
#title           :build.sh
#description     :Builds and packages pogues backend and frontend in a war
#author		     :a-cordier
#==============================================================================
FINAL_WAR_NAME=${1?Final war name must be passed as first argument}
STATIC_GH_URL="https://github.com/InseeFr/Pogues"
MAIN_BRANCH="master"
HTTP_PROXY="http://proxy-rie.http.insee.fr:8080"

# Local Mode detection
if [[ (-z "$CI") && (-z "$TRAVIS") ]];then
    echo "Local Mode detected, setting proxy parameters"
    TRAVIS_BUILD_DIR="$PWD"
    TRAVIS_BUILD_NUMBER=$(date -I)
    git config --global http.proxy "$HTTP_PROXY"
    export http_proxy="$HTTP_PROXY"
fi

# Pull front end sources from github
function get_static(){
    echo "Pull frontend sources from $STATIC_GH_URL:$MAIN_BRANCH"
    STATIC_SOURCES=$(mktemp -d)
    git clone -b "$MAIN_BRANCH" "$STATIC_GH_URL" "$STATIC_SOURCES"
    mv "$STATIC_SOURCES" "$TRAVIS_BUILD_DIR/frontend-$TRAVIS_BUILD_NUMBER"
    STATIC_SOURCES="$TRAVIS_BUILD_DIR/frontend-$TRAVIS_BUILD_NUMBER"
}

# Build frontend bundle
function build_static(){
    echo "Build frontend sources from ${STATIC_SOURCES}"
    target=${STATIC_SOURCES?Please run get_static() before running build_static()}
    pushd ${target}
    npm install && npm run build
    STATIC_BUNDLE="$PWD/dist"
    popd
}

# Build backend web app
function build(){
    mvn clean install -DskipTests -Dfinal.war.name="$FINAL_WAR_NAME"
}

# Update backend war file with front end assets
function package(){
    echo "Package the whole thing in target/$FINAL_WAR_NAME.war"
    static=${STATIC_BUNDLE?Please run build_static() before running update()}
    for asset in $(ls ${static});do
        jar -uvf "target/${FINAL_WAR_NAME}.war" -C ${static} ${asset}
    done
}

# Clean frontend build directory (local mode only)
function clean(){
    rm -rf "$TRAVIS_BUILD_DIR/frontend-$TRAVIS_BUILD_NUMBER"
}

function main(){
    build && get_static && build_static && package
    if [[ (-z "$CI") && (-z "$TRAVIS") ]];then
        clean
    fi
}

main
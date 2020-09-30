#!/usr/bin/env bash
#title           :install_from_vch.sh
#description     :This script installs files stored on github to local mvn repo
#author		     :a-cordier
#==============================================================================

set -e

: ${1?Github url must be passed as a first argument}
: ${2?Group id must be passed as a second argument}
: ${3?Artifact id must be passed as a second argument}
: ${4?Version must be passed as a second argument}

GH_URL="$1"
GROUP_ID="$2"
ARTIFACT_ID="$3"
VERSION="$4"
TARGET=""

function get_sources(){
    TARGET=$(mktemp -d)
    git clone "$GH_URL" "$TARGET"
}

function install_files(){
    pushd "$TARGET"
 #   if [[ -n "$(git tag -l)" ]];then # If tag found, checkout last tag
 #       latest_tag=$(git describe --tags `git rev-list --tags --max-count=1`)
 #       echo "Found latest tag: $latest_tag"
 #       git checkout "$latest_tag"
 #   fi
    mvn clean install -DskipTests -Djar.finalName="$ARTIFACT_ID"
#  mvn install:install-file -Dfile=target/"$ARTIFACT_ID".jar -DgroupId="$GROUP_ID" -DartifactId="$ARTIFACT_ID" -Dversion="$VERSION" -Dpackaging=jar
    popd
}

function main(){
    get_sources && install_files
}

main
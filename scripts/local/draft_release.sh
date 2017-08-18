#!/usr/bin/env bash

MAIN_BRANCH="zenika-dev"
UPSTREAM="https://$GITHUB_TOKEN@github.com/$TRAVIS_REPO_SLUG.git"
MESSAGE="Generated tag from TravisCI for build $TRAVIS_BUILD_NUMBER"
AUTHOR="$USER <>"
BRANCH=$(git branch | sed -n '/\* /s///p')

if [ "$MAIN_BRANCH" != "$BRANCH" ];then
    echo "CANNOT DRAFT A RELEASE: NOT ON BRANCH $MAIN_BRANCH"
    exit 1
fi

# bump version
function bump(){
    pushd `dirname $0` > /dev/null
        script_path=`pwd`
    popd > /dev/null
    git fetch --all
    VERSION=$(python3 ${script_path}/bump.py)
    mvn versions:set -DnewVersion="$VERSION" -DgenerateBackupPoms=false
}

# Push a new tag to trigger travis deployment of a gh release
function tag(){
    : ${VERSION?Please run bump() before running tag()}
    git tag "v$VERSION" --annotate --message "$MESSAGE"
    git push --quiet "$UPSTREAM" --tags
}

function main(){
    bump && tag
}

main
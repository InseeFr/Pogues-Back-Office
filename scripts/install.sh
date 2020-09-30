#!/usr/bin/env bash

set -e

#FINAL_WAR_NAME=${1?Final war name must be passed as first argument}
GROUP_ID="fr.insee"
POGUES_MODEL_URL="https://github.com/InseeFr/Pogues-Model"
POGUES_MODEL_ARTIFACT_ID="pogues-model"
POGUES_MODEL_VERSION="0.1"

function install_pogues_model(){
    bash scripts/gh2mvn.sh  "$POGUES_MODEL_URL" "$GROUP_ID" "$POGUES_MODEL_ARTIFACT_ID" "$POGUES_MODEL_VERSION"
}


function main(){
    install_pogues_model
#    mvn clean install -DskipTests -Dfinal.war.name="$FINAL_WAR_NAME"
}

main
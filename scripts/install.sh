#!/usr/bin/env bash

set -e

#FINAL_WAR_NAME=${1?Final war name must be passed as first argument}
GROUP_ID="fr.insee"
POGUES_MODEL_URL="https://github.com/InseeFr/Pogues-Model"
POGUES_MODEL_ARTIFACT_ID="pogues-model"
POGUES_MODEL_VERSION="0.1"
ENO_CORE_GROUP_ID="fr.insee.eno"
ENO_CORE_URL="https://github.com/InseeFr/Eno.git"
ENO_CORE_ARTIFACT_ID="eno-core"
ENO_CORE_VERSION="1.0.2"

function install_pogues_model(){
    bash scripts/gh2mvn.sh  "$POGUES_MODEL_URL" "$GROUP_ID" "$POGUES_MODEL_ARTIFACT_ID" "$POGUES_MODEL_VERSION"
}

function install_eno(){
    bash scripts/gh2mvn.sh  "$ENO_CORE_URL" "$ENO_CORE_GROUP_ID" "$ENO_CORE_ARTIFACT_ID" "$ENO_CORE_VERSION"
}

function main(){
    install_pogues_model
    install_eno
#    mvn clean install -DskipTests -Dfinal.war.name="$FINAL_WAR_NAME"
}

main
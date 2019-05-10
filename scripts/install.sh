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
ENO_CORE_VERSION="1.1.0"
LUNATIC_MODEL_URL="https://github.com/InseeFr/Lunatic-Model"
LUNATIC_MODEL_ARTIFACT_ID="lunatic-model"
LUNATIC_MODEL_VERSION="0.1"

function install_pogues_model(){
    bash scripts/gh2mvn.sh  "$POGUES_MODEL_URL" "$GROUP_ID" "$POGUES_MODEL_ARTIFACT_ID" "$POGUES_MODEL_VERSION"
}

function install_eno(){
    bash scripts/gh2mvn.sh  "$ENO_CORE_URL" "$ENO_CORE_GROUP_ID" "$ENO_CORE_ARTIFACT_ID" "$ENO_CORE_VERSION"
}

function install_lunatic_model(){
    bash scripts/gh2mvn.sh  "$LUNATIC_MODEL_URL" "$GROUP_ID" "$LUNATIC_MODEL_ARTIFACT_ID" "$LUNATIC_MODEL_VERSION"
}

function main(){
    install_pogues_model
    install_eno
    install_lunatic_model
#    mvn clean install -DskipTests -Dfinal.war.name="$FINAL_WAR_NAME"
}

main
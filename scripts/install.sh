#!/usr/bin/env bash

set -e

GROUP_ID="insee.fr"
POGUES_MODEL_URL="https://github.com/InseeFr/Pogues-Model"
POGUES_MODEL_ARTIFACT_ID="pogues-model"
POGUES_MODEL_VERSION="0.1"
ENO_CORE_URL="https://github.com/InseeFr/Eno.git"
ENO_CORE_ARTIFACT_ID="eno-core"
ENO_CORE_VERSION="0.1"

function install_pogues_model(){
    sh gh2mvn.sh  "$POGUES_MODEL_URL" "$GROUP_ID" "$POGUES_MODEL_ARTIFACT_ID" "$POGUES_MODEL_VERSION"
}

function install_eno(){
    sh gh2mvn.sh  "$ENO_CORE_URL" "$GROUP_ID" "$ENO_CORE_ARTIFACT_ID" "$ENO_CORE_VERSION"
}

function main(){
    install_pogues_model
    install_eno
    mvn clean install -DskipTests
}

main
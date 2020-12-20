. scripts/utils.sh

echo '######## - (COMMON) setup variables - ########'
setupCommonENV
export CC_NAME=mycc03
export INIT_FLAG='{"Function":"Init","Args":["init"]}'
if [[ $# -ge 1 ]]; then
    export CC_NAME=$1
fi

if [[ $# -ge 2 ]]; then
    if [[ "$2" == "upgrade" ]]; then
        export INIT_FLAG='{"Function":"Init","Args":["upgrade"]}'
    fi
fi

echo "'CHAINCODE_NAME' set to '$CC_NAME'"
echo "'CHAINCODE_LANG' set to '$CC_LANG'"
echo "'CHAINCODE_PATH' set to '$CC_PATH'"

echo '######## - (ORG1) init chaincode - ########'
setupPeerENV1
set -x
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS --tlsRootCertFiles $PEER0_ORG1_TLS_ROOTCERT_FILE \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    --isInit -c $INIT_FLAG
else
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    --isInit -c $INIT_FLAG
fi
set +x
sleep 3

setupPeerENV1
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"org.hyperledger.fabric:GetMetadata", "Args":[]}' | jq -C

echo '######## - (ORG1) register user "Tom@Org1" - ########'
setupPeerENV1
set -x
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS --tlsRootCertFiles $PEER0_ORG1_TLS_ROOTCERT_FILE \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"RegisterUser","Args":["Tom"]}'
else
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"RegisterUser","Args":["Tom"]}'
fi
set +x
sleep 3
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAccount", "Args":["Tom"]}' | jq -C


echo '######## - (ORG1) register asset "ASSET_0001" owned by "Tom@ORG1" - ########'
setupPeerENV1
export TRANSIENT_INPUT=$(echo -n "{\"name\":\"IPad\", \"price\":150, \"owner\":\"Tom\"}" | base64 | tr -d \\n)
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS --tlsRootCertFiles $PEER0_ORG1_TLS_ROOTCERT_FILE \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"RegisterAsset","Args":[]}' \
    --transient "{\"asset\":\"$TRANSIENT_INPUT\"}"
else
    peer chaincode invoke -o ${ORDERER_ADDRESS} -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"RegisterAsset","Args":[]}' \
    --transient "{\"asset\":\"$TRANSIENT_INPUT\"}"
fi
sleep 3
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAsset", "Args":["ASSET_0001"]}' | jq -C

echo '######## - (ORG2) register user "Jack@ORG2" - ########'
setupPeerENV2
set -x
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS --tlsRootCertFiles $PEER0_ORG1_TLS_ROOTCERT_FILE \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"function":"RegisterUser","Args":["Jack"]}'
else
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"function":"RegisterUser","Args":["Jack"]}'
fi
set +x
sleep 3
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAccount", "Args":["Jack"]}' | jq -C

echo '######## - (ORG2) register asset "ASSET_0002" owned by "Jack@ORG2" - ########'
setupPeerENV2
export TRANSIENT_INPUT=$(echo -n "{\"name\":\"TV\", \"price\":200, \"owner\":\"Jack\"}" | base64 | tr -d \\n)
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS --tlsRootCertFiles $PEER0_ORG1_TLS_ROOTCERT_FILE \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"RegisterAsset","Args":[]}' \
    --transient "{\"asset\":\"$TRANSIENT_INPUT\"}"
else
    peer chaincode invoke -o ${ORDERER_ADDRESS} -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"RegisterAsset","Args":[]}' \
    --transient "{\"asset\":\"$TRANSIENT_INPUT\"}"
fi
sleep 3
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAsset", "Args":["ASSET_0002"]}' | jq -C

echo '######## - (ORG1) transfer asset "ASSET_0001" from "Tom" to "Jack" - ########'
setupPeerENV1
export TRANSIENT_INPUT=$(echo -n "{\"no\":\"ASSET_0001\", \"seller\":\"Tom\", \"buyer\":\"Jack\"}" | base64 | tr -d \\n)
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS --tlsRootCertFiles $PEER0_ORG1_TLS_ROOTCERT_FILE \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"TransferAsset","Args":[]}' \
    --transient "{\"transfer\":\"$TRANSIENT_INPUT\"}"
else
    peer chaincode invoke -o ${ORDERER_ADDRESS} -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"TransferAsset","Args":[]}' \
    --transient "{\"transfer\":\"$TRANSIENT_INPUT\"}"
fi
sleep 3
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAsset", "Args":["ASSET_0001"]}' | jq -C
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAccount", "Args":["Tom"]}' | jq -C
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAccount", "Args":["Jack"]}' | jq -C

echo '######## - (ORG2) transfer asset "ASSET_0002" from "Jack" to "Tom" - ########'
setupPeerENV2
export TRANSIENT_INPUT=$(echo -n "{\"no\":\"ASSET_0002\", \"seller\":\"Jack\", \"buyer\":\"Tom\"}" | base64 | tr -d \\n)
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS --tlsRootCertFiles $PEER0_ORG1_TLS_ROOTCERT_FILE \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"TransferAsset","Args":[]}' \
    --transient "{\"transfer\":\"$TRANSIENT_INPUT\"}"
else
    peer chaincode invoke -o ${ORDERER_ADDRESS} -C $CHANNEL_NAME -n ${CC_NAME}  \
    --peerAddresses $PEER0_ORG1_ADDRESS \
    --peerAddresses $PEER0_ORG2_ADDRESS --tlsRootCertFiles $PEER0_ORG2_TLS_ROOTCERT_FILE \
    -c '{"Function":"TransferAsset","Args":[]}' \
    --transient "{\"transfer\":\"$TRANSIENT_INPUT\"}"
fi
sleep 3
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAsset", "Args":["ASSET_0002"]}' | jq -C
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"QueryAccount", "Args":["Tom"]}' | jq -C
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"example03.asset_contract:QueryAccount", "Args":["Jack"]}' | jq -C

echo '############# END ###############'
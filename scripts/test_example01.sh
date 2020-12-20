. scripts/utils.sh

echo '######## - (COMMON) setup variables - ########'
setupCommonENV
export CC_NAME=mycc

if [[ $# -ge 1 ]]; then
    export CC_NAME=$1
fi

echo "'CHAINCODE_NAME' set to '$CC_NAME'"
echo "'CHAINCODE_LANG' set to '$CC_LANG'"
echo "'CHAINCODE_PATH' set to '$CC_PATH'"

# echo '######## - (ORG1) init chaincode - ########'
setupPeerENV1
set -x
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    --isInit -c '{"Function":"Init","Args":[]}'
else
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    --isInit -c '{"Function":"Init","Args":[]}'
fi
set +x
sleep 10

echo '######## - (ORG1) query chaincode - ########'
setupPeerENV1
set -x
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"Hi", "Args":[]}'
set +x

echo '######## - (ORG2) query chaincode - ########'
setupPeerENV2
set -x
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"Hi", "Args":[]}'
set +x


# echo '######## - (ORG1) SetValue chaincode - ########'
setupPeerENV1
set -x
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    -c '{"Function":"SetValue","Args":["NewKey1_ORG1","NewValue1_ORG1"]}'    
else
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    -c '{"Function":"SetValue","Args":["NewKey1_ORG1","NewValue1_ORG1"]}'    
fi

if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    -c '{"Function":"SetValue","Args":["NewKey2_ORG1","NewValue2_ORG1"]}'    
else
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    -c '{"Function":"SetValue","Args":["NewKey2_ORG1","NewValue2_ORG1"]}'    
fi
set +x


echo '######## - (ORG2) query chaincode - ########'
setupPeerENV2
set -x
if [[ "$CORE_PEER_TLS_ENABLED" == "true" ]]; then
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} --ordererTLSHostnameOverride orderer.example.com --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    -c '{"Function":"SetValue","Args":["NewKey1_ORG2","NewValue3_ORG2"]}'    
else
    peer chaincode invoke \
    -o ${ORDERER_ADDRESS} \
    -C $CHANNEL_NAME -n ${CC_NAME}  \
    -c '{"Function":"SetValue","Args":["NewKey1_ORG2","NewValue3_ORG2"]}'    
fi
set +x
sleep 10


echo '######## - (ORG1) GetValue chaincode - ########'
setupPeerENV1
set -x
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"GetValue", "Args":["NewKeyORG1"]}'
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"GetValue", "Args":["NewKey1_ORG1"]}'
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"GetValue", "Args":["NewKey2_ORG1"]}'
set +x


echo '######## - (ORG1) GetValue chaincode - ########'
setupPeerENV2
set -x
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"GetValue", "Args":["NewKey1_ORG2"]}'
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"GetValue", "Args":["NewKey2_ORG2"]}'
peer chaincode query -C $CHANNEL_NAME -n $CC_NAME -c '{"Function":"GetValue", "Args":["NewKey3_ORG2"]}'
set +x

echo '############# END ###############'
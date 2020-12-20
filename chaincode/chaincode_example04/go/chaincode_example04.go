package main

import (
	"example03/asset"
	"fmt"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
	"github.com/hyperledger/fabric-contract-api-go/metadata"
)

func main() {
	assetContract := new(asset.SmartContract)
	assetContract.Name = "example03.asset_contract"
	assetContract.Info = metadata.InfoMetadata{
		Title: "Asset Contract",
		Description: "SmartContract Example 03 - Blockchain Workshop",
		Version: "1.0.0",
		Contact: &metadata.ContactMetadata{
			Name: "Bing",
			Email: "23227732@qq.com",
		},
	}

	contractEngine, err := contractapi.NewChaincode(assetContract)
	contractEngine.Info = metadata.InfoMetadata{
		Title: "SmartContract Set Example 03",
		Version: "1.0.0",
	}


	if err != nil {
		fmt.Printf("Error creating Example03 chaincode: %s", err.Error())
		fmt.Println()
		return
	}


	//contractEngine.DefaultContract = assetContract.Name

	err = contractEngine.Start()
	if err != nil {
		fmt.Printf("Error starting Example03 chaincode: %s", err.Error())
		fmt.Println()
	}
}

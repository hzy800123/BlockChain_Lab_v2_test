package main

import (
	"fmt"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
	"github.com/hyperledger/fabric-contract-api-go/metadata"
	"strconv"
)

// SmartContract definition
type MyContract struct {
	contractapi.Contract
}

type ResponseResult struct {
	Status string `json:"status"`
	Result string `json:"result"`
	Error  string `json:"error"`
}

func newSuccess(result string) ResponseResult {
	return ResponseResult{
		Status: "OK",
		Result: result,
	}
}

func newError(err error) ResponseResult {
	return ResponseResult{
		Status: "Error",
		Error: err.Error(),
	}
}

// Init - initialize contract
func (mc *MyContract) Init(ctx contractapi.TransactionContextInterface, keyA string, valA int, keyB string, valB int) ResponseResult {
	stub := ctx.GetStub()

	// Initialize the contract
	fmt.Printf(`[{"key": "%s", "value": %d}, {"key": "%s", "value": %d}]`, keyA, valA, keyB, valB)
	fmt.Println()

	// Write the state to the ledger
	err := stub.PutState(keyA, []byte(strconv.Itoa(valA)))
	if err != nil {
		return newError(err)
	}

	err = stub.PutState(keyB, []byte(strconv.Itoa(valB)))
	if err != nil {
		return newError(err)
	}

	return newSuccess("")
}

func (mc *MyContract) Query(ctx contractapi.TransactionContextInterface, key string) ResponseResult {
	stub := ctx.GetStub()
	// Get the state from the ledger
	valBytes, err := stub.GetState(key)
	if err != nil {
		return newError(err)
	}

	if valBytes == nil || len(valBytes) == 0 {
		return newError(fmt.Errorf(`account "%s" does not exist`, key))
	}

	return newSuccess(string(valBytes))
}

func (mc *MyContract) Transfer(ctx contractapi.TransactionContextInterface, keyFrom string, keyTo string, transAmt int) ResponseResult {
	stub := ctx.GetStub()
	// Get the state from the ledger
	// TODO: will be nice to have a GetAllState call to ledger
	valBytesFrom, err := stub.GetState(keyFrom)
	if err != nil {
		return newError(err)
	}
	if valBytesFrom == nil || len(valBytesFrom) == 0 {
		return newError(fmt.Errorf(`account of "%s" not existing`, keyFrom))
	}
	valFrom, _ := strconv.Atoi(string(valBytesFrom))

	valBytesTo, err := stub.GetState(keyTo)
	if err != nil {
		return newError(err)
	}
	if valBytesTo == nil || len(valBytesTo) == 0 {
		return newError(fmt.Errorf(`account of "%s" not existing`, keyTo))
	}
	valTo, _ := strconv.Atoi(string(valBytesTo))

	valFrom = valFrom - transAmt

	if valFrom <= 0 {
		return newError(fmt.Errorf("invalid transaction amount, balance is not enough for transfer"))
	}

	valTo = valTo + transAmt
	fmt.Printf("from = %d, to = %d\n", valFrom, valTo)

	// Write the state back to the ledger
	err = stub.PutState(keyFrom, []byte(strconv.Itoa(valFrom)))
	if err != nil {
		return newError(err)
	}

	err = stub.PutState(keyTo, []byte(strconv.Itoa(valTo)))
	if err != nil {
		return newError(err)
	}

	return newSuccess("")
}

func main() {
	assetContract := new(MyContract)
	assetContract.Name = "example02.MyContract"
	assetContract.Info = metadata.InfoMetadata{
		Title: "MyContract",
		Description: "SmartContract Example 02 - Blockchain Workshop",
		Version: "1.0.0",
		Contact: &metadata.ContactMetadata{
			Name: "Bing",
			Email: "23227732@qq.com",
		},
	}

	contractEngine, err := contractapi.NewChaincode(assetContract)
	if err != nil {
		fmt.Printf("error creating Example02 chaincode: %s", err.Error())
		fmt.Println()
		return
	}
	contractEngine.Info = metadata.InfoMetadata{
		Title: "SmartContract Set Example 01",
		Version: "1.0.0",
	}

	err = contractEngine.Start()
	if err != nil {
		fmt.Printf("error starting Example02 chaincode: %s", err.Error())
		fmt.Println()
	}
}

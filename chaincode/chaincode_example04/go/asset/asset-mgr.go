package asset

import (
	"encoding/json"
	"errors"
	"fmt"
	"strconv"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// SmartContract definition
type SmartContract struct {
	contractapi.Contract
}

// Account definition
type QueryAccountResult struct {
	Name    string `json:"name"`
	Amount  int    `json:"amount"`
}

// Asset definition
type Asset struct {
	No     string `json:"no"`
	Name   string `json:"name"`
	Price  int    `json:"price"`
}

// AssetDetail definition (private part)
type Detail struct {
	No    string `json:"no"`
	Owner string `json:"owner"`
}

// QueryResult structure used for handling result of query
type QueryAssetResult struct {
	No     string  `json:"no"`
	Asset  *Asset  `json:"asset"`
	Detail *Detail `json:"detail"`
}

//noinspection ALL
const KEY_ASSET_COUNT = "ASSET_COUNT"
//noinspection ALL
const KEY_PREFIX_WALLET = "WALLET_"
//noinspection ALL
const KEY_ASSET_NO = "ASSET_%04d"
//noinspection ALL
const KEY_DETAIL_PREFIX = "DETAIL_"
//noinspection ALL
const TRANSIENT_KEY_ASSET = "asset"
//noinspection ALL
const TRANSIENT_KEY_TRANS = "transfer"
//noinspection ALL
const TRANSIENT_KEY_QUERY = "query"
//noinspection ALL
const TRANSIENT_KEY_USER = "wallet"

// Initialize the ledger
func (s *SmartContract) Init(ctx contractapi.TransactionContextInterface, flag string) error {
	if flag == "init" {
		//init asset count
		err := ctx.GetStub().PutState(KEY_ASSET_COUNT, []byte("0"))
		if err != nil {
			fmt.Printf("SmartContract initialization failed. err: %s", err)
			fmt.Println()
		} else {
			fmt.Println("initialized. (account: 0)")
		}

		return err
	} else if flag == "upgrade" {
		assetCount,_ := ctx.GetStub().GetState(KEY_ASSET_COUNT)
		fmt.Printf("upgraded. (account: %s)", string(assetCount))
		fmt.Println()
	}

	//NOP when upgrade
	return nil
}

// Create wallet wallet with initial amount of 1000
func (s *SmartContract) RegisterUser(ctx contractapi.TransactionContextInterface, userName string) error {
	stub := ctx.GetStub()

	err :=  stub.PutState(KEY_PREFIX_WALLET + userName, []byte("1000"))
	if err != nil {
		return fmt.Errorf("failed to initialize wallet. %s", err.Error())
	}
	fmt.Printf("initialized wallet %s (amount: 1000) successfully", userName)
	fmt.Println()

	return nil
}

func (s *SmartContract) QueryAccount(ctx contractapi.TransactionContextInterface, userName string) (*QueryAccountResult, error) {
	amountByte,err := ctx.GetStub().GetState(KEY_PREFIX_WALLET + userName)
	if err != nil {
		return nil, err
	}

	amountStr := string(amountByte)
	amount, err := strconv.Atoi(amountStr)
	if err != nil {

	}

	result := &QueryAccountResult{
		Name: userName,
		Amount: amount,
	}

	return result, nil
}

// RegisterAsset adds a base set of asset to the ledger
func (s *SmartContract) RegisterAsset(ctx contractapi.TransactionContextInterface) (*Asset, error) {
	stub := ctx.GetStub()
	type AssetRegisterInput struct {
		Name  string `json:"name"`
		Price int    `json:"price"`
		Owner string `json:"owner"`
	}

	transMap, err := stub.GetTransient()
	if err != nil {
		return nil, fmt.Errorf("failed to read transient. %s", err.Error())
	}

	assetInputJSONBytes, ok := transMap[TRANSIENT_KEY_ASSET]
	fmt.Printf("registerAsset - input: %s", string(assetInputJSONBytes))
	fmt.Println()
	if !ok {
		return nil, errors.New("asset must be a key in the transient map")
	}
	if len(assetInputJSONBytes) == 0 {
		return nil, errors.New("input asset in the transient map must be a non-empty JSON string")
	}

	assetInput := new(AssetRegisterInput)
	err = json.Unmarshal(assetInputJSONBytes, assetInput)
	if err != nil {
		return nil, fmt.Errorf("failed to decode JSON of: %s", string(assetInputJSONBytes))
	}

	// ASSET_COUNT++
	assetNoBaseBytes,_ := stub.GetState(KEY_ASSET_COUNT)
	assetNoBase,_ := strconv.Atoi(string(assetNoBaseBytes))
	err = stub.PutState(KEY_ASSET_COUNT, []byte(strconv.Itoa(assetNoBase + 1)))
	if err != nil {
		return nil, err
	}

	// generate & save asset
	asset := &Asset{
		No:    fmt.Sprintf(KEY_ASSET_NO, assetNoBase + 1),
		Name:  assetInput.Name,
		Price: assetInput.Price,
	}
	assetJSONBytes, err := json.Marshal(asset)
	if err != nil {
		return nil, errors.New(err.Error())
	}
	fmt.Printf("Asset - save : %s", string(assetJSONBytes))
	fmt.Println()

	err = stub.PutState(asset.No, assetJSONBytes)
	if err != nil {
		return nil, err
	}

	fmt.Println(string(assetJSONBytes))

	// generate & save asset detail
	assetDetail := &Detail{
		No:    KEY_DETAIL_PREFIX + asset.No,
		Owner: assetInput.Owner,
	}
	assetDetailJSONBytes, err := json.Marshal(assetDetail)
	if err != nil {
		return nil, err
	}
	err = stub.PutState(assetDetail.No, assetDetailJSONBytes)
	if err != nil {
		return nil, err
	}

	fmt.Println(string(assetDetailJSONBytes))
	return asset, nil
}

// QueryAsset returns the asset stored in the world state with given id
func (s *SmartContract) QueryAsset(ctx contractapi.TransactionContextInterface, assetNo string) (*QueryAssetResult, error) {
	asset,err := s.queryAsset(ctx, assetNo)
	if err != nil {
		return nil, err
	}

	result := &QueryAssetResult{
		No:    assetNo,
		Asset: asset,
	}

	assetDetail,_ := s.queryAssetDetail(ctx, assetNo)
	if assetDetail != nil {
		result.Detail = assetDetail
	}

	return result, nil
}

// private:queryAsset
func (s *SmartContract) queryAsset(ctx contractapi.TransactionContextInterface, assetNo string) (*Asset, error) {
	fmt.Printf("query asset: %s", assetNo)
	fmt.Println()
	assetBytes, err := ctx.GetStub().GetState(assetNo)

	if err != nil {
		return nil, fmt.Errorf("failed to read asset of 'No:%s'. %s", assetNo, err.Error())
	}

	if assetBytes == nil {
		return nil, fmt.Errorf("asset of 'No:%s' does not exist", assetNo)
	}

	fmt.Printf("read: %s", string(assetBytes))
	fmt.Println()

	asset := new(Asset)
	err = json.Unmarshal(assetBytes, asset)
	fmt.Printf(`Unmarshal: {"No":"%s","Name":"%s","Price":%d}`, asset.No, asset.Name, asset.Price)
	fmt.Println()
	if err != nil {
		return nil, err
	}

	return asset, nil
}

// private:queryAssetDetail
func (s *SmartContract) queryAssetDetail(ctx contractapi.TransactionContextInterface, assetNo string) (*Detail, error) {
	detailBytes, err := ctx.GetStub().GetState(KEY_DETAIL_PREFIX + assetNo)
	if err == nil && detailBytes != nil {
		fmt.Println(string(detailBytes))
		assetDetail := new(Detail)
		err = json.Unmarshal(detailBytes, assetDetail)

		if err != nil {
			return nil, err
		}

		return assetDetail, nil
	}

	return nil, nil
}


// ListAssets returns all assets found in world state
func (s *SmartContract) ListAssets(ctx contractapi.TransactionContextInterface) ([]Asset, error) {
	startKey := fmt.Sprintf(KEY_ASSET_NO, 1)
	endKey := fmt.Sprintf(KEY_ASSET_NO, 9999)

	resultsIterator, err := ctx.GetStub().GetStateByRange(startKey, endKey)

	if err != nil {
		return nil, err
	}
	//noinspection GoUnhandledErrorResult
	defer resultsIterator.Close()

	var results []Asset

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()

		if err != nil {
			return nil, err
		}

		asset := new(Asset)
		err = json.Unmarshal(queryResponse.Value, asset)
		if err != nil {
			return nil, err
		}
		results = append(results, *asset)
	}

	return results, nil
}

// Transfer change the ownership of asset, and got money from buyer
func (s *SmartContract) TransferAsset(ctx contractapi.TransactionContextInterface) error {
	// get transient input
	type AssetTransferInput struct {
		No     string `json:"no"`
		Buyer  string `json:"buyer"`
		Seller string `json:"seller"`
	}
	stub := ctx.GetStub()
	transMap, err := stub.GetTransient()
	if err != nil {
		return fmt.Errorf("failed to read transient %s", err.Error())
	}

	transferJSON, ok := transMap[TRANSIENT_KEY_TRANS]
	if !ok {
		return errors.New("key in the transient map is not correct")
	}
	if len(transferJSON) == 0 {
		return errors.New("transfer request in the transient map must be a non-empty JSON string")
	}
	fmt.Printf("transfer, input: %s", transferJSON)
	fmt.Println()

	transferInput := new(AssetTransferInput)
	err = json.Unmarshal(transferJSON, transferInput)
	if err != nil {
		return fmt.Errorf("failed to decode JSON of transient: %s", transferJSON)
	}

	assetResult, err := s.QueryAsset(ctx, transferInput.No)
	if err != nil {
		return err
	}

	// change owner
	if assetResult.Detail != nil {
		if assetResult.Detail.Owner != transferInput.Seller  {
			fmt.Printf("%s !+ %s", assetResult.Detail.Owner, transferInput.Seller)
			fmt.Println()
			return errors.New("failed to transfer asset: wrong ownership")
		}

		assetResult.Detail.Owner = transferInput.Buyer
		assetDetailBytes, err := json.Marshal(assetResult.Detail)
		if err != nil {
			return err
		}
		err = stub.PutState(assetResult.No, assetDetailBytes)
		if err != nil {
			fmt.Printf("no permission to access the private collection. err: %s", err)
			fmt.Println()
		}
		fmt.Printf("asset detail added to %s", transferInput.Buyer)
		fmt.Println()
		fmt.Printf(string(assetDetailBytes))
		fmt.Println()
	}
	// payment
	err = s.payment(ctx, transferInput.Buyer, transferInput.Seller, assetResult.Asset.Price)
	if err != nil {
		return err
	}
	assetBytes, _ := json.Marshal(assetResult.Asset)

	return ctx.GetStub().PutState(assetResult.No, assetBytes)
}

//private
func (s *SmartContract) payment(ctx contractapi.TransactionContextInterface, buyer string, seller string, amount int) error {
	if amount <= 0 {
		return errors.New("amount for payment is invalid")
	}

	stub := ctx.GetStub()

	//for seller's side
	amountFromBytes,err := stub.GetState(KEY_PREFIX_WALLET + buyer)
	if err != nil {
		return err
	}

	amountFromStr := string(amountFromBytes)
	amountFrom, err := strconv.Atoi(amountFromStr)
	if err != nil {
		return fmt.Errorf("wallet of '%s' does exist. err: %s", buyer, err.Error())
	}

	if amountFrom < amount {
		return errors.New("no enough amount for payment")
	}

	amountFrom = amountFrom - amount
	amountFromStr = strconv.Itoa(amountFrom)
	err = stub.PutState(KEY_PREFIX_WALLET+buyer, []byte(amountFromStr))
	if err != nil {
		fmt.Printf("no permission to access the private collection. err: %s", err)
		fmt.Println()
	}

	fmt.Printf("%s: %d", buyer, amountFrom)
	fmt.Println()

	//for buyer's side
	amountToBytes,err := stub.GetState(KEY_PREFIX_WALLET + seller)
	if err != nil {
		return err
	}

	amountToStr := string(amountToBytes)
	amountTo, err := strconv.Atoi(amountToStr)
	if err != nil {
		return fmt.Errorf("wallet of '%s' does exist. err: %s", seller, err.Error())
	}

	amountTo = amountTo + amount
	amountToStr = strconv.Itoa(amountTo)
	err = stub.PutState(KEY_PREFIX_WALLET+seller, []byte(amountToStr))
	if err != nil {
		fmt.Printf("no permission to access the private collection. err: %s", err)
		fmt.Println()
	}

	fmt.Printf("Payment performed. (%s: %d)", seller, amountTo)
	fmt.Println()

	return nil
}
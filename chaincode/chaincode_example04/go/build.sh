export GOPROXY=https://goproxy.cn
rm -rf vendor
rm -r go.mod
rm go.sum
rm example04

go mod init example04
go mod vendor
go build
export GOPROXY=https://goproxy.cn
rm -rf vendor
rm go.mod
rm go.sum
rm example02

go mod init example02
go mod vendor
go build
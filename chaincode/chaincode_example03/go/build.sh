export GOPROXY=https://goproxy.cn
rm -rf vendor
rm go.mod
rm go.sum
rm example03

go mod init example03
go mod vendor
go build
'use strict';

const shim = require('fabric-shim');
const util = require('util');

function stringToUint8Array(str) {
    let arr = [];
    for (let i = 0, j = str.length; i < j; ++i) {
        arr.push(str.charCodeAt(i));
    }

    return new Uint8Array(arr);
}

let Chaincode = class {
    async Init(stub) {
        let ret = stub.getFunctionAndParameters();
        console.info(ret);
        try {
            if (ret.fcn === "init") {
                if (ret.params.length !== 4) {
                    return shim.error(stringToUint8Array("Parameter is not correct."));
                }

                await this.init(stub, ret.params);
                return shim.success();
            } else if (ret.fcn === "upgrade") {
                await this.upgrade(stub, ret.params);
                return shim.success();
            }
        } catch(err) {
            return shim.error(stringToUint8Array(err.message));
        }

        return shim.error(stringToUint8Array("Function is not correct."));
    }

    async init(stub, args) {
        let keyA   = args[0];
        let valueA = args[1];
        let keyB   = args[2];
        let valueB = args[3];

        if (typeof parseInt(valueA) !== 'number' || typeof parseInt(valueB) !== 'number') {
            throw new Error("Expecting integer value for asset holding");
        }

        await stub.putState(keyA, Buffer.from(valueA));
        await stub.putState(keyB, Buffer.from(valueB));
    }

    async upgrade(stub, args) {
        //TO BE DONE.
    }

    async Invoke(stub) {
        let ret = stub.getFunctionAndParameters();
        console.info(ret);
        try {
            if (ret.fcn === "query") {
                if (ret.params.length !== 4) {
                    return shim.error(stringToUint8Array("Parameter is not correct."));
                }

                let payload = await this.query(stub, ret.params);
                return shim.success(payload);
            } else if (ret.fcn === "transfer") {
                let payload = await this.transfer(stub, ret.params);
                return shim.success(payload);
            }
        } catch(err) {
            return shim.error(stringToUint8Array(err.message));
        }

        return shim.error(stringToUint8Array("Function is not correct."));
    }

    async query(stub, args) {
        if (args.length === 0) {
            throw new Error("Parameter is not correct.");
        }

        let valueBytes = await stub.getState(args[0]);
        if (valueBytes.length === 0) {
            throw new Error(`Account '${args[0]}' does not exist.`);
        }

        return valueBytes.toString();
    }

    async transfer(stub, args) {
        if (args.length === 3) {
            throw new Error("Parameter is not correct.");
        }

        let keyA        = args[0];
        let keyB        = args[1];
        let transValue  = args[2];

        // query current balance from account A
        let aBytes = await stub.getState(keyA);
        if (!aBytes) {
            throw new Error(`Failed to get state of asset holder ${keyA}`);
        }

        // query current balance from account B
        let bBytes = await stub.getState(keyB);
        if (!bBytes) {
            throw new Error(`Failed to get state of asset holder ${keyB}`);
        }

        let aVal = parseInt(aBytes.toString());
        let bVal = parseInt(bBytes.toString());

        // Perform the transferring
        let amount = parseInt(transValue);
        if (typeof amount !== 'number') {
            throw new Error('Expecting integer value for amount to be transaferred');
        }

        aVal = aVal - amount;
        // ensure Account A balance is enough
        if (aVal < 0) {
            throw new Error('Account balance is not enough to transfer.');
        }

        bVal = bVal + amount;

        await stub.putState(keyA, Buffer.from(aVal.toString()));
        await stub.putState(keyB, Buffer.from(bVal.toString()));
    }
}

shim.start(new Chaincode());

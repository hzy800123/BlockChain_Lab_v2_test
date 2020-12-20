'use strict';

const { Contract } = require('fabric-contract-api');

class MyContract extends Contract {

    constructor() {
        super("example02.MyContract");
    }

    // Init - initialize contract
    async Init(ctx, keyA, valA, keyB, valB)  {
        ctx.stub
        if (typeof parseInt(valA) !== 'number' || typeof parseInt(valB) !== 'number') {
            throw new Error("expecting integer value for asset holding");
        }
        console.log(typeof ctx);
        // Write the state to the ledger
        await ctx.stub.PutState(keyA, Buffer.from(valA))
        await ctx.stub.PutState(keyB, Buffer.from(valB))
    }

    async Query(ctx, key) {
        let valueBytes = await stub.getState(key);
        if (valueBytes.length === 0) {
            throw new Error(`account '${key}' does not exist`);
        }

        return {
            status: "OK",
            result: valueBytes.toString()
        };
    }

    async Transfer(stub, keyFrom, keyTo, transAmt) {
        // query current balance from account A
        let valBytesFrom = await stub.getState(keyFrom);
        if (!valBytesFrom) {
            throw new Error(`Failed to get state of asset holder ${keyFrom}`);
        }

        // query current balance from account B
        let valBytesTo = await stub.getState(keyTo);
        if (!valBytesTo) {
            throw new Error(`Failed to get state of asset holder ${keyTo}`);
        }

        let valFrom = parseInt(valBytesFrom.toString());
        let valTo = parseInt(valBytesTo.toString());

        // Perform the transferring
        let amount = parseInt(transAmt);
        if (typeof amount !== 'number') {
            throw new Error('Expecting integer value for amount to be transferred');
        }

        valFrom = valFrom - amount;
        // ensure Account A balance is enough
        if (valFrom < 0) {
            throw new Error('Account balance is not enough to transfer.');
        }

        valTo = valTo + amount;

        await stub.putState(keyFrom, Buffer.from(valFrom.toString()));
        await stub.putState(keyTo, Buffer.from(valTo.toString()));
    }
}

module.exports = MyContract;

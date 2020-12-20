'use strict';

const { Contract } = require('fabric-contract-api');

class SimpleChaincode extends Contract {

    // Initialize the chaincode
    async init(ctx, keyA, valueA, keyB, valueB) {
        console.info('========= example02 Init =========');

        if (typeof parseInt(valueA) !== 'number' || typeof parseInt(valueB) !== 'number') {
            throw new Error("Expecting integer value for asset holding");
        }

        await ctx.stub.putState(keyA, Buffer.from(valueA));
        await ctx.stub.putState(keyB, Buffer.from(valueB));
    }

    // query account balance
    async query(ctx, key) {
        let valueBytes = await ctx.stub.getState(key);
        if (valueBytes.length == 0) {
            throw new Error(`Account '${key}' does not exist.`);
        }

        return valueBytes.toString();
    }

    // transfer balance between accounts
    async transfer(ctx, keyA, keyB, transValue) {
        let valueBytes = await ctx.stub.getState(key);
        
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

        await stub.putState(A, Buffer.from(aVal.toString()));
        await stub.putState(B, Buffer.from(bVal.toString()));
    }
}

module.exports = SimpleChaincode;

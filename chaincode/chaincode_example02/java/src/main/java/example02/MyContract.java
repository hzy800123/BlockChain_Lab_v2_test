package example02;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Class: MyContract
 */
@Contract(
        name = "example02.MyContract",
        info = @Info(
                title = "MyContract",
                description = "SmartContract Example 02 - Blockchain Workshop",
                version = "1.0.0",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "23227732@qq.com",
                        name = "Bing"
                )
        )
)
@Default
public final class MyContract implements ContractInterface {
    enum Message {
        UNKNOWN_ERROR("chaincode failed with unknown reason."),
        FUNC_NOT_SUPPORT("function name '%s' is not support."),
        ARG_NUM_WRONG("Incorrect number of arguments. (Expecting %d)"),
        ACCOUNT_NOT_EXISTING("Account '%s' does not exist."),
        NO_ENOUGH_BALANCE("There is no enough balance to transfer in account '%s'."),
        BALANCE_INVALID("Account balance is invalid. ('%s': %s)"),

        RECHARGE_AMOUNT_INVALID("Re-charge Amount '%s' is invalid.");
    
        private String tmpl;
    
        private Message(String tmpl) {
            this.tmpl = tmpl;
        }
    
        public String template() {
            return this.tmpl;
        }
    }

    /**
     * Initialize Ledger
     * @param ctx context
     */
    @Transaction(name = "Init", intent = Transaction.TYPE.SUBMIT)
    public void init(final Context ctx, final String keyA, final String valueA, final String keyB, final String valueB) {
        ChaincodeStub stub = ctx.getStub();
        try {
            Integer.valueOf(valueA);
        } catch (Exception e) {
            String errorMessage = String.format(Message.BALANCE_INVALID.template(), keyA, valueA);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, e);
        }

        try {
            Integer.valueOf(valueB);
        } catch (Exception e) {
            String errorMessage = String.format(Message.BALANCE_INVALID.template(), keyB, valueB);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, e);
        }

        // init account A
        stub.putStringState(keyA, valueA);
        // init account B
        stub.putStringState(keyB, valueB);
    }

    /**
     * Query Account
     * @param ctx context
     * @return name state in ledger
     */
    @Transaction(name = "Query", intent = Transaction.TYPE.EVALUATE)
    public String query(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String valueA = stub.getStringState(key);

        // account not existing
        if (valueA.isEmpty()) {
            String errorMessage = String.format(Message.ACCOUNT_NOT_EXISTING.template(), key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        return valueA;
    }

    /**
     * Transfer Amount
     * @param ctx context
     */
    @Transaction(name = "Transfer", intent = Transaction.TYPE.SUBMIT)
    public void transfer(final Context ctx, final String keyFrom, final String keyTo, final String valueTrans) {
        ChaincodeStub stub = ctx.getStub();
        String valueA = stub.getStringState(keyFrom);
        String valueB = stub.getStringState(keyTo);
        int intValueA = Integer.parseInt(valueA);
        int intValueB = Integer.parseInt(valueB);
        int intValueTrans = Integer.parseInt(valueTrans);
        if (intValueA < intValueTrans) {
            String errorMessage = String.format(Message.NO_ENOUGH_BALANCE.template(), keyFrom);
            throw new ChaincodeException(errorMessage);
        }
        intValueA = intValueA - intValueTrans;
        stub.putStringState(keyFrom, String.valueOf(intValueA));
        intValueB =  intValueB + intValueTrans;
        stub.putStringState(keyTo, String.valueOf(intValueB));
    }


    /**
     * Transfer Amount
     * @param ctx context
     */
    @Transaction(name = "Recharge", intent = Transaction.TYPE.SUBMIT)
    public void recharge(final Context ctx, final String key, final String valueCharge) {
        ChaincodeStub stub = ctx.getStub();
        String value = stub.getStringState(key);
        int intValue = Integer.parseInt(value);
        int intValueCharge = Integer.parseInt(valueCharge);

        if (intValueCharge <= 0) {
            String errorMessage = String.format(Message.RECHARGE_AMOUNT_INVALID.template(), valueCharge);
            throw new ChaincodeException(errorMessage);
        }

        intValue = intValue + intValueCharge;
        stub.putStringState(key, String.valueOf(intValue));
    }    
}

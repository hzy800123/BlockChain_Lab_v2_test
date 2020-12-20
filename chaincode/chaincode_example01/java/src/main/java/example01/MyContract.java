package example01;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Class: MyContract
 */
@Contract(
    name = "example01.MyContract",
    info = @Info(
        title = "MyContract",
        description = "SmartContract Example 01 - Blockchain Workshop",
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
        ACCOUNT_NOT_EXISTING("Account '%s' does not exist.");
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
    public void init(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        stub.putStringState("Name", "Fabric@Java");
    }

    /**
     * Query Ledger
     * @param ctx context
     * @return name state in ledger
     */
    @Transaction(name = "Hi", intent = Transaction.TYPE.EVALUATE)
    public String hi(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        return stub.getStringState("Name");
    }

    /*
     * Example01 extension
     * Add one new function of 'SetValue' to set any key with value
     * @param String key
     * @param String value
     */
    @Transaction(name = "SetValue", intent = Transaction.TYPE.SUBMIT)
    public void setValue(final Context ctx, final String key, final String value) {
        ChaincodeStub stub = ctx.getStub();
        stub.putStringState(key, value);
    }

    /*
     * Example01 extension
     * Add one new function of 'GetValue' to get any value by key
     */
    @Transaction(name = "GetValue", intent = Transaction.TYPE.EVALUATE)
    public String getValue(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String result = stub.getStringState(key);

        // account not existing
        if (result.isEmpty()) {
            String errorMessage = String.format(Message.ACCOUNT_NOT_EXISTING.template(), key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        return result;
    }
}


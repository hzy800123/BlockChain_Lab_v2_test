package example02;

import io.netty.handler.ssl.OpenSsl;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 *
 */
public final class SimpleChaincode extends ChaincodeBase {
    enum Message {
        UNKNOWN_ERROR("chaincode failed with unknown reason."),
        FUNC_NOT_SUPPORT("function name '%s' is not support."),
        ARG_NUM_WRONG("Incorrect number of arguments. (Expecting %d)"),
        ACCOUNT_NOT_EXISTING("Account '%s' does not exist."),
        NO_ENOUGH_BALANCE("There is no enough balance to transfer in account '%s'."),
        BALANCE_INVALID("Account balance is invalid. ('%s': %s)");
    
        private String tmpl;
    
        private Message(String tmpl) {
            this.tmpl = tmpl;
        }
    
        public String template() {
            return this.tmpl;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("OpenSSL avaliable: " + OpenSsl.isAvailable());
        new SimpleChaincode().start(args);
    }

    @Override
    public Response init(ChaincodeStub stub) {
        String funcName = stub.getFunction();
        List<String> params = stub.getParameters();
        if (funcName.equals("init")) {
            if (params == null || params.size() != 4) {
                throw new ChaincodeException(String.format(Message.ARG_NUM_WRONG.template(), 4));
            }
            this.init(stub, params.get(0), params.get(1), params.get(2), params.get(3));
            System.out.println("initialization performed.");
        } else if (funcName.equals("upgrade")) {
            System.out.println("upgrade performed.");
        }

        throw new ChaincodeException(String.format(Message.FUNC_NOT_SUPPORT.template(), funcName));
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String funcName = stub.getFunction();
        List<String> params = stub.getParameters();

        try {
            if (funcName.equals("query")) {
                if (params == null || params.size() != 1) {
                    throw new ChaincodeException(String.format(Message.ARG_NUM_WRONG.template(), 1));
                }
                String payload = query(stub, params.get(0));
                return newSuccessResponse(payload);
            } else if (funcName.equals("transfer")) {
                if (params == null || params.size() != 3) {
                    throw new ChaincodeException(String.format(Message.ARG_NUM_WRONG.template(), 3));
                }
                transfer(stub, params.get(0), params.get(1), params.get(2));
                return newSuccessResponse();
            }
        } catch(Exception e) {
            if (!(e instanceof ChaincodeException)) {
                throw new ChaincodeException(Message.UNKNOWN_ERROR.template(), e);
            }
            throw e;
        }

        throw new ChaincodeException(String.format(Message.FUNC_NOT_SUPPORT.template(), funcName));
    }

    private void init(final ChaincodeStub stub, final String keyA, final String valueA, final String keyB, final String valueB) {
        try {
            Integer.valueOf(valueA);
        } catch(Exception e) {
            String errorMessage = String.format(Message.BALANCE_INVALID.template(), keyA, valueA);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, e);
        }

        try {
            Integer.valueOf(valueB);
        } catch(Exception e) {
            String errorMessage = String.format(Message.BALANCE_INVALID.template(), keyB, valueB);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, e);
        }

        // init account A
        stub.putStringState(keyA, valueA);
        // init account B
        stub.putStringState(keyB, valueB);
    }

    public String query(final ChaincodeStub stub, final String key) {
        String valueA = stub.getStringState(key);

        // account not existing
        if (valueA.isEmpty()) {
            String errorMessage = String.format(Message.ACCOUNT_NOT_EXISTING.template(), key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        return valueA;
    }

    public void transfer(final ChaincodeStub stub, final String keyA, final String keyB, final String valueTrans) {
        String valueA = stub.getStringState(keyA);
        String valueB = stub.getStringState(keyB);
        int intValueA = Integer.parseInt(valueA);
        int intValueB = Integer.parseInt(valueB);
        int intValueTrans = Integer.parseInt(valueTrans);
        if (intValueA < intValueTrans) {
            String errorMessage = String.format(Message.NO_ENOUGH_BALANCE.template(), keyA);
            throw new ChaincodeException(errorMessage);
        }
        intValueA = intValueA - intValueTrans;
        stub.putStringState(keyA, String.valueOf(intValueA));
        intValueB =  intValueB + intValueTrans;
        stub.putStringState(keyB, String.valueOf(intValueB));
    }
}

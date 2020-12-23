package example02;

import org.hyperledger.fabric.gateway.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InvokeQueryOrg2 {
    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    private static final String ORGNAME_ORG2 = "Org2";
    private static final String USERNAME_ORG2 = "user01";
    private static final String CHANNEL_NAME = "mychannel";
    private static final String CONTRACT_NAME = "mycc_java02";

    private static void doQuery(String orgName, String userName, String functionName, String key)
            throws IOException, ContractException {
        //get user identity from wallet.
        Path walletPath = Paths.get("wallet", orgName);
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        Identity identity = wallet.get(userName);

        //check identity existence in wallet
        if (identity == null) {
            System.out.println("The identity \"" + userName + "@"+ orgName + "\" doesn't exists in the wallet");
            return;
        }

        //load connection profile
        Path networkConfigPath = Paths.get( "profiles", orgName, "connection.json");
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, userName).networkConfig(networkConfigPath).discovery(true);

        //create a gateway connection
        try (Gateway gateway = builder.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CONTRACT_NAME);

            byte[] result = contract.evaluateTransaction(functionName, key);
            System.out.println("orgName - " + orgName + " , " + "userName - " + userName);
            System.out.println("Query - " + key + " : " + new String(result) + "\n");
        }
    }

    public static void main(String[] args) {
        try {
            doQuery(ORGNAME_ORG2, USERNAME_ORG2, "Query", "a");
        } catch (IOException | ContractException e) {
            e.printStackTrace();
        }
    }
}

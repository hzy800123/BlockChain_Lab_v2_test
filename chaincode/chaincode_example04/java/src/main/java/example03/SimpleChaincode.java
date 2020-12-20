package example03;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import org.hyperledger.fabric.shim.ledger.KeyValue;
import java.util.ArrayList;
import java.util.List;

@Contract(
    name = "Example03",
    info = @Info(
        title = "FabCar contract",
        description = "SmartContract Example 03 - Blockchain Workshop",
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
public final class SimpleChaincode implements ContractInterface {

    private final Genson genson = new Genson();

    enum Message {
        ASSET_NOT_EXIST("Asset '%s' does not exist.");
    
        private String tmpl;
    
        Message(String tmpl) {
            this.tmpl = tmpl;
        }
    
        public String template() {
            return this.tmpl;
        }
    }

    @Transaction(name = "InitLedger", intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String assetNo = null;
        Asset asset = null;
        int i = 0;

        assetNo = String.format("ASSET_%04d", ++i);
        asset = new Asset("blue", "Tomoko");
        stub.putStringState(assetNo, genson.serialize(asset));
        System.out.printf("%s: %s", assetNo, genson.serialize(asset));

        assetNo = String.format("ASSET_%04d", ++i);
        asset = new Asset("red", "Brad");
        stub.putStringState(assetNo, genson.serialize(asset));
        System.out.printf("%s: %s", assetNo, genson.serialize(asset));

        assetNo = String.format("ASSET_%04d", ++i);
        asset = new Asset("green", "Jin Soo");
        stub.putStringState(assetNo, genson.serialize(asset));
        System.out.printf("%s: %s", assetNo, genson.serialize(asset));

        assetNo = String.format("ASSET_%04d", ++i);
        asset = new Asset("yellow", "Max");
        stub.putStringState(assetNo, genson.serialize(asset));
        System.out.printf("%s: %s", assetNo, genson.serialize(asset));
    }
    
    @Transaction(name = "ListAssets", intent = Transaction.TYPE.EVALUATE)
    public Asset[] listAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "ASSET_0001";
        final String endKey = "ASSET_9999";
        List<Asset> assets = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Asset car = genson.deserialize(result.getStringValue(), Asset.class);
            assets.add(car);
        }

        return assets.toArray(new Asset[assets.size()]);
    }

    @Transaction(name = "QueryAsset", intent = Transaction.TYPE.EVALUATE)
    public Asset queryAsset(final Context ctx, final String assetNo) {
        ChaincodeStub stub = ctx.getStub();
        String assetState = stub.getStringState(assetNo);

        if (assetState.isEmpty()) {
            String errorMessage = String.format(Message.ASSET_NOT_EXIST.template(), assetNo);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, Message.ASSET_NOT_EXIST.toString());
        }

        return genson.deserialize(assetState, Asset.class);
    }

    @Transaction(name = "ChangeOwner", intent = Transaction.TYPE.SUBMIT)
    public Asset changeOwner(final Context ctx, final String assetNo, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();

        String assetState = stub.getStringState(assetNo);

        if (assetState.isEmpty()) {
            String errorMessage = String.format(Message.ASSET_NOT_EXIST.template(), assetNo);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, Message.ASSET_NOT_EXIST.toString());
        }

        Asset asset = genson.deserialize(assetState, Asset.class);

        Asset newAsset = new Asset(asset.getColor(), newOwner);
        String newCarState = genson.serialize(newAsset);
        stub.putStringState(assetNo, newCarState);

        return newAsset;
    }
}

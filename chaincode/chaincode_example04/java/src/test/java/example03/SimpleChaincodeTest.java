package example03;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.protos.peer.ChaincodeShim;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.mockito.InOrder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;

public class SimpleChaincodeTest {
//     private final Genson genson = new Genson();

//     @Nested
//     class InvokeInitTransaction {
//         @Test
//         public void initLedger() {
//             SimpleChaincode contract = new SimpleChaincode();
//             Context ctx = mock(Context.class);
//             ChaincodeStub stub = mock(ChaincodeStub.class);
//             when(ctx.getStub()).thenReturn(stub);

//             contract.initLedger(ctx);

//             InOrder inOrder = inOrder(stub);
//             int i = 0;

//             System.out.println(String.format("ASSET_%04d", 1));

//             inOrder.verify(stub).putStringState(
//                     String.format("ASSET_%04d", ++i),
//                     genson.serialize(new Asset("blue", "Tomoko")));
//             inOrder.verify(stub).putStringState(
//                     String.format("ASSET_%04d", ++i),
//                     genson.serialize(new Asset("red", "Brad")));
//             inOrder.verify(stub).putStringState(
//                     String.format("ASSET_%04d", ++i),
//                     genson.serialize(new Asset("green", "Jin Soo")));
//             inOrder.verify(stub).putStringState(
//                     String.format("ASSET_%04d", ++i),
//                     genson.serialize(new Asset("yellow", "Max")));
//         }

// //        @Test
// //        public void whenBalanceANotValid() {
// //            SimpleChaincode contract = new SimpleChaincode();
// //            Context ctx = mock(Context.class);
// //            ChaincodeStub stub = mock(ChaincodeStub.class);
// //            when(ctx.getStub()).thenReturn(stub);
// //
// //            Throwable thrown = catchThrowable(() -> {
// //                contract.initLedger(ctx, "A", "100A", "B", "200");
// //            });
// //
// //            assertThat(thrown)
// //                .isInstanceOf(ChaincodeException.class)
// //                .hasMessage(String.format(SimpleChaincode.Message.ASSET_NOT_EXIST.toString(), "A", "100A"));
// //        }
//     }
// //
// //    @Nested
// //    class InvokeTransferTransaction {
// //        @Test
// //        public void whenBalanceEnough() {
// //            SimpleChaincode contract = new SimpleChaincode();
// //            Context ctx = mock(Context.class);
// //            ChaincodeStub stub = mock(ChaincodeStub.class);
// //
// //            when(ctx.getStub()).thenReturn(stub);
// //            when(stub.getStringState("A")).thenReturn("100");
// //            when(stub.getStringState("B")).thenReturn("100");
// //
// //            Throwable thrown = catchThrowable(() -> {
// //                contract.(ctx, "A", "B", "15");
// //            });
// //
// //            assertThat(thrown).isNull();
// //        }
// //
// //        @Test
// //        public void whenBalanceNotEnough() {
// //            SimpleChaincode contract = new SimpleChaincode();
// //            Context ctx = mock(Context.class);
// //            ChaincodeStub stub = mock(ChaincodeStub.class);
// //
// //            when(ctx.getStub()).thenReturn(stub);
// //            when(stub.getStringState("A")).thenReturn("100");
// //            when(stub.getStringState("B")).thenReturn("100");
// //
// //            Throwable thrown = catchThrowable(() -> {
// //                contract.transfer(ctx, "A", "B", "150");
// //            });
// //
// //            assertThat(thrown)
// //                .isInstanceOf(ChaincodeException.class)
// //                .hasNoCause()
// //                .hasMessage(String.format(SimpleChaincode.Message.NoEnoughBalance.toString(), "A"));
// //        }
// //    }
// //
//     @Nested
//     class QueryAssetTransaction {

//         @Test
//         public void whenAssetNotExists() {
//             SimpleChaincode contract = new SimpleChaincode();
//             Context ctx = mock(Context.class);
//             ChaincodeStub stub = mock(ChaincodeStub.class);
//             when(ctx.getStub()).thenReturn(stub);
//             when(stub.getStringState("ASSET_0001")).thenReturn("");

//             Throwable thrown = catchThrowable(() -> {
//                 contract.queryAsset(ctx, "ASSET_0001");
//             });

//             assertThat(thrown)
//                 .isInstanceOf(ChaincodeException.class)
//                 .hasNoCause()
//                 .hasMessage(String.format(SimpleChaincode.Message.ASSET_NOT_EXIST.template(), "ASSET_0001"));
//         }

//         @Test
//         public void whenAssetExists() {
//             SimpleChaincode contract = new SimpleChaincode();
//             Context ctx = mock(Context.class);
//             ChaincodeStub stub = mock(ChaincodeStub.class);
//             when(ctx.getStub()).thenReturn(stub);
//             when(stub.getStringState("ASSET_0001")).thenReturn(genson.serialize(new Asset("blue", "Tomoko")));
//             assertThat(contract.queryAsset(ctx, "ASSET_0001")).isEqualTo(new Asset("blue", "Tomoko"));
//         }
//     }

//     @Nested
//     class ListAssetsTransaction {

//         @Test
//         public void whenAssetNotExists() {
//             SimpleChaincode contract = new SimpleChaincode();
//             Context ctx = mock(Context.class);
//             ChaincodeStub stub = mock(ChaincodeStub.class);
//             when(ctx.getStub()).thenReturn(stub);
//             Iterator it = new ArrayList<KeyValue>(0).iterator();
//             when(stub.getStateByRange("ASSET_0001", "ASSET_9999")).thenReturn((QueryResultsIterator<KeyValue>) it);

//             Throwable thrown = catchThrowable(() -> {
//                 contract.queryAsset(ctx, "ASSET_0001");
//             });

//             assertThat(thrown)
//                     .isInstanceOf(ChaincodeException.class)
//                     .hasNoCause()
//                     .hasMessage(String.format(SimpleChaincode.Message.ASSET_NOT_EXIST.template(), "ASSET_0001"));
//         }

//         @Test
//         public void whenAssetExists() {
//             SimpleChaincode contract = new SimpleChaincode();
//             Context ctx = mock(Context.class);
//             ChaincodeStub stub = mock(ChaincodeStub.class);
//             when(ctx.getStub()).thenReturn(stub);
//             when(stub.getStringState("ASSET_0001")).thenReturn(genson.serialize(new Asset("blue", "Tomoko")));
//             assertThat(contract.queryAsset(ctx, "ASSET_0001")).isEqualTo(new Asset("blue", "Tomoko"));
//         }
//     }
}

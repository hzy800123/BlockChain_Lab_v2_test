package example01;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.InOrder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MyContractTest {
    @Nested
    class InvokeInitTransaction {
        @Test
        public void init() {
            MyContract contract = new MyContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            contract.init(ctx);

            InOrder inOrder = inOrder(stub);
            inOrder.verify(stub).putStringState("Name", "Fabric@Java");
        }
    }

    @Nested
    class InvokeQueryTransaction {
        @Test
        public void hi() {
            MyContract contract = new MyContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);

            String result = stub.getStringState("Name");
//            System.out.println("result of Name: " + result);

            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("Name")).thenReturn("Fabric@Java");
            assertThat(contract.hi(ctx).equals("Fabric@Java"));
        }
    }

    @Nested
    class InvokeSetAndGetValue {
        @Test
        public void getAndSetValue() {
            MyContract contract = new MyContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);

            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("newKey")).thenReturn("newKey");

            contract.setValue(ctx,"newKey", "newValue");
            String result = contract.getValue(ctx, "newKey");
            System.out.println("result: " + result);
//            // assertThat(contract.getValue(ctx, "newKey").equals("newValue"));
        }
    }
}

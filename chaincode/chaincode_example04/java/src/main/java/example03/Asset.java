package example03;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Asset {

    @Property()
    private final String color;

    @Property()
    private final String owner;

    public String getColor() {
        return color;
    }

    public String getOwner() {
        return owner;
    }

    public Asset(@JsonProperty("color") final String color, @JsonProperty("owner") final String owner) {
        this.color = color;
        this.owner = owner;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Asset other = (Asset) obj;

        return Objects.deepEquals(new String[] {getColor(), getOwner()},
                new String[] {other.getColor(), other.getOwner()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColor(), getOwner());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [color=" + color + ", owner=" + owner + "]";
    }
}

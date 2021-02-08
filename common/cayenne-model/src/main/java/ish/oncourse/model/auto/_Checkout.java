package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.Outcome;

/**
 * Class _Checkout was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Checkout extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String UUID_PROPERTY = "UUID";
    public static final String SHOPPING_CART_PROPERTY = "shoppingCart";
    public static final String COLLEGE_PROPERTY = "college";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> UUID_ = Property.create("UUID", String.class);
    public static final Property<String> SHOPPING_CART = Property.create("shoppingCart", String.class);
    public static final Property<Outcome> COLLEGE = Property.create("college", Outcome.class);

    protected Long angelId;
    protected Date created;
    protected Date modified;
    protected String UUID;
    protected String shoppingCart;

    protected Object college;

    public void setAngelId(Long angelId) {
        beforePropertyWrite("angelId", this.angelId, angelId);
        this.angelId = angelId;
    }

    public Long getAngelId() {
        beforePropertyRead("angelId");
        return this.angelId;
    }

    public void setCreated(Date created) {
        beforePropertyWrite("created", this.created, created);
        this.created = created;
    }

    public Date getCreated() {
        beforePropertyRead("created");
        return this.created;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setUUID(String UUID) {
        beforePropertyWrite("UUID", this.UUID, UUID);
        this.UUID = UUID;
    }

    public String getUUID() {
        beforePropertyRead("UUID");
        return this.UUID;
    }

    public void setShoppingCart(String shoppingCart) {
        beforePropertyWrite("shoppingCart", this.shoppingCart, shoppingCart);
        this.shoppingCart = shoppingCart;
    }

    public String getShoppingCart() {
        beforePropertyRead("shoppingCart");
        return this.shoppingCart;
    }

    public void setCollege(Outcome college) {
        setToOneTarget("college", college, true);
    }

    public Outcome getCollege() {
        return (Outcome)readProperty("college");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "angelId":
                return this.angelId;
            case "created":
                return this.created;
            case "modified":
                return this.modified;
            case "UUID":
                return this.UUID;
            case "shoppingCart":
                return this.shoppingCart;
            case "college":
                return this.college;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "angelId":
                this.angelId = (Long)val;
                break;
            case "created":
                this.created = (Date)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "UUID":
                this.UUID = (String)val;
                break;
            case "shoppingCart":
                this.shoppingCart = (String)val;
                break;
            case "college":
                this.college = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.angelId);
        out.writeObject(this.created);
        out.writeObject(this.modified);
        out.writeObject(this.UUID);
        out.writeObject(this.shoppingCart);
        out.writeObject(this.college);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.angelId = (Long)in.readObject();
        this.created = (Date)in.readObject();
        this.modified = (Date)in.readObject();
        this.UUID = (String)in.readObject();
        this.shoppingCart = (String)in.readObject();
        this.college = in.readObject();
    }

}

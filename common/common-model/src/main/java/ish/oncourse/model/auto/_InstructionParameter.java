package ish.oncourse.model.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.Instruction;

/**
 * Class _InstructionParameter was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _InstructionParameter extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String NAME_PROPERTY = "name";
    @Deprecated
    public static final String VALUE_PROPERTY = "value";
    @Deprecated
    public static final String INSTRUCTION_PROPERTY = "instruction";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<String> VALUE = new Property<String>("value");
    public static final Property<Instruction> INSTRUCTION = new Property<Instruction>("instruction");

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setValue(String value) {
        writeProperty("value", value);
    }
    public String getValue() {
        return (String)readProperty("value");
    }

    public void setInstruction(Instruction instruction) {
        setToOneTarget("instruction", instruction, true);
    }

    public Instruction getInstruction() {
        return (Instruction)readProperty("instruction");
    }


}

package ish.oncourse.enrol.components;

import ish.oncourse.model.Student;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;

public class ConcessionEntry {

	@InjectComponent
	@Property
	private Form concessionForm;

	@InjectComponent
	private Zone concessionZone;

	@Parameter
	@Property
	private Student student;

    @Property
    @Parameter
    private int index;

}

package ish.oncourse.enrol.components;

import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;

public class ConcessionEntry {

	@Inject
	private PropertyAccess propertyAccess;

	@InjectComponent
	@Property
	private Form concessionForm;

	@InjectComponent
	private Zone concessionZone;

	@Parameter
	@Property
	private Student student;

	@Parameter
	@Property
	private Integer index;

	@Property
	private ConcessionType concessionType;

	@Property
	private StudentConcession studentConcession;

	@Property
	private boolean hasCertifiedConcession;

	// TODO @sort.concessionType.name
	@Property
	private List<StudentConcession> concessions;

	@Property
	private StudentConcession studentConcessionItem;

	@Property
	private ListSelectModel<ConcessionType> activeConcessionTypesModel;

	@Property
	private ListValueEncoder<ConcessionType> concessionTypesEncoder;

	@SetupRender
	void beginRender() {
		if (student != null) {
			List<ConcessionType> activeConcessionTypes = student.getCollege()
					.getActiveConcessionTypes();
			activeConcessionTypesModel = new ListSelectModel<ConcessionType>(activeConcessionTypes,
					ConcessionType.NAME_PROPERTY, propertyAccess);
			concessionTypesEncoder = new ListValueEncoder<ConcessionType>(activeConcessionTypes,
					ConcessionType.ANGEL_ID_PROPERTY, propertyAccess);
			concessions = student.getStudentConcessions();
		}
	}

	public boolean isHasConcessions() {
		return concessions != null && !concessions.isEmpty();
	}

	public boolean isHasAnyConcessionNumber() {
		for (StudentConcession studentConcession : concessions) {
			if (studentConcession.getConcessionType().getHasConcessionNumber()) {
				return true;
			}
		}
		return false;
	}

	public String getConcessionNumberInputClass() {
		return "valid";
	}

}

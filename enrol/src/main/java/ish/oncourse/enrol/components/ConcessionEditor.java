package ish.oncourse.enrol.components;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

import java.util.List;

public class ConcessionEditor {
    @Inject
    private PropertyAccess propertyAccess;

    @Property
    private ListSelectModel<ConcessionType> activeConcessionTypesModel;

    @Property
    private ListValueEncoder<ConcessionType> concessionTypesEncoder;

    @Parameter
    @Property
    private Student student;

    @Parameter
    @Property
    private Form parentForm;

    @Property
    private ConcessionType concessionType;

    @Property
    private StudentConcession studentConcession;

    @Property
    private boolean hasCertifiedConcession;


    @SetupRender
    void beginRender() {
        if (student != null) {
            List<ConcessionType> activeConcessionTypes = student.getCollege()
                    .getActiveConcessionTypes();
            activeConcessionTypesModel = new ListSelectModel<ConcessionType>(activeConcessionTypes,
                    ConcessionType.NAME_PROPERTY, propertyAccess);
            concessionTypesEncoder = new ListValueEncoder<ConcessionType>(activeConcessionTypes,
                    ConcessionType.ANGEL_ID_PROPERTY, propertyAccess);
        }
    }

    public String getConcessionNumberInputClass() {
        return "valid";
    }
}

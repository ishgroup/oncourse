package ish.oncourse.willow.model.field

import com.fasterxml.jackson.databind.ObjectMapper
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentEnglishProficiency
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.StudentCitizenship
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.willow.functions.field.FieldBuilder
import org.apache.commons.lang3.RandomUtils
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 25/5/17
 */
class FieldTest {
    @Test
    void test() {

        println toJson(FieldProperty.CITIZENSHIP, StudentCitizenship)
        println toJson(FieldProperty.ENGLISH_PROFICIENCY, AvetmissStudentEnglishProficiency)
        println toJson(FieldProperty.INDIGENOUS_STATUS, AvetmissStudentIndigenousStatus)
        println toJson(FieldProperty.HIGHEST_SCHOOL_LEVEL, AvetmissStudentSchoolLevel)
        println toJson(FieldProperty.PRIOR_EDUCATION_CODE, AvetmissStudentPriorEducation)
        println toJson(FieldProperty.LABOUR_FORCE_STATUS, AvetmissStudentLabourStatus)
        println toJson(FieldProperty.DISABILITY_TYPE, AvetmissStudentDisabilityType)
    }

    private String toJson(FieldProperty property, Class aClass) {
        ish.oncourse.model.Field sField = mock(ish.oncourse.model.Field)
        when(sField.id).thenReturn(RandomUtils.nextLong(0, Long.MAX_VALUE))
        when(sField.name).thenReturn(property.displayName)
        when(sField.property).thenReturn(property.key)
        when(sField.description).thenReturn(property.displayName)

        FieldBuilder builder = new FieldBuilder(field: sField, aClass: aClass)
        Field cField = builder.build()

        ObjectMapper mapper = new ObjectMapper()
        return mapper.writeValueAsString(cField)
    }
}

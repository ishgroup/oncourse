package ish.oncourse.portal.certificate

import ish.common.types.QualificationType
import ish.oncourse.model.Certificate
import ish.oncourse.model.CertificateOutcome
import ish.oncourse.portal.services.PortalUtils
import ish.oncourse.services.preference.PreferenceController
import org.apache.commons.lang3.RandomStringUtils

/**
 * User: akoiro
 * Date: 10/08/2016
 */
class Model {
    String number
    Date issued
    Date revoked

    String firstName
    String lastName

    String collegeName
    String rto
    String collegeUrl

    Qualification qualification
    List<Module> modules
    boolean nrt

    static Model valueOf(Certificate certificate, PreferenceController preferenceController) {
        Model model = new Model()
        model.number = certificate.certificateNumber
        model.issued = certificate.issued
        model.firstName = certificate.studentFirstName
        model.lastName = certificate.studentLastName
        model.collegeName = preferenceController.avetmissCollegeName ?: preferenceController.avetmissCollegeShortName ?: certificate.college.name
        model.revoked = (certificate.revokedWhen != null && certificate.revokedWhen.before(new Date())) ? certificate.revokedWhen: null
        model.rto = preferenceController.avetmissID
        model.collegeUrl = PortalUtils.getDomainName(preferenceController)
        if (model.collegeUrl) {
            model.collegeUrl = "https://${model.collegeUrl}"
        }

        if (certificate.qualification) {
            model.qualification = new Qualification(title: certificate.qualification.title,
                    level: certificate.qualification.level,
                    code: certificate.qualification.nationalCode,
                    type: certificate.qualification.isAccreditedCourse,
                    qualification: certificate.isQualification
            )
        }

        def certificateOutcomes = certificate.certificateOutcomes.findAll({ it.outcome?.module && (model.qualification == null ||  it.outcome?.status?.assessable) })
        model.modules = certificateOutcomes.collect({ CertificateOutcome outcome ->
                new Module(code: outcome.outcome?.module?.nationalCode, title: outcome.outcome?.module?.title)
        })
        model.nrt = model.qualification != null || !model.modules.isEmpty()

        return model
    }

    static class Qualification {
        String code
        String title
        String level
        QualificationType type
        boolean qualification
    }

    static class Module {
        String code
        String title
    }

}

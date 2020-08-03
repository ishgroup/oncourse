package ish.oncourse.portal.certificate

import ish.common.types.OutcomeStatus
import ish.common.types.QualificationType
import ish.oncourse.model.Certificate
import ish.oncourse.model.Outcome
import ish.oncourse.portal.services.PortalUtils
import ish.oncourse.services.preference.PreferenceController

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

        def outcomes = certificate.certificateOutcomes*.outcome.findAll({ it?.module && it?.status in OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE })
        model.modules = outcomes.collect({ Outcome outcome ->
                String description = null
                if (OutcomeStatus.STATUS_ASSESSABLE_RPL_GRANTED == outcome.status) {
                    description = "Recognised prior learning"
                } else if (OutcomeStatus.STATUS_ASSESSABLE_CREDIT_TRANSFER == outcome.status) {
                    description = "Credit transfer"
                }
                new Module(code: outcome?.module?.nationalCode,
                        title: outcome?.module?.title,
                        description: description )
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
        String description
    }

}

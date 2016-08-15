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
    def String number
    def Date issued
    def Date revoked

    def String firstName
    def String lastName

    def String collegeName
    def String rto
    def String collegeUrl

    def Qualification qualification
    def List<Module> modules
    def boolean nrt

    public static Model valueOf(Certificate certificate, PreferenceController preferenceController) {
        Model model = new Model()
        model.number = certificate.certificateNumber
        model.issued = certificate.issued
        model.firstName = certificate.studentFirstName
        model.lastName = certificate.studentLastName
        model.collegeName = certificate.college.name
        model.revoked = (certificate.revokedWhen != null && certificate.revokedWhen.before(new Date())) ? certificate.revokedWhen: null
        model.rto = preferenceController.avetmissID
        model.collegeUrl = PortalUtils.getDomainName(preferenceController)
        if (model.collegeUrl) {
            model.collegeUrl = "http://" + model.collegeUrl
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

    public static Model valueOf() {
        new Model().with {
            it.number = RandomStringUtils.randomNumeric(10)
            it.issued = new Date()
            it.firstName = "Andrei"
            it.lastName = "Koira"
            it.collegeName = "Byron Community College"
            it.rto = "O326A"
            it.collegeUrl = "https://cce.sydney.edu.au/"
            it.qualification = new Qualification(code: "SIR30212", title: "Retail Operations", level: "Certificate III in", type: SKILLSET_TYPE)
            it.modules = new ArrayList<>()
            def i = 0
            while (i < 10) {
                Module module = new Module();
                module.setCode(RandomStringUtils.randomAlphanumeric(8).toLowerCase());
                module.setTitle(RandomStringUtils.randomAlphanumeric(5) + " " +
                        RandomStringUtils.randomAlphanumeric(5) + " " +
                        RandomStringUtils.randomAlphanumeric(5));
                it.modules.add(module);
                i++;
            }
            return it
        }
    }

    public static class Qualification {
        def String code;
        def String title;
        def String level;
        def QualificationType type
        def boolean qualification
    }

    public static class Module {
        def String code;
        def String title;
    }

}

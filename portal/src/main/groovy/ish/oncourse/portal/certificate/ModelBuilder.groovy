package ish.oncourse.portal.certificate

import ish.oncourse.model.Certificate
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.preference.PreferenceControllerFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.portal.certificate.ModelBuilder.Result.revoked

/**
 * User: akoiro
 * Date: 11/08/2016
 */
class ModelBuilder {
    private ICayenneService cayenneService
    private PreferenceControllerFactory preferenceControllerFactory;

    private String code

    private Model model

    public String getCode() {
        return code;
    }

    public Model getModel() {
        println model
        return model
    }

    public Result build() {
        code = StringUtils.trimToNull(code)
        if (code == null) {
            return Result.emptyCode
        }

        Certificate certificate = ObjectSelect.query(Certificate.class)
                .where(Certificate.UNIQUE_CODE.eq(code).andExp(Certificate.ISSUED.isNotNull()))
                .selectFirst(cayenneService.sharedContext())

        if (certificate == null) {
            return Result.certificateNotFound
        }

        model = Model.valueOf(certificate, preferenceControllerFactory.getPreferenceController(certificate.getCollege()))

        if (model.revoked != null) {
            return revoked;
        } else {
            return Result.successFull
        }
    }

    public
    static ModelBuilder valueOf(String code, ICayenneService cayenneService, PreferenceControllerFactory preferenceControllerFactory) {
        ModelBuilder builder = new ModelBuilder()
        builder.code = code
        builder.cayenneService = cayenneService
        builder.preferenceControllerFactory = preferenceControllerFactory
        return builder
    }

    enum Result {
        emptyCode,
        certificateNotFound,
        revoked,
        successFull
    }
}

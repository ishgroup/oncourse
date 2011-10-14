package ish.oncourse.webservices.soap.v4.interceptors;

import ish.oncourse.model.College;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.util.SoapUtil;

import org.apache.cayenne.ObjectContext;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Extract angel version from soap header and save it into college record.
 * College is found by securityCode, which is sent in soap message body.
 * 
 * @author anton
 * 
 */
public class AngelVersionInterceptor extends AbstractSoapInterceptor {

	private static final Logger logger = Logger.getLogger(AngelVersionInterceptor.class);

	@Inject
	@Autowired
	private ICollegeService collegeService;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	public AngelVersionInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {

		try {

			String angelVersion = SoapUtil.getAngelVersion(message);
			String securityCode = SoapUtil.getSecurityCode(message);

			if (securityCode != null) {
				College college = collegeService.findBySecurityCode(securityCode);

				if (college != null) {
					ObjectContext newContext = cayenneService.newContext();
					College local = (College) newContext.localObject(college.getObjectId(), null);
					local.setAngelVersion(angelVersion);
					newContext.commitChanges();
				}
			}

		} catch (Exception e) {
			logger.error("Unable to persist angelVersion.", e);
		}
	}
}

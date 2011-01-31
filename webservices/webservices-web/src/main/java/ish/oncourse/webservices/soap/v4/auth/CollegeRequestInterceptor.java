package ish.oncourse.webservices.soap.v4.auth;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.util.SoapUtil;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * This interceptor inspect incoming requests and does the following:
 *
 * <ul>
 *	<li>If a college is found for a given security code, it updates it's IP,
 *	Angel version and access times.</li>
 *	<li>If a college is not found for a given security code, one is created and
 *	the IP, Angel version and access times are recorded.</li>
 * </ul>
 *
 * <p>This interceptor should only be used for unsecured, Willow to Angel  data 
 * transfers.</p>
 *
 *
 * @author Anton
 */
public class CollegeRequestInterceptor extends AbstractSoapInterceptor {

	private static final Logger LOGGER = Logger.getLogger(CollegeRequestInterceptor.class);

	@Inject
	@Autowired
	private ICollegeService collegeService;

	public CollegeRequestInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		HttpServletRequest req = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

		String ip = (req != null) ? req.getRemoteAddr() : "unknown";
		Date time = new Date();

		BindingOperationInfo boi = message.getExchange().get(BindingOperationInfo.class);

		String securityCode = SoapUtil.getSecurityCode(message);
		String version = SoapUtil.getAngelVersion(message);

		String collegeName = null;

		if (securityCode != null) {
			College college = collegeService.findBySecurityCode(securityCode);

			if (college == null) {
				// This must be a never seen before installation - record
				college = collegeService.recordNewCollege(securityCode, ip, version, time);
			} else {
				collegeService.recordWSAccess(college, ip, version, time);
			}

			if (college != null) {
				collegeName = college.getName();
			} else {
				LOGGER.error("College could not be found or created");
				// TODO: Should the request be interrupted at this point?
			}
		} else {
			LOGGER.error("No security code sent by remote!");
			// TODO: This is probably an error condition that should result in an
			// exception being thrown to the client.
		}

		LOGGER.info(String.format("Invoked %s by %s from %s with version %s at %s.", boi.getName(), collegeName, ip, version, time));
	}
}

package ish.oncourse.webservices.soap.interceptors;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.util.SoapUtil;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


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
 *
 * @author Anton
 */
public class CollegeRequestInterceptor extends AbstractSoapInterceptor {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	@Autowired
	private ICollegeService collegeService;

	public CollegeRequestInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		Fault fault = null;
		try {
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
					fault = new InterceptorErrorHandle(message, logger).handle("College could not be found");
				} else {
					collegeService.recordWSAccess(college, ip, version, time);
					collegeName = college.getName();
				}
			} else {
				fault = new InterceptorErrorHandle(message, logger).handle("No security code sent by remote!");
			}

			logger.info("Invoked {} by {} from {} with version {} at {}.", boi.getName(), collegeName, ip, version, time);
		} catch (Exception e) {
			fault = new InterceptorErrorHandle(message, logger).handle(e);
		}
		
		if (fault != null) {
			throw fault;
		}
	}
}

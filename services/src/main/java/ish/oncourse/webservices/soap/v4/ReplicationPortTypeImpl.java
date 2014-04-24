package ish.oncourse.webservices.soap.v4;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.replication.services.AuthenticateServiceImpl;
import ish.oncourse.webservices.replication.services.IAuthenticateService;
import ish.oncourse.webservices.replication.services.IAuthenticateService.InternalAuthenticationException;
import ish.oncourse.webservices.replication.services.IInstructionService;
import ish.oncourse.webservices.replication.services.IReplicationService;
import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InstructionServiceImpl;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v4.stubs.replication.ErrorCode;
import ish.oncourse.webservices.v4.stubs.replication.FaultReason;
import ish.oncourse.webservices.v4.stubs.replication.InstructionStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import java.util.List;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This decorator was created as workaround for
 * InjectService("Atomic/NotAtomic") which doesn't work by default with
 * tapestry5-spring @Autowired and I don't like to create additional useless
 * interface.
 * 
 * @author anton
 * 
 */
@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.ReplicationPortType", serviceName = "ReplicationService", portName = "ReplicationPort", targetNamespace = "http://repl.v4.soap.webservices.oncourse.ish/")
public class ReplicationPortTypeImpl implements ReplicationPortType {

	@Inject
	@Autowired
	private ICollegeService collegeService;

	@Inject
	@Autowired
	private IReplicationService replicationService;
	
	private IAuthenticateService authenticateService;
	
	private IInstructionService instructionService;

	@Resource
	private WebServiceContext webServiceContext;

	@Inject
	@Autowired
	private IWebSiteService webSiteService;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	/**
	 * Default constructor for injection.
	 */
	public ReplicationPortTypeImpl() {
		this.authenticateService = new AuthenticateServiceImpl() {
			@Override
			public ICayenneService takeCayenneService() {
				return cayenneService;
			}

			@Override
			public WebServiceContext takeWebServiceContext() {
				return webServiceContext;
			}

			@Override
			public ICollegeService takeCollegeService() {
				return collegeService;
			}
		};
		this.instructionService = new InstructionServiceImpl() {
			@Override
			public ICayenneService takeCayenneService() {
				return cayenneService;
			}
			
			@Override
			public IWebSiteService takeWebSiteService() {
				return webSiteService;
			}
		};
	}

	/**
	 * Constructor for unit-testing.
	 * 
	 * @param webServiceContext
	 *            web context
	 * @param collegeService
	 *            college service
	 * @param replicationService
	 *            replication service
	 * @param webSiteService
	 *            website service
	 * @param cayenneService
	 *            cayenne service
	 */
	public ReplicationPortTypeImpl(WebServiceContext webServiceContext, ICollegeService collegeService, IReplicationService replicationService, 
		IWebSiteService webSiteService, ICayenneService cayenneService) {
		super();
		this.collegeService = collegeService;
		this.replicationService = replicationService;
		this.webServiceContext = webServiceContext;
		this.webSiteService = webSiteService;
		this.cayenneService = cayenneService;
		this.authenticateService = new AuthenticateServiceImpl(cayenneService, webServiceContext, collegeService);
		this.instructionService = new InstructionServiceImpl(cayenneService, webSiteService);
	}

	@Override
	public void confirmExecution(Long instrucitonId, String response) {
		instructionService.confirmExecution(instrucitonId, response);
	}

	@Override
	public List<InstructionStub> getInstructions() {
		return PortHelper.convertV4InstructionsList(instructionService.getInstructions(SupportedVersions.V4));
	}

	/**
	 * Authenticates user, stores details in HTTP Session.
	 * 
	 * @param webServicesSecurityCode
	 *            code generated/stored within Angel database
	 * @param lastCommKey
	 *            communication key used in the last communication session
	 * 
	 * @return next communication key to track current conversation.
	 */
	@Override
	public long authenticate(String webServicesSecurityCode, long lastCommKey) throws AuthFailure {
		try {
			return authenticateService.authenticate(webServicesSecurityCode, lastCommKey);
		} catch (InternalAuthenticationException e) {
			throw createAuthFailureForException(e);
		}
	}

	/**
	 * End the session on Willow - this will discard the HTTP Session.
	 * 
	 * @param newCommKey
	 *            the communication key returned for this communication session
	 */
	@Override
	public void logout(long newCommKey) {}

	@Override
	@WebMethod(operationName = "getRecords")
	public ReplicationRecords getRecords() throws ReplicationFault {
		try {
			return PortHelper.getV4ReplicationRecords(replicationService.getRecords(SupportedVersions.V4));
		} catch (InternalReplicationFault e) {
			throw createReplicationFaultForException(e);
		}
	}

	@Override
	@WebMethod(operationName = "sendRecords")
	public ReplicationResult sendRecords(ReplicationRecords replicationRecords) throws ReplicationFault {
		try {
			return PortHelper.getV4ReplicationResult(replicationService.sendRecords(replicationRecords));
		} catch (InternalReplicationFault e) {
			throw createReplicationFaultForException(e);
		}
	}

	@Override
	@WebMethod(operationName = "sendResults")
	public int sendResults(ReplicationResult replicationResults) throws ReplicationFault {
		try {
			return replicationService.sendResults(replicationResults);
		} catch (InternalReplicationFault e) {
			throw createReplicationFaultForException(e);
		}
	}
	
	static ReplicationFault createReplicationFaultForException(final InternalReplicationFault exception) {
		FaultReason faultReason = new FaultReason();
		faultReason.setDetailMessage(exception.getFaultReasonMessage());
		faultReason.setFaultCode(exception.getFaultCode());
		return new ReplicationFault(exception.getMessage(), faultReason);
	}
	
	static AuthFailure createAuthFailureForException(final InternalAuthenticationException exception) {
		if (exception.getErrorCode() != null) {
			ErrorCode errorCode = null;
			switch (exception.getErrorCode()) {
				case INVALID_SESSION:
					errorCode = ErrorCode.INVALID_SESSION;
					break;
				case INVALID_SECURITY_CODE:
					errorCode = ErrorCode.INVALID_SECURITY_CODE;
					break;
				case EMPTY_COMMUNICATION_KEY:
					errorCode = ErrorCode.EMPTY_COMMUNICATION_KEY;
					break;
				case HALT_COMMUNICATION_KEY:
					errorCode = ErrorCode.HALT_COMMUNICATION_KEY;
					break;
				case INVALID_COMMUNICATION_KEY:
					errorCode = ErrorCode.INVALID_COMMUNICATION_KEY;
					break;
				case NO_KEYS:
					errorCode = ErrorCode.NO_KEYS;
					break;
			}
			return new AuthFailure(exception.getMessage(), errorCode);
		} else {
			return new AuthFailure(exception.getMessage());
		}
	}
}

package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.College;
import ish.oncourse.model.Instruction;
import ish.oncourse.model.InstructionParameter;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.access.SessionToken;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.exception.StackTraceUtils;
import ish.oncourse.webservices.replication.services.IReplicationService;
import ish.oncourse.webservices.v4.stubs.replication.ErrorCode;
import ish.oncourse.webservices.v4.stubs.replication.InstructionStub;
import ish.oncourse.webservices.v4.stubs.replication.ParameterEntry;
import ish.oncourse.webservices.v4.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
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

	private final static Logger LOGGER = Logger.getLogger(ReplicationPortTypeImpl.class);

	@Inject
	@Autowired
	private ICollegeService collegeService;

	@Inject
	@Autowired
	private IReplicationService replicationService;

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
	public ReplicationPortTypeImpl(WebServiceContext webServiceContext, ICollegeService collegeService,
			IReplicationService replicationService, IWebSiteService webSiteService, ICayenneService cayenneService) {
		super();
		this.collegeService = collegeService;
		this.replicationService = replicationService;
		this.webServiceContext = webServiceContext;
		this.webSiteService = webSiteService;
		this.cayenneService = cayenneService;
	}

	@Override
	public void confirmExecution(Long instrucitonId, String response) {

		Expression expr = ExpressionFactory.matchDbExp(Instruction.ID_PK_COLUMN, instrucitonId);
		SelectQuery q = new SelectQuery(Instruction.class, expr);

		ObjectContext objectContext = cayenneService.newContext();

		@SuppressWarnings("unchecked")
		List<Instruction> list = objectContext.performQuery(q);
		
		if (!list.isEmpty()) {
			Instruction instruction = list.get(0);
			instruction.setExecuted(new Date());
			instruction.setResponse(response);
			objectContext.commitChanges();
		}
	}

	@Override
	public List<InstructionStub> getInstructions() {

		List<InstructionStub> result = new ArrayList<InstructionStub>();

		Expression expr = ExpressionFactory.matchExp(Instruction.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()).andExp(
				ExpressionFactory.matchExp(Instruction.EXECUTED_PROPERTY, null));

		SelectQuery q = new SelectQuery(Instruction.class, expr);
		q.addPrefetch(Instruction.PARAMETERS_PROPERTY);

		ObjectContext objectContext = cayenneService.newNonReplicatingContext();

		@SuppressWarnings("unchecked")
		List<Instruction> list = objectContext.performQuery(q);

		for (Instruction inst : list) {
			InstructionStub stub = new InstructionStub();

			stub.setId(inst.getId());
			stub.setMessage(inst.getMessage());

			if (!inst.getParameters().isEmpty()) {
				ParametersMap paramMap = new ParametersMap();

				for (InstructionParameter param : inst.getParameters()) {
					ParameterEntry entry = new ParameterEntry();
					entry.setName(param.getName());
					entry.setValue(param.getValue());
					paramMap.getEntry().add(entry);
				}

				stub.setParameters(paramMap);
			}

			result.add(stub);
			
			//set executed to prevent execution during next replication run, 
			//even if session timed out.
			inst.setExecuted(new Date());
		}
		
		objectContext.commitChanges();

		return result;
	}

	/**
	 * Authenticates user, stores details in HTTP Session.
	 * 
	 * @param securityCode
	 *            code generated/stored within Angel database
	 * @param lastCommunicationKey
	 *            communication key used in the last communication session
	 * 
	 * @return next communication key to track current conversation.
	 */
	@Override
	public long authenticate(String webServicesSecurityCode, long lastCommKey) throws AuthFailure {
		try {
			HttpServletRequest request = (HttpServletRequest) webServiceContext.getMessageContext().get(
					AbstractHTTPDestination.HTTP_REQUEST);
			HttpSession session = request.getSession(false);

			LOGGER.info(String.format("Got college request with securityCode:%s, lastCommKey:%s.", webServicesSecurityCode, lastCommKey));

			if (session != null && session.getAttribute(SessionToken.SESSION_TOKEN_KEY) != null) {
				String message = String.format(
						"Authentication failure, existing session:%s must be terminated before next authentication attempt.",
						session.getId());
				AuthFailure e = new AuthFailure(message, ErrorCode.INVALID_SESSION);
				LOGGER.error(message, e);
				throw e;
			}

			College college = collegeService.findBySecurityCode(webServicesSecurityCode);

			if (college == null) {
				String message = String.format("No college found for 'security code':%s.", webServicesSecurityCode);
				AuthFailure e = new AuthFailure(message, ErrorCode.INVALID_SECURITY_CODE);
				LOGGER.error(message, e);
				throw e;
			}

			college = (College) cayenneService.newContext().localObject(college.getObjectId(), null);

			Long currentKey = college.getCommunicationKey();
			boolean recoverFromHALT = (currentKey == null) && (college.getCommunicationKeyStatus() == KeyStatus.HALT);

			if (currentKey == null) {
				// we didn't find key
				if (recoverFromHALT) {
					// recovering from HALT state
					Long newKey = generateNewKey(college);
					return newKey;
				} else {
					AuthFailure e = new AuthFailure(String.format("Invalid communication key:%s", lastCommKey),
							ErrorCode.INVALID_COMMUNICATION_KEY);
					LOGGER.error(String.format("Communication key is null for college:%s, when received key is %s.", college.getId(),
							lastCommKey), e);
					putCollegeInHaltState(college);
					throw e;
				}
			} else {
				if (college.getCommunicationKeyStatus() != KeyStatus.VALID) {
					// Communication key in a HALT state. Refuse authentication
					// attempt.
					AuthFailure e = new AuthFailure(String.format("Communication key:%s in a HALT state.", lastCommKey),
							ErrorCode.HALT_COMMUNICATION_KEY);
					LOGGER.debug(String.format("Communication key:%s for college:%s in a HALT state.", lastCommKey, college.getId()), e);
					throw e;
				}

				if (lastCommKey == currentKey.longValue()) {
					Long newKey = generateNewKey(college);
					return newKey;
				} else {
					AuthFailure e = new AuthFailure(String.format("Invalid communication key: %s.", lastCommKey),
							ErrorCode.INVALID_COMMUNICATION_KEY);
					LOGGER.error(String.format("Invalid communication key:%s, for college:%s, expected:%s.", lastCommKey, college.getId(),
							currentKey), e);
					putCollegeInHaltState(college);

					// TODO: !!!!! Here should be exception, since we're in HALT
					// state !!!!!!
					return generateNewKey(college);// throw e;
				}
			}
		} catch (Exception e) {
			if (e instanceof AuthFailure) {
				throw (AuthFailure) e;
			} else {
				LOGGER.error("Unable to authenticate. Generic exception.", e);
				String message = String.format("Unable to authenticate. Willow generic exception: %s",
						StackTraceUtils.stackTraceAsString(e));
				throw new AuthFailure(message);
			}
		}
	}

	/**
	 * End the session on Willow - this will discard the HTTP Session.
	 * 
	 * @param communicationKey
	 *            the communication key returned for this communication session
	 * 
	 * @return logout status
	 */
	@Override
	public void logout(long newCommKey) {

		HttpServletRequest request = (HttpServletRequest) webServiceContext.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);

		HttpSession session = request.getSession(false);

		if (session != null) {
			SessionToken token = (SessionToken) session.getAttribute(SessionToken.SESSION_TOKEN_KEY);
			if (token.getCommunicationKey().equals(newCommKey)) {
				session.invalidate();
			} else {
				String message = String.format("Invalid communication key:%s, for college:%s, expected:%s.", newCommKey,
						token.getCollegeId(), token.getCommunicationKey());
				LOGGER.error(message, new AuthFailure(message, ErrorCode.INVALID_COMMUNICATION_KEY));
			}
		}
	}

	/**
	 * Puts college into HALT state, which prevents all further replication,
	 * until recovery from HALT is done by admins.
	 * 
	 * @param college
	 *            willow college
	 */
	private void putCollegeInHaltState(College college) {
		LOGGER.error(String.format("Putting college:%s into HALT state.", college.getId()));
		/*
		 * college.setCommunicationKeyStatus(KeyStatus.HALT);
		 * college.getObjectContext().commitChanges();
		 */
	}

	/**
	 * Generates new communication key for college, for details refer
	 * http://intranet.ish.com.au/drupal/ReplicationWorkflow#Use of the
	 * communication key
	 * 
	 * @param college
	 *            willow college
	 * @return communication key
	 */
	private Long generateNewKey(College college) {

		Random randomGen = new Random();
		long newCommunicationKey = ((long) randomGen.nextInt(63) << 59) + System.currentTimeMillis();

		college.setCommunicationKey(newCommunicationKey);
		college.setCommunicationKeyStatus(KeyStatus.VALID);

		college.getObjectContext().commitChanges();

		HttpServletRequest request = (HttpServletRequest) webServiceContext.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
		HttpSession session = request.getSession(true);
		session.setAttribute(SessionToken.SESSION_TOKEN_KEY, new SessionToken(college.getId(), newCommunicationKey));

		return newCommunicationKey;
	}

	@Override
	@WebMethod(operationName = "getRecords")
	public ReplicationRecords getRecords() throws ReplicationFault {
		return replicationService.getRecords();
	}

	@Override
	@WebMethod(operationName = "sendRecords")
	public ReplicationResult sendRecords(ReplicationRecords replicationRecords) throws ReplicationFault {
		return replicationService.sendRecords(replicationRecords);
	}

	@Override
	@WebMethod(operationName = "sendResults")
	public int sendResults(ReplicationResult replicationResults) throws ReplicationFault {
		return replicationService.sendResults(replicationResults);
	}
}

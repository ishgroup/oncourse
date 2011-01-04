package ish.oncourse.webservices.soap.auth;

import ish.oncourse.webservices.soap.Status;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import javax.jws.WebService;


/**
 * Service responsible for the authentication of the Angel system.
 *
 * @author Marek Wawrzyczny
 */
@WebService
public interface AuthenticationPortType {

	/**
	 * Authenticates user, stores details in HTTP Session.
	 * 
	 * @param securityCode code generated/stored within Angel database
	 * @param lastCommunicationKey communication key used in the last
	 *			communication session
	 *
	 * @return next communication key to track current conversation.
	 */
	@WebMethod(operationName="authenticate")
	Long authenticate(
			@WebParam(name="securityCode") String securityCode,
			@WebParam(name="lastCommunicationKey") Long lastCommunicationKey);

	/**
	 * End the session on Willow - this will discard the HTTP Session.
	 *
	 * @param communicationKey the communication key returned for this
	 *			communication session
	 *
	 * @return logout status
	 */
	@WebMethod(operationName="logout")
	Status logout(
			@WebParam(name="communicationKey") Long communicationKey);
	
}

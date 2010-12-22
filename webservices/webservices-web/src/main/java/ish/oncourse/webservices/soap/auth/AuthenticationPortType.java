package ish.oncourse.webservices.soap.auth;

import ish.oncourse.webservices.soap.Status;

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
	 * @param securityCode code generated/stored within Angel database.
	 * @return next communication key to track current conversation.
	 */
	Long authenticate(String securityCode, Long lastCommunicationKey);

	/**
	 * End the session on Willow - this will discard the HTTP Session.
	 */
	Status logout(Long communicationKey);
	
}

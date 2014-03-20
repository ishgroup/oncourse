package ish.oncourse.portal.access;

import ish.oncourse.model.Contact;

import java.util.List;

public interface IAuthenticationService {
	
	/**
	 * Stores currently logged in user into session.
	 * @param user current user
	 */
	void storeCurrentUser(Contact user);
	
	/**
	 * Look for user by first, last names, email and password across all colleges.
	 * @param firstName first name
	 * @param lastName last name
	 * @param email user's email
	 * @param password user's password
	 * @return list of users
	 */
	List<Contact> authenticate(String firstName, String lastName, String email, String password);

	/**
	 * Look for user by supportLogin.
	 * @param supportLogin user's password
	 * @return true when the supportLogin is correct and actual  
	 */
	boolean authenticate(String supportLogin);
	
	/**
	 * Look for user as a company by company name, email and password across all colleges.
	 * @param companyName company name
	 * @param email user's email
	 * @param password user's password
	 * @return list of users
	 */
	List<Contact> authenticateCompany(String companyName, String email, String password);
	
	/**
	 * Finds user by password recovery key.
	 * @param recoveryKey passwd recovery key
	 * @return user
	 */
	Contact findByPasswordRecoveryKey(String recoveryKey);
	
	/**
	 * Finds users for password recovery.
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @return
	 */
	List<Contact> findForPasswordRecovery(String firstName, String lastName, String email);
	
	
	/**
	 * Finds users for password recovery.
	 * @param companyName
	 * @param email
	 * @return
	 */
	List<Contact> findCompanyForPasswordRecovery(String companyName, String email);
	
	/**
	 * Gets currently logged in user.
	 * @return
	 */
	Contact getUser();
	
	/**
	 * Performs logout, cleanups cookies, invalidates http session.
	 */
	void logout();
	
	/**
	 * Whether current user is a tutor.
	 * @return
	 */
	boolean isTutor();
}

package ish.oncourse.services.jndi;

/**
 * Service to hide JNDI lookup mechanism. Primary used to decouple the rest of services from 
 * container specific dependecies during unit testing.
 * @author anton
 *
 */
public interface ILookupService {
	Object lookup(String path);
}

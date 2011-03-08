package ish.oncourse.model;

import org.apache.cayenne.Persistent;


/**
 * Marker interface to indicate which Entities are to be Queued/Replicated.
 *
 * @author marek
 */
public interface Queueable extends Persistent {

	Long getId();

	College getCollege();
	
	void setAngelId(Long angelId);
	
	Long getAngelId();

}

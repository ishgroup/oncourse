package ish.oncourse.model;

import java.util.Date;

import org.apache.cayenne.Persistent;


/**
 * Marker interface to indicate which Entities are to be Queued/Replicated.
 *
 * @author marek
 */
public interface Queueable extends Persistent {

	Long getId();

	College getCollege();
	
	void setCollege(College college);
	
	void setAngelId(Long angelId);
	
	Long getAngelId();
	
	void setCreated(Date created);
	
	void setModified(Date modified);

    Date getCreated();
}

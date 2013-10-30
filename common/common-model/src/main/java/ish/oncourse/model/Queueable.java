package ish.oncourse.model;

import org.apache.cayenne.Persistent;

import java.util.Date;


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

	boolean isAsyncReplicationAllowed();
}

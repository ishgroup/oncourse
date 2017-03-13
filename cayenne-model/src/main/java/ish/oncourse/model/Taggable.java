package ish.oncourse.model;

import ish.oncourse.model.auto._Taggable;
import ish.oncourse.utils.QueueableObjectUtils;

/**
 * Represents the entity which can be linked to one for more tags. It looks similar to NodeRelation<entityAngelId, entityWillowId, entityIdentifier, tagId> table in angel, 
 * but Marek put <entityAngelId, entityWillowId, entityIdentifier> into separate table I think to avoid duplication of the same values 
 * if the same entity is assigned of more then one tag.
 * 
 * Thus NodeRelation from Angel, corresponds to two records in two tables in willow. The first record is <entityAngelId, entityWillowId, entityIdentifier> 
 * in Taggable, the second record <taggableId, tagId> in TaggableTag table.
 *
 * 
 * @author anton
 *
 */
public class Taggable extends _Taggable implements Queueable {
	private static final long serialVersionUID = 6153917245616586209L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}

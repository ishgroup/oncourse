/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.tag;

import ish.oncourse.function.IGet;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Tag;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;

import static ish.oncourse.model.auto._Tag.ID_PK_COLUMN;
import static ish.oncourse.model.auto._Tag.TAGGABLE_TAGS;
import static ish.oncourse.model.auto._Taggable.ENTITY_IDENTIFIER;
import static ish.oncourse.model.auto._Taggable.ENTITY_WILLOW_ID;
import static ish.oncourse.model.auto._TaggableTag.TAGGABLE;

/**
 * User: akoiro
 * Date: 12/07/2016
 */
public class EntityHasTag<E extends Queueable> implements IGet<Boolean> {
	private IGet<E> getEntity;
	private IGet<Tag> getTag;

	public EntityHasTag(final E entity, IGet<Tag> getTag) {
		this.getEntity = new IGet<E>() {
			@Override
			public E get() {
				return entity;
			}
		};
		this.getTag = getTag;
	}

	public EntityHasTag(IGet<E> getEntity, IGet<Tag> getTag) {
		this.getEntity = getEntity;
		this.getTag = getTag;
	}

	public Boolean get() {
		E entity = getEntity.get();
		Tag tag = getTag.get();
		if (tag == null) {
			return false;
		}
		tag = ObjectSelect.query(Tag.class)
				.where(TAGGABLE_TAGS.dot(TAGGABLE).dot(ENTITY_IDENTIFIER).eq(entity.getObjectId().getEntityName()))
				.and(TAGGABLE_TAGS.dot(TAGGABLE).dot(ENTITY_WILLOW_ID).eq(entity.getId()))
				.and(ExpressionFactory.matchDbExp(ID_PK_COLUMN, tag.getId()))
				.selectOne(entity.getObjectContext());
		return tag != null;
	}
}

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.binary;

import ish.oncourse.function.IGet;
import ish.oncourse.model.College;
import ish.oncourse.model.Document;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

import static ish.oncourse.model.auto._BinaryInfoRelation.ENTITY_IDENTIFIER;
import static ish.oncourse.model.auto._BinaryInfoRelation.ENTITY_WILLOW_ID;
import static ish.oncourse.model.auto._Document.BINARY_INFO_RELATIONS;

/**
 * User: akoiro
 * Date: 11/07/2016
 */
public class GetDocuments implements IGet<List<Document>> {
	private ObjectContext objectContext;
	private String entityIdentifier;
	private Long entityId;
	private College college;
	private boolean hidePrivate;
	private boolean isStudentLoggedIn;

	public GetDocuments(String entityIdentifier, Long entityId, College college, boolean isStudentLoggedIn) {
		this(entityIdentifier, entityId, college, true, isStudentLoggedIn, college.getObjectContext());
	}

	public GetDocuments(String entityIdentifier, Long entityId, College college) {
		this(entityIdentifier, entityId, college, true, false, college.getObjectContext());

	}

	public GetDocuments(String entityIdentifier, Long entityId, College college, boolean hidePrivate, boolean isStudentLoggedIn, ObjectContext objectContext) {
		this.entityIdentifier = entityIdentifier;
		this.entityId = entityId;
		this.college = college;
		this.hidePrivate = hidePrivate;
		this.isStudentLoggedIn = isStudentLoggedIn;
		this.objectContext = objectContext;
	}

	public List<Document> get() {
		return ObjectSelect.query(Document.class)
				.where(BINARY_INFO_RELATIONS.dot(ENTITY_IDENTIFIER).eq(entityIdentifier))
				.and(BINARY_INFO_RELATIONS.dot(ENTITY_WILLOW_ID).eq(entityId))
				.and(new GetCollegeExpression(college, hidePrivate, isStudentLoggedIn).get())
				.select(objectContext);
	}
}

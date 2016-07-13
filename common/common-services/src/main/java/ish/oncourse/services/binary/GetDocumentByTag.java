/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.binary;

import ish.oncourse.function.IGet;
import ish.oncourse.model.Document;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.EntityHasTag;

import java.util.List;

/**
 * User: akoiro
 * Date: 12/07/2016
 */
public class GetDocumentByTag {
	private IGet<Tag> getTag;
	private IGet<List<Document>> getDocuments;

	public GetDocumentByTag(IGet<Tag> getTag, IGet<List<Document>> getDocuments) {
		this.getTag = getTag;
		this.getDocuments = getDocuments;
	}

	public Document get() {
		List<Document> documents = getDocuments.get();
		for (Document document : documents) {
			if (new EntityHasTag<>(document, getTag).get()) {
				return document;
			}
		}
		return null;
	}
}

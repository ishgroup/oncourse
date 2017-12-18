/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.services;

import ish.oncourse.model.*;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MailingListHelper {

	private List<Long> selectedSubscriptions;
	private ObjectContext objectContext;
	private Contact currentUser;
	private College currentCollege;
	private ITagService tagService;
	private Set<Tag> currentSubscription;

	private MailingListHelper() {
	}

	public static MailingListHelper valueOf(ITagService tagService, List<Long> selectedSubscriptions, Set<Tag> currentSubscription, ObjectContext objectContext, Contact currentUser, College currentCollege) {
		MailingListHelper helper = new MailingListHelper();
		helper.selectedSubscriptions = selectedSubscriptions;
		helper.objectContext = objectContext;
		helper.currentUser = currentUser;
		helper.currentCollege = currentCollege;
		helper.tagService = tagService;
		helper.currentSubscription = currentSubscription;
		return helper;
	}

	public void saveSubscriptions() {

		for (Long id : selectedSubscriptions) {
			List<Tag> tagList = tagService.loadByIds(id);
			if (!tagList.isEmpty()) {
				Tag tag = tagList.get(0);
				if (currentSubscription.stream().noneMatch((s) -> s.getId().equals(tag.getId()))) {
					Tag local = objectContext.localObject(tag);
					Taggable taggable = objectContext.newObject(Taggable.class);
					taggable.setCollege(local.getCollege());
					Date date = new Date();
					taggable.setCreated(date);
					taggable.setModified(date);
					taggable.setEntityIdentifier(Contact.class.getSimpleName());
					taggable.setEntityWillowId(currentUser.getId());
					taggable.setEntityAngelId(currentUser.getAngelId());

					TaggableTag taggableTag = objectContext.newObject(TaggableTag.class);
					taggableTag.setTag(local);
					taggableTag.setCollege(local.getCollege());
					taggable.addToTaggableTags(taggableTag);
				} else {
					currentSubscription.removeAll(
							currentSubscription.stream().filter((s) -> s.getId().equals(tag.getId())).collect(Collectors.toSet())
					);
				}
			}
		}

		if (!currentSubscription.isEmpty()) {
			for (Tag tag : new ArrayList<>(currentSubscription)) {
				List<Taggable> taggableList = ObjectSelect.query(Taggable.class).where(Taggable.ENTITY_IDENTIFIER.eq(Contact.class.getSimpleName()))
						.and(Taggable.ENTITY_WILLOW_ID.eq(currentUser.getId()))
						.and(Taggable.COLLEGE.eq(currentCollege))
						.and(Taggable.TAGGABLE_TAGS.dot(TaggableTag.TAG).eq(tag)).select(objectContext);

				for (Taggable t : new ArrayList<>(taggableList)) {
					for (final TaggableTag tg : new ArrayList<>(t.getTaggableTags())) {
						objectContext.deleteObjects(tg);
						objectContext.deleteObjects(t);
					}
				}
			}
		}
	}

}

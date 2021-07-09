/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.types.NodeSpecialType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.cayenne.Taggable
import ish.oncourse.common.NodeInterface
import ish.oncourse.server.cayenne.glue._Tag
import ish.validation.ValidationFailure
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.validation.ValidationResult

import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.swing.tree.MutableTreeNode
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException

/**
 * A tag is a piece of arbitrary information which can be attached to many other types of objects.
 * Tags are arranged hierarchically, and each tree of tags has a root node also called a "tag group"
 * The tag group "Subjects" is special and always exists in every onCourse database.
 *
 * The complete path to a tag is unique (that is, siblings cannot have the same tag name).
 *
 */
@API
@QueueableEntity
class Tag extends _Tag implements NodeInterface, Queueable, AttachableTrait {

	public static final String NAME_KEY = "name"
	public static final String SHORT_NAME_KEY = "shortName"
	public static final String PARENT_TAG_KEY = "parentTag"
	public static final String TAG_REQUIREMENTS_KEY = "tagRequirements"
	public static final String ENTITY_IDENTIFIER_PROPERTY = "entityIdentifier";
	public static final String TAG_PROPERTY = "tag";


	public static final String ROOT_NODE = "root_node" // root node
	public static final String ADD_NODE_ACTION = "add_node_action"
	public static final String REMOVE_NODE_ACTION = "remove_node_action"

	public static final DataFlavor NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Node")
	private final DataFlavor[] flavors = [ NODE_FLAVOR ] as DataFlavor[]

	@Override
	void postAdd() {
		super.postAdd()

		if (getIsWebVisible() == null) {
			setIsWebVisible(true)
		}
		if (getWeight() == null) {
			setWeight(1)
		}
		if (getIsVocabulary() == null) {
			setIsVocabulary(false)
		}
	}

	/**
	 * @param type
	 * @return requirement for param class (encoded as integer)
	 */
	@Nullable
	TagRequirement createTagRequirement(final Class<? extends Taggable> type) {
		if (getTagRequirement(type) != null) {
			return getTagRequirement(type)
		}
		final TagRequirement result = getObjectContext().newObject(TagRequirement.class)
		result.setTag(this)
		result.setEntityIdentifier(TagRequirement.getTaggableClasses().get(type))
		result.setIsRequired(Boolean.FALSE)
		result.setManyTermsAllowed(Boolean.FALSE)

		return result
	}

	/**
	 * @param type
	 */
	void destroyNodeRequirement(final Class<? extends Taggable> type) {
		if (getTagRequirement(type) == null) {
			return
		}
		final TagRequirement result = getTagRequirement(type)
		getObjectContext().deleteObjects(result)
	}

	/**
	 * The resulting map has the tag name as the key and the tag itself as a value.
	 * @return map of children tags
	 */
	@Nonnull
	@API
	Map<String, Tag> getAllChildren() {
		final Map<String, Tag> result = new LinkedHashMap<>()
		List<Tag> childTags = getChildTags()
		for (Tag tag : childTags) {
			result.put(tag.getName(), tag)
			result.putAll(tag.getAllChildren())
		}
		return result
	}

	/**
	 * @return the top-most tag of this tree. Also known as a tag group.
	 */
	@Nonnull
	@API
	Tag getRoot() {
		if (getParentTag() == null) {
			return this
		}
		return getParentTag().getRoot()
	}

	/**
	 * @param type
	 * @return requirement for param class
	 */
	@Nullable
	TagRequirement getTagRequirement(@Nullable final Class<? extends Taggable> type) {
		if (getTagRequirements() != null && type != null) {
			for (final TagRequirement nr : getTagRequirements()) {
				if (nr.getPersistenceState() != PersistenceState.TRANSIENT && nr.getPersistenceState() != PersistenceState.DELETED &&
						nr.getEntityClass() == type) {
					return nr
				}
			}
		}
		return null
	}

	@Override
	Object readProperty(final String key) {
		if (ROOT_NODE == key) {
			return getRoot()
		}
		return super.readProperty(key)
	}

	void setParent(final MutableTreeNode treeNode) {
		if (treeNode instanceof Tag) {
			super.setParentTag((Tag) treeNode)
		} else {
			throw new IllegalArgumentException()
		}
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor == NODE_FLAVOR) {
			return this
		}
		throw new UnsupportedFlavorException(flavor)
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	DataFlavor[] getTransferDataFlavors() {
		return this.flavors
	}

	@Override
	boolean isDataFlavorSupported(final DataFlavor flavor) {
		return Arrays.asList(this.flavors).contains(flavor)
	}

	/**
	 * Some objects can have multiple tags from the same tag group attached to them.
	 * For example, a course may be tagged with both "Finance" and "Computing" subjects.
	 *
	 * @param entity the entity which is being tagged
	 * @return true if multiple tags from the same group are allowed
	 */
	@API
	boolean isMultipleFor(final Class<? extends Taggable> entity) {
		return getTagRequirement(entity) != null && getTagRequirement(entity).getManyTermsAllowed() != null && getTagRequirement(entity).getManyTermsAllowed()
	}

	/**
	 * Determine whether this object is an ancestor of {@code anotherTag}. Note: ancestor is the opposite of descendant!
	 *
	 * @param anotherTag - the node to search is ancestors on.
	 * @return true if this object is an ancestor of {@code anotherNode}
	 */
	@API
	boolean isTagAncestor(Tag anotherTag) {
		if (!anotherTag) {
			return false
		}
		if (this.equalsIgnoreContext(anotherTag)) {
			return true
		}

		Tag ancestor = anotherTag

		while ((ancestor = ancestor.getParentTag()) != null) {
			if (this.equalsIgnoreContext(ancestor)) {
				return true
			}
		}
		return false
	}

	/**
	 *
	 * @return true if this tag is the top of the tree (the tag group)
	 */
	@API
	boolean isRoot() {
		return getParentTag() == null
	}

	boolean isTagGroup() {
		return !getChildTags().isEmpty()
	}

	boolean isTagDescendant(@Nonnull final Tag parentTag) {
		return parentTag.getChildTags().contains(this)
	}

	/**
	 * The objects from some entities must be tagged with at least one tag from a certain tag group
	 * @param entity the entity class which is being tagged
	 * @return true if a tag is required from the tag group this tag is part of
	 */
	@API
	boolean isRequiredFor(final Class<? extends Taggable> entity) {
		final TagRequirement nr = getTagRequirement(entity)
		if (nr == null) {
			return false
		}
		return getTagRequirement(entity) != null && getTagRequirement(entity).getIsRequired() != null && getTagRequirement(entity).getIsRequired()
	}

	void validateForDelete(@Nonnull ValidationResult validationResult) {
		if (getSpecialType() != null) {
			String message;
			switch (getSpecialType()) {
				case NodeSpecialType.SUBJECTS:
					message = "This tag group represents the categories of courses on your web site and cannot be deleted.";
					break;
				case NodeSpecialType.PAYROLL_WAGE_INTERVALS:
					message = "This tag group is required for the onCourse tutor pay feature.";
					break;
				case NodeSpecialType.ASSESSMENT_METHOD:
					message = "This tag group is required for the assessments.";
					break;
				default:
					throw new IllegalArgumentException("Unknown special type for tag");
			}

			validationResult.addFailure(ValidationFailure.validationFailure(this, NAME_KEY, message));
		}
	}

	@Override
	void validateForSave(@Nonnull ValidationResult result) {
		super.validateForSave(result)
		if (getTagRequirements().size() > 0 && !getIsVocabulary()) {
			result.addFailure(new ValidationFailure(this, TAG_REQUIREMENTS.getName(), "Only parent tags cann have requirements."));
		}
	}

	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((TagAttachmentRelation) relation)
	}

	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((TagAttachmentRelation) relation)
	}

	Class<? extends AttachmentRelation> getRelationClass() {
		return TagAttachmentRelation.class
	}

	/**
	 * This method returns the 'depth' of the tag For example, calculating the generation on the tag 'Japanese' with the following path would
	 * return 2. 'Japanese' has parent 'Languages', which has the parent 'Subjects'
	 *
	 * Subjects/Languages/Japanese
	 * @return the depth of a tag
	 */
	@API
	int getGeneration() {
		if (getParentTag() == null) {
			return 0
		}
		return getParentTag().getGeneration() + 1
	}

	/**
	 * @return full path name for this tag starting from root delimited with "-"
	 */
	@Nonnull
	@API
	String getPathName() {
		String result = getName()
		if (!isRoot()) {
			result = getParentTag().getPathName() + "-" + result
		}
		return result
	}

	/**
	 * @return the description of the tag (rich text)
	 */
	@API
	@Override
	String getContents() {
		return super.getContents()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

		/**
	 * @return
	 */
	@Nonnull
	@Override
	Boolean getIsVocabulary() {
		return super.getIsVocabulary()
	}

	/**
	 * @return true if this tag is visible on the website
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsWebVisible() {
		return super.getIsWebVisible()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return the name of the tag
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * Tags can have a shorter name which is used in URLs on a website
	 * The short name must be unique amongst siblings. If the short name is null, then
	 * the long name is used for the URL.
	 *
	 * @return the short name
	 */
	@API
	@Override
	String getShortName() {
		return super.getShortName()
	}

	/**
	 * @return
	 */
	@Override
	NodeSpecialType getSpecialType() {
		return super.getSpecialType()
	}

	/**
	 * A weight is an arbitrary number for determining ordering. The actual value of the weight
	 * should be ignored and this value should only be used for sorting.
	 * @return weight
	 */
	@API
	@Override
	Integer getWeight() {
		return super.getWeight()
	}

	/**
	 * @return all the children of this tag
	 * @see Tag#getAllChildren()
	 */
	@Nonnull
	@API
	@Override
	List<Tag> getChildTags() {
		return super.getChildTags()
	}

	/**
	 * @return the parent of this tag
	 */
	@Nullable
	@API
	@Override
	Tag getParentTag() {
		return super.getParentTag()
	}

	/**
	 * @return all the requirements for this tag group
	 */
	@Nonnull
	@API
	@Override
	List<TagRequirement> getTagRequirements() {
		return super.getTagRequirements()
	}

	/**
	 * This method gets all tags with the same parent as the current tag
	 * @return siblings of this tag
	 */
	@API @Nonnull
	List<Tag> getSiblings() {
		List<Tag> siblings

		if (isRoot()) {
			siblings = ObjectSelect.query(Tag.class)
					.where(PARENT_TAG.isNull())
					.select(getObjectContext())
		} else {
			siblings = new ArrayList<>(getParentTag().getChildTags())
		}

		// remove ourselves from the siblings list
		siblings.remove(this)

		return siblings
	}
}

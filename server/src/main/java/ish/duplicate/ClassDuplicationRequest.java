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
package ish.duplicate;

import ish.oncourse.cayenne.PersistentObjectI;
import org.apache.cayenne.Cayenne;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ClassDuplicationRequest implements Serializable {

	private List<Long> ids = new LinkedList<>();
	private Long courseId;
	private int daysTo;
	private boolean copyTutors;
	private	boolean copyTrainingPlans;
	private boolean applyDiscounts;
	private boolean copyCosts;
	private boolean copySitesAndRooms;
	private boolean tutorRosterOverrides;
	private boolean copyVetData;
	private boolean copyNotes;
	private boolean copyAssessments;
	private boolean copyOnlyMandatoryTags;

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}


	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}


	public <T extends PersistentObjectI> void setClasses(List<T> classes) {
		for (T record : classes) {
			ids.add(Cayenne.longPKForObject(record));
		}
	}

	public int getDaysTo() {
		return daysTo;
	}

	public void setDaysTo(int daysTo) {
		this.daysTo = daysTo;
	}

	public boolean isCopyTutors() {
		return copyTutors;
	}

	public void setCopyTutors(boolean copyTutors) {
		this.copyTutors = copyTutors;
	}

	public boolean isCopyTrainingPlans() {
		return copyTrainingPlans;
	}

	public void setCopyTrainingPlans(boolean copyTrainingPlans) {
		this.copyTrainingPlans = copyTrainingPlans;
	}

	public boolean isApplyDiscounts() {
		return applyDiscounts;
	}

	public void setApplyDiscounts(boolean applyDiscounts) {
		this.applyDiscounts = applyDiscounts;
	}

	public boolean isCopyCosts() {
		return copyCosts;
	}

	public void setCopyCosts(boolean copyCosts) {
		this.copyCosts = copyCosts;
	}

	public boolean isCopySitesAndRooms() {
		return copySitesAndRooms;
	}

	public void setCopySitesAndRooms(boolean copySitesAndRooms) {
		this.copySitesAndRooms = copySitesAndRooms;
	}

	public boolean isTutorRosterOverrides() {
		return tutorRosterOverrides;
	}

	public void setTutorRosterOverrides(boolean tutorRosterOverrides) {
		this.tutorRosterOverrides = tutorRosterOverrides;
	}

	public boolean isCopyVetData() {
		return copyVetData;
	}

	public void setCopyVetData(boolean copyVetData) {
		this.copyVetData = copyVetData;
	}

	public boolean isCopyNotes() {
		return copyNotes;
	}

	public void setCopyNotes(boolean copyNotes) {
		this.copyNotes = copyNotes;
	}

	public boolean isCopyAssessments() {
		return copyAssessments;
	}

	public void setCopyAssessments(boolean copyAssessments) {
		this.copyAssessments = copyAssessments;
	}

	public boolean isCopyOnlyMandatoryTags() {
		return copyOnlyMandatoryTags;
	}

	public void setCopyOnlyMandatoryTags(boolean copyOnlyMandatoryTags) {
		this.copyOnlyMandatoryTags = copyOnlyMandatoryTags;
	}
}

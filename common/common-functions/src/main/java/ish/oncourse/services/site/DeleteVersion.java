/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistentObject;
import org.apache.cayenne.query.SQLSelect;

import java.util.ArrayList;
import java.util.List;

class DeleteVersion {
	private ObjectContext context;
	private WebSiteVersion version;
	private Boolean liveBlankVersion = false;
	
	private void sleep() {
		try {
			Thread.sleep(1L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void delete() {
		for (WebSiteLayout layoutToDelete : version.getLayouts()) {
			deleteTemplates(layoutToDelete);
		}

		deleteAllContents();

		deleteVersionRelatedObjects();

		if (!liveBlankVersion) {
			context.invalidateObjects(version);
			context.deleteObjects(version);
		}
		
		context.commitChanges();
	}

	void deleteVersionRelatedObjects() {
		deleteEntities(WebMenu.class);
		deleteEntities(WebUrlAlias.class);
		deleteEntities(WebNode.class);
		deleteEntities(WebNodeType.class);
		deleteEntities(WebSiteLayout.class);
	}

	void deleteEntities(Class<? extends PersistentObject> entityClass) {
		SQLSelect.dataRowQuery("DELETE FROM $tableName where webSiteVersionId = $id").paramsArray(
				getTableNameBy(entityClass),
				version.getId()).select(context);
	}

	void deleteAllContents() {
		for (WebContent contentToDelete : version.getContents()) {
			sleep();
			context.deleteObjects(contentToDelete.getWebContentVisibilities());
			context.commitChanges();
		}
		SQLSelect.dataRowQuery("DELETE FROM $tableName where webSiteVersionId = $id").paramsArray(
				getTableNameBy(WebContent.class),
				version.getId()).select(context);
		context.deleteObjects(version.getContents());
	}

	void deleteTemplates(WebSiteLayout layout) {
		SQLSelect.dataRowQuery("DELETE FROM $tableName where layoutId = $layoutId").paramsArray(
				getTableNameBy(WebTemplate.class),
				layout.getId()).select(context);
	}

	//recursively remove all childrenMenus then remove parent
	private void deleteChildrenMenus(List<WebMenu> webMenus, ObjectContext objectContext) {
		sleep();
		List<WebMenu> copyList = new ArrayList<>(webMenus);
		for (WebMenu webMenu : copyList) {
			if (!webMenu.getChildrenMenus().isEmpty()) {
				deleteChildrenMenus(webMenu.getChildrenMenus(), objectContext);
			}
			objectContext.deleteObject(webMenu);
		}
	}

	String getTableNameBy(Class<? extends PersistentObject> entityClass) {
		return context.getEntityResolver().getObjEntity(entityClass).getDbEntity().getName();
	}

	public static DeleteVersion valueOf(WebSiteVersion version) {
		return valueOf(version, version.getObjectContext());
	}
	
	public static DeleteVersion valueOf(WebSiteVersion version, ObjectContext context) {
		return valueOf(version, context, false);
	}

	public static DeleteVersion valueOf(WebSiteVersion version, ObjectContext context, Boolean liveBlankVersion) {
		DeleteVersion deleteVersion = new DeleteVersion();
		deleteVersion.version = version;
		deleteVersion.context = context;
		deleteVersion.liveBlankVersion = liveBlankVersion;
		return deleteVersion;
	}
}

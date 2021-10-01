/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.SystemUser
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class UserDao implements CayenneLayer<SystemUser> {
    @Override
    SystemUser newObject(ObjectContext context) {
        context.newObject(SystemUser)
    }

    static SystemUser createSystemUser(DataContext context, Boolean isAdmin) {
        context.newObject(SystemUser).with { user ->
            user.canEditCMS = Boolean.TRUE
            user.canEditTara = Boolean.TRUE
            user.isActive = Boolean.TRUE
            user.isAdmin = isAdmin
            user.setDefaultAdministrationCentre(Site.getDefaultSite(context))
            user
        }
    }

    @Override
    SystemUser getById(ObjectContext context, Long id) {
        SelectById.query(SystemUser, id)
                .selectOne(context)
    }

    static SystemUser getByLogin(ObjectContext context, String login) {
        ObjectSelect.query(SystemUser)
                .where(SystemUser.LOGIN.eq(login).orExp(SystemUser.EMAIL.eq(login)))
                .selectOne(context)
    }

    static SystemUser getByEmail(ObjectContext context, String email) {
        ObjectSelect.query(SystemUser)
                .where(SystemUser.EMAIL.eq(email))
                .selectOne(context)
    }

    static SystemUser getByInvitation(ObjectContext context, String invitationToken) {
        ObjectSelect.query(SystemUser)
                .where(SystemUser.INVITATION_TOKEN.eq(invitationToken))
                .selectOne(context)
    }

    static List<SystemUser> getList(ObjectContext context) {
        ObjectSelect.query(SystemUser)
                .prefetch(SystemUser.ACL_ROLES.joint())
                .prefetch(SystemUser.DEFAULT_ADMINISTRATION_CENTRE.joint())
                .orderBy(SystemUser.IS_ACTIVE.desc())
                .orderBy(SystemUser.EMAIL.asc())
                .select(context)
    }

    static List<SystemUser> getActive(ObjectContext context) {
        ObjectSelect.query(SystemUser)
                .where(SystemUser.IS_ACTIVE.isTrue())
                .orderBy(SystemUser.LAST_NAME.asc())
                .select(context)
    }
}

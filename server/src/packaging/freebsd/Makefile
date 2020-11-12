# $FreeBSD$

PORTNAME=               onCourseServer
PORTVERSION=            8.20
JENKINS_BUILD=          lastReleaseBuild
MASTER_SITES=           https://build.ish.com.au/job/angel-branches/branch/release-${PORTVERSION}/${JENKINS_BUILD}/artifact/server/build/distributions/unix/
DISTNAME=               ${PORTNAME}

MAINTAINER=             support@ish.com.au
COMMENT=                A port for onCourseServer

LICENSE=                ish
LICENSE_NAME=           ish
LICENSE_FILE=           ${FILESDIR}/eula.html
LICENSE_PERMS=          pkg-sell auto-accept

NO_BUILD=               yes

CATEGORIES=             java

USE_RC_SUBR=            onCourseServer.sh

USES=                   zip
USE_JAVA=               yes
JAVA_VERSION=           1.8+

ONCOURSE_HOME=          ${PREFIX}/onCourse
SUB_LIST+=              PORTVERSION=${PORTVERSION}

do-install:
	${MKDIR} ${STAGEDIR}${ONCOURSE_HOME}
	${INSTALL_DATA} ${WRKDIR}/${DISTNAME}/server-${PORTVERSION}.jar ${STAGEDIR}${ONCOURSE_HOME}
	${INSTALL_DATA} ${WRKDIR}/${DISTNAME}/onCourse.cfg ${STAGEDIR}${ONCOURSE_HOME}/onCourse.cfg.sample
	(cd ${WRKDIR}/${DISTNAME} && ${COPYTREE_SHARE} lib ${STAGEDIR}${ONCOURSE_HOME})

.include <bsd.port.mk>
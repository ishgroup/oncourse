#!/usr/local/bin/bash

VERSION=$1


echo "*****"
echo "***** Updating the onCourseServer port definition *****"
echo "*****"

unset JAVA_HOME
export PORTSDIR=/var/poudriere/ports/default

rm -f /var/poudriere/ports/default/distfiles/onCourseServer*
rm -f /var/poudriere/distfiles/onCourseServer*

cd /var/poudriere/ports/default/ish/onCourseServer/

/usr/bin/sed -i '' "s|^PORTVERSION=.*$|PORTVERSION=            ${VERSION}|" Makefile

echo "*****"
echo "***** Fetching the onCourseServer binaries *****"
echo "*****"

make DISABLE_VULNERABILITIES=yes distclean clean makesum

# Need to run it twice since it spits out some garbage the first time
make DISABLE_VULNERABILITIES=yes makeplist
make DISABLE_VULNERABILITIES=yes makeplist | tail -n +2 > pkg-plist

make clean

echo "*****"
echo "***** Building onCourseServer packages *****"
echo "*****"

/usr/local/bin/poudriere bulk -j 10 ish/onCourseServer

echo "*****"
echo "***** Upgrading the template jail *****"
echo "*****"

pkg -j oncourse-template upgrade -y
jexec oncourse-template /usr/local/bin/fc-cache -f   # Rebuild font cache
freebsd-update --not-running-from-cron -b /jails/templates/oncourse fetch install

echo "*****"
echo "***** Removing some surplus folders to make things smaller ******"
echo "*****"

rm -rf /jails/templates/oncourse/var/cache
rm -rf /jails/templates/oncourse/boot
rm -rf /jails/templates/oncourse/rescue


echo "*****"
echo "***** Snaphotting ${VERSION} and distributing to cloud jail hosts *****"
echo "*****"


# Then make a snapshot with the correct version
zfs snapshot storage/jails/templates/oncourse@${VERSION}


# Targets:
# * splash
# * SCC
# * CCE
# * WEA Hunter

TARGETS="splash.internal,tank 192.168.33.12,storage 10.19.40.11,tank 180.214.92.196,tank"
for i in $TARGETS; do IFS=","; set $i

  HOST=$1
  POOL=$2
  unset IFS

  echo "*****"
  echo "***** Sending snapshot version ${VERSION} to host ${HOST} and pool ${POOL}"
  echo "*****"

  SSH="/usr/bin/ssh -i /var/lib/jenkins/.ssh/id_rsa jenkins@$HOST"

  # Get the last version on the remote
  LAST_VERSION=$($SSH 'zfs list -H -t snapshot -o name -S creation' | grep ^"$POOL"/jails/templates/oncourse | head -1 | cut -f 2 -d '@')

  echo $LAST_VERSION

  if [ "$LAST_VERSION" != "$VERSION" ]; then
    ZFS_SEND="zfs send -i storage/jails/templates/oncourse@${LAST_VERSION} storage/jails/templates/oncourse@${VERSION}"

    # Push that to destination target
    $ZFS_SEND | bzip2 -c | $SSH "bzcat | zfs recv -F $POOL/jails/templates/oncourse"
  fi
done
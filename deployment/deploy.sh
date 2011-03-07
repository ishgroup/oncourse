#!/bin/sh

# A script to deploy the onCourse web application suite from Jenkins

DEPLOYMENT_USER=wapp

usage () {
  echo "${0##*/} [-b buildnumber] -a application -e environment"
  echo ""
  echo "  -b Specify the Jenkins build number to deploy. If not set, then deploy the most recent build."
  echo "  -a The application to deploy. Must be one of:"
  echo "       web"
  echo "       services"
  echo "       search"
  echo "       cms"
  echo "       portal"
  echo "       enrol"
  echo "       asset"
  echo "  -e The environment to deploy to."
  echo "       dev"
  echo "       test"
  echo "       staging"
  exit 0;
}

fail () {
  echo '' ; echo "===>>> $*" ; echo "===>>> Aborting script"
  usage
  exit 1;
}

while getopts b:a:e: arg
  do case ${arg} in
    b) buildnum=${OPTARG};;
    a) app="${OPTARG}";;
    e) environment=${OPTARG};;
    ?) usage; ;;
  esac;
done; shift $(( ${OPTIND} - 1 ))

[ -z "$app" ] && fail "You must specify the application to deploy."
[ -z "$environment" ] && fail "You must specify the environment."


test_servers="freebsd.vm.ish.com.au"
#test_servers="freebsd.vm.ish.com.au freebsd2.vm.ish.com.au"
staging_servers="splash.internal"

#set binary's
rsync="/usr/local/bin/rsync -avv"
ssh="/usr/bin/ssh"

if [ "${environment}" = "dev" ]; then
  servers=${test_servers}
  rootPath=/var/onCourse
elif [ "${environment}" = "test" ]; then
  servers=${test_servers}
  rootPath=/var/onCourseTest
elif [ "${environment}" = "staging" ]; then
  servers=${staging_servers}
  rootPath=/var/tomonCourseStaging
fi


if [ -z "$buildnum" ]; then
  hudsonPath=${WORKSPACE}/../lastSuccessful/archive/trunk
else
  hudsonPath=${WORKSPACE}/../builds/${buildnum}/archive/trunk
fi

case ${app} in
      Web|web) 
        hudsonPath=${hudsonPath}/website/website-web/target/web.war
        destPath=${rootPath}/app/A1
        appName=web
        ;;
      Search|search) 
        hudsonPath=${hudsonPath}/searchapp/target/search.war
        destPath=${rootPath}/app/A1
        appName=search
        ;;
      Enrol|enrol)
        hudsonPath=${hudsonPath}/enrol/target/enrol.war
        destPath=${rootPath}/app/B1
        appName=enrol
        ;;
      Portal|portal)
        hudsonPath=${hudsonPath}/portal/target/portal.war
        destPath=${rootPath}/app/B1
        appName=portal
        ;;
      CMS|cms) 
        hudsonPath=${hudsonPath}/cms/cms-web/target/cms.war
        destPath=${rootPath}/app/B1
        appName=cms
        ;;
      Services|services)
        hudsonPath=${hudsonPath}/webservices/webservices-web/target/webservices.war
        destPath=${rootPath}/app/C1
        appName=services
        ;;
      Asset|asset)
        hudsonPath=${hudsonPath}/asset/target/asset.war
        destPath=${rootPath}/app/D1
        appName=asset
        ;;
      All|all)
        for a in "web" "services" "cms" "enrol" "portal" "search" "asset"; do
          thisscript=$(pwd)/$0
	    if [ -z "$buildnum" ]; then
              echo "${thisscript} -e ${environment} -a ${a}"
              (${thisscript} -e ${environment} -a ${a})
	    else
              echo "${thisscript} -e ${environment} -a ${a} -b ${buildnum}"
              (${thisscript} -e ${environment} -a ${a} -b ${buildnum})
	    fi
        done
        exit 0
        ;;
      *)
        fail "Invalid application passed."
        ;;
esac;

echo "Deploying ${app} to path ---> ${destPath}"

for server in ${servers}; do
  echo ""
  echo "Uploading to ${server} now..."

  ${ssh} ${DEPLOYMENT_USER}@${server} "cd ${destPath}; ${destPath}/app.sh stop"
  ${rsync} ${hudsonPath} ${DEPLOYMENT_USER}@${server}:${destPath}/webapps/${appName}.war
  ${ssh} ${DEPLOYMENT_USER}@${server} "cd ${destPath}; ${destPath}/app.sh start"

done


echo "All done."

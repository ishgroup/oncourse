This folder contains reports written in JasperReports format for onCourse. Reports can be uploaded directly into onCourse through the client application.

onCourse customers are welcome to modify and use these reports as you need.


== Developer notes

If you edit all the reports in bulk and need to update the version numbers easily, this script will help:

    find . -name *.jrxml | xargs perl -i -pe 's/versionNumber" value="([0-9]+)/"versionNumber\" value=\"".($1+1)/e'
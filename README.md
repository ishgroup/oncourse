#How to check current deployed version of willow apps

Each willow app has  /ISHHealthCheck servlet implemented, so to check current app version just open followed URL 

services:
https://secure-payment.oncourse.net.au/services/ISHHealthCheck

usi:
https://secure-payment.oncourse.net.au/usi/ISHHealthCheck

web:
https://template-a.oncourse.cc/ISHHealthCheck

checkout-api:
https://template-a.oncourse.cc/a/ISHHealthCheck

editor-api:
https://template-a.oncourse.cc/editor/ISHHealthCheck

billing-api:
https://provisioning.ish.com.au/b/ISHHealthCheck

new portal:
https://www.skillsoncourse.com.au/p/ISHHealthCheck

old portal:
https://www.skillsoncourse.com.au/portal/ISHHealthCheck

Ract apps could be checked by:

checkout:
https://template-a.oncourse.cc/s/oncourse-releases/checkout/stable/dynamic.js
and look for APP_VERSION variable

editor:
https://template-a.oncourse.cc/s/oncourse-releases/editor/stable/editor.js

#run willow app
lets review unexample of admin app
Admin, as an almost all app, require zk srver, so
1. unzip solr-7.zip (se root dir) to any dir on your mashine
2. Run zk cluster by followed command
`{zkDir}/zk.sh start`
3. Copy config file example

    from /admin/src/dist/application.properties
   
    to /admin/application.properties
4. Change db props to your ones (db_user, db_pass ,db_url)
5. Run admin via intellij run congiguration

    main class: ish.oncourse.admin.AdminApp

    working dir: /admin
6. http://127.0.0.1/8306/willowAdmin
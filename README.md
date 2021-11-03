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
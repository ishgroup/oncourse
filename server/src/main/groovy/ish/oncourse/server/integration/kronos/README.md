#Kronos integration
Integration allows us to establish interaction between Canvas LMS and onCourse enrol system.

1. Open a Kronos integration form in onCourse****
2. Fill in all fields and press 'Configure'
   - Username (Provided by NIDA)
   - Password (Provided by NIDA)
   - API developer key (Provided by NIDA)
   - Company short name (you can get it from urls https://secure.workforceready.com.au/ta/61***11.login?NoRedirect=1 OR https://secure.workforceready.com.au/ta/61***11.home, it is "61***11")
   - Company id (you can find it by ui request or use company short name with '|', e.g. '|61***11')
3. Press 'Save'
6. The Kronos integration is ready to work

You can use the following closures:

```
kronos {
    scheduleName "Weekly Test Schedule"
    session record
  }
```




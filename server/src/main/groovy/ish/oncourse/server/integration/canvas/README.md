#Canvas Integration
Integration allows us to establish interaction between Canvas LMS and onCourse enrol system.

1. Create 'developer key' in Canvas ([ https://YOUR.CANVAS.DOMAIN.com/accounts/1/developer_keys](https://YOUR.CANVAS.DOMAIN.com/accounts/1/developer_keys))
   1. Fill in the fields
      - Key Name
      - Owner Email
      - Redirect URIs (your onCourse uri:  [https://YOUR.ONCOURSE.xx/automation/integration/8/new](https://YOUR.ONCOURSE.xx/automation/integration/8/new))
   2. Save
   3. Make State 'ON'

2. Open Canvas integration form in onCourse

3. Fill in all fields and press 'Configure'
   - Name
   - Base url ([YOUR.CANVAS.DOMAIN.com](YOUR.CANVAS.DOMAIN.com)) without 'https://'
   - Account id (Canvas account)
   - Client id (from 'developer key')
   - Client secret (from 'developer key', click 'show key')
   
4. After pressing 'Configure' you will be redirected to the page [https://YOUR.CANVAS.DOMAIN.com/login/oauth2/confirm](https://YOUR.CANVAS.DOMAIN.com/login/oauth2/confirm)
   1. If page don't contain 'Authorise' button or [YOUR.CANVAS.DOMAIN]() is wrong, please manually paste and follow [https://YOUR.CANVAS.DOMAIN.com/login/canvas](https://YOUR.CANVAS.DOMAIN.com/login/canvas) and authorise with credentials

5. Press 'Authorise' button, you will be redirected to the onCourse Canvas integration page with filled fields.
6. Press 'Save'
7. Canvas integration is ready to work

You can use the following closures:

```
Canvas {
   enrolment e
   course_code "My-Canvas-Course"
   section_code "key." + e.courseClass.uniqueCode
   create_section true
   create_student true
   authentication_provider_id 1
   create_password "myCustomField"
   course_blueprint "ABC"
   add_tutors true
   student_attributes student
}
```

```
canvas_expire {
   ends "enrolment.canvas_expire"
}
```




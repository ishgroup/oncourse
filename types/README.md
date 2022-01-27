# Publishing Types module

OnCourse is stored in Nexus Repository Manager.
https://repo.ish.com.au/

## Сonfig Gradle
Open `~/.gradle/gradle.properties`.If it doesn't exist, create it:
* open terminal 
* execute command

    ```open ~/.gradle/gradle.properties```

Add properties:

    nexusUsername = 'your username in nexus'
    nexusPassword = 'your password'

## Publication to Nexus
Execute command:

    ./gradlew -x test types:publish -PreleaseVersion='your_version'

###### Don't forget to update version in dependencies


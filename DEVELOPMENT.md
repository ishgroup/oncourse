## Install and configure onCourse

Instructions can be found [here](https://www.ish.com.au/onCourse/doc/manual/#install).


## Running from source

Rather than downloading onCourse from releases, you can run it from checked out source.

    ./gradlew -x test server:runServer


If you use Intellij you'll find that the project already has an .idea folder with most of the configuration already there. Don't import the project, just file->open from inside Intellij.

To run the project from Intellij, Run-> Edit configurations. Create a new configuration and set the main class to `server/src/main/java/ish/oncourse/server/AngelServer.java`, module `onCourse.server.main` and working directory as `server`.


## Help

Post in our [forums](https://oncourse.discourse.group/) with any questions or suggestions.

## How to run angel server

1. Install mariadb. 
   <ul>
      <li>If you are a macOS user, first you should install homebrew:
   
         /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
      and then run
   
         brew install mariadb
      For the moment of this doc writing homebrew installs 10.6.4 mariadb version by default.<br></li>
      <li>Run <code>brew services start mariadb</code> to start server.</li>
      <li>Run <code>mysql_secure_installation</code> and configure root account.</li>
      <li>Run <code>mariadb -uroot -p[root password, if you changed it]</code> to launch mariadb, then enter next commands to create new user with root privileges:

         CREATE USER 'onCourse' IDENTIFIED BY 'password';
         GRANT ALL PRIVILEGES ON * . * TO 'onCourse';
         FLUSH PRIVILEGES;

      </li>
      <li>Create database: run<br>

         CREATE DATABASE angel_db;
         exit;

      </li>
   </ul>
2.  Install Java 11. Run
    <code>brew cask install java11</code>
3. Setup onCourse.yml.<br>
   <ul>
   <li>Copy file <code>onCourse.yml</code> from <code>server/src/packaging/onCourse.yml</code> to <code>server</code> directory.<br>
   <li>Configure smtp. Change <code>[your_email@gmail.com]</code> with your email and <code>[your_email_password]</code> with password to your email account. Follow <a href="https://support.google.com/mail/answer/7126229?hl=en">Google documentation</a> to configure smtp for your email address.
   </ul>
4. Create and put near configured <code>onCourse.yml</code> file <code>createAdminUsers.txt</code>, that must have lines in the same format: <code>[firstname] [lastname] [your email]</code> like <code>Peter Peterson your_email@gmail.com</code> of new system user, which will be added to database. After first running of the server you will receive on selected email letter with credentials to the system.
5. Build project with command <code>./gradlew -x test server:build</code> executed from root folder.
6. If you use Intellij Idea, create Application configuration, set main class <code>server/src/main/java/ish/oncourse/server/AngelServer.java</code>, module <code>onCourse.server.main</code> and working directory as <code>server</code>.
   Then run configuration.<br>
   Else run task <code> ./gradlew -x test server:runServer</code> from root directory.
8. Check email. Assign link from email message.

If you have any error, you know how to avoid any error, or you want to improve any point, contact dmitry@ish.com.au. Your improvements may help next developers.

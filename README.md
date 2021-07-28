Running using jar:

1. In project parent directory, start CMD and run command "mvn clean install" to create runnable jar
2. Make sure your java version is java 15 
3. Navigate to \target folder and open CMD from this folder
4. Run command "{path to your java.exe}\java" -jar {path to jar}\Sample-0.0.1-SNAPSHOT.jar
5. "rates.json" included in project parent directory needs to be with the jar so copy it to \target folder, unless you want to update application.yml to the correct path and rebuild jar. 
   (Ideally this would be a command line argument in the future)
6. Now you can hit the rest endpoints that are described in the assignment file.


Running/Debugging using Intellij:

1. In Intellij, open project folder. Go to File -> Project Structure to make sure SDK is version 15
2. Go to Run\Edit Configurations
3. Add new configuration -> Application
4. Name the configuration and choose SampleApplication as main class
5. Reload all maven projects via maven tab
6. You can now perform any part of the maven lifecycle and run/debug the application
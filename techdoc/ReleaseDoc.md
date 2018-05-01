# Release Doc

## Manual Tests
 * Drag an invalid file into the LogViewer -> no exception should be thrown.
 * Drag a valid log file into the LogViewer and compare the displayed details of an Exception with a text file. -> no difference should be visible.
 * Search for a substring of the title -> the entry should be found
 * Search for a substring in an exception message -> the entry should be found
 * Select an entry and copy it to a text editor -> it should look the same as the one found in the log file

## Release zip
 * Increase version number
 * Generate .jar file (mvn clean verify)
 * Rename .jar to ivy-log-viewer.jar
 * Make an IvyLogViewer-0.x.x.zip file containing ivy-log-viewer.jar and both of the start scripts
 * Release it on Github
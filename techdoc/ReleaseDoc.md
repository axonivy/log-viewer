# Release Doc

## Manual Tests
 * Drag an invalid file into the LogViewer -> no exception should be thrown.
 * Drag a valid log file into the LogViewer and compare the displayed details of an Exception with a text file. -> no difference should be visible.
 * Search for a substring of the title -> the entry should be found
 * Search for a substring in an exception message -> the entry should be found
 * Select an entry and copy it to a text editor -> it should look the same as the one found in the log file
 * Milton
    * Analyse testlogs -> it should find issues
    * Find a issue -> number of "issues found" should be right

## Release zip
 * Increase version number in pom.xml (use versions plugin in the future)
 * Generate .zip file (mvn clean verify)
 * Release it on Github
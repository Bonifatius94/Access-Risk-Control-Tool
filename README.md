# HOW TO BUILD THIS PROJECT:

## Development Machine Setup:
- install IntelliJ Idea Community Edition 2018.1 (including maven)
- install JDK 8 Update 172 (this should also include JRE 8 Update 172)
- install VC++ 2005 runtime (for sapjco3.jar; only required on Windows, see sap jco documentation inside zip file in resources)

## Project Build Setup:
- open IntelliJ Idea
- select the 'open project' option and choose the 'art' folder inside your local repository clone (this should load the project which may take some time)
- check if the JDK location is specified for the project: File -> Project Structure -> New -> JDK -> C:\Program Files\Java\jdk1.8.0_172 -> OK
- set the project language level to 8 (Java 8) and apply changes

## Compiling the Project:
- load maven side bar: View -> Tool Windows -> Maven Projects (if the side bar is empty, click it, or if that does not help, click the refresh button at the top of the side bar)
- open to the 'Lifecycle' region -> execute 'clean' -> execute 'compile'

## Running the Unit Tests:
- execute 'clean' (this should not be required but is good measure)
- execute 'test'

## Making a Release:
- execute 'clean' -> execute 'package'
- go to the 'target' folder (inside 'art') -> copy 'AccessRiskTool.jar' and the entire 'lib' folder
- make sure that the lib folder does not contain any file except *.jar files and also not the file 'sapjco3.jar'
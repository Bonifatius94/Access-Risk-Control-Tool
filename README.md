# HOW TO BUILD THIS PROJECT

## Development Machine Setup:
- install IntelliJ Idea 2018.1 including Maven (at least the free Community Edition), see: [Jetbrains IntelliJ Idea Downloads](https://www.jetbrains.com/idea/download/)
- install JDK 8 Update 172 (this should also include JRE 8 Update 172), see: [JDK 8 Downloads](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- install VC++ 2005 runtime (for sapjco3.jar; only required on Windows, see sap jco documentation inside zip file in resources), see: [Microsoft VC++ 2005 Downloads](https://www.microsoft.com/en-US/download/details.aspx?id=21254)

## Project Build Setup:
- open IntelliJ Idea
- select the 'open project' option and choose the 'art' folder inside your local repository clone (this should load the project which may take some time)
- check if the JDK location is specified for the project: File -> Project Structure -> New -> JDK -> C:\Program Files\Java\jdk1.8.0_172 -> OK
- set the project language level to 8 (Java 8) and apply changes

## Compiling the Project:
- load maven side bar: View -> Tool Windows -> Maven Projects (if the side bar is empty, click it, or if that does not help, click the refresh button at the top of the side bar)
- open the 'Lifecycle' region
- execute 'clean'
- execute 'compile'

## Running the Unit Tests:
- open the 'Lifecycle' region
- execute 'clean' (this should not be required but is good measure)
- execute 'test'

## Making a Release:
- open the 'Lifecycle' region
- execute 'clean'
- execute 'package'
- go to the 'target' folder (inside 'art') -> copy 'AccessRiskTool.jar' and the entire 'lib' folder to the release build folder of your choice
- make sure that the lib folder does not contain any files except *.jar files and also not the file 'sapjco3.jar'
- add the README.txt file from a former build
- zip the build folder and release it

## Test Environment Notes:
- install only JRE 8 Update 172 and uninstall all other JREs on the machine (it was very difficult to find a JRE version that works best with all dependencies)
- on Windows: execute the JRE setup and follow the installation wizard
- on linux: install the JRE using following commands ([source](https://wiki.ubuntuusers.de/Java/Installation/Oracle_Java/Java_8/))

```sh
#!/bin/bash

# ========================================================
#              JAVA 8 SETUP INSTRUCTIONS
# ========================================================

# preparation
# ===========
# unzip and copy your desired java JRE package into the folder /opt/Oracle_Java

# define Java 8 update number
JAVA8_INSTALLATION_FOLDER=jre1.8.0_172
JAVA_ROOT=/opt/Oracle_Java

# install java on ubuntu
# ======================
sudo update-alternatives --install "/usr/bin/java" "java" "$JAVA_ROOT/$JAVA8_INSTALLATION_FOLDER/bin/java" 1
sudo update-alternatives --install "/usr/bin/javaws" "javaws" "$JAVA_ROOT/$JAVA8_INSTALLATION_FOLDER/bin/javaws" 1 
sudo update-alternatives --set "java" "$JAVA_ROOT/$JAVA8_INSTALLATION_FOLDER/bin/java"
sudo update-alternatives --set "javaws" "$JAVA_ROOT/$JAVA8_INSTALLATION_FOLDER/bin/javaws"

# note: this script was tested on linux ubuntu 16.04 LTS
# see: https://wiki.ubuntuusers.de/Java/Installation/Oracle_Java/Java_8/

# ========================================================
#                Marco Tröster, 12.07.2018
# ========================================================
```

## Notes:
The development setup procedure was only tested on a Windows 10 machine.

Please feel free to report any issues with your deveploment or test environment setup.

Technical documentation was made by Marco Tröster (marco.troester@student.uni-augsburg.de)
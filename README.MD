# Building an executable

## Windows
We use jpackage to build an installer for this project

# Prerequisites 
You will need Wix 3.0 or later installed on your machine, you can download it from here https://wixtoolset.org/

First run 

```text
mvn clean package
```

Run the 
```text
build_windows_exe.bat
```
To generate a production build

### Or

Run the
```text
build_windows_exe_debuggable.bat
```
To generate a debuggable build for testing, this will have a console where all std output will be piped to

The installer exe will be output to the `/builds/windows` folder in the root of this project.
# noa-libre
UNO API of LibreOffice easy and understandable - Nice Office Access

NOA-libre is an object-oriented lightweight Java wrapper around LibreOffice UNO API, providing higher-level abstraction of 
many UNO interfaces. It is a fork of Ubion's NOA (Nice Office Access) built around API of OpenOffice.org.

This again is a fork of LibreOffice/noa-libre.

## Documentation

Find documentation in the [Wiki](https://github.com/LibreOffice/noa-libre/wiki).

There are also a number of examples in the [examples](https://github.com/LibreOffice/noa-libre/tree/master/examples) folder.

## Building

~~To build NOA-libre, [maven-ant-tasks](https://maven.apache.org/ant-tasks/) is needed to pull LibreOffice Java artifacts from~~
~~Maven central repository. Install it into your ant's lib/ directory or adjust ${ant.lib} property in build.xml accordingly to~~ 
~~point to maven-ant-tasks location.~~

~~Additionally, you need swt.jar added to your build path. Download the matching version for your operating system from http://maven-eclipse.github.io/maven and add it to the build path (e.g. place it in `lib` folder.~~

Build with Maven. Use do.skipXXX properties to enable/disable some plugins. 

## Release Notes

Please read [Release Notes](https://github.com/LibreOffice/noa-libre/releases/tag/v3.0.0) if you are upgrading from older versions of NOA-libre


## FORK

This is a fork heaving this wonderful project into Maven universe.
SWT becomes an optional dependency, may be used with IDesktopServiceSWT / DesktopServiceSWT.

**Differences to Origin:**
* Mavenized
* Build with Java 11
* Linked to Libreoffice 7.x
* Migrated back to bootstrap-connector
* SWT support via NativeViewHandle without explicit SWT dependency
* ApplicationAssistant functionality is mostly deactivated on Windows.
  Need to reimplement registry access first (unfortunately com.github.sarxos:windows-registry-util0.3 is not compatible with Java 9 upwards)
* Using IOfficeApplication.NOA_NATIVE_LIB_SKIP system property you can skip loading of native libs in NativeView.class (and do it yourself then of course)
* Solves "The default package '.' is not permitted by the Import-Package syntax" problem when used with maven-bundle-plugin
* Removes GPLv3 threat by removing ag.ion.bion.helper.TextHandler. In case you need that functionality, have a look on origin and reimplement it yourself.


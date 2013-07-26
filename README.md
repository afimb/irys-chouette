# Irys-Chouette

Irys-chouette is a java open source project on SIRI normalization projet connected to a Chouette Database. It's divided in differents module :
* irys-chouette-common : common classes
* irys-chouette-server : SIRI server including following SIRI services in Request/Response mode
  * General Messaging
  * Stop Monitoring
  * Check Status
  * Line Discovery
  * Stop Discovery
* irys-chouette-client : SIRI client including same services as server and populating a chouette Database
* irys-chouette-realtime-simulator : small tool populating server database with simulated realtime data on vehicle journeys and general messages
* irys-chouette-siri-hibernate-dao : database model and connectivity on realtime tables
* irys-chouette-webtopo-server : SIRI server including following SIRI services in Request/Response mode extended to webtopo service
* irys-chouette-webtopo-client :  SIRI client including same services as server and populating a chouette Database  using webtopo service for initialization

Requirements
------------

* oraclejdk7
* openjdk7

External Deps
-------------
On Debian/Ubuntu/Kubuntu OS :
```sh
sudo apt-get install openjdk-7-jdk
sudo apt-get install git
```

Installation
------------

Get git repository
```sh
cd workspace
git clone -b V2_0_3 git://github.com/afimb/irys-chouette
cd irys-chouette
```

Test
----

```sh
mvn test
```

More Information
----------------

More information can be found on the [project website on GitHub](.).
There is extensive usage documentation available [on the wiki](../../wiki).

Example Usage
-------------

Install
```sh
mvn -Dmaven.test.skip=true install
```

License
-------

This project is licensed under the CeCILL-B license, a copy of which can be found in the [LICENSE](./LICENSE.md) file.

Release Notes
-------------

The release notes can be found in [CHANGELOG](./CHANGELOG.md) file

Support
-------

Users looking for support should file an issue on the GitHub [issue tracking page](../../issues), or file a [pull request](../../pulls) if you have a fix available.

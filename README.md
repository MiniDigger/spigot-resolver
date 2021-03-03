# Spigot-Resolver

This is a small tool , to create a wiki over at spigotmc.org

### These pages contain all versions of spigot you can build using buildtools (java -jar BuildTools.jar --rev <version>), together with the nms and bukkit (maven) version and links to the sources on stash for that version.

Legacy versions 1.8 - 1.9 - https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-legacy/

Versions 1.10 - 1.15 - https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-10-1-15/

Versions 1.16 and up - https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/

Forum Thread on spigotmc - https://www.spigotmc.org/threads/spigot-nms-and-minecraft-version-overview.233194/

---------------------------------------------------------------------------------
## How To: (Build it from source)

To compile Spigot-Resolver , You need:

+ Internet connection
+ JDK 8 or newer version of JDK

### You need to : 
Clone this repo , and run this in the project directory:

```shell
./gradlew clean build shadowjar
```

The program should be located in `/libs/build/`

To run this program You need to be in this directory and run from terminal/cmd `java -jar shadow-all.jar`

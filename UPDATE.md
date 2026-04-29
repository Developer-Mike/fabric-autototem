1. Update dependencies in [./gradle.properties](./gradle.properties)
  - https://fabricmc.net/develop/
2. Update loom dependency in [./build.gradle](./build.gradle)
3. Update java version in [./build.gradle](./build.gradle) and [./shell.nix](./shell.nix) and [./src/main/resources/autototem.mixins.json](./src/main/resources/autototem.mixins.json)
4. Update [./gradlew](./gradlew)
  - https://github.com/FabricMC/fabric-example-mod/blob/<VERSION>
5. Update [./gradle/wrapper/gradle-wrapper.properties](./gradle/wrapper/gradle-wrapper.properties)
  - https://github.com/FabricMC/fabric-example-mod/blob/<VERSION>
6. Update [./gradle/wrapper/gradle-wrapper.jar](./gradle/wrapper/gradle-wrapper.jar)
  - https://github.com/FabricMC/fabric-example-mod/blob/<VERSION>
7. Delete `./.gradle`, `./run` folder
8. Run `./gradlew genSources`
# SecureRandom Sample App

Simple application that will use `SecureRandom` to generate and 
then print random bytes.

### Running

Java:
```shell
./gradlew :sample:run -PKMP_TARGETS="JVM"
```

Native:
```shell
./gradlew :sample:runDebugExecutableNativeSample -PKMP_TARGETS="LINUX_ARM64,LINUX_X64,MACOS_ARM64,MACOS_X64,MINGW_X64,MINGW_X86"
```

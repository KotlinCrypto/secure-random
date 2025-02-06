# SecureRandom Sample App

<!-- TODO: CryptoRand Issue #23 -->
Simple application that will use `SecureRandom` to generate and then print random bytes.

### Running

Java:
```shell
./gradlew :sample:jvmRun -PKMP_TARGETS="JVM"
```

Native:
```shell
./gradlew :sample:runDebugExecutableNativeHost -PKMP_TARGETS="LINUX_ARM64,LINUX_X64,MACOS_ARM64,MACOS_X64,MINGW_X64"
```

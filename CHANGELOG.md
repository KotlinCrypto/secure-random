# CHANGELOG

## Version 0.3.2 (2024-08-31)
 - Updates dependencies
     - Kotlin `1.9.23` -> `1.9.24`
 - Fixes multiplatform metadata manifest `unique_name` parameter for 
   all source sets to be truly unique. [[#19]][19]
 - Updates jvm `.kotlin_module` with truly unique file name. [[#19]][19]

## Version 0.3.1 (2024-03-20)
 - Fix `js.nodejs` and `wasmJs.nodejs` array type exception [[#14]][14]
 - Ensure `wasmWasi` temporary array is back-filled when copying bytes [[#16]][16]

## Version 0.3.0 (2024-03-18)
 - Updates dependencies
     - Kotlin `1.9.21` -> `1.9.23`
 - Fix `ClassCastException` for `js.nodejs` [[#12]][12]
 - Fix max size exception for `js.browser` [[#12]][12]
 - Add experimental support for `wasmJs` & `wasmWasi` [[#12]][12]
 - Add support for Java9 `JPMS` via Multi-Release jar [#13][13]

## Version 0.2.0 (2023-11-30)
 - Updates dependencies
     - Kotlin `1.8.10` -> `1.9.21`
 - Drops support for the following deprecated targets:
     - `iosArm32`
     - `watchosX86`
     - `linuxArm32Hfp`
     - `linuxMips32`
     - `linuxMipsel32`
     - `mingwX86`
     - `wasm32`

## Version 0.1.0 (2023-03-07)
 - Initial Release

[12]: https://github.com/KotlinCrypto/secure-random/pull/12
[13]: https://github.com/KotlinCrypto/secure-random/pull/13
[14]: https://github.com/KotlinCrypto/secure-random/pull/14
[16]: https://github.com/KotlinCrypto/secure-random/pull/16
[19]: https://github.com/KotlinCrypto/secure-random/pull/19

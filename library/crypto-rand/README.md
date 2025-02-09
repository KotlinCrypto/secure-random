# Module crypto-rand

Obtain cryptographically secure random bytes from system sources.

```kotlin
fun main() {
    val bytes = try {
        CryptoRand.Default.nextBytes(ByteArray(16))
    } catch (e: RandomnessProcurementException) {
        // Underlying platform APIs failed to procure data
        // from system sources.
        e.printStackTrace()
        return
    }

    println(bytes.toList())
    // 
}
```

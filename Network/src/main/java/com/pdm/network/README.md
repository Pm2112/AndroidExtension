# NETWORK

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        networkHelper = NetworkHelper(this)
    }

    companion object {
        lateinit var instance: MyApplication
        lateinit var networkHelper: NetworkHelper
    }
}
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lắng nghe trạng thái mạng
        MyApplication.networkHelper.observe(this, Observer { isConnected ->
            if (isConnected) {
                Toast.makeText(this, "Có kết nối mạng", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
```
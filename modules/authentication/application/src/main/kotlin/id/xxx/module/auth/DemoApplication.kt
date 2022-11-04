package id.xxx.module.auth

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class DemoApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        val options = FirebaseOptions.Builder()
            .setDatabaseUrl(null)
            .setGcmSenderId("560050292747")
            .setApiKey("AIzaSyCOeHD009OhZAZcweg1JdUU-qQ7VWkGkPY")
            .setApplicationId("1:560050292747:android:dbaa366daf2aae83411f0e")
            .setStorageBucket("app-x-x-x.firebasestorage.app")
            .setProjectId("app-x-x-x")
            .build()
        FirebaseApp.initializeApp(base, options)
    }
}
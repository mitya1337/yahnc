package mitya.yahnc

import android.app.Application
import io.reactivex.plugins.RxJavaPlugins

class App : Application() {
    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        RxJavaPlugins.setErrorHandler { it.printStackTrace() }
    }
}
package com.katiekilroy.maitestapplication

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.android.gms.common.api.internal.GoogleServices.initialize
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Component
import dagger.Module
import dagger.Provides
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Module
class ContextModule(private val context: Application) {

    @Provides @Singleton
    fun providesContext() : Context = context
}

@Module
object AnalyticsModule {

    @JvmStatic @Provides @Singleton
    fun providesFirebaseAnalytics(ctx: Context) = FirebaseAnalytics.getInstance(ctx)
}
@Module
object BluetoothModule {

    const val RUUVI_LAYOUT = "m:0-2=0499,i:4-19,i:20-21,i:22-23,p:24-24" // TBD
    const val IBEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"
    const val ALTBEACON_LAYOUT = BeaconParser.ALTBEACON_LAYOUT
    const val EDDYSTONE_UID_LAYOUT = BeaconParser.EDDYSTONE_UID_LAYOUT
    const val EDDYSTONE_URL_LAYOUT = BeaconParser.EDDYSTONE_URL_LAYOUT
    const val EDDYSTONE_TLM_LAYOUT = BeaconParser.EDDYSTONE_TLM_LAYOUT

    @JvmStatic @Provides @Singleton
    fun providesBluetoothAdapter() = BluetoothAdapter.getDefaultAdapter()

    @JvmStatic @Provides // Not a Singleton
    fun providesBeaconManager(ctx: Context): BeaconManager {
        val instance = BeaconManager.getInstanceForApplication(ctx)

        // Sets the delay between each scans according to the settings
        instance.foregroundBetweenScanPeriod = 1000

        // Add all the beacon types we want to discover
        instance.beaconParsers.add(BeaconParser().setBeaconLayout(IBEACON_LAYOUT))
        instance.beaconParsers.add(BeaconParser().setBeaconLayout(EDDYSTONE_UID_LAYOUT))
        instance.beaconParsers.add(BeaconParser().setBeaconLayout(EDDYSTONE_URL_LAYOUT))
        instance.beaconParsers.add(BeaconParser().setBeaconLayout(EDDYSTONE_TLM_LAYOUT))

        return instance
    }
}


@Singleton
@Component(modules = [
    ContextModule::class,
    AnalyticsModule::class,
    BluetoothModule::class
//    DatabaseModule::class,
//    NetworkModule::class,
//    PreferencesModule::class,
])

interface AppComponent {
    fun providesBeaconManager() : BeaconManager

    fun inject(app: AppSingleton)
    fun inject(activity: MainActivity2)
//    fun inject(activity: SettingsActivity)
//    fun inject(activity: BlockedActivity)
//    fun inject(bs: ControlsBottomSheetDialog)
}


class AppSingleton : MultiDexApplication() {

        companion object {
            lateinit var appComponent: AppComponent
        }

        @Inject
        lateinit var tracker: FirebaseAnalytics

        override fun onCreate() {
            super.onCreate()

            // Dagger
            appComponent = DaggerAppComponent.builder()
                .contextModule(ContextModule(this))
                .build()
            appComponent.inject(this)

            // Timber
            Timber.plant(CrashReportingTree())

//            // Analytics
//            tracker.setAnalyticsCollectionEnabled(BuildTypes.isRelease())
        }
    }


/** A tree which logs important information for crash reporting.  */
    class CrashReportingTree : Timber.DebugTree() {

//	override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
//		FirebaseAnalytics.log(priority, tag, message)
//
//		t?.let {
//			if (priority == ERROR) {
//				FirebaseCrashlytics.logExcetion(t)
//			}
//		}
//	}

}
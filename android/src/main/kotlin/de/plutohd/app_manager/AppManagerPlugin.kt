package de.plutohd.app_manager

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import java.io.File
import android.app.Activity
import android.content.*
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import android.content.Context
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


/** AppManagerPlugin */
class AppManagerPlugin : ActivityAware, FlutterPlugin, MethodChannel.MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    private lateinit var context: Context
    private lateinit var activity: Activity

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "app_manager")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "installApk") {
            var apkPath = call.argument<String>("apkPath")
            if(apkPath == null || apkPath == "")  {
                result.error("1", "apkPath is not given or is empty.", "")
            } else {
                installApk(apkPath)
                result.success("Installing apk...")
            }
        } else if (call.method == "uninstallApp") {
            var packageName = call.argument<String>("packageName")
            if(packageName == null || packageName == "")  {
                result.error("1", "packageName is not given or is empty.", "")
            } else {
                uninstallApp(packageName)
                result.success("Uninstalling app...")
            }
        } else {
            result.notImplemented()
        }
    }

    private fun installApk(apkPath: String) {
        val fileBasePath = "file://"
        showInstallOption(apkPath, Uri.parse("$fileBasePath$apkPath"))
    }

    private fun showInstallOption(destination: String, uri: Uri) {
        val providerPath = ".provider"
        val appInstallPath = "\"application/vnd.android.package-archive\""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri = FileProvider.getUriForFile(context, BuildConfig.LIBRARY_PACKAGE_NAME + providerPath, File(destination))
            val install = Intent(Intent.ACTION_VIEW)
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            install.data = contentUri
            context.startActivity(install)
        } else {
            val install = Intent(Intent.ACTION_VIEW)
            // install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            install.setDataAndType(uri, appInstallPath)
            context.startActivity(install)
        }
    }

    private fun uninstallApp(packageName: String) {
        val uninstall = Intent(Intent.ACTION_DELETE)
        // uninstall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        uninstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        uninstall.data = Uri.parse("package:$packageName")
        context.startActivity(uninstall)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity;
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }
}

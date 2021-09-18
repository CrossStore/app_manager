import 'dart:async';

import 'package:flutter/services.dart';

class AppManager {
  static const MethodChannel _channel = MethodChannel('app_manager');

  // static Future<String?> get platformVersion async {
  //   final String? version = await _channel.invokeMethod('getPlatformVersion');
  //   return version;
  // }

  // static function
  // returns a Future<void>
  // triggers installation of an apk file
  // is given the path to the apk file
  static Future<void> installApk(final String apkPath) async => await _channel.invokeMethod('installApk', {'apkPath': apkPath});

  // static function
  // returns a Future<void>
  // triggers uninstallation of an app
  // is given the package name of the app
  static Future<void> uninstallApp(final String packageName) async => await _channel.invokeMethod('uninstallApp', {'packageName': packageName});
}

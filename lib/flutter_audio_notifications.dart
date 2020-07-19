import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAudioNotifications {
  static const MethodChannel _channel =
  const MethodChannel('flutter_audio_notifications');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  static Map<String, Function> _listeners = new Map();

  /// [_utilsHandler] called by [setListener] to add all events
  /// to platform side for an action.
  static Future<dynamic> _utilsHandler(MethodCall methodCall) async {
    _listeners.forEach((event, callback) {
      if (methodCall.method == event) {
        callback();
      }
    });
  }

  /// To show your media notification you have to pass [title] and
  /// [author] of music. If music is pausing you have to set
  /// [isPlaying] false.
  static Future showNotification(
      {title = '', author = '',imgurl = '', isPlaying = true}) async {
    try {
      final Map<String, dynamic> params = <String, dynamic>{
        'title': title,
        'author': author,
        'imgurl': imgurl,
        'isPlaying': isPlaying
      };
      await _channel.invokeMethod('showNotification', params);
      _channel.setMethodCallHandler(_utilsHandler);
    } on PlatformException catch (e) {
      print("Failed to show notification: '${e.message}'.");
    }
  }

  /// If you want to hide media notification call this method.
  static Future hideNotification() async {
    try {
      await _channel.invokeMethod('hideNotification');
    } on PlatformException catch (e) {
      print("Failed to hide notification: '${e.message}'.");
    }
  }

  /// Set listener for all action we need to listen of notification.
  static setListener(String event, Function callback) {
    _listeners.addAll({event: callback});
  }
}

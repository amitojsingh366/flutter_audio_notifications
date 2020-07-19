package net.amitoj.flutter_audio_notifications;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.content.Intent;

import androidx.core.content.ContextCompat;

import java.net.URL;

/** FlutterMediaNotificationPlugin */
public class FlutterAudioNotificationsPlugin implements MethodCallHandler {
  private static Registrar registrar;
  private static MethodChannel channel;

  private FlutterAudioNotificationsPlugin(Registrar r) {
    registrar = r;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    FlutterAudioNotificationsPlugin.channel = new MethodChannel(registrar.messenger(), "flutter_audio_notifications");
    FlutterAudioNotificationsPlugin.channel.setMethodCallHandler(new FlutterAudioNotificationsPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {
      case "showNotification":
        final String title = call.argument("title");
        final String author = call.argument("author");
        final String imgurl = call.argument("imgurl");
        final boolean isPlaying = call.argument("isPlaying");
        showNotification(title, author, imgurl, isPlaying);
        result.success(null);
        break;
      case "hideNotification":
        hideNotification();
        result.success(null);
        break;
      default:
        result.notImplemented();
    }
  }

  static void callEvent(String event) {

    FlutterAudioNotificationsPlugin.channel.invokeMethod(event, null, new Result() {
      @Override
      public void success(Object o) {
        // this will be called with o = "some string"
      }

      @Override
      public void error(String s, String s1, Object o) {}

      @Override
      public void notImplemented() {}
    });
  }

  static void showNotification(String title, String author,String imgurl, boolean play) {

    Intent serviceIntent = new Intent(registrar.context(), NotificationService.class);
    serviceIntent.putExtra("title", title);
    serviceIntent.putExtra("author", author);
    serviceIntent.putExtra("imgurl", imgurl);
    serviceIntent.putExtra("isPlaying", play);

    registrar.context().startService(serviceIntent);
  }

  private void hideNotification() {
    Intent serviceIntent = new Intent(registrar.context(), NotificationService.class);
    registrar.context().stopService(serviceIntent);
  }
}
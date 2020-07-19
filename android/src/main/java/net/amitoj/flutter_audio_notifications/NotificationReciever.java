package net.amitoj.flutter_audio_notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class NotificationReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "prev":
                FlutterAudioNotificationsPlugin.callEvent("prev");
                break;
            case "next":
                FlutterAudioNotificationsPlugin.callEvent("next");
                break;
            case "toggle":
                String title = intent.getStringExtra("title");
                String author = intent.getStringExtra("author");
                String image = intent.getStringExtra("imgurl");
                boolean play = intent.getBooleanExtra("play",true);

                if(play)
                    FlutterAudioNotificationsPlugin.callEvent("play");
                else
                    FlutterAudioNotificationsPlugin.callEvent("pause");

                FlutterAudioNotificationsPlugin.showNotification(title, author,image,play);
                break;
            case "select":
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.sendBroadcast(closeDialog);
                String packageName = context.getPackageName();
                PackageManager pm = context.getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
                context.startActivity(launchIntent);

                FlutterAudioNotificationsPlugin.callEvent("select");
        }
    }
}


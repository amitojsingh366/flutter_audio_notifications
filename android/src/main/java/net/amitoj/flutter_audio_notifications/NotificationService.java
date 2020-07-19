package net.amitoj.flutter_audio_notifications;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class NotificationService extends Service {
    public static int NOTIFICATION_ID = 1;
    public  static final String CHANNEL_ID = "flutter_audio_notifications";
    public  static final String MEDIA_SESSION_TAG = "flutter_audio_notifications";

    @Override
    public void onCreate() {
        super.onCreate();
    }


    int iconPlayPause = R.drawable.baseline_play_arrow_black_48;
    String titlePlayPause = "pause";



    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String isPlaying = String.valueOf(intent.getBooleanExtra("isPlaying", true));
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String image = intent.getStringExtra("imgurl");
        createNotificationChannel();
        Map<String, String> data = new HashMap<String, String>();
        data.put("title",title);
        data.put("author",author);
        data.put("image",image);
        data.put("isPlaying",isPlaying);
        new getImage(data).execute();

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription("flutter_audio_notifications");
            serviceChannel.setShowBadge(false);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopForeground(true);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public class getImage extends AsyncTask<Void,Bitmap,Bitmap>{

        private Map<String,String> data = null;

        // constructor
        public getImage(final Map<String,String> data) {
            this.data  = data;
        }


        @SafeVarargs
        @Override
        protected final Bitmap doInBackground(final Void... datas) {

            if(Looper.myLooper() == null){
                Looper.prepare();
            }
                String title = this.data.get("title");
                String author = this.data.get("author");
                String imageurl = this.data.get("image");
                Boolean isPlaying = Boolean.valueOf(this.data.get("isPlaying"));

                URL urls = null;
                try {
                    urls = new URL(imageurl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                 return download_image(urls,title,author,isPlaying);

        }
        private Bitmap download_image(URL imgurl, String title, String author, Boolean isPlaying) {
            Bitmap Bitmapdecodedimage;
            if(imgurl != null){
                try {
                    Bitmapdecodedimage = BitmapFactory.decodeStream(imgurl.openConnection().getInputStream());
                } catch (IOException e) {
                    Bitmapdecodedimage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_stat_music_note);
                    e.printStackTrace();
                }
            }else{
                Bitmapdecodedimage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_stat_music_note);
            }
            sendNoti(Bitmapdecodedimage, title, author, isPlaying, imgurl);
            return Bitmapdecodedimage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }

        private void sendNoti(Bitmap bitmapimage, String title, String author, Boolean isPlaying, URL image){
            if(isPlaying){
                iconPlayPause=R.drawable.baseline_pause_black_48;
                titlePlayPause="play";
            }else{
                iconPlayPause = R.drawable.baseline_play_arrow_black_48;
                titlePlayPause = "pause";
            }
            String imageurl;
            if (image == null){
                imageurl = "";
            }else{
                imageurl = image.toString();
            }
            MediaSessionCompat mediaSession = new MediaSessionCompat(getApplicationContext(), MEDIA_SESSION_TAG);
            Intent toggleIntent = new Intent(getApplicationContext(), NotificationReciever.class)
                    .setAction("toggle")
                    .putExtra("title",  title)
                    .putExtra("author",  author)
                    .putExtra("imgurl",  imageurl)
                    .putExtra("play", !isPlaying);
            PendingIntent pendingToggleIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            MediaButtonReceiver.handleIntent(mediaSession, toggleIntent);
            //TODO(ALI): add media mediaSession Buttons and handle them
            Intent nextIntent = new Intent(getApplicationContext(), NotificationReciever.class)
                    .setAction("next");
            PendingIntent pendingNextIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        MediaButtonReceiver.handleIntent(mediaSession, nextIntent);

            Intent prevIntent = new Intent(getApplicationContext(), NotificationReciever.class)
                    .setAction("prev");
            PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        MediaButtonReceiver.handleIntent(mediaSession, prevIntent);

            Intent selectIntent = new Intent(getApplicationContext(), NotificationReciever.class)
                    .setAction("select");
            PendingIntent selectPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, selectIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        MediaButtonReceiver.handleIntent(mediaSession, selectIntent);
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .addAction(R.drawable.baseline_skip_previous_black_48, "prev", pendingPrevIntent)
                    .addAction(iconPlayPause, titlePlayPause, pendingToggleIntent)
                    .addAction(R.drawable.baseline_skip_next_black_48, "next", pendingNextIntent)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1,2)
                            .setShowCancelButton(true)
                            .setMediaSession(mediaSession.getSessionToken()))
                    .setSmallIcon(R.drawable.ic_stat_music_note)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setVibrate(new long[]{0L})
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setContentText(author)
                    .setSubText(title)
                    .setContentIntent(selectPendingIntent)
                    .setLargeIcon(bitmapimage)
                    .build();

            startForeground(NOTIFICATION_ID, notification);
            if(!isPlaying) {
                stopForeground(false);
            }
        }

    }



}




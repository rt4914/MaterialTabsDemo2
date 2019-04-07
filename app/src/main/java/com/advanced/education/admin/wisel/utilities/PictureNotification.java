package com.advanced.education.admin.wisel.utilities;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.os.AsyncTask;
import android.os.Build;

import com.advanced.education.admin.wisel.R;
import com.advanced.education.admin.wisel.activity.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class PictureNotification extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = PictureNotification.class.getSimpleName();

    private Context mContext;
    private String title, message, imageUrl;

    public PictureNotification(Context context, String title, String message, String imageUrl) {
        super();
        this.mContext = context;
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        InputStream in;
        try {
            URL url = new URL(this.imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            in = connection.getInputStream();
            return BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationChannel notificationChannel = null;

        int iRequestId = (int) System.currentTimeMillis();
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, iRequestId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "some_channel_id_2";
            CharSequence channelName = "Some Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            notificationChannel = new NotificationChannel(channelId, channelName, importance);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

            if (imageUrl != null && imageUrl.length() > 2) {

                Notification notification = new Notification.Builder(mContext)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(createBitmap(R.mipmap.ic_launcher))
                        .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                        .setChannelId(channelId)
                        .build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(m, notification);

            } else {

                Notification notification = new Notification.Builder(mContext)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(createBitmap(R.mipmap.ic_launcher))
                        .setChannelId(channelId)
                        .build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(m, notification);

            }

        } else {

            if (imageUrl != null && imageUrl.length() > 2) {

                Notification notification = new Notification.Builder(mContext)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                        .build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(m, notification);

            } else {

                Notification notification = new Notification.Builder(mContext)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(m, notification);

            }

        }

    }

    public void clearAllNotifications(){
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    public Bitmap createBitmap(int id ){
        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), id);
        return largeIcon;
    }

}

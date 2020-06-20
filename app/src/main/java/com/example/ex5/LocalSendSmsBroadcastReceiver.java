package com.example.ex5;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;

import androidx.core.app.NotificationCompat;

public class LocalSendSmsBroadcastReceiver extends BroadcastReceiver {

    public static final String PHONE = "phoneNumber";
    public static final String CONTENT = "msgContent";
    public static final String SMS_SENT = "sendingSMS";
    public static final String SMS_DELIVERED = "deliveredSMS";
    private static final String CHANNEL_ID = "POST_PC_Ex6";
    private static final String DESCRIPTION = "Sending message to ";
    private static final String NOTIFICATION_TITLE = "Ex6";
    public static final String actionReceiver = "POST_PC.ACTION_SEND_SMS";

    public static final int MSG_REQUEST_CODE = 14;
    private static final int NOTIFICATION_ID = 10;

    private Context context;
    private String phoneNum;
    private String msgContent;


    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        assert action != null;
        if(action.equals(actionReceiver))
        {
            this.context = context;
            this.phoneNum = intent.getStringExtra(PHONE);
            this.msgContent = intent.getStringExtra(CONTENT);

            sendSMS(context, phoneNum, msgContent);
            //NotificationHandler notificationHandler = new NotificationHandler(phoneNum, msgContent, context);
            this.createNotificationChannel();
            this.displayNotification();
        }
    }


    public void sendSMS(Context context, String phoneNum, String content) {
        PendingIntent piSend = PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(context, 0, new Intent(SMS_DELIVERED), 0);
        SmsManager.getDefault().sendTextMessage(phoneNum, null, content, piSend, piDelivered);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_TITLE, importance);
            channel.setDescription(NOTIFICATION_TITLE);
            NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void displayNotification()
    {
        Notification builder = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(DESCRIPTION + phoneNum + ": " + this.msgContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder);
        }
    }
}

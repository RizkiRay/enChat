package net.enjoystudio.enchat.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.enjoystudio.enchat.AES;
import net.enjoystudio.enchat.C;
import net.enjoystudio.enchat.R;
import net.enjoystudio.enchat.conversation.Chat;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rizki on 12/07/2016.
 */
public class MessagingService extends FirebaseMessagingService {
    SharedPreferences sp;
    ArrayList<HashMap<String, String>> cList;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        String receiver = remoteMessage.getData().get(C.RECEIVER_ID);
        String sender = remoteMessage.getData().get(C.SENDER_ID);
        String name = remoteMessage.getData().get(C.NAME);
        String status = remoteMessage.getData().get(C.STATUS);

        String cID = remoteMessage.getData().get(C.ID);
        String photo = remoteMessage.getData().get(C.PHOTO);
        String isActive = sp.getString(C.ISACTIVE, "0;0").split(";")[0];
        String id = sp.getString(C.ISACTIVE, "0;0").split(";")[1];
        if (receiver.equals(sp.getString(C.USER_ID, "0"))) {
            if (cID.equals("0")) cID = sp.getString(C.CONVERSATION + sender,"0");
            else sp.edit().putString(C.CONVERSATION + sender,cID).apply();
            Log.i("CEKKEY", cID + " tes");
            String message = new String(AES.decrypt(remoteMessage.getData().get("message")
                    .getBytes(Charset.forName(C.CHARSET)), cID
                    .getBytes(Charset.forName(C.CHARSET))), Charset.forName(C.CHARSET));
            if (isActive.equals("1") && id.equals(sender)) {
                Intent intent = new Intent(C.UPDATE_MESSAGE);
                intent.putExtra(C.CONTENT, message);
                intent.putExtra(C.RECEIVER_ID, receiver);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                Log.i("CEK", isActive + " " + id + " " + receiver);
            } else {
                Intent i = new Intent(this, Chat.class);
                i.putExtra(C.NAME, name);
                i.putExtra(C.STATUS, status);
                i.putExtra(C.USER_ID, sender);
                i.putExtra(C.PHOTO, photo);
                sendNotif(name, message, i);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
                }.getType();
                cList = gson.fromJson(sp.getString(C.CONVERSATION, ""), type);
                if (cList == null) cList = new ArrayList<>();
                int pos = getPos(sender);
                HashMap<String, String> conv = new HashMap<>();
                conv.put(C.USER_ID, sender);
                conv.put(C.PHOTO, photo);
                conv.put(C.NAME, name);
                conv.put(C.STATUS, status);
                conv.put(C.CHAT, message);
                if (pos != -1) {
                    cList.remove(pos);
                    cList.add(pos, conv);
                } else cList.add(conv);
                sp.edit().putString(C.CONVERSATION, gson.toJson(cList)).apply();
                Intent intent = new Intent(C.UPDATE_CONVERSATION);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }

    private void sendNotif(String judul, String message, Intent i) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notif = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(judul)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pi);
        notif.setLights(Color.BLUE, 1, 1);
        notif.setVibrate(new long[]{0, 50, 10, 100});
        NotificationManager notifManager = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);
        notifManager.notify(1, notif.build());
    }

    private int getPos(String id) {
        Gson gson = new Gson();
        SharedPreferences sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        String data = sp.getString(C.CONVERSATION, "");
        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();
        ArrayList<HashMap<String, String>> cList = gson.fromJson(data, type);
        if (cList == null) cList = new ArrayList<>();
        for (int i = 0; i < cList.size(); i++) {
            HashMap<String, String> conv = cList.get(i);
            if (conv.get(C.USER_ID).equals(id)) {
                return i;
            }
        }
        return -1;
    }

//    private String getID(String id) {
//        int pos = getPos(id);
//        Gson gson = new Gson();
//        SharedPreferences sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
//        String data = sp.getString(C.CONVERSATION, "");
//        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
//        }.getType();
//        ArrayList<HashMap<String, String>> cList = gson.fromJson(data, type);
//        HashMap<String, String> c = cList.get(pos);
//        return c.get(C.ID);
//    }
}

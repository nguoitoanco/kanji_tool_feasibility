package muscular.man.tools.kanjinvk.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.activity.KanjiDetailActivity;
import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.constant.Constant;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiDao;

/**
 * Created by KhanhNV10 on 2015/12/03.
 */
public class KanjiAlarmService extends IntentService {
    Handler mMainThreadHandler = null;

    private static final String TAG = KanjiAlarmService.class.getSimpleName();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public KanjiAlarmService() {
        super(KanjiAlarmService.class.getSimpleName());
        mMainThreadHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String[] selectedJlpt = CommonSharedPreferencesManager
                .loadPreference(this, Constant.SELECTED_JLPT_TYPE_LIST_KEYS, "N5")
                .split(Constant.SEMI_COLON);

        int indexRandom = 0;
        if (selectedJlpt.length > 0) {
            indexRandom = new Random().nextInt(selectedJlpt.length);
        }

        Log.d(TAG, "JLPT TYPE:" + indexRandom);
        Log.d(TAG, "JLPT TYPE:" + selectedJlpt[indexRandom]);


        final KanjiDao kanjiDao = new KanjiDao(this);
        final int finalIndexRandom = indexRandom;
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                kanjiDao.getKanjiNotificationInfo(selectedJlpt[finalIndexRandom] + "%",
                        new CommonActionListener.CallBackListener<KanjiDto>() {
                            @Override
                            public boolean onSuccess(KanjiDto dto) {
                                Log.d(TAG, "onSuccess dto");
                                CommonSharedPreferencesManager
                                        .savePreference(getApplicationContext(),
                                                Constant.KANJI_NOTIFICATION_ID_KEY, dto.kid);
                                pushNotification(dto);
                                return true;
                            }
                        });
            }
        });

    }

    private void pushNotification(KanjiDto dto) {
        Log.d(TAG, "" + (dto == null));
        if (dto != null) {
            Log.d(TAG, dto.word);
            RemoteViews mContentView = new RemoteViews(getPackageName(), R.layout.kanji_notification_remote_view);
            mContentView.setTextViewText(R.id.kanji_notification_word, dto.word);
            mContentView.setTextViewText(R.id.kanji_notification_on, dto.onyomi);
            mContentView.setTextViewText(R.id.kanji_notification_kun, dto.kuniomi);
            boolean contentIsEnglish = CommonSharedPreferencesManager.loadBooleanPreference(
                    this, Constant.CONTENT_LANGUAGE_KEY, true);
            if (contentIsEnglish) {
                mContentView.setTextViewText(R.id.kanji_notification_mean, dto.enMean);
            } else {
                mContentView.setTextViewText(R.id.kanji_notification_mean, dto.vnMean);
            }

            // Create notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.ic_launcher);
//            builder.setContentTitle(dto.word);
//            builder.setContentText(Html.fromHtml(kanjiContent));
            builder.setContent(mContentView);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);

            final String kid =dto.kid;
            Intent detailIntent = new Intent(this, KanjiDetailActivity.class);
            detailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            List<String> kanjiIds = new ArrayList<>();
            kanjiIds.add(kid);
            detailIntent.putStringArrayListExtra("kanjiIds", (ArrayList<String>) kanjiIds);
            detailIntent.putExtra("currentPos", 0);

            // Using PendingIntent.FLAG_UPDATE_CURRENT for extra intent.
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            Bundle pendingBundle = new Bundle();

            pendingBundle.putInt("currentPos", 0);
            builder.addExtras(pendingBundle);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            Calendar calendar = Calendar.getInstance();
            mNotificationManager.notify(getNotificationKey(), builder.build());

            // Send broad cast to inform that reminder has been updated or deleted
            Intent intentInform = new Intent("ReminderUpdated");
            intentInform.putExtra("reminder_id", dto.kid);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intentInform);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentInform);
        }
    }

    private int getNotificationKey() {
//        Calendar calendar = Calendar.getInstance();
//        String strKey = calendar.get(Calendar.YEAR) + StringUtils.EMPTY
//                + calendar.get(Calendar.MONTH) + StringUtils.EMPTY
//                + calendar.get(Calendar.DAY_OF_MONTH) + StringUtils.EMPTY;
//        Log.d(KanjiAlarmService.class.getSimpleName(), strKey);
        return 0;
    }
}

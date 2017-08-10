package muscular.man.tools.kanjinvk.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


/**
 * Created by KhanhNV10 on 2015/12/03.
 */
public class ReminderService {

//    int
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Context mContext;

    public void createNewReminder(Context context) {
        mContext = context;

        Intent intent = new Intent(mContext, KanjiAlarmService.class);
//        intent.putExtra(Constant.KANJI_NOTIFICATION_DTO_KEY, dto);
        int requestCode = (int) Calendar.getInstance().getTimeInMillis();
        alarmIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}

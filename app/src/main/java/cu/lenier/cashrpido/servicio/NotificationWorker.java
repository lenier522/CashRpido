package cu.lenier.cashrpido.servicio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import cu.lenier.cashrpido.R;

public class NotificationWorker extends Worker {
    private static final String CHANNEL_ID = "MyBackgroundServiceChannel";
    private static final int NOTIFICATION_ID = 123;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        createNotificationChannel();
        showNotification();
        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Servicio Notificacion",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Cash Rapido")
                .setContentText(getApplicationContext().getString(R.string.t_mate_5_minutos_para_ver_tus_ingresos))
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
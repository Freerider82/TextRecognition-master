//https://github.com/qureshiayaz29/TextRecognition
//https://www.youtube.com/watch?v=MAvLWza1e04&t=45s
package in.insideandroid.textrecognizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "Files";
    ImageView imageView;
    TextView detectedText;
    Button btn_detect;

    private boolean is_started = false;
    private int counter = 0;
    private TextView tv,tvCountFiles;

    final String DIR_BAMBU = "Images_Bambu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.img);
        detectedText = findViewById(R.id.detectedText);
        btn_detect = findViewById(R.id.button_detect);

        tv = findViewById(R.id.textView);
        tvCountFiles = findViewById(R.id.textViewCountFiles);

        btn_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect();
            }
        });

        startThread();

    }

    public void detect() {
        //perform text detection here

        //TODO 1. define TextRecognizer
        TextRecognizer recognizer = new TextRecognizer.Builder(MainActivity.this).build();

        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "Images_Bambu");

        String path = Environment.getExternalStorageDirectory().toString()+"/Images_Bambu";
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d(LOG_TAG, "Size: "+ files.length);

        for (int i = 0; i < files.length; i++) {
            Log.d(LOG_TAG, "FileName:" + files[i].getName());
        }

        File sdFile = new File(sdPath, "Bambu1.jpg");

        Bitmap bitmapImageFile = BitmapFactory.decodeFile(sdFile.getPath());
        imageView.setImageBitmap(bitmapImageFile);


        //TODO 2. Get bitmap from imageview
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        //TODO 3. get frame from bitmap
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        //TODO 4. get data from frame
        SparseArray<TextBlock> sparseArray =  recognizer.detect(frame);

        //TODO 5. set data on textview
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i < sparseArray.size(); i++){
            TextBlock tx = sparseArray.get(i);
            String str = tx.getValue();

            stringBuilder.append(str);
        }

        detectedText.setText(stringBuilder);
    }

    public  void startThread(){
        is_started = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (is_started){
                    counter++;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Запуск на основном потоке
                            tv.setText(String.valueOf(counter));
                            tvCountFiles.setText((String.valueOf(getCountFilesDirBambu())));
                        }
                    });
                    try {
                        Thread.sleep(1000);


                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public  int getCountFilesDirBambu(){

        String path = Environment.getExternalStorageDirectory().toString()+"/"+DIR_BAMBU;
        File directory = new File(path);
        File[] files = directory.listFiles();

        return files.length;
    }

    public void onClickDeleteAllFile(View view) throws InterruptedException {
        /*
        Удаление 1 файла Bambu1.jpg
        String path = Environment.getExternalStorageDirectory().toString()+"/"+"/Images_Bambu/Bambu1.jpg";
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        */
        Intent intentBambu = getPackageManager().getLaunchIntentForPackage("bbl.intl.bambulab.com");
        Intent intentMain = getPackageManager().getLaunchIntentForPackage("in.insideandroid.textrecognizer");

        PendingIntent pendingIntentBambu = PendingIntent.getActivity(MainActivity.this,0,intentBambu,0);
        PendingIntent pendingIntentMain = PendingIntent.getActivity(MainActivity.this,0,intentMain,0);

        try{
            pendingIntentBambu.send();
        }catch (Exception e){
            e.printStackTrace();
        }

        addNotification();

        Thread.sleep(8000);
        try{
            pendingIntentMain.send();
        }catch (Exception e){
            e.printStackTrace();
        }

        String path = Environment.getExternalStorageDirectory().toString()+"/"+DIR_BAMBU;
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d(LOG_TAG, "Size: "+ files.length);
        int countFiles = files.length;
        for (int i = 0; i < countFiles; i++) {
            Log.d(LOG_TAG, "FileName:" + files[i].getName());
            if (files[i].exists()) {
                files[i].delete();
            }
        }

    }

    private void addNotification() {
        // Builds your notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("John's Android Studio Tutorials")
                .setContentText("ScreenshotBambu");

        // Creates the intent needed to show the notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}

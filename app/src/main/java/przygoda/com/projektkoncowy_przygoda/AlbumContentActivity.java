package przygoda.com.projektkoncowy_przygoda;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileFilter;

public class AlbumContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_content);
        getSupportActionBar().hide();

        //Get folder path
        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("folderPath").toString();
        File album = new File(path);
        //===============

//        MyImageView myiv1 = new MyImageView(AlbumContentActivity.this);
//        LinearLayout layout = (LinearLayout) findViewById(R.id.albumLayout);
//        layout.addView(myiv1);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
//        Log.d("DS", "" + point.x);
//        Log.d("DS", "" + point.y);

        FileFilter isFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile();
            }
        };

        int rowsCount;
        if(album.listFiles(isFileFilter).length % 2 == 0){
            rowsCount = album.listFiles(isFileFilter).length / 2;
        }
        else{
            rowsCount = album.listFiles(isFileFilter).length / 2 + 1;
        }

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(300, 300);
        Object[] rows;
        for(int i = 0; i < rowsCount; i++){
            LinearLayout rowLayout = new LinearLayout(AlbumContentActivity.this);
            rowLayout.setLayoutParams(lparams);
//            rows.
        }
    }
}

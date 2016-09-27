package przygoda.com.projektkoncowy_przygoda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileFilter;

public class AlbumContentActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_content);

        //Get selected album
        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("folderPath");
        File album = new File(path);
        //===============

        LinearLayout albumContainer = (LinearLayout) findViewById(R.id.llAlbumContainer);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        FileFilter isFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile();
            }
        };

        File[] files = album.listFiles(isFileFilter);
        double picturesCount = files.length;
        int rowsCount = (int) Math.ceil(picturesCount / 2);

        int rowWidth = point.x;
        int rowHeight = (rowWidth/5)*2;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(rowWidth, rowHeight);
        LinearLayout.LayoutParams smallViewParams = new LinearLayout.LayoutParams((rowWidth/5)*2, rowHeight);
        LinearLayout.LayoutParams largeViewParams = new LinearLayout.LayoutParams((rowWidth/5)*3, rowHeight);
        LinearLayout.LayoutParams bigViewParams = new LinearLayout.LayoutParams(rowWidth, rowHeight);

        LinearLayout[] rows = new LinearLayout[rowsCount];
        int currentPicture = 0;
        for(int i = 0; i < rowsCount; i++){
            LinearLayout rowLayout = new LinearLayout(AlbumContentActivity.this);
            rowLayout.setLayoutParams(layoutParams);
            rows[i] = rowLayout;

            if( i%2 == 0 && currentPicture < picturesCount-2 ){         //at least 2 pictures left for adding
                ImageView picture1 = new ImageView(AlbumContentActivity.this);
                picture1.setLayoutParams(smallViewParams);
                String imagePath1 = files[currentPicture].getPath();
                Bitmap bmp1 = betterImageDecode(imagePath1);
                picture1.setImageBitmap(bmp1);
                picture1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                picture1.setOnClickListener(this);
                picture1.setTag(files[currentPicture].getPath());
                currentPicture++;

                ImageView picture2 = new ImageView(AlbumContentActivity.this);
                picture2.setLayoutParams(largeViewParams);
                String imagePath2 = files[currentPicture].getPath();
                Bitmap bmp2 = betterImageDecode(imagePath2);
                picture2.setImageBitmap(bmp2);
                picture2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                picture2.setOnClickListener(this);
                picture2.setTag(files[currentPicture].getPath());
                currentPicture++;

                rows[i].addView(picture1);
                rows[i].addView(picture2);
            }
            else if( currentPicture < picturesCount-2 ){                //at least 2 pictures left for adding
                ImageView picture1 = new ImageView(AlbumContentActivity.this);
                picture1.setLayoutParams(largeViewParams);
                String imagePath1 = files[currentPicture].getPath();
                Bitmap bmp1 = betterImageDecode(imagePath1);
                picture1.setImageBitmap(bmp1);
                picture1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                picture1.setOnClickListener(this);
                picture1.setTag(files[currentPicture].getPath());
                currentPicture++;

                ImageView picture2 = new ImageView(AlbumContentActivity.this);
                picture2.setLayoutParams(smallViewParams);
                String imagePath2 = files[currentPicture].getPath();
                Bitmap bmp2 = betterImageDecode(imagePath2);
                picture2.setImageBitmap(bmp2);
                picture2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                picture2.setOnClickListener(this);
                picture2.setTag(files[currentPicture].getPath());
                currentPicture++;

                rows[i].addView(picture1);
                rows[i].addView(picture2);
            }
            else{
                ImageView picture = new ImageView(AlbumContentActivity.this);
                picture.setLayoutParams(bigViewParams);
                String imagePath = files[currentPicture].getPath();
                Bitmap bmp = betterImageDecode(imagePath);
                picture.setImageBitmap(bmp);
                picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                picture.setOnClickListener(this);
                picture.setTag(files[currentPicture].getPath());
                currentPicture++;

                rows[i].addView(picture);
            }

            albumContainer.addView(rows[i]);
        }
    }

    public Bitmap betterImageDecode(String filePath) {
        Bitmap myBitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 16;                                      // zmniejszenie jakości bitmapy 4x
        myBitmap = BitmapFactory.decodeFile(filePath, options);
        return myBitmap;
    }
    public static Bitmap imageDecode(String filePath) {
        Bitmap myBitmap;
        myBitmap = BitmapFactory.decodeFile(filePath);
        return myBitmap;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent (AlbumContentActivity.this, ViewPictureActivity.class);
        intent.putExtra("picturePath", v.getTag().toString());
        startActivity(intent);
    }
}

package przygoda.com.projektkoncowy_przygoda;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ViewPictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        Bundle b = getIntent().getExtras();
        String picturePath = b.getString("picturePath");

        Bitmap bmp = AlbumContentActivity.imageDecode(picturePath);

        ImageView iv = (ImageView) findViewById(R.id.ivPicturePreview);
        iv.setImageBitmap(bmp);
    }
}

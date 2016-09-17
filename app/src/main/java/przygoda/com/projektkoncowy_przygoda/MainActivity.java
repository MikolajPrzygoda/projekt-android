package przygoda.com.projektkoncowy_przygoda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        //Prepare folders
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mainDir = new File(picturesDir, "MikolajPrzygoda");
        File peopleDir = new File(mainDir, "People");
        File placesDir = new File(mainDir, "Places");
        File thingsDir = new File(mainDir, "Things");

        if (!peopleDir.exists())
            peopleDir.mkdirs();
        if (!placesDir.exists())
            placesDir.mkdirs();
        if (!thingsDir.exists())
            thingsDir.mkdirs();
        //===============

        //Create listeners
        LinearLayout picturesLayout = (LinearLayout) findViewById(R.id.pictureLayout);
        LinearLayout albumsLayout = (LinearLayout) findViewById(R.id.albumsLayout);
        LinearLayout collageLayout = (LinearLayout) findViewById(R.id.collageLayout);
        LinearLayout internetLayout = (LinearLayout) findViewById(R.id.internetLayout);

        albumsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AlbumsActivity.class);
                startActivity(i);
            }
        });

    }
}

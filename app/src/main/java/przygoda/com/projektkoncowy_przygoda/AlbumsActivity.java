package przygoda.com.projektkoncowy_przygoda;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.File;

public class AlbumsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        getSupportActionBar().setTitle(R.string.albumsTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridView grid = (GridView) findViewById(R.id.gridLayout);
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File[] files = picturesDir.listFiles();
        String[] fileNames = new String[files.length];
        int i = 0;
        for (File file : files) {
            fileNames[i] = file.getName();
            i++;
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                AlbumsActivity.this,    // Context
                R.layout.cell_layout,   // nazwa pliku xml naszej komórki
                R.id.tvFolderName,      // id pola txt w komórce
                fileNames);                // tablica przechowująca dane


        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //test
                Log.d("TAG", "index = " + i);
            }
        });
    }
}

package przygoda.com.projektkoncowy_przygoda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;

public class AlbumsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        final File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File rootDir = new File(picturesDir, "MikolajPrzygoda");
        loadDirectory(rootDir);
    }

    private void loadDirectory(File file) {

        FileFilter isDirFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        final File[] files = file.listFiles(isDirFilter);

        final String[] fileNames = new String[files.length];
        int i = 0;
        for (File f : files) {
            fileNames[i] = f.getName();
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                AlbumsActivity.this,    // Context
                R.layout.cell_layout,   // nazwa pliku xml naszej komórki
                R.id.tvFolderName,      // id pola txt w komórce
                fileNames);             // tablica przechowująca dane

        GridView grid = (GridView) findViewById(R.id.gridView);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //test
                Log.d("TAG", files[i].getName());
                Intent intent = new Intent(AlbumsActivity.this, AlbumContentActivity.class);
                intent.putExtra("folderPath", files[i].getAbsolutePath());
                startActivity(intent);
            }
        });
    }
}

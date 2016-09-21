package przygoda.com.projektkoncowy_przygoda;

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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileFilter;

public class PicturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);
        getSupportActionBar().hide();

        FileFilter isDirFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        file = new File(file, "MikolajPrzygoda");
        final File[] files = file.listFiles(isDirFilter);

        String[] fileNames = new String[files.length];
        int i = 0;
        for (File f : files) {
            fileNames[i] = f.getName();
            i++;
        }

        ArrayAdapter<String> adapter = new MyArrayAdapter(
                PicturesActivity.this,              // Context
                R.layout.pictures_cell_layout,      // nazwa pliku xml naszej komórki
                fileNames);                         // tablica przechowująca dane

        GridView grid = (GridView) findViewById(R.id.gvPictures);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv1 = (TextView) findViewById(R.id.tvPicturesSelectedFolder);
                tv1.setText(files[i].getName());
            }
        });

        ImageView ivAccept = (ImageView) findViewById(R.id.ivAccept);
        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}

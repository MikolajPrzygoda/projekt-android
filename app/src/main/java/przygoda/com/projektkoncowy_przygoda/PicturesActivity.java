package przygoda.com.projektkoncowy_przygoda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileFilter;

public class PicturesActivity extends AppCompatActivity {

    private File file;
    public File[] files;
    private FileFilter isDirFilter;
    private MyArrayAdapter adapter;
    private GridView grid;
    private String[] fileNames;
    private boolean saveLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        isDirFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        file = new File(file, "MikolajPrzygoda");
        files = file.listFiles(isDirFilter);

        EditText editText = (EditText) findViewById(R.id.etPicturesSelectedFolder);
        editText.setText(files[0].getName());

        fileNames = new String[files.length];
        int i = 0;
        for (File f : files) {
            fileNames[i] = f.getName();
            i++;
        }

        adapter = new MyArrayAdapter(
                PicturesActivity.this,              // Context
                R.layout.pictures_cell_layout,      // nazwa pliku xml naszej komórki
                fileNames);                         // tablica przechowująca dane

        grid = (GridView) findViewById(R.id.gvPictures);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EditText tv1 = (EditText) findViewById(R.id.etPicturesSelectedFolder);
                tv1.setText("");
                tv1.append(files[i].getName());
            }
        });

        ImageView ivAccept = (ImageView) findViewById(R.id.ivAccept);
        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PicturesActivity.this, CameraActivity.class);
                intent.putExtra("saveLocation",saveLocation);
                startActivity(intent);
            }
        });

        final ImageView ivSave = (ImageView) findViewById(R.id.ivSave);
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveLocation){
                    saveLocation = false;
                    ivSave.setImageResource(R.drawable.ic_check_box_black_24dp);
                }
                else{
                    saveLocation = true;
                    ivSave.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                }
            }
        });

    }

    public void refreshGridView(){
        adapter = new MyArrayAdapter(
                PicturesActivity.this,
                R.layout.pictures_cell_layout,
                fileNames);
        grid.setAdapter(adapter);
    }

    public void refreshFiles(){
        files = file.listFiles(isDirFilter);
        fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }
    }
}

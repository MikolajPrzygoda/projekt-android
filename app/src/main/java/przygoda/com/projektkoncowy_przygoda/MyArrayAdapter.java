package przygoda.com.projektkoncowy_przygoda;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;

/**
 * Created by 4ia1 on 2016-09-21.
 */
public class MyArrayAdapter extends ArrayAdapter{

    public String[] array;
    private Context _context;

    public MyArrayAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
        array = (String[]) objects;
        _context = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //inflater - klasa konwertująca xml na kod javy
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.pictures_cell_layout, null);
        //szukam kontrolki w layoucie

        TextView tv1 = (TextView) convertView.findViewById(R.id.tvFolderNamePictures);
        tv1.setText(array[position]);
        //
        ImageView iv1 = (ImageView) convertView.findViewById(R.id.ivPicturesDeleteFolder);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog(position);
            }
        });

        return convertView;
    }

    private void confirmDialog(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setTitle("Confirm")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deleteRecursive(((PicturesActivity)_context).files[position]);
                        ((PicturesActivity)_context).refreshFiles();
                        ((PicturesActivity)_context).refreshGridView();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }
}

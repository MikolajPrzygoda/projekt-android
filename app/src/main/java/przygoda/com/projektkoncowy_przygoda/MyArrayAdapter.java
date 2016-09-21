package przygoda.com.projektkoncowy_przygoda;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 4ia1 on 2016-09-21.
 */
public class MyArrayAdapter extends ArrayAdapter{

    private String[] array;
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
                // klik w obrazek
                Log.d("--", "TEST - " + array[position]);
            }
        });

        return convertView;
    }
}

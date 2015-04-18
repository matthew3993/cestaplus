package bc.cestaplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bc.cestaplus.R;
import bc.cestaplus.objects.ArticleObj;

/**
 * Created by Matej on 28.2.2015.
 */
public class RubrikyAdapter
    extends ArrayAdapter<String>{

    public LayoutInflater inflater;
    public String [] rubriky;

    /**
     * Konstruktor
     * @param context
     * @param viewID
     * @param data
     */
    public RubrikyAdapter(Context context, int viewID, String[] data){
        super(context, viewID, data); // (context / activity, view in the listView, List of data - source)

        this.inflater = LayoutInflater.from(context);

        this.rubriky = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ustime sa, ze mame View, s ktorym mozeme pracovat, lebo convertView moze byt null
        View itemView = convertView;

        if (itemView == null){
            itemView = inflater.inflate(R.layout.rubrika_list_item, parent, false);
        }

        // Najdime sectionName, s ktorym budeme pracovat
        String sectionName = rubriky[position];

    //naplnenie View
        //nadpis
        TextView txtvSectionName = (TextView) itemView.findViewById(R.id.item_tvSectionName);
        txtvSectionName.setText(sectionName);

        return itemView;
    }

}//end of RubrikyAdapter

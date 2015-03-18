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

import bc.cestaplus.ArticleObj;
import bc.cestaplus.R;

/**
 * Created by Matej on 28.2.2015.
 */
public class ClanokAdapter
    extends ArrayAdapter<ArticleObj>{

    public LayoutInflater inflater;
    public ArrayList<ArticleObj> clanky;

    /**
     * Konstruktor
     * @param context
     * @param viewID
     * @param data
     */
    /*public ClanokAdapter(Context context, int viewID, List data){
        super(context, viewID, data); // (context / activity, view in the listView, List of data - source)
    }*/

    public ClanokAdapter(Context context, int viewID, List data, LayoutInflater inflater){
        super(context, viewID, data); // (context / activity, view in the listView, List of data - source)

        this.inflater = inflater;
        this.clanky = (ArrayList) data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ustime sa, ze mame View, s ktorym mozeme pracovat, lebo convertView moze byt null
        View itemView = convertView;

        if (itemView == null){
            itemView = inflater.inflate(R.layout.clanok_list_item, parent, false);
        }

        // Najdime clanok, s ktorym budeme pracovat
        ArticleObj clanok = clanky.get(position);

    //naplnenie View
        //obrazok
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_ivObr);
        imageView.setImageResource(clanok.getImageID());

        //nadpis
        TextView txtvTitle = (TextView) itemView.findViewById(R.id.item_tvTitle);
        txtvTitle.setText(clanok.getTitle());

        //popis
        TextView txtvDescription = (TextView) itemView.findViewById(R.id.item_tvDescription);
        txtvDescription.setText(clanok.getShort_text());

        return itemView;
        //return super.getView(position, convertView, parent);
    }


}//end of ClanokAdapter

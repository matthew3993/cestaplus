package bc.cestaplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bc.cestaplus.ClanokObj;
import bc.cestaplus.R;
import bc.cestaplus.activities.ClanokActivity;
import bc.cestaplus.activities.MainActivity;
import bc.cestaplus.network.VolleySingleton;

/**
 * Created by Matej on 4.3.2015.
 */
public class ClanokRecyclerViewAdapter
    extends RecyclerView.Adapter<ClanokRecyclerViewAdapter.ClanokViewHolder> {

    public static final String EXTRA_RUBRIKA = "bc.cesta.RUBRIKA_CLANKU";

    private LayoutInflater inflater;
    private ArrayList<ClanokObj> clanky  = new ArrayList<>(); // vseobecne pomenovanie v adaptery

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    /**
     * Konstruktor
     * @param context
     */
    public ClanokRecyclerViewAdapter(Context context/*, List data*/){
        inflater = LayoutInflater.from(context);
        /*this.clanky = (ArrayList) data;*/
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
    }

    /**
     * Tu sa len "nafukne" zopar layout-ov item-om (podla toho, kolko sa ich zmesti na obrazovku)
     * a tie sa naplnaju v onBindViewHolder metode = netreba nafukovat 100 itemov, iba zopar
     * @param viewGroup
     * @param i
     * @return
     */
    @Override
    public ClanokViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.clanok_list_item, viewGroup, false);
        //Log.d("Lifecycle", "onCreateHolder called");
        ClanokViewHolder holder = new ClanokViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ClanokViewHolder viewHolder, int i) {
        ClanokObj aktClanok = clanky.get(i);

        //Log.d("Lifecycle", "onBindHolder called" + i);

        viewHolder.title.setText(aktClanok.getTitle());
        viewHolder.description.setText(aktClanok.getDescription());

        String imageUrl = aktClanok.getImageUrl();
        loadImage(imageUrl, viewHolder);

        if (aktClanok.isLocked()){
            viewHolder.lockImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.lockImage.setVisibility(View.GONE);
        }

        /*
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.getAPPContext(), "Klikliste na " + i, Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    private void loadImage(String imageUrl, final ClanokViewHolder viewHolder){

        if (!imageUrl.equals("NA")){
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    viewHolder.image.setImageBitmap(response.getBitmap()); //nastavenie obrazka, ak je dostupny na nete
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return clanky.size(); // dolezite !!!
    }

    public void setClanky(ArrayList<ClanokObj> clanky){
        this.clanky = clanky;
        notifyItemRangeChanged(0, clanky.size()); //upornenie adaptera na zmenu rozsahu (poctu) clankov
    }


    /**
     * ViewHolder sa vytvori raz a drz jednotlive Views z item_View, takze ich potom netreba hladat
     */
    class ClanokViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        TextView title;
        TextView description;
        ImageView image;
        ImageView lockImage;

        public ClanokViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_tvTitle);
            description = (TextView) itemView.findViewById(R.id.item_tvDescription);
            image = (ImageView) itemView.findViewById(R.id.item_ivObr);
            lockImage = (ImageView) itemView.findViewById(R.id.item_ivLock);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(MainActivity.getAPPContext(), "Klikli ste na " + getPosition(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.context, ClanokActivity.class);
            intent.putExtra(EXTRA_RUBRIKA, clanky.get(getPosition()).getRubrika());

            //ActivityCompat.startActivity(ClanokActivity, intent, null);
            v.getContext().startActivity(intent);

        }
    }







}//end ClanokRecyclerViewAdapter

package bc.cestaplus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import bc.cestaplus.ArticleObj;
import bc.cestaplus.R;
import bc.cestaplus.network.VolleySingletonOld;

/**
 * Created by Matej on 4.3.2015.
 */
public class ClanokRecyclerViewAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 100; //preco prave sto!? :) - len tak

    private LayoutInflater inflater;
    private ArrayList<ArticleObj> clanky  = new ArrayList<>(); // vseobecne pomenovanie v adaptery

    private VolleySingletonOld volleySingleton;
    private ImageLoader imageLoader;

    /**
     * Konstruktor
     * @param context
     */
    public ClanokRecyclerViewAdapter(Context context/*, List data*/){
        inflater = LayoutInflater.from(context);
        /*this.clanky = (ArrayList) data;*/
        volleySingleton = VolleySingletonOld.getInstance();
        imageLoader = volleySingleton.getImageLoader();
    }

    /**
     * Tu sa len "nafukne" zopar layout-ov item-om (podla toho, kolko sa ich zmesti na obrazovku)
     * a tie sa naplnaju v onBindViewHolder metode = netreba nafukovat 100 itemov, iba zopar
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder holder;

        switch (viewType) {
            case 0: {
                View view = inflater.inflate(R.layout.clanok_list_item, viewGroup, false);
                holder = new ArticleViewHolder(view);
                break;  // !!!!
            }

            case TYPE_FOOTER: {
                View view = inflater.inflate(R.layout.button_load_more, viewGroup, false);
                holder = new FooterViewHolder(view);
                break; // !!!!
            }

            default:{
                View view = inflater.inflate(R.layout.clanok_list_item, viewGroup, false);
                holder = new ArticleViewHolder(view);
                break;  // !!!!
            }
        }
        //Log.d("Lifecycle", "onCreateHolder called");

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof FooterViewHolder){
            FooterViewHolder holder = (FooterViewHolder) viewHolder;
            holder.btnLoadMore.setText("Load more");

        } else {
            ArticleViewHolder holder = (ArticleViewHolder) viewHolder;
            ArticleObj actArticle = clanky.get(i);

            holder.title.setText(actArticle.getTitle());
            holder.description.setText(actArticle.getShort_text());

            String imageUrl = actArticle.getImageUrl();
            loadImage(imageUrl, holder);

        // ak je clanok zamknuty treba zobrazit zamok
            if (actArticle.isLocked()) {
                holder.lockImage.setVisibility(View.VISIBLE);
            } else {
                holder.lockImage.setVisibility(View.GONE);
            }

        }

    }// end onBindViewHolder

        /*
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.getAPPContext(), "Klikliste na " + i, Toast.LENGTH_SHORT).show();
            }
        });
        */


    @Override
    public int getItemViewType(int position) {
        if (position == clanky.size()){
            return TYPE_FOOTER;
        } else {
            return 0;
        }
    } //end getItemViewType

    private void loadImage(String imageUrl, final ArticleViewHolder viewHolder){

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
    }// end loadImage

    @Override
    public int getItemCount() {
        return clanky.size()+1; // dolezite !!! // +1 kvoli footeru
    }

    public void setClanky(ArrayList<ArticleObj> clanky){
        this.clanky = clanky;
        notifyItemRangeChanged(0, clanky.size()); //upornenie adaptera na zmenu rozsahu (poctu) clankov
    }


    /**
     * ViewHolder sa vytvori raz a drz jednotlive Views z item_View, takze ich potom netreba hladat
     */
    class ArticleViewHolder
        extends RecyclerView.ViewHolder
        //implements View.OnClickListener
        {

        TextView title;
        TextView description;
        ImageView image;
        ImageView lockImage;

            public ArticleViewHolder(View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.item_tvTitle);
                description = (TextView) itemView.findViewById(R.id.item_tvDescription);
                image = (ImageView) itemView.findViewById(R.id.item_ivObr);
                lockImage = (ImageView) itemView.findViewById(R.id.item_ivLock);

                //itemView.setOnClickListener(this);
            } //end konstructor ArticleViewHolder(View itemView)

        } // end ArticleViewHolder

        /*
        @Override
        public void onClick(View v) {
            //Toast.makeText(CustomApplication.getCustomAppContext(), "Klikli ste na " + getPosition(), Toast.LENGTH_SHORT).show();

            if (itemView instanceof Button){
                Toast.makeText(CustomApplication.getCustomAppContext(), "Load more", Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(MainActivity.context, ClanokActivity.class);
                intent.putExtra(EXTRA_RUBRIKA, clanky.get( getPosition() ) .getSection());

                //ActivityCompat.startActivity(ClanokActivity, intent, null);
                v.getContext().startActivity(intent);
            }
        }
        */

    /**
     * Footer ViewHolder
     */
    class FooterViewHolder
        extends RecyclerView.ViewHolder
        {

        Button btnLoadMore;

        public FooterViewHolder(View view) {
            super(view);
            btnLoadMore = (Button) itemView.findViewById(R.id.btnLoadMore);
        }

            public Button getBtnLoadMore() {
                return btnLoadMore;
            }
        } // end FooterViewHolder


}//end ClanokRecyclerViewAdapter
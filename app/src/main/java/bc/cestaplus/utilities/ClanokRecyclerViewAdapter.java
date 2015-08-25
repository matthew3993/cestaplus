package bc.cestaplus.utilities;

import android.content.Context;
import android.content.res.Configuration;
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

import bc.cestaplus.R;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.objects.ArticleObj;

/**
 * Created by Matej on 4.3.2015.
 */
public abstract class ClanokRecyclerViewAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int TYPE_NORMAL = 0; //ľubovoľná hodnota
    protected static final int TYPE_FOOTER = 100; //ľubovoľná hodnota

    protected LayoutInflater inflater;
    protected ArrayList<ArticleObj> clanky  = new ArrayList<>(); // vseobecne pomenovanie v adaptery
    protected int rola;
    protected int screenSize;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    /**
     * Konstruktor
     * @param context
     */
    public ClanokRecyclerViewAdapter(Context context){
        inflater = LayoutInflater.from(context);
        /*this.rubriky = (ArrayList) data;*/
        volleySingleton = VolleySingleton.getInstance(CustomApplication.getCustomAppContext());
        imageLoader = volleySingleton.getImageLoader();
        rola = new SessionManager(CustomApplication.getCustomAppContext()).getRola(); //get rola
        this.screenSize = CustomApplication.getCustomAppScreenSize(); //get screen size
    }

    /**
     * Tu sa len "nafukne" zopar layout-ov item-om (podla toho, kolko sa ich zmesti na obrazovku)
     * a tie sa naplnaju v onBindViewHolder metode = netreba nafukovat 100 itemov, iba zopar
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType);


    @Override
    public abstract void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i);

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
            return TYPE_NORMAL;
        }
    } //end getItemViewType

    /**
     * Load the image from url, using ImageLoader
     * @param imageUrl
     * @param viewHolder
     */
    protected void loadImage(String imageUrl, final ArticleViewHolder viewHolder){

        if (!imageUrl.equals("NA")){
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    viewHolder.image.setImageBitmap(response.getBitmap()); //nastavenie obrazka, ak je dostupny na nete
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //do nothing
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
     * ViewHolder sa vytvori raz a drží jednotlive Views z item_view, takze ich potom netreba hladat
     */
    protected abstract class ArticleViewHolder
        extends RecyclerView.ViewHolder
        //implements View.OnClickListener
        {

        /*TextView title;
        TextView description;*/
        protected ImageView image;

            /*ImageView lockImage;
        */

            public ArticleViewHolder(View itemView) {
                super(itemView);
                /*
                title = (TextView) itemView.findViewById(R.id.item_tvTitle);
                description = (TextView) itemView.findViewById(R.id.item_tvDescription);
                */
                image = (ImageView) itemView.findViewById(R.id.item_ivObr);
                /*
                lockImage = (ImageView) itemView.findViewById(R.id.item_ivLock);
                */
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
                Intent intent = new Intent(MainActivity.context, ArticleActivity_OtherWay.class);
                intent.putExtra(EXTRA_NAZOV_RUBRIKY, rubriky.get( getPosition() ) .getSection());

                //ActivityCompat.startActivity(ArticleActivity_OtherWay, intent, null);
                v.getContext().startActivity(intent);
            }
        }
        */

    /**
     * Footer ViewHolder
     */
    protected class FooterViewHolder
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
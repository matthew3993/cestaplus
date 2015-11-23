package bc.cestaplus.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

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
    protected static final int TYPE_LOAD_MORE = 100; //ľubovoľná hodnota
    protected static final int TYPE_PROGRESS_BAR = 150; //ľubovoľná hodnota

    protected LayoutInflater inflater;
    protected ArrayList<ArticleObj> clanky  = new ArrayList<>(); // vseobecne pomenovanie v adaptery
    protected int rola;
    protected int screenSize;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    private boolean loading; //if we are loading more data at the specified moment
    private boolean noMoreArticles; //if

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
        loading = false;
        noMoreArticles = false;
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
        if (noMoreArticles){
            return TYPE_NORMAL; //if there are no more articles, do not show footer

        } else {
            if (position == clanky.size()) {//
                if (loading) {
                    return TYPE_PROGRESS_BAR;
                } else {
                    return TYPE_LOAD_MORE;
                }
            } else {
                return TYPE_NORMAL;
            }
        }
    } //end getItemViewType

    /**
     * Load the image from url, using ImageLoader
     * @param imageUrl
     * @param viewHolder
     */
    protected void loadImage(String imageUrl, final ArticleViewHolder viewHolder){

        if (!imageUrl.equals("NA")){
            //imageLoader.get(imageUrl, ImageLoader.getImageListener(viewHolder.image, 0, R.drawable.err_pic)); //not good way
                // - this way is slower and causes image changes during scrolling = showing image that doesn't belong to selected
                // article for short while = very annoing

            viewHolder.image.setImageUrl(imageUrl, imageLoader);
            viewHolder.image.setErrorImageResId(R.drawable.err_pic); //better way of showing error picture

            /* old implementation using classic ImageView instead Volley's NetworkImageView
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    viewHolder.image.setImageBitmap(response.getBitmap()); //nastavenie obrazka, ak je dostupny na nete
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //do nothing
                }
            });*/
        }
    }// end loadImage

    @Override
    public int getItemCount() {
        if (noMoreArticles){
            return clanky.size(); //if there are no more articles, do not show footer

        } else {  //if there ARE more articles, do NEED to show footer
            return clanky.size() + 1; // dolezite !!! // +1 kvoli footeru
        }
    }

    public void setClanky(ArrayList<ArticleObj> clanky){
        this.clanky = clanky;
        loading = false; //in case we're setting articles, we are not loading nothing anymore
        notifyItemRangeChanged(0, clanky.size()); //upornenie adaptera na zmenu rozsahu (poctu) clankov
    }

    public void startAnim() {
        loading = true;
        notifyItemChanged(clanky.size());
    }

    public void setError() {
        loading = false;
        notifyItemChanged(clanky.size());
    }

    public void setNoMoreArticles() {
        noMoreArticles = true;
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
        protected NetworkImageView image;

            /*ImageView lockImage;
        */

            public ArticleViewHolder(View itemView) {
                super(itemView);
                /*
                title = (TextView) itemView.findViewById(R.id.item_tvTitle);
                description = (TextView) itemView.findViewById(R.id.item_tvDescription);
                */
                image = (NetworkImageView) itemView.findViewById(R.id.item_ivObr);
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
     * LoadMoreBtn ViewHolder
     */
    protected class LoadMoreBtnViewHolder
        extends RecyclerView.ViewHolder
        {

        Button btnLoadMore;

        public LoadMoreBtnViewHolder(View view) {
            super(view);
            btnLoadMore = (Button) itemView.findViewById(R.id.btnLoadMore);
        }

            public Button getBtnLoadMore() {
                return btnLoadMore;
            }
    } // end FooterViewHolder


    /**
     * ProgressBar ViewHolder
     */
    public static class ProgressViewHolder
        extends RecyclerView.ViewHolder
        {

        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }//end of ProgressViewHolder class

}//end ClanokRecyclerViewAdapter
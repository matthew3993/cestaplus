package sk.cestaplus.cestaplusapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;

/**
 * Created by Matej on 4.3.2015.
 * SOURCES
 * - for adding header and footer code:
 *      http://stackoverflow.com/questions/26530685/is-there-an-addheaderview-equivalent-for-recyclerview/26573338#26573338
 *      http://takeoffandroid.com/android-customview/header-and-footer-layout-for-recylerview/
 */
public abstract class ArticlesRecyclerViewAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    protected static final int TYPE_HEADER = 50;        // arbitrary value
    protected static final int TYPE_NORMAL = 0;         // arbitrary value

    // only one of these is shown at the time
    protected static final int TYPE_LOAD_MORE = 100;    // arbitrary value
    protected static final int TYPE_PROGRESS_BAR = 150; // arbitrary value

    protected LayoutInflater inflater;
    protected ArticleObj headerArticle;
    protected ArrayList<ArticleObj> articlesList = new ArrayList<>();
    protected int role;
    protected int screenSize;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    protected boolean hasHeader;
    private boolean loading; //if we are loading more data at the specified moment
    private boolean noMoreArticles; //if there are NOT more article to load

    public ArticlesRecyclerViewAdapter(Context context, boolean hasHeader){
        inflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance(CustomApplication.getCustomAppContext());
        imageLoader = volleySingleton.getImageLoader();
        role = new SessionManager(CustomApplication.getCustomAppContext()).getRole(); //get role
        this.screenSize = CustomApplication.getCustomAppScreenSize(); //get screen size

        this.hasHeader = hasHeader;
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

    @Override
    public int getItemViewType(int position) {
        //return resolveArticleOrHeader(position);
        if (articlesList.isEmpty()){
            return resolveArticleOrHeader(position);

        } else {
            if (noMoreArticles) {
                //if there are no more articles, do not show footer
                return resolveArticleOrHeader(position);

            } else {
                if (position == (articlesList.size() + (hasHeader ? 1 : 0))) {// +1 because of header
                    //footer
                    if (loading) {
                        return TYPE_PROGRESS_BAR;
                    } else {
                        return TYPE_LOAD_MORE;
                    }
                } else { //articles or header
                    return resolveArticleOrHeader(position);
                }
            }
        }
    } //end getItemViewType

    private int resolveArticleOrHeader(int position) {
        if (hasHeader && position == 0){
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (articlesList.isEmpty()) {
            return 0;

        } else {
            if (noMoreArticles) {
                //if there are no more articles, do not show footer, show only articles and header
                return articlesList.size() + (hasHeader ? 1 : 0);  // +1 because of header

            } else {

                //if there ARE more articles, do NEED to show footer + don't forget HEADER
                return articlesList.size() + 1 + (hasHeader ? 1 : 0); // +1 because of footer - load more button // +1 because of header
            }
        }
    }

    protected void onBindViewHolderHeaderView(final RecyclerView.ViewHolder viewHolder, int i){

        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

        if (headerArticle != null) {
            //Author name
            holder.author.setText(headerArticle.getAuthor().toUpperCase());

            //Title
            holder.title.setText(headerArticle.getTitle());

            //Short text
            holder.description.setText(headerArticle.getShort_text());

            //Image - start to load image and check if user is logged in or not
            String imageUrl = headerArticle.getImageUrl();
            holder.image.setImageUrl(imageUrl, imageLoader);
            holder.image.setErrorImageResId(R.drawable.err_pic); //better way of showing error picture

            /*
            // ak je role 0 a článok je zamknuty treba zobrazit zamok
            if (role == 0 && actArticle.isLocked()) {
                holder.lockImage.setVisibility(View.VISIBLE);
                holder.getImage().setAlpha( (float) 0.3);

            } else {
                holder.lockImage.setVisibility(View.GONE);
                holder.getImage().setAlpha( (float) 1);
            }
            */
        }
    }

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

            //region OLD IMPLEMENTATION using classic ImageView instead Volley's NetworkImageView
            /*
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
            */
            //endregion
        }
    }// end loadImage

    public void setHeaderArticle(ArticleObj headerArticle) {
        //this.headerArticle = headerArticle;
    }

    public void setArticlesList(ArrayList<ArticleObj> articlesList, ArticleObj headerArticle){
        this.headerArticle = headerArticle;
        setArticlesList(articlesList);
    }

    public void setArticlesList(ArrayList<ArticleObj> articlesList){
        this.articlesList = articlesList;
        loading = false; //in case we're setting articles, we are not loading nothing anymore
        notifyItemRangeChanged(0, articlesList.size()); //upornenie adaptera na zmenu rozsahu (poctu) clankov
    }

    public void startAnim() {
        loading = true;
        notifyItemChanged(articlesList.size());
    }

    public void setError() {
        loading = false;
        notifyItemChanged(articlesList.size());
    }

    public void setNoMoreArticles() {
        noMoreArticles = true;
    }

    public boolean isLoading() {
        return loading;
    }

    /**
     * ViewHolder sa vytvori raz a drží jednotlive Views z item_view, takze ich potom netreba hladat
     * This class is extended in ArticlesRecyclerViewAdapter_All & ArticlesRecyclerViewAdapter_PicturesAndTitles classes
     */
    protected abstract class ArticleViewHolder
        extends RecyclerView.ViewHolder
        {

        protected NetworkImageView image;

        public ArticleViewHolder(View view) {
            super(view);
            image = (NetworkImageView) view.findViewById(R.id.nivListItem);
        } //end constructor ArticleViewHolder(View view)
    } // end ArticleViewHolder

    /**
     * Header ViewHolder
     */
    protected class HeaderViewHolder
        extends RecyclerView.ViewHolder
    {

        protected TextView author;
        protected TextView title;
        protected TextView description;
        protected NetworkImageView image;
        //protected ImageView lockImage;

        public HeaderViewHolder(View view) {
            super(view);
            author = (TextView) itemView.findViewById(R.id.item_tvAuthor);
            title = (TextView) itemView.findViewById(R.id.item_tvTitle);
            description = (TextView) itemView.findViewById(R.id.item_tvShortText);
            image = (NetworkImageView) view.findViewById(R.id.nivListItem);
            //lockImage = (ImageView) itemView.findViewById(R.id.item_ivLock);
        }
    }//end of ProgressViewHolder class

    /**
     * LoadMoreBtn ViewHolder
     */
    protected class LoadMoreBtnViewHolder
        extends RecyclerView.ViewHolder
        {

        Button btnLoadMore;
        //TextView loadMore;

        public LoadMoreBtnViewHolder(View view) {
            super(view);
            btnLoadMore = (Button) itemView.findViewById(R.id.btnLoadMore);
            //loadMore = (TextView) itemView.findViewById(R.id.tvLoadMore);
        }


    } // end FooterViewHolder


    /**
     * ProgressBar ViewHolder
     */
    public static class ProgressViewHolder
        extends RecyclerView.ViewHolder
        {

        public ProgressBar progressBar;

        public ProgressViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBarRecyclerViewLoadMore);
        }
    }//end of ProgressViewHolder class

}//end ArticlesRecyclerViewAdapter
package sk.cestaplus.cestaplusapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.ImageUtil;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextUtil;
import sk.cestaplus.cestaplusapp.views.CustomVolleyImageView;

/**
 * Created by Matej on 4.3.2015.
 */
public class ArticlesRecyclerViewAdapter_All
    extends ArticlesRecyclerViewAdapter {

    /**
     * Konstruktor
     * @param context
     */
    public ArticlesRecyclerViewAdapter_All(Context context, boolean hasHeader){
        super(context, hasHeader);
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

        switch (viewType) { // inflate corresponding type of view - footer or normal type
            case TYPE_HEADER: {
                View view = inflater.inflate(R.layout.header_list_item, viewGroup, false);
                holder = new HeaderViewHolder(view);
                break; // !!!!
            }

            case TYPE_NORMAL: {
                View view;
                view = inflater.inflate(R.layout.article_list_item, viewGroup, false);
                /*
                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        view = inflater.inflate(R.layout.article_list_item, viewGroup, false);
                        break;

                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    case Configuration.SCREENLAYOUT_SIZE_SMALL:
                        view = inflater.inflate(R.layout.article_list_item_normal, viewGroup, false);
                        break;

                    default:
                        view = inflater.inflate(R.layout.article_list_item_normal, viewGroup, false);
                }
                */
                holder = new ArticleViewHolder_All(view);
                break;  // !!!!
            }

            case TYPE_LOAD_MORE: {
                View view = inflater.inflate(R.layout.button_load_more, viewGroup, false);
                holder = new LoadMoreBtnViewHolder(view);
                break; // !!!!
            }

            case TYPE_PROGRESS_BAR: {
                View view = inflater.inflate(R.layout.progressbar_item, viewGroup, false);
                holder = new ProgressViewHolder(view);
                break; // !!!!
            }

            default:{
                View view = inflater.inflate(R.layout.article_list_item, viewGroup, false);
                holder = new ArticleViewHolder_All(view);
                break;  // !!!!
            }
        }//end switch

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof HeaderViewHolder){
            super.onBindViewHolderHeaderView(viewHolder, i);

        } else if (viewHolder instanceof LoadMoreBtnViewHolder){
            LoadMoreBtnViewHolder holder = (LoadMoreBtnViewHolder) viewHolder;
            //holder.btnLoadMore.setText("Load more");

        } else if (viewHolder instanceof ProgressViewHolder) {
            ProgressViewHolder holder = (ProgressViewHolder) viewHolder;
            holder.progressBar.setIndeterminate(true);

        } else {
            ArticleViewHolder_All holder = (ArticleViewHolder_All) viewHolder;
            ArticleObj actArticle = articlesList.get(i - (super.hasHeader ? 1 : 0));    // -1 because of header

        //Author name
            holder.author.setText(actArticle.getAuthor().toUpperCase());

        //Title
            TextUtil.setTitleText(CustomApplication.getCustomAppContext(), TextUtil.showLock(role, actArticle.isLocked()),
                    actArticle.getTitle(), holder.title, R.drawable.lock_black);

        //Short text
            holder.description.setText(actArticle.getShort_text());

        //Image - start to load image and check if user is logged in or not
            String articleId = actArticle.getID();
            String imageDimenUrl = ImageUtil.getImageDimenUrl(context, actArticle);
            String imageDefUrl = actArticle.getImageDefaulUrl();

            /*
            String articleId = articlesList.get(0).getID();
            String imageDimenUrl = ImageUtil.getImageDimenUrl(context, articlesList.get(0));
            String imageDefUrl = articlesList.get(0).getImageDefaulUrl();
            */

            loadImage(actArticle, holder);
            //holder.getImage().setImageUrl(imageDimenUrl, im);

            // ak je role 0 a článok je zamknuty treba zobrazit zamok
            if (TextUtil.showLock(role, actArticle.isLocked())) {
                holder.lockImage.setVisibility(View.VISIBLE);
                holder.getImage().setAlpha( (float) 0.3);

            } else {
                holder.lockImage.setVisibility(View.GONE);
                holder.getImage().setAlpha( (float) 1);
            }

        }
    }// end onBindViewHolder

    /**
     * ViewHolder sa vytvori raz a drží jednotlive Views z item_view, takze ich potom netreba hladat
     */
    class ArticleViewHolder_All
        extends ArticleViewHolder
        //implements View.OnClickListener
        {

        TextView author;
        TextView title;
        TextView description;
        //ImageView image; //in abstract ArticleViewHolder in ArticlesRecyclerViewAdapter
        ImageView lockImage;

            public ArticleViewHolder_All(View itemView) {
                super(itemView);

                author = (TextView) itemView.findViewById(R.id.item_tvAuthor);
                title = (TextView) itemView.findViewById(R.id.item_tvTitle);
                description = (TextView) itemView.findViewById(R.id.item_tvShortText);
                //image = (ImageView) itemView.findViewById(R.id.item_ivObr);
                lockImage = (ImageView) itemView.findViewById(R.id.item_ivLock);

                //itemView.setOnClickListener(this);
            } //end constructor ArticleViewHolder(View itemView)

            private CustomVolleyImageView getImage(){
                return super.image;
            }
        } // end ArticleViewHolder

}//end ArticlesRecyclerViewAdapter
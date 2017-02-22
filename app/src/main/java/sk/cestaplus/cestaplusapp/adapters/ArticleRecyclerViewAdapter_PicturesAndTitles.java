package sk.cestaplus.cestaplusapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextUtil;

/**
 * Created by Matej on 4.3.2015.
 */
public class ArticleRecyclerViewAdapter_PicturesAndTitles
    extends ArticleRecyclerViewAdapter {

    public ArticleRecyclerViewAdapter_PicturesAndTitles(Context context, boolean hasHeader){
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

        switch (viewType) { // inflate coresponding type of view - footer or normal type
            case TYPE_NORMAL: {
                View view = inflater.inflate(R.layout.article_list_item_pictures_and_titles, viewGroup, false);
                holder = new ArticleViewHolder_PicturesAndTitles(view);
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
                View view = inflater.inflate(R.layout.article_list_item_pictures_and_titles, viewGroup, false);
                holder = new ArticleViewHolder_PicturesAndTitles(view);
                break;  // !!!!
            }
        }//end switch

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof LoadMoreBtnViewHolder){
            LoadMoreBtnViewHolder holder = (LoadMoreBtnViewHolder) viewHolder;

        } else if (viewHolder instanceof ProgressViewHolder) {
            ProgressViewHolder holder = (ProgressViewHolder) viewHolder;
            holder.progressBar.setIndeterminate(true);

        } else {
            ArticleViewHolder_PicturesAndTitles holder = (ArticleViewHolder_PicturesAndTitles) viewHolder;
            ArticleObj actArticle = articlesList.get(i - (super.hasHeader ? 1 : 0));    // -1 beacuse of header

        //Author name
            holder.author.setText(actArticle.getAuthor().toUpperCase());

        //Title
            TextUtil.setTitleText(CustomApplication.getCustomAppContext(), TextUtil.showLock(role, actArticle.isLocked()),
                    actArticle.getTitle(), holder.title, R.drawable.lock_black);

        //Image - start to load image and check if user is logged in or not
            String imageUrl = actArticle.getImageUrl();
            loadImage(imageUrl, holder);

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
    class ArticleViewHolder_PicturesAndTitles
        extends ArticleViewHolder
        {

        TextView author;
        TextView title;
        ImageView lockImage;

            public ArticleViewHolder_PicturesAndTitles(View itemView) {
                super(itemView);

                author = (TextView) itemView.findViewById(R.id.item_tvAuthor);
                title = (TextView) itemView.findViewById(R.id.item_tvTitle);
                lockImage = (ImageView) itemView.findViewById(R.id.item_ivLock);

            } //end constructor ArticleViewHolder(View itemView)

            private ImageView getImage(){
                return super.image;
            }

        } // end ArticleViewHolder

}//end ArticleRecyclerViewAdapter
package bc.cestaplus.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bc.cestaplus.R;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.utilities.ClanokRecyclerViewAdapter;

/**
 * Created by Matej on 4.3.2015.
 */
public class ClanokRecyclerViewAdapter_PicturesAndTitles
    extends ClanokRecyclerViewAdapter {


    /**
     * Konstruktor
     * @param context
     */
    public ClanokRecyclerViewAdapter_PicturesAndTitles(Context context/*, int rola*/){
        super(context);
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
                View view;
                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        view = inflater.inflate(R.layout.clanok_list_item_pictures_and_titles_large, viewGroup, false);
                        break;

                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    case Configuration.SCREENLAYOUT_SIZE_SMALL:
                        view = inflater.inflate(R.layout.clanok_list_item_pictures_and_titles_normal, viewGroup, false);
                        break;

                    default:
                        view = inflater.inflate(R.layout.clanok_list_item_pictures_and_titles_normal, viewGroup, false);
                }
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
                View view = inflater.inflate(R.layout.clanok_list_item_pictures_and_titles_normal, viewGroup, false);
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
            //holder.btnLoadMore.setText("Load more");

        } else if (viewHolder instanceof ProgressViewHolder) {
            ProgressViewHolder holder = (ProgressViewHolder) viewHolder;
            holder.progressBar.setIndeterminate(true);

        } else {
            ArticleViewHolder_PicturesAndTitles holder = (ArticleViewHolder_PicturesAndTitles) viewHolder;
            ArticleObj actArticle = clanky.get(i);

        //Title
            holder.title.setText(actArticle.getTitle());

        /*
        //Short text
            holder.description.setText(actArticle.getShort_text());*/

        //Image - start to load image and check if user is logged in or not
            String imageUrl = actArticle.getImageUrl();
            loadImage(imageUrl, holder);

            // ak je rola 0 a článok je zamknuty treba zobrazit zamok
            if (rola == 0 && actArticle.isLocked()) {
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
        //implements View.OnClickListener
        {

        TextView title;
        //TextView description;
        //ImageView image;
        ImageView lockImage;

            public ArticleViewHolder_PicturesAndTitles(View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.item_tvTitle);
                //description = (TextView) itemView.findViewById(R.id.item_tvDescription);
                //image = (ImageView) itemView.findViewById(R.id.item_ivObr);
                lockImage = (ImageView) itemView.findViewById(R.id.item_ivLock);

                //itemView.setOnClickListener(this);
            } //end konstructor ArticleViewHolder(View itemView)

            private ImageView getImage(){
                return super.image;
            }

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
        } // end FooterViewHolder*/


}//end ClanokRecyclerViewAdapter
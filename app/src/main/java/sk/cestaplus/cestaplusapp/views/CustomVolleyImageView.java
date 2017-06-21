package sk.cestaplus.cestaplusapp.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.support.v7.widget.AppCompatImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.ImageUtil;

import static sk.cestaplus.cestaplusapp.extras.Constants.IMAGE_DEBUG;

/**
 * This is customised {@link com.android.volley.toolbox.NetworkImageView} class to load and show images of
 * articles - {@link ArticleObj}.
 * Main method of this class is {@link CustomVolleyImageView#setImageUrl(ArticleObj, ImageLoader, Context)}.
 * This method is used to load images of articles. At first so called "dimen" image is loaded - image with reduced size.
 * URL of this reduced sized image is get from {@link ImageUtil#getImageDimenUrl(Context, ArticleObj)}.
 * But because not all of images are available in all sizes, if there is error during loading dimen image,
 * class starts to load image from default URL - so called "default" image. URL of default image is received from server's API.
 *
 * If there is error loading dimen image, class also sets {@link ArticleObj#setWasErrorDimenImage(boolean)} to true,
 * to mark that there was problem when loading dimen image of this INSTANCE of ArticleObj.
 *
 * When article is set to be loaded to this View, class checks if previously there was an error loading dimen image
 * and if was, it skips try to load dimen image and starts to load default image at once.
 * This improves performance during scrolling down and up again.
 * !!! WARNING !!! - error mark is set only to INSTANCE of ArticleObj, not to database or any persistent storage.
 * SO if articles are loaded again or refreshed, all error marks are set to false!!!
 *
 * Error picture is showed only if there is problem with also with default image.
 *
 * SOURCE of first idea: https://stackoverflow.com/a/22053985
 * original code of {@link com.android.volley.toolbox.NetworkImageView} class:
 * https://android.googlesource.com/platform/frameworks/volley/+/d62a616ebca5bfa4f9ec5517208e13f2d501b69a/src/com/android/volley/toolbox/NetworkImageView.java
 */
public class CustomVolleyImageView extends AppCompatImageView {

    public interface ResponseObserver
    {
        void onDimenError(CustomVolleyImageView customVolleyImageView);
        void onDimenSuccess(CustomVolleyImageView customVolleyImageView);

        void onDefaultError(CustomVolleyImageView customVolleyImageView);
        void onDefaultSuccess(CustomVolleyImageView customVolleyImageView);
    }

    private ResponseObserver mObserver;

    public void setResponseObserver(ResponseObserver observer) {
        mObserver = observer;
    }

    private CustomVolleyImageView thisImgView;

    private ArticleObj articleObj;

    /**
     * The URL of the network image to load - DIMEN
     */
    private String mDimenUrl;

    /**
     * The URL of the network image to load in case of error when loading from mDimenUrl
     */
    private String mDefaultUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network image is loaded.
     */
    private int mDefaultImageId;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    private int mErrorImageId;

    /**
     * Local copy of the ImageLoader.
     */
    private ImageLoader mImageLoader;

    /**
     * Current ImageContainer. (either in-flight or finished)
     */
    private ImageContainer mDimenImageContainer;


    private ImageContainer mDefaultImageContainer;

    public CustomVolleyImageView(Context context) {
        this(context, null);
    }

    public CustomVolleyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVolleyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        thisImgView = this;
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link CustomVolleyImageView#setDefaultImageResId(int)} on the view.
     *
     * NOTE: If applicable, {@link CustomVolleyImageView#setDefaultImageResId(int)} and {@link
     * CustomVolleyImageView#setErrorImageResId(int)} should be called prior to calling this function.
     *
     * @param imageLoader ImageLoader that will be used to make the request.
     */
    public void setImageUrl(ArticleObj articleObj, ImageLoader imageLoader, Context context) {

        if (this.articleObj == null){
            // first load of article - in recycler view during inflating of row layouts

            this.articleObj = articleObj;
            mDimenUrl = ImageUtil.getImageDimenUrl(context, articleObj);
            mDefaultUrl = articleObj.getImageDefaultUrl();
            mImageLoader = imageLoader;

            resolveLoadImages(false); //start load dimen or default image

            return;
        }
        //some article loaded before

        if (this.articleObj.getID().equalsIgnoreCase(articleObj.getID())){
            // there was a some article loaded before AND new articleObj EQUALS to old articleObj
            // => same article was set - do NOTHING
            //Log.d(IMAGE_DEBUG, "Setting same article: " + mDimenUrl);
            return;

        } else {
            // there was a some article loaded before new articleObj DON'T equals to old articleObj
            // => load image of new article

            // cancel requests
            if (mDimenImageContainer != null && mDimenImageContainer.getRequestUrl() != null) {
                mDimenImageContainer.cancelRequest();
                setDefaultImageOrNull();
            }
            if (mDefaultImageContainer != null && mDefaultImageContainer.getRequestUrl() != null) {
                mDefaultImageContainer.cancelRequest();
                setDefaultImageOrNull();
            }

            // set new values
            this.articleObj = articleObj;
            mDimenUrl = ImageUtil.getImageDimenUrl(context, articleObj);
            mDefaultUrl = articleObj.getImageDefaultUrl();
            mImageLoader = imageLoader;

            resolveLoadImages(false); //start load dimen or default image
        }
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public void setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public void setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
    }

    /**
     * Loads the image for the view if it isn't already loaded.
     *
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     */
    private void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = getWidth();
        int height = getHeight();

        boolean isFullyWrapContent = getLayoutParams() != null
                && getLayoutParams().height == LayoutParams.WRAP_CONTENT
                && getLayoutParams().width == LayoutParams.WRAP_CONTENT;
        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        // if the URL to be loaded in this view is empty, cancel any old requests and clear the
        // currently loaded image.
        if (TextUtils.isEmpty(mDimenUrl)) {
            if (mDimenImageContainer != null) {
                mDimenImageContainer.cancelRequest();
                mDimenImageContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }

        if (!articleObj.wasErrorDimenImage()) {

            // if there was an old request in this view, check if it needs to be canceled.
            if (mDimenImageContainer != null && mDimenImageContainer.getRequestUrl() != null) {
                // there is pre-existing request
                if (mDimenImageContainer.getRequestUrl().equals(mDimenUrl)) {
                    // if the request is from the same URL, return.
                    //Log.d(IMAGE_DEBUG, "mDimenImageContainer: Request from same url - return");
                    return;
                } else {
                    // if there is a pre-existing request, cancel it if it's fetching a different URL.
                    mDimenImageContainer.cancelRequest();
                    setDefaultImageOrNull();
                }
            }
        } else {

            // if there was an old request in this view, check if it needs to be canceled.
            if (mDefaultImageContainer != null && mDefaultImageContainer.getRequestUrl() != null) {
                // there is pre-existing request
                if (mDefaultImageContainer.getRequestUrl().equals(mDefaultUrl)) {
                    // if the request is from the same URL, return.
                    //Log.d(IMAGE_DEBUG, "mDefaultImageContainer: Request from same url - return");
                    return;
                } else {
                    // if there is a pre-existing request, cancel it if it's fetching a different URL.
                    mDefaultImageContainer.cancelRequest();
                    setDefaultImageOrNull();
                }
            }

        }

        resolveLoadImages(isInLayoutPass); //start load dimen or default image
    }

    private void resolveLoadImages(boolean isInLayoutPass) {
        if (!articleObj.wasErrorDimenImage()){
            loadImages(isInLayoutPass);
        } else {
            loadDefaultImage(isInLayoutPass);
        }
    }

    private void loadImages(final boolean isInLayoutPass) {
        // The pre-existing content of this view didn't match the current URL. Load the new image
        // from the network.
        ImageContainer newContainer = mImageLoader.get(mDimenUrl, new ImageListener() {

            @Override
            public void onResponse(final ImageContainer response, boolean isImmediate) {
                // success loading image from DIMEN url

                // If this was an immediate response that was delivered inside of a layout
                // pass do not set the image immediately as it will trigger a requestLayout
                // inside of a layout. Instead, defer setting the image by posting back to
                // the main thread.
                if (isImmediate && isInLayoutPass) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            onResponse(response, false);
                        }
                    });
                    return;
                }

                if (response.getBitmap() != null) {
                    setImageBitmap(response.getBitmap());

                } else if (mDefaultImageId != 0) {
                    articleObj.setWasErrorDimenImage(true);
                    loadDefaultImage(isInLayoutPass);
                }

                if(mObserver!=null)
                {
                    mObserver.onDimenSuccess(thisImgView);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // error loading image from DIMEN url
                // => load from default url

                articleObj.setWasErrorDimenImage(true);
                loadDefaultImage(isInLayoutPass);

                if(mObserver!=null)
                {
                    mObserver.onDimenError(thisImgView);
                }
            }
        });

        // update the ImageContainer to be the new bitmap container.
        mDimenImageContainer = newContainer;
    }

    private void loadDefaultImage(final boolean isInLayoutPass) {
        ImageContainer defaultContainer = mImageLoader.get(mDefaultUrl, new ImageListener() {

            @Override
            public void onResponse(final ImageContainer response, boolean isImmediate) {
                // success loading image from DEFAULT url
                //Log.d(IMAGE_DEBUG, "Successfully loaded image from DEFAULT url " + mDefaultUrl);

                // If this was an immediate response that was delivered inside of a layout
                // pass do not set the image immediately as it will trigger a requestLayout
                // inside of a layout. Instead, defer setting the image by posting back to
                // the main thread.
                if (isImmediate && isInLayoutPass) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            onResponse(response, false);
                        }
                    });
                    return;
                }

                if (response.getBitmap() != null) {
                    setImageBitmap(response.getBitmap());
                } else if (mDefaultImageId != 0) {
                    setImageResource(mDefaultImageId);
                }

                if(mObserver!=null)
                {
                    mObserver.onDefaultSuccess(thisImgView);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // Error loading DEFAULT image => show error image
                //Log.d(IMAGE_DEBUG, "Error loading DEFAULT url!");

                if (mErrorImageId != 0) {
                    setImageResource(mErrorImageId);
                }

                if(mObserver!=null)
                {
                    mObserver.onDefaultError(thisImgView);
                }
            }
        });

        mDefaultImageContainer = defaultContainer;
    }

    private void setDefaultImageOrNull() {
        if (mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        } else {
            setImageBitmap(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mDimenImageContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view.
            mDimenImageContainer.cancelRequest();
            setImageBitmap(null);
            // also clear out the container so we can reload the image if necessary.
            mDimenImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }
}
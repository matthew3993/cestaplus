package bc.cestaplus.services;

import android.widget.Toast;

import java.util.ArrayList;

import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.extras.ArticlesLoadedListener;
import bc.cestaplus.tasks.UpdateTask;
import bc.cestaplus.utilities.CustomApplication;
import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by Matej on 1. 4. 2015.
 */
public class UpdateService
    extends JobService
    implements ArticlesLoadedListener {

    private JobParameters jobParameters;

    /**
     * onStartJob
     * onStartJob run on MAIN THREAD!!!!! ---> we MUST create a AsyncTask!!
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //Toast.makeText(CustomApplication.getCustomAppContext(), "onstartjob", Toast.LENGTH_SHORT).show();

        this.jobParameters = jobParameters;

        new UpdateTask(this, true).execute(); //true = this is periodic task - we want to issue notifications

        return true; //true = processing takes place in background thread
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public void onArticlesLoaded(ArrayList<ArticleObj> listArticles) {
        jobFinished(jobParameters, false);
    } //end onArticlesLoaded

} //end class UpdateService

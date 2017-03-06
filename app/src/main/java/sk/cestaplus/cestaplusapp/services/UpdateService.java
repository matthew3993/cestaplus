package sk.cestaplus.cestaplusapp.services;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import sk.cestaplus.cestaplusapp.listeners.ArticlesLoadedListener;
import sk.cestaplus.cestaplusapp.tasks.UpdateTask;

import sk.cestaplus.cestaplusapp.utilities.ResponseCrate;

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
        this.jobParameters = jobParameters;

        new UpdateTask(this, true).execute(); //true = this is periodic task - we want to issue notifications

        return true; // Answers the question: "Is there still work going on?"
                     // true = processing takes place in background thread
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false; // Answers the question: "Should this job be retried?"
    }

    @Override
    public void onArticlesLoaded(ResponseCrate responseCrate) {
        jobFinished(jobParameters, false);
    } //end onArticlesLoaded

    @Override
    public void numNewArticles(int count) {
        //do nothing in this case for now
    }

    @Override
    public void onLoadingError() {
        jobFinished(jobParameters, false); // even there is a problem, job of this service is finished
    }

} //end class UpdateService

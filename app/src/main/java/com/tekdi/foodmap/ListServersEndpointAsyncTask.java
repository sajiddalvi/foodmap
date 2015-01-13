package com.tekdi.foodmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.tekdi.foodmap.backend.serveFoodEntityApi.ServeFoodEntityApi;
import com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

//class ListServersEndpointAsyncTask

class ListServersEndpointAsyncTask extends AsyncTask<Void, Void, List<ServeFoodEntity>> {
    private static ServeFoodEntityApi myApiService = null;
    private Context context;
    private FindActivity caller;

    //ListServersEndpointAsyncTask(Context context) {
    //    this.context = context;
    //}

    ListServersEndpointAsyncTask(FindActivity caller) {
        this.caller = caller;
    }

    @Override
    protected List<ServeFoodEntity> doInBackground(Void... params) {
        if(myApiService == null) { // Only do this once
            ServeFoodEntityApi.Builder builder = new ServeFoodEntityApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://tekdi-foodmap.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            myApiService = builder.build();
        }

        try {
            Log.v("sajid","executing list servefoodentity");
            return myApiService.listServers().execute().getItems();
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<ServeFoodEntity> result) {
        Log.v("sajid","finished executing listservers");
        if (result != null) {
            for (ServeFoodEntity q : result) {

                Log.v("sajid", "name=" + q.getName());
                caller.setupServers(result);

            }
        }
    }
}
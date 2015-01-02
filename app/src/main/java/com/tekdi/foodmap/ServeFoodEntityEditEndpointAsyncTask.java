package com.tekdi.foodmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.tekdi.foodmap.backend.serveFoodEntityApi.ServeFoodEntityApi;
import com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity;

import java.io.IOException;

/**
 * Created by fsd017 on 1/2/15.
 */

class ServeFoodEntityEditEndpointAsyncTask extends AsyncTask<Pair<Context, ServeFoodEntity>, Void, ServeFoodEntity> {
    private static ServeFoodEntityApi myApiService = null;
    private Context context;
    private EditServerActivity caller;


    ServeFoodEntityEditEndpointAsyncTask(EditServerActivity caller) {
        this.caller = caller;
    }

    @Override
    protected ServeFoodEntity doInBackground(Pair<Context, ServeFoodEntity>... params) {
        if(myApiService == null) {  // Only do this once
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

        context = params[0].first;
        ServeFoodEntity server = params[0].second;

        try {
            ServeFoodEntity serveFoodEntity =myApiService.update(server.getId(), server).execute();
            return serveFoodEntity;
        } catch (IOException e) {
            Log.v("sajid",e.getMessage());
            return null;

        }
    }

    @Override
    protected void onPostExecute(ServeFoodEntity result) {
        Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
        caller.doneEditingServer(result);
    }
}
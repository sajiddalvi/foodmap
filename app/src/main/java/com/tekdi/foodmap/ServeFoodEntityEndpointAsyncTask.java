package com.tekdi.foodmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.tekdi.foodmap.backend.serveFoodEntityApi.ServeFoodEntityApi;
import com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity;

import java.io.IOException;

class ServeFoodEntityEndpointAsyncTask extends AsyncTask<Pair<Context, ServeFoodEntity>, Void, String> {
    private static ServeFoodEntityApi myApiService = null;
    private Context context;

    @Override
    protected String doInBackground(Pair<Context, ServeFoodEntity>... params) {
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
            ServeFoodEntity serveFoodEntity =myApiService.insertServeFoodEntity(server).execute();
            Prefs.setServerIdPref(context,serveFoodEntity.getId().toString());

            return "success";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}
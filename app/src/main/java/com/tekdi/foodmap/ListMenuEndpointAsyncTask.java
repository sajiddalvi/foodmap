package com.tekdi.foodmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.tekdi.foodmap.backend.menuEntityApi.MenuEntityApi;
import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

class ListMenuEndpointAsyncTask extends AsyncTask<Void, Void, List<MenuEntity>> {
    private static MenuEntityApi myApiService = null;
    private Context context;
    private ListMenuActivity caller;
    private Long serverId;

    ListMenuEndpointAsyncTask(ListMenuActivity caller) {
        this.caller = caller;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    @Override
    protected List<MenuEntity> doInBackground(Void... params) {
        if(myApiService == null) { // Only do this once
            MenuEntityApi.Builder builder = new MenuEntityApi.Builder(AndroidHttp.newCompatibleTransport(),
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
            Log.v("sajid","executing list menu");
            return myApiService.listForServer(serverId).execute().getItems();
            //return myApiService.list().execute().getItems();
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<MenuEntity> result) {
        Log.v("sajid","finished executing list menu");
        if (result == null) {
            Log.v("sajid", "result is null");
            return;
        }
        for (MenuEntity q : result) {
            Log.v("sajid","name="+q.getName());
            caller.showMenu(result);

        }
    }
}
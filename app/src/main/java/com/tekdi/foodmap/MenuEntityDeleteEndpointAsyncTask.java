package com.tekdi.foodmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.tekdi.foodmap.backend.menuEntityApi.MenuEntityApi;
import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

import java.io.IOException;

class MenuEntityDeleteEndpointAsyncTask extends AsyncTask<Pair<Context, MenuEntity>, Void, String> {
    private static MenuEntityApi myApiService = null;
    private Context context;
    private ListMenuActivity caller;

    MenuEntityDeleteEndpointAsyncTask(ListMenuActivity caller) {
        this.caller = caller;
    }

    @Override
    protected String doInBackground(Pair<Context, MenuEntity>... params) {
        if(myApiService == null) {  // Only do this once
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

        context = params[0].first;
        MenuEntity menu = params[0].second;

        try {
            myApiService.remove(menu.getId()).execute();
            return "deleted menu";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        caller.doneDeletingMenu();
    }
}

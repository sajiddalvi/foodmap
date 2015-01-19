package com.tekdi.foodmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.tekdi.foodmap.backend.orderEntityApi.OrderEntityApi;
import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class ListOrdersEndpointAsyncTask extends AsyncTask<Void, Void, List<OrderEntity>> {
    private static OrderEntityApi myApiService = null;
    private Context context;
    private ListOrderServerActivity caller;
    private Long serverId;

    ListOrdersEndpointAsyncTask(ListOrderServerActivity caller) {

        this.caller = caller;
        Log.v("sajid","in ListOrdersEndpointAsyncTask setting up caller");
    }

    public void setServerId(Long serverId) {

        this.serverId = serverId;
        Log.v("sajid","setting serverId = "+serverId.toString());
    }

    @Override
    protected List<OrderEntity> doInBackground(Void... params) {
        if(myApiService == null) { // Only do this once
            OrderEntityApi.Builder builder = new OrderEntityApi.Builder(AndroidHttp.newCompatibleTransport(),
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
            Log.v("sajid", "executing list order");
            return myApiService.listForServer(serverId).execute().getItems();
            //return myApiService.list().execute().getItems();
        } catch (IOException e) {
            Log.v("sajid", "executing list order returned empty list");
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<OrderEntity> result) {
        Log.v("sajid","finished executing list order");
        if (result == null) {
            Log.v("sajid", "result is null");
        }
        caller.showOrder(result);
    }
}
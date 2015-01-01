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
import com.tekdi.foodmap.backend.orderEntityApi.OrderEntityApi;
import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.io.IOException;

class ConfirmOrderEndpointAsyncTask extends AsyncTask<Pair<Context, OrderEntity>, Void, String> {
    private static OrderEntityApi myApiService = null;
    private Context context;
    private ListOrderActivity caller;

    ConfirmOrderEndpointAsyncTask(ListOrderActivity caller) {

        this.caller = caller;
        Log.v("sajid", "in ConfirmOrderEndpointAsyncTask setting up caller");
    }

    @Override
    protected String doInBackground(Pair<Context, OrderEntity>... params) {
        if(myApiService == null) {  // Only do this once
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

        context = params[0].first;
        OrderEntity order = params[0].second;
        order.setOrderState(1);

        try {
            OrderEntity orderEntity =myApiService.update(order.getId(),order).execute();
            return "confirm order";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result+" confirmed", Toast.LENGTH_LONG).show();
        Log.v("sajid","order confirmed");

        caller.refreshOrder();
    }
}
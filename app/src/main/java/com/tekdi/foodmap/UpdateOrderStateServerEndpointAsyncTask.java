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

class UpdateOrderStateServerEndpointAsyncTask extends AsyncTask<Pair<Context, OrderEntity>, Void, String> {
    private static OrderEntityApi myApiService = null;
    private Context context;
    private ListOrderServerActivity caller;
    private int orderState;

    UpdateOrderStateServerEndpointAsyncTask(ListOrderServerActivity caller) {

        this.caller = caller;
        Log.v("sajid", "in ConfirmOrderEndpointAsyncTask setting up caller");
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
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
        order.setOrderState(this.orderState);

        try {
            OrderEntity orderEntity =myApiService.update(order.getId(),order).execute();
            return "order processed state="+this.orderState;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        Log.v("sajid","order received");

        caller.onPostExecute();
    }
}
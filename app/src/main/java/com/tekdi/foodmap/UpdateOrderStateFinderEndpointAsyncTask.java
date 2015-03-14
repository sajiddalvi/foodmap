package com.tekdi.foodmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.tekdi.foodmap.backend.orderEntityApi.OrderEntityApi;
import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.io.IOException;

class UpdateOrderStateFinderEndpointAsyncTask extends AsyncTask<Pair<Context, OrderEntity>, Void, String> {
        private static OrderEntityApi myApiService = null;
        private Context context;
        private ListOrderFinderActivity caller;

        UpdateOrderStateFinderEndpointAsyncTask(ListOrderFinderActivity caller) {
            this.caller = caller;
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

            try {
                if (order.getOrderState() == OrderState.ORDER_STATE_SEND) {
                    Log.v("sajid","inserting");
                    OrderEntity orderEntity =myApiService.insert(order).execute();
                    return "order sent";
                } else {
                    Log.v("sajid", "updating");
                    OrderEntity orderEntity = myApiService.update(order.getId(), order).execute();
                    return "order processed state=" + orderEntity.getOrderState();
                }
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("sajid","order updated");
            caller.onPostExecute(result);
        }
}
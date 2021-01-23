package ru.myzakupka.webdivision.myzakupka;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ru.myzakupka.webdivision.myzakupka.model.Dist;
import ru.myzakupka.webdivision.myzakupka.model.Item;
import ru.myzakupka.webdivision.myzakupka.model.Order;
import ru.myzakupka.webdivision.myzakupka.model.Pay;
import ru.myzakupka.webdivision.myzakupka.model.Stat;

public class ListOrdersActivity extends AppCompatActivity {

    private HTTP http = new HTTP();
    List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_orders);
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String s = "";
                try {
                    String response = http.get("https://api.vmeste.market/orders/cart");
                    orders = getOrders(response);
                    Collections.sort(orders, new Comparator<Order>() {
                        @Override
                        public int compare(final Order order1, final Order order2) {
                            return order1.stat.sort - order2.stat.sort;
                        }
                    });
                    Collections.reverse(orders);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            }

            @Override
            protected void onPostExecute(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addOrdersToTable(orders);
                    }
                });
            }
        }.execute();
    }

    private List<Order> getOrders(String response) throws JSONException {
        List<Order> orders = new ArrayList<>();
        JSONObject obj = new JSONObject(response);
        JSONObject data = obj.getJSONObject("data");
        JSONArray objects = new JSONArray(data.getString("orders"));
        for (int i = 0; i < objects.length(); i++) {
            JSONObject item = objects.getJSONObject(i);
            Order order = new Order();
            order.name = item.getString("name");
            order.items = getItems(item);
            order.pays = getPays(data, item);
            order.dists = getDists(data, item);
            order.stat = getStat(data, item);
            orders.add(order);
        }
        return orders;
    }

    private List<Item> getItems(JSONObject order) throws JSONException {
        List<Item> items = new ArrayList<>();
        JSONObject objects = new JSONObject(order.getString("items"));
        Iterator<?> keys = objects.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if ( objects.get(key) instanceof JSONObject ) {
                JSONObject obj = new JSONObject(objects.getString(key));
                Item item = new Item();
                item.id = key;
                item.name = obj.getString("name");
                item.price = obj.getDouble("price");
                item.img = new JSONObject(obj.getString("photo")).getString("url");
                item.qty = obj.getInt("qty");
                items.add(item);
            }
        }
        return items;
    }

    private List<Pay> getPays(JSONObject data, JSONObject order) throws JSONException {
        List<Pay> pays = new ArrayList<>();
        if (order.has("payway")) {
            String name = order.getString("payway");
            Pay pay = getPayByName(data, name);
            pays.add(pay);
        }
        if (order.has("payways")) {
            JSONArray objects = new JSONArray(order.getString("payways"));
            for (int i = 0; i < objects.length(); i++) {
                String name = objects.getString(i);
                Pay pay = getPayByName(data, name);
                pays.add(pay);
            }
        }
        return pays;
    }

    private Pay getPayByName(JSONObject data, String name) throws JSONException {
        JSONObject payways = new JSONObject(data.getString("payways"));
        JSONObject obj = payways.getJSONObject(name);
        Pay pay = new Pay();
        pay.name = obj.getString("name");
        pay.note = obj.getString("note");
        return pay;
    }

    private List<Dist> getDists(JSONObject data, JSONObject order) throws JSONException {
        List<Dist> dists = new ArrayList<>();
        if (order.has("distway")) {
            String name = order.getString("distway");
            Dist dist = getDistByName(data, name);
            dists.add(dist);
        }
        if (order.has("payways")) {
            JSONArray objects = new JSONArray(order.getString("distways"));
            for (int i = 0; i < objects.length(); i++) {
                String name = objects.getString(i);
                Dist dist = getDistByName(data, name);
                dists.add(dist);
            }
        }
        return dists;
    }

    private Dist getDistByName(JSONObject data, String name) throws JSONException {
        JSONObject distways = new JSONObject(data.getString("distways"));
        JSONObject obj = distways.getJSONObject(name);
        Dist dist = new Dist();
        dist.name = obj.getString("name");
        dist.note = obj.getString("note");
        return dist;
    }

    private Stat getStat(JSONObject data, JSONObject order) throws JSONException {
        String name = order.getString("state");
        return getStatByName(data, name);
    }

    private Stat getStatByName(JSONObject data, String name) throws JSONException {
        JSONObject states = new JSONObject(data.getString("states"));
        JSONObject obj = states.getJSONObject(name);
        Stat stat = new Stat();
        stat.name = obj.getString("name");
        stat.sort = obj.getInt("sort");
        return stat;
    }

    private void addOrdersToTable(final List<Order> orders) {
        ListView ordersList = (ListView) findViewById(R.id.ordersList);
        ordersList.setAdapter(new ArrayAdapter<Order>(ListOrdersActivity.this, android.R.layout.simple_list_item_1, orders));
        ordersList.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                        Intent intent = new Intent(ListOrdersActivity.this, OrderActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra("order", gson.toJson(orders.get((int) id)));
                        startActivity(intent);
                    }
                }
        );
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}

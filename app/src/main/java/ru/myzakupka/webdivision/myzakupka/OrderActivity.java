package ru.myzakupka.webdivision.myzakupka;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.myzakupka.webdivision.myzakupka.model.Dist;
import ru.myzakupka.webdivision.myzakupka.model.Item;
import ru.myzakupka.webdivision.myzakupka.model.Order;
import ru.myzakupka.webdivision.myzakupka.model.Pay;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Order order = new Gson().fromJson(getIntent().getStringExtra("order"), Order.class);
        TextView orderName = (TextView) findViewById(R.id.orderName);
        orderName.setText(order.name);

        ItemsAdapter adapterItems = new ItemsAdapter(this, order.items);
        ListView items = (ListView)findViewById(R.id.orderItems);
        items.setAdapter(adapterItems);

        PaysAdapter adapterPays = new PaysAdapter(this, order.pays);
        ListView orderPays = (ListView)findViewById(R.id.orderPays);
        orderPays.setAdapter(adapterPays);
        orderPays.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                    }
                }
        );

        DistsAdapter adapterDists = new DistsAdapter(this, order.dists);
        ListView orderDists = (ListView)findViewById(R.id.orderDists);
        orderDists.setAdapter(adapterDists);
        orderDists.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                    }
                }
        );

        TextView distLabel = (TextView) findViewById(R.id.distLabel);
        distLabel.setText("Пункт выдачи");
        TextView payLabel = (TextView) findViewById(R.id.payLabel);
        payLabel.setText("К оплате " + order.getTotal() + " руб.");
    }

    private class ItemsAdapter extends ArrayAdapter<Item> {

        private  List<Item> items;

        public ItemsAdapter(Context context, List<Item> items) {
            super(context, 0, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.item_layout, null);
            }

            Item item = items.get(position);
            if (item != null) {
                ImageView img = (ImageView) v.findViewById(R.id.imgItem);
                if (img != null) {
                    Picasso.get().load(item.img).into(img);
                }
                TextView name = (TextView) v.findViewById(R.id.nameItem);
                name.setText(item.name);
                TextView qty = (TextView) v.findViewById(R.id.qtyItem);
                qty.setText("Количество: " + item.qty + ", ");
                TextView price = (TextView) v.findViewById(R.id.priceItem);
                price.setText("Итого: " + item.price + " руб.");
            }

            return v;
        }
    }

    private class PaysAdapter extends ArrayAdapter<Pay> {
        private  List<Pay> pays;
        public PaysAdapter(Context context, List<Pay> pays) {
            super(context, 0, pays);
            this.pays = pays;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.pay_layout, null);
            }

            Pay pay = pays.get(position);
            if (pay != null) {
                TextView payName = (TextView) v.findViewById(R.id.payName);
                payName.setText(pay.name);
            }
            return v;
        }
    }

    private class DistsAdapter extends ArrayAdapter<Dist> {
        private  List<Dist> dists;
        public DistsAdapter(Context context, List<Dist> dists) {
            super(context, 0, dists);
            this.dists = dists;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.dist_layout, null);
            }

            Dist dist = dists.get(position);
            if (dist != null) {
                TextView distName = (TextView) v.findViewById(R.id.distName);
                distName.setText(dist.name);
            }
            return v;
        }
    }

    public void showProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}

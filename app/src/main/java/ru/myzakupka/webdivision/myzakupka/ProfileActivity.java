package ru.myzakupka.webdivision.myzakupka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        VKRequest yourRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_50"));
        yourRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONArray repons = response.json.getJSONArray("response");
                    JSONObject obj = repons.getJSONObject(0);
                    String last_name = obj.getString("last_name");
                    String first_name = obj.getString("first_name");
                    String photo_50 = obj.getString("photo_50");
                    TextView username = (TextView) findViewById(R.id.username);
                    username.setText(last_name + " " + first_name);
                    ImageView img = (ImageView) findViewById(R.id.userImg);
                    Picasso.get().load(photo_50).into(img);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logoutVK(View view) {
        VKSdk.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}

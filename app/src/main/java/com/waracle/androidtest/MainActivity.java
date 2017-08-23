package com.waracle.androidtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.waracle.androidtest.models.Cake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CAKES_BUNDLE = "CAKES_BUNDLE";

    private static final String TAG = MainActivity.class.getName();
    private static final String JSON_URL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/" +
            "raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";

    private CakesAdapter mAdapter;
    private List<Cake> mCakes;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCakes = savedInstanceState.getParcelableArrayList(CAKES_BUNDLE);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mAdapter = new CakesAdapter();
        recyclerView.setAdapter(mAdapter);

        loadCakes();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(CAKES_BUNDLE, (ArrayList<Cake>) mCakes);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            mCakes = null;
            loadCakes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadCakes() {
        if (mCakes == null) {
            showProgressBar();
            APIRequest request = new APIRequest(JSON_URL);
            request.loadData(new APIRequest.APIRequestCallback() {
                @Override
                public void onSuccess(byte[] data, String charset) {
                    try {
                        String jsonText = new String(data, charset);
                        JSONArray jsonArray = new JSONArray(jsonText);

                        mCakes = parseCakes(jsonArray);
                        mAdapter.setCakes(mCakes);
                        hideProgressBar();

                    } catch (UnsupportedEncodingException | JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        } else {
            mAdapter.setCakes(mCakes);
        }
    }

    private List<Cake> parseCakes(JSONArray jsonArray) throws JSONException {
        List<Cake> cakes = new ArrayList<>();

        for (int n = 0; n < jsonArray.length(); n++) {
            JSONObject object = (JSONObject) jsonArray.get(n);
            if (object != null) {
                Cake cake = new Cake();
                cake.setDescription(object.getString("desc"));
                cake.setTitle(object.getString("title"));
                cake.setImageUrl(object.getString("image"));
                cakes.add(cake);
            }
        }
        return cakes;
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }
}

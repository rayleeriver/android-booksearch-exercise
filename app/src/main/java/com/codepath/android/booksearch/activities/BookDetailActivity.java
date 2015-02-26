package com.codepath.android.booksearch.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.models.Book;
import com.codepath.android.booksearch.net.BookClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends ActionBarActivity {
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublishers;
    private TextView tvNumPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        // Fetch views
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvPublishers = (TextView) findViewById(R.id.tvPublishers);
        tvNumPages = (TextView) findViewById(R.id.tvNumPages);

        // Extract book object from intent extras

        Book book = (Book) getIntent().getSerializableExtra("book");

        // Use book object to populate data into views
        Picasso.with(getApplicationContext()).load(book.getLargeCoverUrl()).into(ivBookCover);
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());

        //bibkeys=ISBN:9780980200447&jscmd=data&format=json
        fetchBook(book.getIsbn());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void fetchBook(final String isbn) {

        BookListActivity.client.getBook(isbn, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject isbnJson = null;
                    if (response != null) {
                        // Get the docs json array
                        isbnJson = response.getJSONObject("ISBN:" + isbn);
                        JSONArray publishersJson = isbnJson.getJSONArray("publishers");

                        // Parse json array into array of model objects
                        List<String> names = new ArrayList<String>();
                        for (int i = 0; i < publishersJson.length(); i++) {
                            names.add(publishersJson.getJSONObject(i).getString("name"));
                        }
                        tvPublishers.setText(TextUtils.join(", ", names));

                        String numPages = String.valueOf(isbnJson.getInt("number_of_pages"));
                        tvNumPages.setText("Number of pages: " + numPages);
                    }
                } catch (JSONException e) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

}

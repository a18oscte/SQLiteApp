package org.brohede.marcus.sqliteapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    ArrayAdapter adapter;
    boolean vilken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_a){
            vilken = true;
            new FetchData().execute();

            return true;
        }

        if(id == R.id.action_h){
            vilken = false;
            new FetchData().execute();
        }

        return super.onOptionsItemSelected(item);
    }


    /*
        TODO: Create an App that stores Mountain data in SQLite database

        TODO: Schema for the database must include columns for all member variables in Mountain class
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The Main Activity must have a ListView that displays the names of all the Mountains
              currently in the local SQLite database.

        TODO: In the details activity an ImageView should display the img_url
              See: https://developer.android.com/reference/android/widget/ImageView.html

        TODO: The main activity must have an Options Menu with the following options:
              * "Fetch mountains" - Which fetches mountains from the same Internet service as in
                "Use JSON data over Internet" assignment. Re-use code.
              * "Drop database" - Which drops the local SQLite database

        TODO: All fields in the details activity should be EditText elements

        TODO: The details activity must have a button "Update" that updates the current mountain
              in the local SQLite database with the values from the EditText boxes.
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The details activity must have a button "Delete" that removes the
              current mountain from the local SQLite database
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The SQLite database must not contain any duplicate mountain names

     */
    private class FetchData extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            // These two variables need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a Java string.
            String jsonStr = null;

            try {
                // Construct the URL for the Internet service
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in
                // attempting to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }

        }


        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            try {

                JSONArray a = new JSONArray(o);
                MountainReaderDbHelper mountainReaderDBHelper = new MountainReaderDbHelper(getApplicationContext());
                final SQLiteDatabase db = mountainReaderDBHelper.getWritableDatabase();
                db.delete(MountainReaderContract.MountainEntry.TABLE_NAME,null,null);

                for(int i = 0; i < a.length(); i++){
                    JSONObject json1 = (JSONObject) a.get(i);

                    JSONArray arr = new JSONArray("["+json1.getString("auxdata")+ "]");
                    JSONObject img = (JSONObject) arr.get(0);

                    Mountain m = new Mountain(json1.getString("name"),json1.getString("location"), json1.getInt("size"), img.getString("img"),img.getString("url") );
                    // Gets the data repository in write mode


// Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME, m.getName());
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION, m.getLocation());
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGTH, m.getHeight());
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_BILD, m.getBild());
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_URL, m.getUrl());

// Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert(MountainReaderContract.MountainEntry.TABLE_NAME, null, values);




                }


                String[] projection = {
                        //BaseColumns._ID,
                        MountainReaderContract.MountainEntry.COLUMN_NAME_NAME
                        /*MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION,
                        MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGTH,
                        MountainReaderContract.MountainEntry.COLUMN_NAME_BILD,
                        MountainReaderContract.MountainEntry.COLUMN_NAME_URL*/

                };

                String sortOrder;

                if (vilken == true) {
                    sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " DESC";
                }else{
                    sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGTH+ " DESC";
                }

                final Cursor cursor = db.query(
                        MountainReaderContract.MountainEntry.TABLE_NAME,   // The table to query
                        projection,             // The array of columns to return (pass null to get all)
                        null,              // The columns for the WHERE clause
                        null,          // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        sortOrder               // The sort order
                );

                final List itemIds = new ArrayList<>();
                while(cursor.moveToNext()) {
                    String itemId = cursor.getString(
                            cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME));
                    itemIds.add(itemId);
                }
                cursor.close();





                adapter = new ArrayAdapter(getApplicationContext(),R.layout.list_item_textview,R.id.list_item_textview,itemIds);


                ListView myListView = (ListView)findViewById(R.id.my_listview);


                myListView.setAdapter(adapter);

                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String[] projection = {
                                //BaseColumns._ID,
                                //MountainReaderContract.MountainEntry.COLUMN_NAME_NAME
                                MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION,
                                MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGTH,
                                /*MountainReaderContract.MountainEntry.COLUMN_NAME_BILD,
                                MountainReaderContract.MountainEntry.COLUMN_NAME_URL*/

                        };

                        String sortOrder;

                        if (vilken == true) {
                            sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " DESC";
                        }else{
                            sortOrder = MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGTH+ " DESC";
                        }



                        final Cursor cursor = db.query(
                                MountainReaderContract.MountainEntry.TABLE_NAME,   // The table to query
                                projection,             // The array of columns to return (pass null to get all)
                                null,              // The columns for the WHERE clause
                                null,          // The values for the WHERE clause
                                null,                   // don't group the rows
                                null,                   // don't filter by row groups
                                sortOrder               // The sort order
                        );

                        cursor.moveToPosition(position);
                        String tmp = "Location: ";
                        tmp += cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION));
                        tmp +="\nHeight: ";
                        tmp += cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGTH));
                        tmp +=" meters";
                        Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_SHORT).show();
                        cursor.close();
                    }
                });



            }catch (JSONException e) {
                Log.e("brom", "E:" + e.getMessage());
            }

        }
    }
}

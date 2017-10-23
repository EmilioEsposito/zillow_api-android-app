package eesposit.cmu.edu.project4android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;


public class HousingValue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully retrieving the data from Zillow, it
         * can callback to this object with the resulting data values.
         */
        final HousingValue hv = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submitbutton);

        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {

                //get the address string from the  User input text field for address
                String address = ((EditText)findViewById(R.id.addressField)).getText().toString();

                //get the citystatezip string from the User input text field for citystatezip
                String citystatezip = ((EditText)findViewById(R.id.citystatezipField)).getText().toString();

                //create a GetHousingData object
                GetHousingData gh = new GetHousingData();

                //make a query for a housing value
                gh.query(address, citystatezip, hv); // Done asynchronously in another thread.  It calls hv.dataReady() in this thread when complete.

            }
        });
    }

/*
 * This is called by the GetHousingData object's onPostExecute method when the data is ready.
 * It receives data stored in a hashmp and updates the text views accordingly
 */
    public void dataReady(HashMap<String,String> dataMap) {

        //get the houseValue text field
        TextView houseValue = (TextView) findViewById(R.id.houseValueField);
        //update it with the house value
        houseValue.setText(dataMap.get("houseValue"));

        //get the neighborhoodValue text field
        TextView neighborhoodValue = (TextView) findViewById(R.id.neighborhoodValueField);
        //update it with the neighborhoodValue value
        neighborhoodValue.setText(dataMap.get("neighborhoodValue"));

        //get the percDiff text field
        TextView percDiff = (TextView) findViewById(R.id.percDiffField);
        //update it with the percDiff value
        percDiff.setText(dataMap.get("percDiff"));
    }
}

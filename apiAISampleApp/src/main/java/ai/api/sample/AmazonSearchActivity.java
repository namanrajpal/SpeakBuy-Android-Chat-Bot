package ai.api.sample;

import java.math.BigInteger;
import java.util.ArrayList;

import ai.api.sample.AWSECommerceClient;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Errors;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Item;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearch;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Items;
import com.amazon.webservices.awsecommerceservice._2011_08_01.client.AWSECommerceServicePortType_SOAPClient;
import com.leansoft.nano.ws.SOAPServiceCallback;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ai.api.sample.BaseActivity;

public class AmazonSearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchtest);

        Button searchButton = (Button) this.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get shared client
                AWSECommerceServicePortType_SOAPClient client = AWSECommerceClient.getSharedClient();
                client.setDebug(true);

                // Build request
                ItemSearch request = new ItemSearch();
                request.associateTag = "teg"; // seems any tag is ok
                request.shared = new ItemSearchRequest();
                request.shared.searchIndex = "Electronics";
                request.shared.responseGroup = new ArrayList<String>();
                request.shared.responseGroup.add("Images");
                request.shared.responseGroup.add("Small");

                ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
                itemSearchRequest.keywords = ((EditText)findViewById(R.id.keyword_input)).getText().toString();

                itemSearchRequest.browseNode = "13896615011";

                itemSearchRequest.manufacturer = ((EditText)findViewById(R.id.editText)).getText().toString();
                BigInteger min = new BigInteger(((EditText)findViewById(R.id.editText4)).getText().toString());
                BigInteger max = new BigInteger(((EditText)findViewById(R.id.editText5)).getText().toString());
                itemSearchRequest.minimumPrice = min;
                itemSearchRequest.sort = "salesrank";
                itemSearchRequest.maximumPrice = max;



                request.request = new ArrayList<ItemSearchRequest>();
                request.request.add(itemSearchRequest);

                // authenticate the request
                // http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
                AWSECommerceClient.authenticateRequest("ItemSearch");
                // Make API call and register callbacks
                client.itemSearch(request, new SOAPServiceCallback<ItemSearchResponse>() {

                    @Override
                    public void onSuccess(ItemSearchResponse responseObject) {
                        // success handling logic
                        if (responseObject.items != null && responseObject.items.size() > 0) {
                            Items items = responseObject.items.get(0);

                            for(Items i : responseObject.items)
                            {
                                for(Item ii : i.item)
                                {
                                    Log.i("Checking Items",ii.detailPageURL);

                                }
                            }



                            if (items.item != null && items.item.size() > 0) {
                                Item item = items.item.get(0);
                                Toast.makeText(AmazonSearchActivity.this, item.itemAttributes.title, Toast.LENGTH_LONG).show();
                                Log.i("Searchresult",items.item.get(1).itemAttributes.title);
                                Log.i("Searchresult",items.item.get(2).itemAttributes.title);

                            } else {
                                Toast.makeText(AmazonSearchActivity.this, "No result", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                                Errors errors = responseObject.operationRequest.errors;
                                if (errors.error != null && errors.error.size() > 0) {
                                    com.amazon.webservices.awsecommerceservice._2011_08_01.errors.Error error = errors.error.get(0);
                                    Toast.makeText(AmazonSearchActivity.this, error.message, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(AmazonSearchActivity.this, "No result", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(AmazonSearchActivity.this, "No result", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Throwable error, String errorMessage) { // http or parsing error
                        Toast.makeText(AmazonSearchActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSOAPFault(Object soapFault) { // soap fault
                        com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault)soapFault;
                        Toast.makeText(AmazonSearchActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();
                    }

                });
            }

        });
    }


}
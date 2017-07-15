package ai.api.sample;

/***********************************************************************************************************************
 *
 * API.AI Android SDK -  API.AI libraries usage example
 * =================================================
 *
 * Copyright (C) 2015 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************/

import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.support.design.widget.Snackbar;
import android.widget.Toast;


import com.amazon.webservices.awsecommerceservice._2011_08_01.Errors;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Item;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearch;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Items;
import com.amazon.webservices.awsecommerceservice._2011_08_01.client.AWSECommerceServicePortType_SOAPClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.leansoft.nano.ws.SOAPServiceCallback;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ai.api.AIConfiguration;
import ai.api.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIOutputContext;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import ai.api.ui.AIDialog;

public class AIDialogSampleActivity extends BaseActivity implements AIDialog.AIDialogListener {

    private static final String TAG = AIDialogSampleActivity.class.getName();

    private TextView resultTextView;
    private AIDialog aiDialog;
    private String dialogue;
    private Gson gson = GsonFactory.getGson();
    private Handler handler;

    //laptop variables
    String topfeature;
    String brand;
    String OS;
    String minprice;
    String maxprice;


    //Mobile variable
    String mobile_browsenode;
    String mobile_minprice;
    String mobile_maxprice;
    //String mobile_carrier;
    String mobile_os;
    String mobile_brand;

    //Tablet variable
    String tablet_browsenode;
    String tablet_minprice;
    String tablet_maxprice;
    String tablet_os;
    String tablet_brand;
    String tablet_model;


    ArrayList<String> itemsToAdd;
    boolean isDialogEnd;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidialog_sample);

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        resultTextView.setMovementMethod(new ScrollingMovementMethod());


        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDialog = new AIDialog(this, config);
        aiDialog.setResultsListener(this);
        dialogue = "";
        handler = new Handler(Looper.getMainLooper());
        isDialogEnd =false;
        //aiDialog.getAIService().resetContexts();
        itemsToAdd = new ArrayList<String>();
    }

    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResult");




                resultTextView.setText(gson.toJson(response));

                Log.i(TAG,gson.toJson(response));

                Log.i(TAG, "Received success response");

                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());



                Log.i(TAG, "Action: " + result.getAction());
                final String speech = result.getFulfillment().getSpeech();



                Log.i(TAG, "Speech: " + speech);
                dialogue = dialogue +"\n\n"+ Html.fromHtml("<b>User : </b>")+"\n"+ result.getResolvedQuery() + "\n" +Html.fromHtml("<b>Speakbuy : </b>")+"\n"+ speech;
                resultTextView.setText(dialogue);
                TTS.speak(speech);

                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }


                if(result.getAction().equalsIgnoreCase("app.fuking.close"))
                {
                    //do exit here
                    aiDialog.getAIService().stopListening();
                    AIDialogSampleActivity.this.finish();
                }



                laptop(result);

                forMobile(result);

                forTablet(result);

                /*final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }*/

                if(!isDialogEnd){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitforTTStostop();
                    }
                },0500);}
                else{
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            waitforTTStostop();
                        }
                    },3000);
                }



            //run ending here
            }

        });
    }

    private void forTablet(Result result) {

        tablet_browsenode = "1232597011";

        if(result.getAction().equals("tablet.addos")) {
            final HashMap<String, JsonElement> params = result.getParameters();
            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ");
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    if (entry.getKey().equalsIgnoreCase("Tablet_OS") && entry.getValue().toString() != null)
                        tablet_os = entry.getValue().toString();
                }
            }
        }

        if(result.getAction().equals("tablet.addios")) {
            final HashMap<String, JsonElement> params = result.getParameters();
            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ");
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    if (entry.getKey().equalsIgnoreCase("Tablet_OS") && entry.getValue().toString() != null) {
                        tablet_os = entry.getValue().toString();
                        tablet_brand = " apple ";
                    }
                }
            }
        }

        if(result.getAction().equals("tablet.addmodel")) {
            final HashMap<String, JsonElement> params = result.getParameters();
            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ");
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    if (entry.getKey().equalsIgnoreCase("Tablet_apple") && entry.getValue().toString() != null)
                        tablet_model = entry.getValue().toString();
                    else
                        tablet_model = "null";
                }
            }
        }


        //checking for brand
        if(result.getAction().equals("tablet.addbrand")) {

            if(result.getResolvedQuery().contains("Samsung")||result.getResolvedQuery().contains("samsung")||result.getResolvedQuery().contains("samson"))
            {
                tablet_brand = " Samsung ";
            }

            if(result.getResolvedQuery().contains("Amazon")||result.getResolvedQuery().contains("amazon"))
            {
                tablet_brand = " Amazon ";
            }

            if(result.getResolvedQuery().contains("Asus")||result.getResolvedQuery().contains("asus"))
            {
                tablet_brand = " Asus ";
            }

            if(result.getResolvedQuery().contains("HP")||result.getResolvedQuery().contains("hp"))
            {
                tablet_brand = " HP ";
            }

            if(result.getResolvedQuery().contains("Lenovo")||result.getResolvedQuery().contains("lenovo"))
            {
                tablet_brand = " Lenovo ";
            }

           /* final HashMap<String, JsonElement> params = result.getParameters();
            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ");
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    if (entry.getKey().equalsIgnoreCase("Tablet_Brand"))
                        tablet_brand = entry.getValue().toString();
                }
            }*/
            tablet_model = "null";
        }





//        if(result.getAction().equals("tablet.addbrand")) {
//            final HashMap<String, JsonElement> params = result.getParameters();
//            if (params != null && !params.isEmpty()) {
//                Log.i(TAG, "Parameters: ");
//                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
//                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
//                    if (entry.getKey().equalsIgnoreCase("Tablet_Brand") && entry.getValue().toString() != null) {
//                        tablet_brand = entry.getValue().toString();
//                        tablet_model = " null ";
//                    }
//                }
//            }
//        }

        if(result.getAction().equals("tablet.finished")) {
            final HashMap<String, JsonElement> params = result.getParameters();
            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ");
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    if (entry.getKey().equalsIgnoreCase("min"))
                        tablet_minprice = entry.getValue().toString();
                    if (entry.getKey().equalsIgnoreCase("max"))
                        tablet_maxprice = entry.getValue().toString();
//                    if (entry.getKey().equalsIgnoreCase("Tablet_OS"))
//                        tablet_os = entry.getValue().toString();

                }
            }

            if(tablet_maxprice == null)
                tablet_maxprice = " above ";
            if(tablet_minprice == null)
                tablet_minprice = " below ";

            Log.i("final tablet min", tablet_minprice);
            Log.i("final tablet max", tablet_maxprice);
            Log.i("final tablet os", tablet_os);
            Log.i("final tablet model", tablet_model);
            Log.i("final tablet brand", tablet_brand);

            tablet_brand = tablet_brand.substring(1,tablet_brand.length()-1);
            tablet_minprice = tablet_minprice.substring(1,tablet_minprice.length()-1);
            tablet_maxprice = tablet_maxprice.substring(1,tablet_maxprice.length()-1);

            isDialogEnd = true;
            searchTabletAmazon(tablet_brand,tablet_browsenode,tablet_maxprice,tablet_minprice,tablet_os, tablet_model);
        }

    }

    private void searchTabletAmazon(String tablet_brand, String tablet_browsenode, String tablet_maxprice, String tablet_minprice, String tablet_os, String tablet_model) {

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

        if(tablet_model.equals("null") == false) {
            itemSearchRequest.keywords = tablet_model;
            itemSearchRequest.brand = "apple";
        }
        else {
            itemSearchRequest.keywords = tablet_os;
            itemSearchRequest.brand = tablet_brand;
        }

        itemSearchRequest.browseNode = tablet_browsenode; //unlcoked or carrier

//        if(!mobile_carrier.equalsIgnoreCase("none"))
//        {
//            itemSearchRequest.keywords = mobile_os + " " + mobile_carrier;
//        }
//        else{
//            itemSearchRequest.keywords = mobile_os + " unlocked";
//        }

        itemSearchRequest.sort = "salesrank";

        if(tablet_maxprice.contains("above")) tablet_maxprice = "100000";
        if(tablet_minprice.contains("below")) tablet_minprice = "0";

        if(Integer.parseInt(tablet_maxprice)<Integer.parseInt(tablet_minprice))
        {
            tablet_maxprice = "1000000";
        }

        BigInteger min = new BigInteger(tablet_minprice+"00");
        BigInteger max = new BigInteger(tablet_maxprice+"00");
        itemSearchRequest.minimumPrice = min;

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


                    /*for(Items i : responseObject.items)
                    {
                        if(i!=null) {
                            for (Item ii : i.item) {
                                Log.i("Checking Items", ii.detailPageURL);
                            }
                        }
                    }*/

                    if (items.item != null && items.item.size() > 0) {
                        Item item = items.item.get(0);
                        Toast.makeText(AIDialogSampleActivity.this, item.itemAttributes.title, Toast.LENGTH_LONG).show();

                        Log.i("Searchresult",items.item.get(1).itemAttributes.title);
                        Log.i("Searchresult",items.item.get(2).itemAttributes.title);

                        Log.i("SearchresultExtended",items.item.get(0).itemAttributes.title);

                        if(items.item.get(0).itemAttributes.audienceRating!=null)
                            Log.i("SearchresultExtended",items.item.get(0).itemAttributes.audienceRating);
                        else
                            Log.i("SearchresultExtended","rating is null");

                        if(items.item.get(0).itemAttributes.manufacturer!=null)
                            Log.i("SearchresultExtended",items.item.get(0).itemAttributes.manufacturer);
                        else
                            Log.i("SearchresultExtended","manufacturer is null");




                        if(items.item.get(0).itemAttributes.listPrice!=null)
                            Log.i("SearchresultExtended",items.item.get(0).itemAttributes.listPrice.toString());



                        TTS.speakadd("I think the best product is " + item.itemAttributes.title + ". Dont worry! I have added links for top 3 product to your Speakbuy app Do you want to do another search?, say Yes or No");
                        /*TTS.speakadd(item.itemAttributes.title);
                        TTS.speakadd("Dont worry! I have added links for top 3 product to your Speakbuy app");
                        TTS.speakadd("");*/
                        //code to save links
                        int x=0;



                        try{


                            for(Items i : responseObject.items)
                            {
                                for(Item ii : i.item)
                                {

                                    Log.i("Checking Items",ii.detailPageURL);
                                    if(x<3)
                                    {
                                        //ReminderActivity.itemsAdapter.add(ii.detailPageURL);
                                        itemsToAdd.add(ii.detailPageURL);
                                    }
                                    x++;
                                }
                            }}
                        catch (NullPointerException e)
                        {

                            Toast.makeText(AIDialogSampleActivity.this, "Some error Occured", Toast.LENGTH_LONG).show();
                        }

                        File filesDir = getFilesDir();
                        File todoFile = new File(filesDir, "todo.txt");
                        try {

                            FileUtils.writeLines(todoFile, itemsToAdd , true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }





                        //TTS.speakadd(items.item.get(1).itemAttributes.title);
                        //TTS.speakadd("Next item is :");
                        //TTS.speakadd(items.item.get(2).itemAttributes.title);


                    } else {
                        Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                        TTS.speakadd("Sorry, I found no phones in that price range");
                        TTS.speakadd("Do you wanna use SpeakBuy again?");
                    }

                } else {
                    if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                        Errors errors = responseObject.operationRequest.errors;
                        if (errors.error != null && errors.error.size() > 0) {
                            com.amazon.webservices.awsecommerceservice._2011_08_01.errors.Error error = errors.error.get(0);
                            Toast.makeText(AIDialogSampleActivity.this, error.message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Throwable error, String errorMessage) { // http or parsing error
                Toast.makeText(AIDialogSampleActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSOAPFault(Object soapFault) { // soap fault
                com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault)soapFault;
                Toast.makeText(AIDialogSampleActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();
            }

        });

    }

    private void forMobile(Result result) {


        /*String mobile_browsenode;
        String mobile_minprice;
        String mobile_maxprice;
        String mobile_carrier;
        String mobile_os;
        String mobile_brand;*/


//        if(result.getAction().equals("phone.carrier"))
//        {
//            //browse node for carrier
//            mobile_browsenode = "2407748011";
//
//        }
//        if(result.getAction().equals("phone.unlocked"))
//        {
//            //browse node for unlocked
//            mobile_browsenode = "2407749011";
//            mobile_carrier = " none ";
//        }


        mobile_browsenode = "2407749011";


//        if(result.getAction().equals("phone.carrier.selected"))
//        {
//            final HashMap<String, JsonElement> params = result.getParameters();
//            if (params != null && !params.isEmpty()) {
//                Log.i(TAG, "Parameters: ");
//                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
//                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
//                    if (entry.getKey().equalsIgnoreCase("phone_addcarrier"))
//                        mobile_carrier = entry.getValue().toString();
//                }
//            }
//        }


        //checking for OS
        if(result.getAction().equals("phone.windows"))
        {
            mobile_os = "Windows";
        }
        if(result.getAction().equals("phone.android"))
        {
            mobile_os = "Android";
        }
//        if(result.getAction().equals("phone.blackberry"))
//        {
//            mobile_os = "Blackberry";
//            mobile_brand = " Blackberry ";
//            //set bran manually
//        }
        if(result.getAction().equals("phone.iOS"))
        {
            mobile_os = "IOS";
            mobile_brand = " Apple ";
            //set brand manually
        }

        //checking for brand
        if(result.getAction().equals("phone.brand")) {

            final HashMap<String, JsonElement> params = result.getParameters();
            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ");
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    if (entry.getKey().equalsIgnoreCase("phone_brand"))
                        mobile_brand = entry.getValue().toString();
                }
            }

            if(result.getResolvedQuery().contains("Nokia")||result.getResolvedQuery().contains("nokia"))
            {
                mobile_brand = " Nokia ";
            }

            if(result.getResolvedQuery().contains("HTC")||result.getResolvedQuery().contains("htc")||result.getResolvedQuery().contains("HBC")||result.getResolvedQuery().contains("HDC"))
            {
                mobile_brand = " HTC ";
            }

            if(result.getResolvedQuery().contains("Samsung")||result.getResolvedQuery().contains("samsung"))
            {
                mobile_brand = " Samsung ";
            }

            if(result.getResolvedQuery().contains("LG")||result.getResolvedQuery().contains("lg"))
            {
                mobile_brand = " LG ";
            }

            if(result.getResolvedQuery().contains("Motorola")||result.getResolvedQuery().contains("motorola"))
            {
                mobile_brand = " Motorola ";
            }


        }

        if(result.getAction().equals("phone.price.finished"))
        {
            final HashMap<String, JsonElement> params = result.getParameters();
            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ");
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    if (entry.getKey().equalsIgnoreCase("price_start"))
                        mobile_minprice = entry.getValue().toString();
                    if (entry.getKey().equalsIgnoreCase("price_end"))
                        mobile_maxprice = entry.getValue().toString();
                }
            }

            if(mobile_maxprice == null)
                mobile_maxprice = " above ";
            if(mobile_minprice == null)
                mobile_minprice = " below ";

            //checking final values
            Log.i("final mobile params",mobile_brand );
            Log.i("final mobile params",mobile_browsenode );
            //Log.i("final mobile params",mobile_carrier );
            Log.i("final mobile params",mobile_maxprice );
            Log.i("final mobile params",mobile_minprice );
            Log.i("final mobile params",mobile_os );

            mobile_brand = mobile_brand.substring(1,mobile_brand.length()-1);
            mobile_minprice = mobile_minprice.substring(1,mobile_minprice.length()-1);
            mobile_maxprice = mobile_maxprice.substring(1,mobile_maxprice.length()-1);
            //mobile_carrier = mobile_carrier.substring(1,mobile_carrier.length()-1);

            isDialogEnd = true;
            searchMobileAmazon(mobile_brand,mobile_browsenode,mobile_maxprice,mobile_minprice,mobile_os);


        }







    }

    private void searchMobileAmazon(String mobile_brand, String mobile_browsenode, String mobile_maxprice, String mobile_minprice, String mobile_os) {


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


        itemSearchRequest.keywords = mobile_os;

        itemSearchRequest.browseNode = mobile_browsenode; //unlcoked or carrier

//        if(!mobile_carrier.equalsIgnoreCase("none"))
//        {
//            itemSearchRequest.keywords = mobile_os + " " + mobile_carrier;
//        }
//        else{
//            itemSearchRequest.keywords = mobile_os + " unlocked";
//        }

        itemSearchRequest.sort = "salesrank";

        itemSearchRequest.brand = mobile_brand;
        if(mobile_maxprice.contains("above")) mobile_maxprice = "100000";
        if(mobile_minprice.contains("below")) mobile_minprice = "0";


        if(Integer.parseInt(mobile_maxprice)<Integer.parseInt(mobile_minprice))
        {
            mobile_maxprice = "1000000";
        }
        BigInteger min = new BigInteger(mobile_minprice+"00");
        BigInteger max = new BigInteger(mobile_maxprice+"00");
        itemSearchRequest.minimumPrice = min;

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


                    /*for(Items i : responseObject.items)
                    {
                        if(i!=null) {
                            for (Item ii : i.item) {
                                Log.i("Checking Items", ii.detailPageURL);
                            }
                        }
                    }*/

                    if (items.item != null && items.item.size() > 0) {
                        Item item = items.item.get(0);
                        Toast.makeText(AIDialogSampleActivity.this, item.itemAttributes.title, Toast.LENGTH_LONG).show();

                        /*
                        Log.i("Searchresult",items.item.get(1).itemAttributes.title);
                        Log.i("Searchresult",items.item.get(2).itemAttributes.title);
*/
                        Log.i("SearchresultExtended",items.item.get(0).itemAttributes.title);

                        if(items.item.get(0).itemAttributes.audienceRating!=null)
                            Log.i("SearchresultExtended",items.item.get(0).itemAttributes.audienceRating);
                        else
                            Log.i("SearchresultExtended","rating is null");

                        if(items.item.get(0).itemAttributes.manufacturer!=null)
                            Log.i("SearchresultExtended",items.item.get(0).itemAttributes.manufacturer);
                        else
                            Log.i("SearchresultExtended","manufacturer is null");




                        if(items.item.get(0).itemAttributes.listPrice!=null)
                            Log.i("SearchresultExtended",items.item.get(0).itemAttributes.listPrice.toString());



                        TTS.speakadd("I think the best product is ");
                        TTS.speakadd(item.itemAttributes.title);
                        TTS.speakadd("Dont worry! I have added links for top 3 product to your Speakbuy app");
                        TTS.speakadd("Do you want to continue using speakbuy?");
                        //code to save links
                        int x=0;



                        try{


                            for(Items i : responseObject.items)
                            {
                                for(Item ii : i.item)
                                {

                                    Log.i("Checking Items",ii.detailPageURL);
                                    if(x<3)
                                    {
                                        //ReminderActivity.itemsAdapter.add(ii.detailPageURL);
                                        itemsToAdd.add(ii.detailPageURL);
                                    }
                                    x++;
                                }
                            }}
                        catch (NullPointerException e)
                        {

                            Toast.makeText(AIDialogSampleActivity.this, "Some error Occured", Toast.LENGTH_LONG).show();
                        }

                        File filesDir = getFilesDir();
                        File todoFile = new File(filesDir, "todo.txt");
                        try {

                            FileUtils.writeLines(todoFile, itemsToAdd , true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }





                        //TTS.speakadd(items.item.get(1).itemAttributes.title);
                        //TTS.speakadd("Next item is :");
                        //TTS.speakadd(items.item.get(2).itemAttributes.title);


                    } else {
                        Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                        TTS.speakadd("Sorry, I found no phones in that price range");
                        TTS.speakadd("Do you want to continue using speakbuy?");
                    }

                } else {
                    if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                        Errors errors = responseObject.operationRequest.errors;
                        if (errors.error != null && errors.error.size() > 0) {
                            com.amazon.webservices.awsecommerceservice._2011_08_01.errors.Error error = errors.error.get(0);
                            Toast.makeText(AIDialogSampleActivity.this, error.message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Throwable error, String errorMessage) { // http or parsing error
                Toast.makeText(AIDialogSampleActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSOAPFault(Object soapFault) { // soap fault
                com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault)soapFault;
                Toast.makeText(AIDialogSampleActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();
            }

        });








    }


    //code to catch laptop from api.ai
    private void laptop(Result result) {

        if(result.getAction().equals("laptop.addbrand"))
        {
            boolean found;
            found = false;
            if(result.getResolvedQuery().contains("HP")||result.getResolvedQuery().contains("hp"))
            {
                brand = "HP";
                found = true;
            }


            if(result.getResolvedQuery().contains("Dell")||result.getResolvedQuery().contains("dell"))
            {brand = "Dell";
                found = true;
            }


            if(result.getResolvedQuery().contains("Samsung")||result.getResolvedQuery().contains("samsung"))
            {       brand = "samsung";

                found = true;
            }

            if(result.getResolvedQuery().contains("Asus")||result.getResolvedQuery().contains("asus"))
            {       brand = "asus";

                found = true;
            }

            //brand = result.getResolvedQuery();
            if(found == false) {
                final HashMap<String, JsonElement> params = result.getParameters();

                if (params != null && !params.isEmpty()) {

                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {

                        Log.i(TAG + "getting brand", String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                        if (entry.getKey().equalsIgnoreCase("laptop_brand"))
                            brand = entry.getValue().toString();
                    }
                }
            }
        }

        if(result.getAction().equals("laptop.addtopfeature"))
        {
            //topfeature = result.getParameters().get("Laptop");
            if(result.getResolvedQuery().contains("gaming")||result.getResolvedQuery().contains("Gaming"))
                topfeature = "Gaming";


            if(result.getResolvedQuery().contains("Business")||result.getResolvedQuery().contains("business"))
                topfeature = "business";

            if(result.getResolvedQuery().contains("Multimedia")||result.getResolvedQuery().contains("multimedia"))
                topfeature = "multimedia";

            if(result.getResolvedQuery().contains("Everyday")||result.getResolvedQuery().contains("everyday"))
                topfeature = "everyday";
                    /*final HashMap<String, JsonElement> params = result.getParameters();

                    if (params != null && !params.isEmpty()) {

                        for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {

                            Log.i(TAG + "getting top", String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                            if(entry.getKey().equalsIgnoreCase("laptop"))topfeature = entry.getValue().toString();
                        }
                    }*/
        }


        //if action is finished adding
        if(result.getAction().equals("laptop.finishadding"))
        {
            isDialogEnd = true;
            AIOutputContext laptop = result.getContext("laptop");
            final Map<String, JsonElement> params = laptop.getParameters();

            if (params != null && !params.isEmpty()) {
                Log.i(TAG, "Parameters: ");
                for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                    Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    //if(entry.getKey().equalsIgnoreCase("laptop")) topfeature = entry.getValue().toString();
                    //if(entry.getKey().equalsIgnoreCase("laptop_brand")) brand = entry.getValue().toString();
                    if (entry.getKey().equalsIgnoreCase("operating_system"))
                        OS = entry.getValue().toString();
                    if (entry.getKey().equalsIgnoreCase("price_start"))
                        minprice = (entry.getValue().toString());
                    if (entry.getKey().equalsIgnoreCase("price_end"))
                        maxprice = (entry.getValue().toString());
                }

            }
            Log.i("Final params",topfeature);

            if(OS.length()>2)
            {
                OS= OS.substring(1,OS.length()-1);
                Log.i("Final params",OS);
                if(OS.contains("Mac")||OS.contains("Michael"))
                {
                    brand = "Apple";
                }
            }
            Log.i("Final params",brand);


            if(minprice!=null&&minprice.length()>2)
            {
                minprice= minprice.substring(1,minprice.length()-1);
                Log.i("Final params",minprice);
            }else
            {
                minprice = "000";
            }


            if(maxprice!=null&&maxprice.length()>2)
            {
                maxprice= maxprice.substring(1,maxprice.length()-1);
                Log.i("Final params",maxprice);
            }else
            {
                maxprice = "100000";
            }



            Toast.makeText(AIDialogSampleActivity.this, "Searching for laptops now", Toast.LENGTH_SHORT).show();
            //do search here from amazon

            serachLaptopAmazon(topfeature,brand,minprice,maxprice);


        }

    }

    private void waitforTTStostop() {

        while(true)
        {
            if(TTS.isPlaying())
            {

            }else
            {
  /*              handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
  */                      aiDialog.showAndListen();
/*
                    }
                },0400);*/
                break;

            }
        }


    }



    private void serachLaptopAmazon(String keyword, String manufacturer, String mini, String maxi ) {


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

        if(keyword.charAt(0)=='g'||keyword.charAt(0)=='G'||keyword.equalsIgnoreCase("gaining")||keyword.equalsIgnoreCase("giving")||keyword.equalsIgnoreCase("gimme")||keyword.equalsIgnoreCase("jimme")||keyword.equalsIgnoreCase("MN"))
        {
            keyword = "gaming";
        }

        itemSearchRequest.keywords = keyword;

        itemSearchRequest.browseNode = "13896615011";
        itemSearchRequest.browseNode = "565108";

        itemSearchRequest.sort = "salesrank";

        if(manufacturer.equalsIgnoreCase("HBO")||manufacturer.equalsIgnoreCase("FB")||manufacturer.equalsIgnoreCase("HD")||manufacturer.equalsIgnoreCase("XP")||manufacturer.length()==2||manufacturer.equalsIgnoreCase("xb"))
        {
            Log.i("changing ",manufacturer);
            manufacturer = "HP";
        }

        if(manufacturer.equalsIgnoreCase("adelle")||manufacturer.equalsIgnoreCase("adele")||manufacturer.equalsIgnoreCase("Bell")||manufacturer.equalsIgnoreCase("Den"))
        {
            manufacturer = "Dell";
        }



        itemSearchRequest.manufacturer = manufacturer;
        BigInteger min = new BigInteger(mini+"00");
        BigInteger max = new BigInteger(maxi+"00");


        itemSearchRequest.minimumPrice = min;

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


                    /*for(Items i : responseObject.items)
                    {
                        if(i!=null) {
                            for (Item ii : i.item) {
                                Log.i("Checking Items", ii.detailPageURL);
                            }
                        }
                    }*/

                    if (items.item != null && items.item.size() > 0) {
                        Item item = items.item.get(0);
                        Toast.makeText(AIDialogSampleActivity.this, item.itemAttributes.title, Toast.LENGTH_LONG).show();

                        Log.i("Searchresult",items.item.get(1).itemAttributes.title);
                        Log.i("Searchresult",items.item.get(2).itemAttributes.title);

                        Log.i("SearchresultExtended",items.item.get(0).itemAttributes.title);

                        if(items.item.get(0).itemAttributes.audienceRating!=null)
                        Log.i("SearchresultExtended",items.item.get(0).itemAttributes.audienceRating);
                        else
                        Log.i("SearchresultExtended","rating is null");

                        if(items.item.get(0).itemAttributes.manufacturer!=null)
                            Log.i("SearchresultExtended",items.item.get(0).itemAttributes.manufacturer);
                        else
                            Log.i("SearchresultExtended","manufacturer is null");




                        if(items.item.get(0).itemAttributes.listPrice!=null)
                            Log.i("SearchresultExtended",items.item.get(0).itemAttributes.listPrice.toString());



                        TTS.speakadd("I think the best product is ");
                        TTS.speakadd(item.itemAttributes.title);
                        TTS.speakadd("Dont worry! I have added links for top 3 product to your Speakbuy app");
                        TTS.speakadd("Do you want to continue using speakbuy?");

                        //code to save links
                        int x=0;



                        try{


                        for(Items i : responseObject.items)
                        {
                            for(Item ii : i.item)
                            {

                                Log.i("Checking Items",ii.detailPageURL);
                                if(x<3)
                                {
                                    //ReminderActivity.itemsAdapter.add(ii.detailPageURL);
                                    itemsToAdd.add(ii.detailPageURL);
                                }
                                x++;
                            }
                        }}
                        catch (NullPointerException e)
                        {

                            Toast.makeText(AIDialogSampleActivity.this, "Some error Occured", Toast.LENGTH_LONG).show();
                        }

                        File filesDir = getFilesDir();
                        File todoFile = new File(filesDir, "todo.txt");
                        try {

                            FileUtils.writeLines(todoFile, itemsToAdd , true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }





                        //TTS.speakadd(items.item.get(1).itemAttributes.title);
                        //TTS.speakadd("Next item is :");
                        //TTS.speakadd(items.item.get(2).itemAttributes.title);


                    } else {
                        Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                        TTS.speakadd("Sorry, I found no products in that price range");
                        TTS.speakadd("Do you want to continue using speakbuy?");
                    }

                } else {
                    if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                        Errors errors = responseObject.operationRequest.errors;
                        if (errors.error != null && errors.error.size() > 0) {
                            com.amazon.webservices.awsecommerceservice._2011_08_01.errors.Error error = errors.error.get(0);
                            Toast.makeText(AIDialogSampleActivity.this, error.message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AIDialogSampleActivity.this, "No result", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Throwable error, String errorMessage) { // http or parsing error
                //Toast.makeText(AmazonSearchActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSOAPFault(Object soapFault) { // soap fault
                com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault)soapFault;
                //Toast.makeText(AmazonSearchActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();
            }

        });




    }



    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(error.toString());
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText("");
            }
        });
    }

    @Override
    protected void onPause() {
        if (aiDialog != null) {
            aiDialog.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (aiDialog != null) {
            aiDialog.resume();
        }
        super.onResume();
    }

    public void buttonListenOnClick(final View view) {
        TTS.stop();
        aiDialog.showAndListen();
        ViewGroup.LayoutParams params = resultTextView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

    }
}

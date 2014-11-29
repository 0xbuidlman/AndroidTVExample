package com.exasmart.androidtvexample;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nsaid on 05/10/14.
 */
public class RetrieveFeedTask extends AsyncTask<String, Void, RetrieveFeedTask.Response> {


    private final Callback callback;
    private Exception exception;
    private Map<Long, String> streamingLinkMapping = new HashMap<Long, String>();


    public RetrieveFeedTask(Callback callback) {
        this.callback = callback;
        // add streaming url to channelId mapping here:
        //streamingLinkMapping.put(channelid, "http://steaming-url.com");

    }

    protected Response doInBackground(String... urls) {
        String channels = null;
        String streaminglinks = null;
        try {
            String url= urls[0];
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            // add authentication header here
            channels = EntityUtils.toString(client.execute(request).getEntity());
            url= urls[1];
            String bodyEntity = "contentUri=mobiletv-streaming/vdfde-ch27,mobiletv-streaming/vdfde-ch28,mobiletv-streaming/vdfde-ch61,mobiletv-streaming/vdfde-ch31,mobiletv-streaming/vdfde-ch30,mobiletv-streaming/vdfde-ch29,mobiletv-streaming/vdfde-ch68,mobiletv-streaming/vdfde-ch64,mobiletv-streaming/vdfde-ch69,mobiletv-streaming/vdfde-ch66,mobiletv-streaming/vdfde-ch65,mobiletv-streaming/vdfde-ch67,mobiletv-streaming/vdfde-ch70,mobiletv-streaming/vdfde-ch71,mobiletv-streaming/vdfde-ch72,mobiletv-streaming/vdfde-ch60,mobiletv-streaming/vdfde-ch22,mobiletv-streaming/vdfde-ch36,mobiletv-streaming/vdfde-ch34,mobiletv-streaming/vdfde-ch35,mobiletv-streaming/vdfde-ch33,mobiletv-streaming/vdfde-ch32,mobiletv-streaming/vdfde-ch6,mobiletv-streaming/vdfde-ch73&contentOwner=content_vfde_mobiletv_channels&servicePoser=vodafonede_mobiletv_nonerotic&servicePoserId=45037080&rtmp=false&hls=true&connectionSpeed=4G";
//            String bodyEntity = "contentUri=mobiletv-streaming/vdfde-ch27&contentOwner=content_vfde_mobiletv_channels&servicePoser=vodafonede_mobiletv_nonerotic&servicePoserId=45037080&rtmp=false&hls=true&connectionSpeed=4G";
            HttpPost requestPost = new HttpPost(url);
            // add authentication headers here
            requestPost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestPost.setHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2; de-ch; HTC Desire HD");
            requestPost.setEntity(new StringEntity(bodyEntity));
            streaminglinks = EntityUtils.toString(client.execute(requestPost).getEntity());


        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
        }
        return new Response(channels,streaminglinks);
    }

    protected void onPostExecute(Response response) {
        if (exception== null) {
            Log.d("nsaid", response.channels);
            Log.d("nsaid", response.streamingLinks);
            callback.onSuccess(transform(response));
        }
    }

    private List<Channel> transform(Response response) {
        List<Channel> list = new ArrayList<Channel>();
        try {
            JSONArray channels = new JSONArray(response.getChannels());
            JSONArray streamingLinks = new JSONArray(response.getStreamingLinks());

            for (int i = 0 ; i < channels.length(); i++){
                JSONObject channelObj = (JSONObject)channels.get(i);
                Channel ch = new Channel();
                ch.setBackgroundImageUrl(channelObj.getString("channelLogo"));
                ch.setCardImageUrl(channelObj.getString("previewImage") + "&f=f9");
                JSONObject temp = ((JSONObject)channelObj.getJSONArray("shows").get(0));
                ch.setDescription(temp.getJSONObject("description").getString("content"));
                ch.setStudio(temp.getJSONObject("formatTitle").getString("content"));
                ch.setId(Long.valueOf(channelObj.getString("id")));
                ch.setTitle(channelObj.getString("name"));

                ch.setVideoUrl(streamingLinkMapping.get(ch.getId()));


//                for (int j = 0 ; j < streamingLinks.length(); j++){
//                    JSONObject streamingLinkObj = (JSONObject)streamingLinks.get(j);
//                    if (streamingLinkObj.getString("contentUri").equals("mobiletv-streaming/vdfde-ch" + ch.getId())){
//                        ch.setVideoUrl(streamingLinkObj.getString("url"));
//                    }
//                }
                list.add(ch);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    interface Callback {
        void onSuccess(List<Channel> list);
    }

    class Response{
        Response(String channels, String streamingLinks) {
            this.channels = channels;
            this.streamingLinks = streamingLinks;
        }

        public String getChannels() {
            return channels;
        }

        public String getStreamingLinks() {
            return streamingLinks;
        }

        private final String channels;
        private final String streamingLinks;

    }
}

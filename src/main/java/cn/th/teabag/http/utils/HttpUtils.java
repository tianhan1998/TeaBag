package cn.th.teabag.http.utils;

import cn.th.teabag.context.Path;
import cn.th.teabag.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HttpUtils {
    private static final CloseableHttpClient client;
    private static final Header[] headers;
    private static final Header[] ppHeaders;
    private static final String TOKEN_URL="http://osu.ppy.sh/oauth/token";
    private static final String USER_INFO_URL="http://osu.ppy.sh/api/v2/users/";
    private static final String ENCODE="utf-8";
    private static final String TOKEN_JSON="{\"grant_type\":\"client_credentials\",\"client_id\":\"4313\",\"client_secret\":\"6xtjsPUsItTubj4cxiMnGgdPczlzWW4rih1esYlp\",\"scope\":\"public\"}";
    private static final ObjectMapper objectMapper=new ObjectMapper();
    private static final String BASIC_PR_RECENT_URL="https://osu.ppy.sh/api/v2/users//scores/recent?limit=1";
    private static final String PP_API_URL="https://api.tillerino.org/beatmapinfo";
    private static final String BEATMAP_INFO_URL="https://osu.ppy.sh/api/v2/beatmaps/";
    private static final String PP_API_KEY="1d34f9f932b648c19cd596f49c4e65e5";
    private static final String BG_FILE_URL= Path.BEATMAPS_BG_PATH;
    private static final String COVER_FILE_URL= Path.BEATMAPSETS_COVER_PATH;
    private static final String BG_DOWNLOAD_URL="http://bloodcat.com/osu/i/";

    static {
        client = getHttpClient();
        headers = new Header[3];
        ppHeaders=new Header[2];
        ppHeaders[0] = new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
        ppHeaders[1]=new BasicHeader("Content-Type","application/json");
        headers[0] = new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
        headers[1]=new BasicHeader("Content-Type","application/json");
        headers[2]=new BasicHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI0MzEzIiwianRpIjoiM2Q1OWJmOTJmNzNhNzAyY2FmNjU1YTUwMjgyZTgzODU2MTYzMDFlYjI4MzNhMDJhMmVlNTM4ZGYxMmZjOTE4ZGY2MGIxMTE4NjkyM2MzMWMiLCJpYXQiOjE2MDkyMjE0MjUsIm5iZiI6MTYwOTIyMTQyNSwiZXhwIjoxNjA5MzA3ODI1LCJzdWIiOiIiLCJzY29wZXMiOlsicHVibGljIl19.IjRYEQrLp8Wj6Z0Pee3rY7ECVZJHEtBz7hZk7slL2AhEi3kp0n1de3kIeIPHwzua5SBaeCaplcsm5FRuKuBpI4HweEU91LHmopc6_l0DBL5i1F85mKb7Dy-bUC3_3FyZICJkebEdzmdT9GyyeOA2B9zwezRWlW3-hjx15_btqz2_TnWuCqCEuwSIzztVINZOLuYUOdo8V2BbGKdCeXGnY31mRDFIbnVKJVGrDA0GO0Uo0MveGKpTVRI5LbIS3sqvBZdgc-EuqGXBRE41hCZ8wDaE0OgYh5-hKYabaUN7t6s47L9DqMIyQzaXAS4iLvguRhx2_5d9P4_pSyUyk5r7NaSQTL9xSZIk5DcCV1_-zu8pv-Ur17tjmkRsYINn436KZs5JSPzisCQ8yK89k0PIaluq9ssjbeWBdl3pt8k4aLe7Nm82lyjy5MxOTlEpyZijmMlEaBXzHkELit74iI5vxFHpT-iQpY-FngJzCGwJzxXF0s-f3Pzc6OeUL4p98vaRdacYT82xdg2i7qnipIaQkrKgrCanWdhYgx2BDdCOgYzOry_fSUI0BhEV09CIG3G7YG18SJjo-xJeK1fCjLL1qO8gmesOKKYLNKFZxbfWLqT1nQDdmbZO7dHTNgNSMub6OGbokf0-Wh5RBPpY9wGFlcoafbe1lRiDf5O8BLsrnO0");
    }

    private static CloseableHttpClient getHttpClient(){
        return HttpClientBuilder.create().setRetryHandler((e, i, httpContext) -> i < 3)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
    }

    private static File download(String url,String filePath,String fileName) {
        try {
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = client.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            File file = new File(filePath +File.separator+fileName);
            file.getParentFile().mkdirs();
            FileOutputStream fileout = new FileOutputStream(file);
            byte[] buffer = new byte[10*1024];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer, 0, ch);
            }
            is.close();
            fileout.flush();
            fileout.close();
//            ImageUtils.changeSize(file.getPath());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File downloadBgFromBloodCat(Long bid,String fileName){
        return download(BG_DOWNLOAD_URL+bid,BG_FILE_URL,bid.toString()+".jpg");
    }

    public static File downloadCover(String url,Long beatMapSetsId){
        return download(url,COVER_FILE_URL,beatMapSetsId+".jpg");
    }

    public static String getBeatmapInfoUrl(Long bid){
        return BEATMAP_INFO_URL +bid;
    }
    public static Long getBeatMapCombo(Long bid) throws URISyntaxException {
        HttpGet get=getHttpGet(new URIBuilder(getBeatmapInfoUrl(bid)));
        try{
            CloseableHttpResponse execute = client.execute(get);
            HttpEntity entity = execute.getEntity();
            String ppString = EntityUtils.toString(entity, ENCODE);
            return objectMapper.readTree(ppString).get("max_combo").asLong();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode getPPJson(Long bid,Double acc) throws URISyntaxException {
        HttpGet get;
        List<NameValuePair> accList=new ArrayList<NameValuePair>(){{
            this.add(new BasicNameValuePair("beatmapid",bid.toString()));
            this.add(new BasicNameValuePair("wait","1000"));
            this.add(new BasicNameValuePair("k",PP_API_KEY));
            this.add(new BasicNameValuePair("acc","0.95"));
            this.add(new BasicNameValuePair("acc","0.97"));
            this.add(new BasicNameValuePair("acc","0.98"));
            this.add(new BasicNameValuePair("acc","0.99"));
            this.add(new BasicNameValuePair("acc","1"));
            this.add(new BasicNameValuePair("acc",acc.toString()));
        }};
        get = getHttpGet(new URIBuilder(PP_API_URL),accList);
        if(get!=null) {
            try {
                get.setHeaders(ppHeaders);
                CloseableHttpResponse execute = client.execute(get);
                HttpEntity entity = execute.getEntity();
                String ppString = EntityUtils.toString(entity, ENCODE);
                return objectMapper.readTree(ppString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getRecentUrl(Long uid){
        return new StringBuilder(BASIC_PR_RECENT_URL).insert(32,uid.toString()).append("&include_fails=1").toString();
    }

    public static String getPrUrl(Long uid){
        return new StringBuilder(BASIC_PR_RECENT_URL).insert(32,uid.toString()).toString();
    }

    public static JsonNode getRecentJson(Long uid) throws URISyntaxException {
        HttpGet get=getHttpGet(new URIBuilder(getRecentUrl(uid)));
        try{
            CloseableHttpResponse execute = client.execute(get);
            HttpEntity entity = execute.getEntity();
            String recentJson = EntityUtils.toString(entity, ENCODE);
            return objectMapper.readTree(recentJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode getPrJson(Long uid) throws URISyntaxException {
        HttpGet get=getHttpGet(new URIBuilder(getPrUrl(uid)));
            try {
                CloseableHttpResponse execute = client.execute(get);
                HttpEntity entity = execute.getEntity();
                String recentJson = EntityUtils.toString(entity, ENCODE);
                return objectMapper.readTree(recentJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

    public static Long getUidByUserName(String userName) throws URISyntaxException, IOException, UserNotFoundException {
        HttpGet httpGet = getHttpGet(new URIBuilder(encodeURIComponent(USER_INFO_URL + userName)));
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String userInfo = EntityUtils.toString(entity, ENCODE);
            JsonNode jsonNode = objectMapper.readTree(userInfo);
            return jsonNode.get("id").asLong();
        }catch(Exception e){
            throw new UserNotFoundException("网络有问题，请稍后再试...");
        }
    }

    public static void getToken() throws URISyntaxException {
        HttpPost httpPost = getHttpPost(new URIBuilder(TOKEN_URL));
        if(httpPost!=null) {
            try {
                Header[] header=new Header[2];
                header[0] =new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
                header[1]=new BasicHeader("Content-Type","application/json");
                httpPost.setHeaders(header);
                StringEntity stringEntity = new StringEntity(TOKEN_JSON, ENCODE);
                stringEntity.setContentEncoding("UTF-8");
                httpPost.setEntity(stringEntity);
                CloseableHttpResponse response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String tokenString = EntityUtils.toString(entity, ENCODE);
                JsonNode jsonNode = objectMapper.readTree(tokenString);
                String token=jsonNode.get("access_token").asText();
                headers[2]=new BasicHeader("Authorization","Bearer "+token);
                log.info("Headers中的Token已更新");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HttpGet getHttpGet(URIBuilder uriBuilder) {
        HttpGet get = new HttpGet();
        get.setHeaders(headers);
        try {
            get.setURI(uriBuilder.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return get;
    }
    public static String decodeURIComponent(String s) {
        if (s == null) {
            return null;
        }
        String result = null;
        try {
            result = URLDecoder.decode(s, "UTF-8");
        }
        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        }
        return result;
    }

    public static String encodeURIComponent(String s) {
        String result = null;
        result =s.replaceAll(" ", "%20");
        return result;
    }

    public static HttpGet getHttpGet(URIBuilder uri, List<NameValuePair> lists) {
        HttpGet get = new HttpGet();
        uri.setParameters(lists);
        get.setHeaders(headers);
        try {
            get.setURI(uri.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        return get;
    }

    public static HttpPost getHttpPost(URIBuilder uri) {
        HttpPost post = new HttpPost();
        try {
            post.setURI(uri.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        post.setHeaders(headers);
        return post;
    }

    public static HttpPost getHttpPost(URIBuilder uri, List<NameValuePair> lists) {
        HttpPost post = new HttpPost();
        uri.setParameters(lists);
        try {
            post.setURI(uri.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        post.setHeaders(headers);
        return post;
    }

}

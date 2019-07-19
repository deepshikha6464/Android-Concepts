//example showing the network calling in background thread using AsyncTask

//MainActivity.java

public class MainActivity extends AppCompatActivity {

EditText et_url;
TextView tv_search_result, tv_display_url;
   
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //binding Layout file with java file
       
       et_url = (EditText)findViewById(R.id.et_url);                               //getting search keyword
        tv_search_result = (TextView)findViewById(R.id.tv_search_result);          //displaying search result
        tv_display_url = (TextView)findViewById(R.id.tv_display_url);              // displaying the URL of search keyword
    }
     
     
     // creating menu
     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main , menu);
        return true;
    }


//setting click event on menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatGotSelected = item.getItemId();
        if(itemThatGotSelected == R.id.search)
        {
        
        //this function is for searching the given query... calls the  background thread
            makeGitHubSearchQuery();
           
        }
        return true;
    }



    public void makeGitHubSearchQuery()  {
    
    //getting search keyword from screen
        String githubSearchQuery =  et_url.getText().toString();
        
        //building url from that keyword........ "networkUtils.buildURL" .. written in networkUtil class written below this class
        URL url = networkUtils.buildURL(githubSearchQuery);
        tv_display_url.setText(""+url);
        
        //calling BACKGROUND THREAD .... the async task
       new gitResult().execute(url);
       }
       
       
       //INNER CLASS created with extending AsyncTask
       public class gitResult extends AsyncTask<URL , Void , String>
    {
        @Override
        protected String doInBackground(URL... params) {
        
        //first index of params contains URL 
            URL url = params[0];
            String gitHubSearchResults = null;
            
            
            try {
                  // sending URL to network for  HTTP response
                gitHubSearchResults = networkUtils.getResponseFromHttpUrl(url);
            }catch (IOException i){

            }
                // response is  return by this method when this class is executed 
                return gitHubSearchResults;
        }

//called when background operations are complete .... to update UI THREAD...
        @Override
        protected void onPostExecute(String gitHubSearchResults ) {
            if(gitHubSearchResults != null && gitHubSearchResults != "") {
            
            //UPDATING MAIN THREAD  FOR SETTING RESULT
                tv_search_result.setText(gitHubSearchResults);
            }
        }
    }
}





//************************************************* NERTOWK UTIL CLASS *********************************************




/**
 * These utilities will be used to communicate with the network.
 */
public class networkUtils {

    //variables for  building url

    static  String GITHUB_BASE_URL = "https://api.github.com/search/repositories";
    static  String PARAM_QUERY = "q";
    static String PARAM_SORT = "sort";
    static String sortBy = "stars";

    /**
     * Builds the URL used to query GitHub.
     *
     * @param githubSearchQuery The keyword that will be queried for.
     * @return The URL to use to query the GitHub server.
     */


    public static URL buildURL(String githubSearchQuery)
    {
        Uri builtUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, githubSearchQuery)
                .appendQueryParameter(PARAM_SORT, sortBy)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }


    //returns response



    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}

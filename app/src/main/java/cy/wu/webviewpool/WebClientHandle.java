package cy.wu.webviewpool;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengyun.wu on 2017/12/29.
 *
 * @author chengyun.wu
 */

public class WebClientHandle extends WebViewClient{


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(mWebChromeClientList.size() > 0) {
            WebClientIntercept webClientIntercept = new WebClientIntercept(mWebChromeClientList.get(0), 0);
            return webClientIntercept.shouldOverrideUrlLoading(view, url);
        }else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if(mWebChromeClientList.size() > 0) {
            WebClientIntercept webClientIntercept = new WebClientIntercept(mWebChromeClientList.get(0), 0);
            webClientIntercept.onPageFinished(view, url);
        }
    }






    private List<WebViewClient> mWebChromeClientList = new ArrayList<>();


    public void addWebClient(WebViewClient webViewClient){
        mWebChromeClientList.add(webViewClient);
    }


    class WebClientIntercept extends WebViewClient {
        WebViewClient mWebChromeClient;

        WebViewClient mNextWebChromeClient;

        public WebClientIntercept(WebViewClient mWebChromeClient, int index) {
            this.mWebChromeClient = mWebChromeClient;
            if(index + 1 < mWebChromeClientList.size()) {
                this.mNextWebChromeClient = new WebClientIntercept(mWebChromeClientList.get(index + 1), index + 1);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(mWebChromeClient.shouldOverrideUrlLoading(view, url)){
                return true;
            }else {
                return mNextWebChromeClient == null ? super.shouldOverrideUrlLoading(view, url)
                        : mNextWebChromeClient.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mWebChromeClient.onPageFinished(view, url);
            if(mNextWebChromeClient != null){
                mNextWebChromeClient.onPageFinished(view, url);
            }
        }
    }

}

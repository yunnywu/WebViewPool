package cy.wu.webviewpool;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.webkit.WebView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengyun.wu on 2017/12/27.
 *
 * @author chengyun.wu
 *
 * WebView池
 * 注意， 该webviewpool 创建的webview, 只能用于MainActivity中的Fragment
 *
 */

public class WebViewPool {

    private Context mContext;

    private int mMaxSize;
    private List<WebView> mAvailableList;
    private List<WebView> mInUseList;

    private WebViewController mWebViewController;

    private static class InnerHolder{
        private static WebViewPool holder = new WebViewPool();
    }

    public static WebViewPool getInstance(){
        return InnerHolder.holder;
    }

    public interface WebViewController {
        /**
         * create a new WebView
         * @param context
         * @return
         */
        WebView createWebView(Context context);

        /**
         * WebFragment destoryed reset WebView for next use time
         * @param WebView
         */
        void resetWebView(WebView WebView);

        /**
         * webview never use again, destroy it
         * @param WebView
         */
        void destroyWebView(WebView WebView);
    }

    public void init(Context context, int maxSize, WebViewController webViewController) {
        mContext = context;
        mMaxSize = maxSize;
        this.mWebViewController = webViewController;
        mAvailableList = new ArrayList<>(maxSize);
        mInUseList = new ArrayList<>(maxSize);
        createWebView();
    }

    private synchronized void createWebView() {
        for(int i = 0; i < mMaxSize; i++){
            if(mWebViewController != null){
                WebView webView = mWebViewController.createWebView(new MutableContextWrapper(mContext));
                mAvailableList.add(webView);
            }
        }
    }

    public synchronized WebView getWebView(Context context){
        WebView webView = null;
        if(mAvailableList.size() > 0){
             webView = mAvailableList.remove(0);
        }else {
            if(mWebViewController != null) {
                webView = mWebViewController.createWebView(new MutableContextWrapper(mContext));

            }
        }
        ((MutableContextWrapper)webView.getContext()).setBaseContext(context);
        mInUseList.add(webView);
        return  webView;
    }

    public synchronized void resetWebView(WebView webView){
        mWebViewController.resetWebView(webView);
        ((MutableContextWrapper)webView.getContext()).setBaseContext(mContext);
        if(mInUseList.contains(webView)){
            mInUseList.remove(webView);
            if(mAvailableList.size() < mMaxSize) {
                mAvailableList.add(webView);
            }else {
                mWebViewController.destroyWebView(webView);
            }
        }else {
            mWebViewController.destroyWebView(webView);
        }
    }


}

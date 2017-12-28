package cy.wu.webviewpool;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;


/**
 * Created by chengyun.wu on 2017/12/27.
 *
 * @author chengyun.wu
 */

public class WebViewCreateController implements WebViewPool.WebViewController {


    @Override
    public WebView createWebView(Context context) {
        WebView webView = new WebView(context);
        initListener(context, webView);
        dealJavascriptLeak(webView);
        return webView;
    }


    private void initListener(Context context, WebView webView) {
        WebSettings mWebSettings = webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        if (mWebSettings != null) {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 16) {
                try {
                    mWebSettings.setMediaPlaybackRequiresUserGesture(false);
                    if (SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            mWebSettings.setUseWideViewPort(true);
            mWebSettings.setLoadWithOverviewMode(true);
            mWebSettings.setDomStorageEnabled(true);
            //部分手机setJavaScriptEnabled函数诡异崩溃
            try {
                mWebSettings.setJavaScriptEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //有赞sdk不能设置UserAgent
            String userAgent = mWebSettings.getUserAgentString();
            mWebSettings.setUserAgentString(userAgent);
            mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            mWebSettings.setBlockNetworkLoads(false);

            mWebSettings.setAppCacheEnabled(true);
            String appCaceDir = context.getDir("cache", Context.MODE_PRIVATE)
                    .getPath();
            mWebSettings.setAppCachePath(appCaceDir);
            mWebSettings.setAllowFileAccess(true);
        }

    }

    /**
     * 移除不安全方法
     */
    private void dealJavascriptLeak(WebView webView) {
        try {
            if (webView != null) {
                webView.removeJavascriptInterface("searchBoxJavaBridge_");
                webView.removeJavascriptInterface("accessibility");
                webView.removeJavascriptInterface("accessibilityTraversal");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void destroyWebView(WebView webView) {
        if (webView != null) {
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.removeAllViews();
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(false);
            webView.destroy();
            webView = null;
        }
    }

    public void resetWebView(final WebView webView) {
        if (webView != null) {
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.stopLoading();
            webView.clearCache(false);
            webView.loadUrl("about:blank");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.clearHistory();
                }
            }, 500);
        }
    }
}

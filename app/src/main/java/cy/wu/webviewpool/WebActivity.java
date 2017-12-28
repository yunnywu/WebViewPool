package cy.wu.webviewpool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class WebActivity extends AppCompatActivity {

    String mUrl;

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getIntent().getStringExtra("url");
        if(TextUtils.isEmpty(mUrl)){
            finish();
            return;
        }

        setContentView(R.layout.activity_web);
        RelativeLayout rootLayout = findViewById(R.id.root_layout);
        mWebView = WebViewPool.getInstance().getWebView(WebActivity.this);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rootLayout.addView(mWebView, rl);


        mWebView.loadUrl(mUrl);

    }


    @Override
    protected void onDestroy() {
        WebViewPool.getInstance().resetWebView(mWebView);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            if (!mWebView.getUrl().contains(mUrl)) {
                mWebView.goBack();
                return ;
            } else {
                super.onBackPressed();
            }
        }
        super.onBackPressed();
    }
}

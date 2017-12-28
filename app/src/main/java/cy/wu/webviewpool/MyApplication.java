package cy.wu.webviewpool;

import android.app.Application;

/**
 * Created by chengyun.wu on 2017/12/28.
 *
 * @author chengyun.wu
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        WebViewPool.getInstance().init(getApplicationContext(), 3, new WebViewCreateController());
    }
}

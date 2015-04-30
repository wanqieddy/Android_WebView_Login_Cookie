package com.example.webviewplu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.webviewplu.R;

public class MainActivity extends Activity {

	private WebView webView;
	private String url = "http://login..cn/qq/login";//登陆
	private String targetUrl = "http://streamauth..cn/api/auth/token";//获得cookie
	Thread getCookieThread;
	String cookie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new MyWebClinet());
		webView.loadUrl(url);

		CookieSyncManager.createInstance(getApplicationContext());
		CookieManager cookieManager = CookieManager.getInstance();
		// cookieManager.removeAllCookie();
		cookie = cookieManager.getCookie(url);
		cookieManager.setAcceptCookie(true);
		cookieManager.setCookie(url, cookie);
		CookieSyncManager.getInstance().sync();
	}

	class MyWebClinet extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);

			Runnable sendable = new Runnable() {

				@Override
				public void run() {

					URL url = null;
					try {
						url = new URL(targetUrl);
						HttpURLConnection urlConnection = (HttpURLConnection) url .openConnection();
						urlConnection.setRequestProperty("Cookie", cookie);
						urlConnection.connect();

						InputStream inputStream = urlConnection.getInputStream();
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						byte[] data = new byte[1024];
						int len = 0;
						String result = "";
						if (inputStream != null) {

							while ((len = inputStream.read(data)) != -1) {
								outputStream.write(data, 0, len);
							}
							result = new String(outputStream.toByteArray(), "UTF-8");
							Log.i("ss", "_________________________result:" + result);

						}
						urlConnection.disconnect();
						outputStream.close();
						inputStream.close();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			};
			new Thread(sendable).start();
			
			return true;
		}
	}

}

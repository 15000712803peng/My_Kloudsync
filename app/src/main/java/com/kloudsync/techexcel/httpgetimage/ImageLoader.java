package com.kloudsync.techexcel.httpgetimage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;

public class ImageLoader {   
    
    MemoryCache memoryCache=new MemoryCache();   
    FileCache fileCache;   
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());   
    ExecutorService executorService;    
    
    public ImageLoader(Context context){   
        fileCache=new FileCache(context);   
        executorService=Executors.newFixedThreadPool(5);   
    }   
    
    final int stub_id = R.drawable.hello; 
    final int stub_idd = R.drawable.nopicv4;  
    public void DisplayImage(String url, ImageView imageView)   
    {   
    	if (null == url || url.length() < 1) {
            imageView.setImageResource(stub_id);			
		}
        imageViews.put(imageView, url);   
        Bitmap bitmap=memoryCache.get(url);   
        if(bitmap!=null)   
            imageView.setImageBitmap(bitmap);   
        else  
        {   
            queuePhoto(url, imageView);   
            imageView.setImageResource(stub_id);   
        }   
    }   
    
    public void DisplayImage2(String url, ImageView imageView)   
    {   
    	if (null == url || url.length() < 1) {
            imageView.setImageResource(stub_idd);			
		}
        imageViews.put(imageView, url);   
        Bitmap bitmap=memoryCache.get(url);   
        if(bitmap!=null)   
            imageView.setImageBitmap(bitmap);   
        else  
        {   
            queuePhoto(url, imageView);   
            imageView.setImageResource(stub_idd);   
        }   
    }  
    
    public void DisplayImage3(String url, ImageView imageView)   
    {   
    	if (null == url || url.length() < 1) {
//            imageView.setImageResource(stub_idd);			
		}
        imageViews.put(imageView, url);   
        Bitmap bitmap=memoryCache.get(url);   
        if(bitmap!=null)   
            imageView.setImageBitmap(bitmap);   
        else  
        {   
            queuePhoto(url, imageView);   
//            imageView.setImageResource(stub_idd);   
        }   
    } 
    
    private void queuePhoto(String url, ImageView imageView)   
    {   
        PhotoToLoad p=new PhotoToLoad(url, imageView);   
        executorService.submit(new PhotosLoader(p));   
    }   
    
    private Bitmap getBitmap(String url)   
    {   
        File f=fileCache.getFile(url);   
    
        //从sd卡  
        Bitmap b = decodeFile(f);   
        if(b!=null)   
            return b;   
    
        //从网络  
       /* try {   
            Bitmap bitmap=null;   
            URL imageUrl = new URL(url);   
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();   
            conn.setConnectTimeout(30000);   
            conn.setReadTimeout(30000);   
            conn.setInstanceFollowRedirects(true);   
            InputStream is=conn.getInputStream();   
            OutputStream os = new FileOutputStream(f);   
            Utils.CopyStream(is, os);   
            os.close();   
            bitmap = decodeFile(f);   
        	Log.e("bitmap", bitmap + ":");
            return bitmap;   
        } catch (Exception ex){   
           ex.printStackTrace();   
           return null;   
        }   */
        
        //httpGet连接对象  
        HttpGet httpRequest = new HttpGet(url);  
        //取得HttpClient 对象  
        HttpClient httpclient = new DefaultHttpClient();  
        try {  
            //请求httpClient ，取得HttpRestponse  
            HttpResponse httpResponse = httpclient.execute(httpRequest);  
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
                //取得相关信息 取得HttpEntiy  
                HttpEntity httpEntity = httpResponse.getEntity();  
                //获得一个输入流  
                InputStream is = httpEntity.getContent();  
                Bitmap bitmap = BitmapFactory.decodeStream(is);  
                is.close();  
                
                Log.i("bitmap", bitmap + "");
                return bitmap;   
            }  
              
        } catch (ClientProtocolException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace(); 
        }  
        return null;    
    }   
    
    //解码图像用来减少内存消耗  
    private Bitmap decodeFile(File f){   
        try {   
            //解码图像大小  
            BitmapFactory.Options o = new BitmapFactory.Options();   
            o.inJustDecodeBounds = true;   
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);   
    
            //找到正确的刻度值，它应该是2的幂。  
            final int REQUIRED_SIZE=70;   
            int width_tmp=o.outWidth, height_tmp=o.outHeight;   
            int scale=1;   
            while(true){   
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)   
                    break;   
                width_tmp/=2;   
                height_tmp/=2;   
                scale*=2;   
            }   
    
            BitmapFactory.Options o2 = new BitmapFactory.Options();   
            o2.inSampleSize=scale;   
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);   
        } catch (FileNotFoundException e) {}   
        return null;   
    }   
    
    //任务队列  
    private class PhotoToLoad   
    {   
        public String url;   
        public ImageView imageView;   
        public PhotoToLoad(String u, ImageView i){   
            url=u;   
            imageView=i;   
        }   
    }   
    
    class PhotosLoader implements Runnable {   
        PhotoToLoad photoToLoad;   
        PhotosLoader(PhotoToLoad photoToLoad){   
            this.photoToLoad=photoToLoad;   
        }   
    
        @Override  
        public void run() {   
            if(imageViewReused(photoToLoad))   
                return;   
            Bitmap bmp=getBitmap(photoToLoad.url);   
            memoryCache.put(photoToLoad.url, bmp);   
            if(imageViewReused(photoToLoad))   
                return;   
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);   
            Activity a=(Activity)photoToLoad.imageView.getContext();   
            a.runOnUiThread(bd);   
        }   
    }   
    
    boolean imageViewReused(PhotoToLoad photoToLoad){   
        String tag=imageViews.get(photoToLoad.imageView);   
        if(tag==null || !tag.equals(photoToLoad.url))   
            return true;   
        return false;   
    }   
    
    //用于显示位图在UI线程  
    class BitmapDisplayer implements Runnable   
    {   
        Bitmap bitmap;   
        PhotoToLoad photoToLoad;   
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}   
        public void run()   
        {   
            if(imageViewReused(photoToLoad))   
                return;   
            if(bitmap!=null)   
                photoToLoad.imageView.setImageBitmap(bitmap);   
            /*else  
                photoToLoad.imageView.setImageResource(stub_id);  */ 
        }   
    }   
    
    public void clearCache() {   
        memoryCache.clear();   
        fileCache.clear();   
    }   
    
}   

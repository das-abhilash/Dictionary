package in.zollet.abhilash.dictionary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadManager {

    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static DownloadManager sInstance;
    ThreadPoolExecutor mDecodeThreadPool;
    public static DownloadManager getInstance() {
        if (sInstance == null) {
            synchronized (DownloadManager.class) {
                sInstance = new DownloadManager();
            }
        }
        return sInstance;
    }

    private DownloadManager(){
        final BlockingQueue<Runnable> mDecodeWorkQueue;
        mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();

         final int KEEP_ALIVE_TIME = 60;
        // Sets the Time Unit to seconds
         final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        // Creates a thread pool manager
        mDecodeThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES ,       // Initial pool size
                NUMBER_OF_CORES ,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mDecodeWorkQueue);

    }

}

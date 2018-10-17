package com.dlz.mail.task;

import com.dlz.mail.Job.ExcuteSqlJob;
import com.dlz.mail.utils.Constant;
import com.dlz.mail.utils.Log;
import com.dlz.mail.utils.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.*;
import java.util.List;

/**
 * 文件变化监听的类
 */
public class MonitorTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MonitorTask.class);
    private boolean EXITS;
    private String mPath;
    private FileChangeListener mFileChangeListener;

    public MonitorTask(String mPath, FileChangeListener mFileChangeListener) {
        this.mPath = mPath;
        this.mFileChangeListener = mFileChangeListener;
    }

    @Override
    public void run() {
        monitor(mPath);

    }

    public void monitor(String filePath ){

        if (TextUtil.isEmpty(filePath)){
            return;
        }
        File file = new File(filePath);
        if (file == null || !file.exists()){
            return;
        }
        logger.debug("开始监听文件：" + filePath);
        Path sqlMonitorPath = Paths.get(filePath);
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            sqlMonitorPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE ,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (!EXITS) {
                logger.debug( "SQL文件的监听");
                WatchKey watckKey = watcher.take();
                List<WatchEvent<?>> events = watckKey.pollEvents();
                for (WatchEvent event : events) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("Created: " + event.context().toString());
                        if (mFileChangeListener != null){
                            mFileChangeListener.onCreated(event.context().toString());
                        }

                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("Delete: " + event.context().toString());
                        if (mFileChangeListener != null){
                            mFileChangeListener.onDelete(event.context().toString());
                        }

                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("Modify: " + event.context().toString());
                        String fileName = event.context().toString();
                        if (mFileChangeListener != null && !TextUtil.isEmpty(fileName) && fileName.equalsIgnoreCase( Constant.FileNames.SQL_MONITOR_FILE)){
                            mFileChangeListener.onModify(event.context().toString());
                        }

                    }
                }

                boolean valid = watckKey.reset();
                if (!valid ) {
                    System. out.println("Key has been unregisterede" );
                }

            }

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    public interface FileChangeListener{
        void onCreated(String path);
        void onDelete(String path);
        void onModify(String path);
    }


    public void setEXITS(boolean EXITS) {
        this.EXITS = EXITS;
    }
}

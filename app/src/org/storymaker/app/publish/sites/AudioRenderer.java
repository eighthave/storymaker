package org.storymaker.app.publish.sites;

import timber.log.Timber;

import java.io.File;

import org.storymaker.app.EditorBaseActivity;
import org.storymaker.app.R;
import org.storymaker.app.media.MediaProjectManager;
import org.storymaker.app.model.Job;
import org.storymaker.app.publish.RenderWorker;
import org.storymaker.app.publish.RendererBase;

import org.ffmpeg.android.MediaDesc;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class AudioRenderer extends RendererBase {
    private final String TAG = "AudioRenderer";
	public static String SPEC_KEY = "audio";
	private MediaProjectManager mMPM;

    static HandlerThread bgThread = new HandlerThread("AudioRenderHandlerThread");
    static {
        bgThread.start();
    }
	
	public AudioRenderer(Context context, RenderWorker worker, Job job) {
		super(context, worker, job);
        mMPM = new MediaProjectManager(null, mContext, mHandlerPub, mJob.getProjectId());
	}

    @Override
    public void start() {
//        super.start(); // FIXME should we call the parent for any reason?
        renderVideo();
    }
	
    private void renderVideo() {
        File exportFile = mMPM.getExportMediaFile();
        boolean compress = mSettings.getBoolean("pcompress", false);// compress
        boolean doOverwrite = true;
        try {
            MediaDesc mdExported = mMPM.doExportMedia(exportFile, compress, doOverwrite);
            jobSucceeded(mdExported.path);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            jobFailed(e, 0, e.getMessage());
        }
    }
    
    public Handler mHandlerPub = new Handler(bgThread.getLooper()) {

        @Override
        public void handleMessage(Message msg) {
            // TODO move this to base class?
            // TODO opt. don't call getData() every time:  Bundle data = msg.getData();
            String statusTitle = msg.getData().getString("statusTitle");
            String status = msg.getData().getString("status");
            if (status != null) {
                Timber.d(status);
            }

            String error = msg.getData().getString("error");
            if (error == null) {
                error = msg.getData().getString("err");
            }
            
            int progress = msg.getData().getInt("progress");

            if (status != null) {
                jobProgress(AudioRenderer.this.mJob, progress, status);
            }

//            if (mProgressDialog != null) {
//                if (progress >= 0) {
//                    mProgressDialog.setProgress(progress);
//                }
//
//                if (statusTitle != null) {
//                    mProgressDialog.setTitle(statusTitle);
//                }
//            }

//            switch (msg.what)
//            {
//                case 0:
//                case 1:
//                    if (status != null) {
//                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                            mProgressDialog.setMessage(status);
//                        } else {
//                            Toast.makeText(EditorBaseActivity.this, status, Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    }
//                    break;
//
//                case 999:
//                    mProgressDialog = new ProgressDialog(EditorBaseActivity.this);
//                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                    mProgressDialog.setTitle(getString(R.string.rendering));
//                    mProgressDialog.setMessage(getString(R.string.rendering_project_));
//                    mProgressDialog.setCancelable(true);
//                    mProgressDialog.show();
//
//                    break;
//
//                case 888:
//                    if (mProgressDialog != null) {
//                        mProgressDialog.setMessage(status);
//                    }
//                    break;
//
//                case 777:
//                    String videoId = msg.getData().getString("youtubeid");
//                    String url = msg.getData().getString("urlPost");
//                    String localPath = msg.getData().getString("fileMedia");
//                    String mimeType = msg.getData().getString("mime");
//
//                    if (mProgressDialog != null) {
//                        try {
//                            mProgressDialog.dismiss();
//                            mProgressDialog = null;
//                        } catch (Exception e) {
//                            // ignore:
//                            // http://stackoverflow.com/questions/2745061/java-lang-illegalargumentexception-view-not-attached-to-window-manager
//                        }
//                    }
//
//                    File fileMedia = new File(localPath);
//
//                    if (fileMedia.exists() && fileMedia.length() > 0) {
//                        showPublished(url, fileMedia, videoId, mimeType);
//                    } else {
//                        // show what?
//                    }
//
//                    break;
//
//                case -1:
//                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                        try {
//                            mProgressDialog.dismiss();
//                            mProgressDialog = null;
//                        } catch (Exception e) {
//                            // ignore:
//                            // http://stackoverflow.com/questions/2745061/java-lang-illegalargumentexception-view-not-attached-to-window-manager
//                        }
//                    }
//
////                    AlertDialog.Builder builder = new AlertDialog.Builder(EditorBaseActivity.this);
////                    builder.setMessage(error).show();
//                    break;
//                default:
//            }
        }
    };
}

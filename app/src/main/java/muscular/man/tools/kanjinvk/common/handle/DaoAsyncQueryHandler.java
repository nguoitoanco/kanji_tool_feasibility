package muscular.man.tools.kanjinvk.common.handle;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KhanhNV10 on 14/03/2016.
 */
@TargetApi(4)
public class DaoAsyncQueryHandler<T> extends AsyncQueryHandler {
    protected DaoAsyncQueryHandler.OnQueryCompleteListener<List<T>> mOnQueryCompleteListener;
    protected DaoAsyncQueryHandler.OnInsertCompleteListener mOnInsertCompleteListener;
    protected DaoAsyncQueryHandler.OnUpdateCompleteListener mOnUpdateCompleteListener;
    protected DaoAsyncQueryHandler.OnDeleteCompleteListener mOnDeleteCompleteListener;
    protected DaoAsyncQueryHandler.LoaderTask<T> mLoaderTask;
    protected DaoAsyncQueryHandler.LoaderTaskHandler<T> mLoaderTaskHandler;
    protected ProgressDialog mProgressDialog;
    protected ProgressBar mProgressBar;

    public DaoAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    public DaoAsyncQueryHandler.LoaderTask<T> loadContents(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        if (this.mLoaderTask == null) {
            this.mLoaderTask = new DaoAsyncQueryHandler.LoaderTask(token);
        }

        this.mLoaderTask.mLoaderTaskHandler = this.mLoaderTaskHandler;
        this.mLoaderTask.mOnQueryCompleteListener = this.mOnQueryCompleteListener;
        if (this.mLoaderTask.mProgressBar != null) {
            this.mLoaderTask.mProgressBar = this.mProgressBar;
        }

        if (this.mLoaderTask.mProgressDialog != null) {
            this.mLoaderTask.mProgressDialog = this.mProgressDialog;
        }

        super.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
        return this.mLoaderTask;
    }

    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        if (this.mOnInsertCompleteListener != null) {
            this.mOnInsertCompleteListener.onInsertComplete(token, uri);
        }

    }

    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        if (this.mOnUpdateCompleteListener != null) {
            this.mOnUpdateCompleteListener.onUpdateComplete(token, result);
        }

    }

    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        if (this.mOnDeleteCompleteListener != null) {
            this.mOnDeleteCompleteListener.onDeleteComplete(token, result);
        }

    }

    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        this.onPreQueryTaskExecute();
        if (this.mLoaderTask != null) {
            this.mLoaderTask.mCursor = cursor;
            this.mLoaderTask.execute(new Void[0]);
            this.mLoaderTask = null;
        }

        this.mProgressDialog = null;
        this.mProgressBar = null;
        this.mOnQueryCompleteListener = null;
    }

    protected void onPreQueryTaskExecute() {
    }

    public void setOnQueryCompleteListener(DaoAsyncQueryHandler.OnQueryCompleteListener<List<T>> listener) {
        this.mOnQueryCompleteListener = listener;
    }

    public void setOnInsertCompleteListener(DaoAsyncQueryHandler.OnInsertCompleteListener listener) {
        this.mOnInsertCompleteListener = listener;
    }

    public void setOnUpdateCompleteListener(DaoAsyncQueryHandler.OnUpdateCompleteListener listener) {
        this.mOnUpdateCompleteListener = listener;
    }

    public void setOnDeleteCompleteListener(DaoAsyncQueryHandler.OnDeleteCompleteListener listener) {
        this.mOnDeleteCompleteListener = listener;
    }

    public void setLoaderTaskHandler(DaoAsyncQueryHandler.LoaderTaskHandler<T> handler) {
        this.mLoaderTaskHandler = handler;
    }

    public void setProgressDialog(ProgressDialog dialog) {
        this.mProgressDialog = dialog;
    }

    public void setProgressBar(ProgressBar bar) {
        this.mProgressBar = bar;
    }

    public void setLoaderTask(DaoAsyncQueryHandler.LoaderTask<T> loaderTask) {
        this.mLoaderTask = loaderTask;
    }

    public interface LoaderTaskHandler<T> {
        T createEntity(int var1, Cursor var2);
    }

    public static class LoaderTask<E> extends AsyncTask<Void, Integer, List<E>> {
        int mToken = 0;
        protected DaoAsyncQueryHandler.OnQueryCompleteListener<List<E>> mOnQueryCompleteListener;
        protected DaoAsyncQueryHandler.LoaderTaskHandler<E> mLoaderTaskHandler;
        protected ProgressDialog mProgressDialog;
        protected ProgressBar mProgressBar;
        protected Cursor mCursor;

        protected LoaderTask(int token) {
            this.mToken = token;
        }

        protected void onPreExecute() {
            if (this.mProgressDialog != null) {
                this.mProgressDialog.show();
            }

        }

        protected List<E> doInBackground(Void... params) {
            ArrayList results = new ArrayList();

            try {
                if (this.mCursor == null || this.mLoaderTaskHandler == null) {
//                    if(ConfigUtil.isDebug()) {
//                        Log.d("r2core", "Cursor is null or LoaderTaskHandler not set.");
//                    }

                    ArrayList var9 = results;
                    return var9;
                }

                int rowCount = this.mCursor.getCount();
                int counter = 1;

                while (this.mCursor.moveToNext() && !this.isCancelled()) {
                    this.publishProgress(new Integer[]{Integer.valueOf((int) ((float) (counter++) / (float) rowCount * 100.0F))});
                    Object t = this.mLoaderTaskHandler.createEntity(this.mToken, this.mCursor);
                    if (t != null) {
                        results.add(t);
                    }
                }
            } finally {
                if (this.mCursor != null) {
                    this.mCursor.close();
                }

            }

            return results;
        }

        protected void onProgressUpdate(Integer... values) {
            if (this.mProgressBar != null) {
                this.mProgressBar.setProgress(values[0].intValue());
            } else if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
                this.mProgressDialog.setProgress(values[0].intValue());
            }

        }

        protected void onPostExecute(List<E> result) {
            if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
                this.mProgressDialog.dismiss();
            }

            if (this.mOnQueryCompleteListener != null) {
                this.mOnQueryCompleteListener.onQueryComplete(this.mToken, result);
            }

            this.releaseReference();
        }

        protected void onCancelled() {
            super.onCancelled();
            if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
                this.mProgressDialog.dismiss();
            }

            if (this.mCursor != null && !this.mCursor.isClosed()) {
                this.mCursor.close();
            }

            this.releaseReference();
        }

        protected void releaseReference() {
            this.mCursor = null;
            this.mProgressDialog = null;
            this.mProgressBar = null;
            this.mLoaderTaskHandler = null;
            this.mOnQueryCompleteListener = null;
        }
    }

    public interface OnInsertCompleteListener {
        void onInsertComplete(int var1, Uri var2);
    }

    public interface OnUpdateCompleteListener {
        void onUpdateComplete(int var1, int var2);
    }

    public interface OnQueryCompleteListener<T> {
        void onQueryComplete(int var1, T var2);
    }

    public interface OnDeleteCompleteListener {
        void onDeleteComplete(int var1, int var2);
    }

    public void cancel() {
        if (this.mLoaderTask != null) {
            mLoaderTask.cancel(true);
        }

        mLoaderTaskHandler = null;
    }
}
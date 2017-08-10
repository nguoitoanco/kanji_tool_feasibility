package muscular.man.tools.kanjinvk.common;

/**
 * Created by nguoitoanco on 2/28/2016.
 */
public class CommonActionListener {
    public interface OnclickItemListener<T> {
        public boolean onSuccess(T t, int i);
    }

    public interface CallBackListener<T> {
        public boolean onSuccess(T t);
    }

    public interface CallBackAfterFinishTaskListener<T> {
        public boolean onSuccess(T t);
        public boolean onError(String message);
    }
}

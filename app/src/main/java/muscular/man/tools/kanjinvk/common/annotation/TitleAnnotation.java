package muscular.man.tools.kanjinvk.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by KhanhNV10 on 2015/12/25.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TitleAnnotation {
    /** Resource Id */
    int rId();
}

package extensions;

import java.util.ResourceBundle;

public class ResourceBundleHelper {

    // ===========================================
    //            S I N G L E T O N
    // ===========================================

    private ResourceBundleHelper() {
        // nothing to do here ...
    }

    private static ResourceBundleHelper instance;

    public static ResourceBundleHelper getInstance() {
        return instance = ((instance != null) ? instance : new ResourceBundleHelper());
    }

    // ===========================================
    //               M E T H O D S
    // ===========================================

    public ResourceBundle getLanguageBundle() {
        return ResourceBundle.getBundle("lang", new Utf8Control());
    }

}

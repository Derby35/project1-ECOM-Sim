import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String generateTransactionId() {
        return new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
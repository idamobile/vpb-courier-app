import android.widget.TextView;
import com.idamobile.vpb.courier.LoginActivity_;
import com.idamobile.vpb.courier.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestLoginActivity {

    @Test
    public void testEmptyLoginMessage() throws Exception {
        LoginActivity_ activity = new LoginActivity_();
        activity.onCreate(null);
        activity.tryToLogin(null, 1234);
        TextView loginView = (TextView) activity.findViewById(R.id.login_field);
        assertEquals(loginView.getError(), activity.getString(R.string.login_empty_error));
    }
}

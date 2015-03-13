package yangmbin.beauty.ui;

import com.yangmbin.beauty.R;

import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract.Instances;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class LoadingActivity extends Activity {
	public static Activity instance;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.loading);
			
		instance = this;
    }
}
package yangmbin.beauty.fragment;

import com.yangmbin.beauty.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.about_fragment, group, false);
	}
}

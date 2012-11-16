package edu.northwestern.cbits.purple_robot_manager.probes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.WazaBe.HoloEverywhere.preference.PreferenceScreen;
import com.WazaBe.HoloEverywhere.sherlock.SPreferenceActivity;

import android.content.Context;
import android.os.Bundle;

import edu.northwestern.cbits.purple_robot_manager.R;

public abstract class Probe
{
	public static final String PROBE_READING = "edu.northwestern.cbits.purple_robot.PROBE_READING";

	public static final String DURATION = "DURATION";
	public static final String PERIOD = "PERIOD";

	public abstract String name(Context context);
	public abstract String title(Context context);
	public abstract String probeCategory(Context context);
	public abstract Bundle[] dataRequestBundles(Context context);
	public abstract PreferenceScreen preferenceScreen(SPreferenceActivity settingsActivity);

	@SuppressWarnings("rawtypes")
	private static List<Class> _probeClasses = new ArrayList<Class>();

	@SuppressWarnings("rawtypes")
	public static void registerProbeClass(Class probeClass)
	{
		if (!Probe._probeClasses.contains(probeClass))
			Probe._probeClasses.add(probeClass);
	}

	@SuppressWarnings("rawtypes")
	public static List<Class> availableProbeClasses()
	{
		return Probe._probeClasses;
	}

	public static void loadProbeClasses(Context context)
	{
		String packageName = Probe.class.getPackage().getName();

		String[] probeClasses = context.getResources().getStringArray(R.array.probe_classes);

		for (String className : probeClasses)
		{
			try
			{
				Probe.registerProbeClass(Class.forName(packageName + "." + className));
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	public abstract boolean isEnabled(Context context);

	public String summarizeValue(Context context, Bundle bundle)
	{
		return bundle.toString();
	}

	public abstract void updateFromJSON(Context context, JSONObject json) throws JSONException;

	public Bundle formattedBundle(Context context, Bundle bundle)
	{
		Bundle formatted = new Bundle();

		if (bundle.containsKey("TIMESTAMP"))
		{
			double time = bundle.getDouble("TIMESTAMP");

			Date d = new Date(((long) time) * 1000);

			formatted.putString("dATE rECORDED", d.toString());
		}

		return formatted;
	};
}

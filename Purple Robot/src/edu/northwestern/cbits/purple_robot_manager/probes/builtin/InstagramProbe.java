package edu.northwestern.cbits.purple_robot_manager.probes.builtin;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import edu.northwestern.cbits.purple_robot_manager.EncryptionManager;
import edu.northwestern.cbits.purple_robot_manager.R;
import edu.northwestern.cbits.purple_robot_manager.activities.OAuthActivity;
import edu.northwestern.cbits.purple_robot_manager.activities.settings.FlexibleListPreference;
import edu.northwestern.cbits.purple_robot_manager.logging.LogManager;
import edu.northwestern.cbits.purple_robot_manager.logging.SanityCheck;
import edu.northwestern.cbits.purple_robot_manager.logging.SanityManager;
import edu.northwestern.cbits.purple_robot_manager.oauth.InstagramApi;
import edu.northwestern.cbits.purple_robot_manager.probes.Probe;

public class InstagramProbe extends Probe
{
    // public static final String CALLBACK =
    // "http://purple.robot.com/oauth/instagram";
    public static final String CALLBACK = "http://tech.cbits.northwestern.edu/oauth/instagram";

    private static final boolean DEFAULT_ENABLED = false;
    private static final boolean DEFAULT_ENCRYPT = false;

    protected static final String HOUR_COUNT = "HOUR_COUNT";

    private static final String ENABLED = "config_probe_instagram_enabled";
    private static final String FREQUENCY = "config_probe_instagram_frequency";
    private static final String ENCRYPT_DATA = "config_probe_instagram_encrypt_data";

    private long _lastCheck = 0;

    private String _token = null;
    private String _secret = null;

    @Override
    public String name(Context context)
    {
        return "edu.northwestern.cbits.purple_robot_manager.probes.builtin.InstagramProbe";
    }

    @Override
    public String title(Context context)
    {
        return context.getString(R.string.title_instagram_probe);
    }

    @Override
    public String probeCategory(Context context)
    {
        return context.getResources().getString(R.string.probe_external_services_category);
    }

    @Override
    public void enable(Context context)
    {
        SharedPreferences prefs = Probe.getPreferences(context);

        Editor e = prefs.edit();
        e.putBoolean(InstagramProbe.ENABLED, true);

        e.commit();
    }

    @Override
    public void disable(Context context)
    {
        SharedPreferences prefs = Probe.getPreferences(context);

        Editor e = prefs.edit();
        e.putBoolean(InstagramProbe.ENABLED, false);

        e.commit();
    }

    @Override
    public boolean isEnabled(final Context context)
    {
        final SharedPreferences prefs = Probe.getPreferences(context);

        if (super.isEnabled(context))
        {
            final long now = System.currentTimeMillis();

            if (prefs.getBoolean(InstagramProbe.ENABLED, InstagramProbe.DEFAULT_ENABLED))
            {
                synchronized (this)
                {
                    long freq = Long.parseLong(prefs.getString(InstagramProbe.FREQUENCY, Probe.DEFAULT_FREQUENCY));
                    final boolean doEncrypt = prefs.getBoolean(InstagramProbe.ENCRYPT_DATA, InstagramProbe.DEFAULT_ENCRYPT);

                    final EncryptionManager em = EncryptionManager.getInstance();

                    if (now - this._lastCheck > freq)
                    {
                        final InstagramProbe me = this;

                        this._token = prefs.getString("oauth_instagram_token", null);
                        this._secret = prefs.getString("oauth_instagram_secret", null);

                        final String title = context.getString(R.string.title_instagram_check);
                        final SanityManager sanity = SanityManager.getInstance(context);

                        if (this._token == null || this._secret == null)
                        {
                            String message = context.getString(R.string.message_instagram_check);

                            Runnable action = new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    me.fetchAuth(context);
                                }
                            };

                            sanity.addAlert(SanityCheck.WARNING, title, message, action);
                        }
                        else
                        {
                            sanity.clearAlert(title);

                            Token accessToken = new Token(this._token, this._secret);

                            ServiceBuilder builder = new ServiceBuilder();
                            builder = builder.provider(InstagramApi.class);
                            builder = builder.apiKey(context.getString(R.string.instagram_consumer_key));
                            builder = builder.apiSecret(context.getString(R.string.instagram_consumer_secret));

                            final OAuthService service = builder.build();

                            final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.instagram.com/v1/users/self/feed");
                            service.signRequest(accessToken, request);

                            Runnable r = new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        long mostRecent = prefs.getLong("config_instagram_most_recent", 0);

                                        Response response = request.send();

                                        JSONObject root = new JSONObject(response.getBody());

                                        JSONArray data = root.getJSONArray("data");

                                        for (int i = (data.length() - 1); i >= 0; i--)
                                        {
                                            JSONObject post = data.getJSONObject(i);

                                            long postTime = post.getLong("created_time") * 1000;

                                            if (postTime > mostRecent)
                                            {
                                                Bundle eventBundle = new Bundle();
                                                eventBundle.putString("PROBE", me.name(context));
                                                eventBundle.putLong("TIMESTAMP", postTime / 1000);

                                                JSONObject location = post.getJSONObject("location");

                                                if (location != null)
                                                {
                                                    eventBundle.putDouble("LATITUDE", location.getDouble("latitude"));
                                                    eventBundle.putDouble("LONGITUDE", location.getDouble("longitude"));

                                                    if (location.has("name"))
                                                    {
                                                        String name = location.getString("name");

                                                        if (name != null)
                                                            eventBundle.putString("LOCATION_NAME", name);
                                                    }
                                                }

                                                eventBundle.putString("LINK", post.getString("link"));
                                                eventBundle.putString("TYPE", post.getString("type"));
                                                eventBundle.putString("FILTER", post.getString("filter"));

                                                if (post.has("caption"))
                                                {
                                                    String caption = post.getJSONObject("caption").getString("text");

                                                    if (caption != null)
                                                    {
                                                        if (doEncrypt)
                                                            caption = em.encryptString(context, caption);

                                                        eventBundle.putString("CAPTION", caption);
                                                    }
                                                }

                                                eventBundle.putBoolean("IS_OBFUSCATED", doEncrypt);
                                                me.transmitData(context, eventBundle);
                                            }
                                        }

                                        Editor e = prefs.edit();
                                        e.putLong("config_instagram_most_recent", System.currentTimeMillis());
                                        e.commit();
                                    }
                                    catch (JSONException e)
                                    {
                                        LogManager.getInstance(context).logException(e);
                                    }
                                    catch (OAuthException e)
                                    {
                                        LogManager.getInstance(context).logException(e);
                                    }
                                    catch (IllegalBlockSizeException e)
                                    {
                                        LogManager.getInstance(context).logException(e);
                                    }
                                    catch (BadPaddingException e)
                                    {
                                        LogManager.getInstance(context).logException(e);
                                    }
                                    catch (UnsupportedEncodingException e)
                                    {
                                        LogManager.getInstance(context).logException(e);
                                    }
                                    catch (IllegalArgumentException e)
                                    {
                                        LogManager.getInstance(context).logException(e);
                                    }
                                    catch (IllegalStateException e)
                                    {
                                        LogManager.getInstance(context).logException(e);
                                    }
                                }
                            };

                            Thread t = new Thread(r);
                            t.start();
                        }

                        me._lastCheck = now;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private void fetchAuth(Context context)
    {
        Intent intent = new Intent(context, OAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(OAuthActivity.CONSUMER_KEY, context.getString(R.string.instagram_consumer_key));
        intent.putExtra(OAuthActivity.CONSUMER_SECRET, context.getString(R.string.instagram_consumer_secret));
        intent.putExtra(OAuthActivity.REQUESTER, "instagram");
        intent.putExtra(OAuthActivity.CALLBACK_URL, CALLBACK);

        context.startActivity(intent);
    }

    @Override
    public String summarizeValue(Context context, Bundle bundle)
    {
        if (bundle.containsKey("CAPTION"))
            return bundle.getString("CAPTION") + " (" + bundle.getString("TYPE") + ")";

        return bundle.getString("TYPE");
    }

    @Override
    public Map<String, Object> configuration(Context context)
    {
        Map<String, Object> map = super.configuration(context);

        SharedPreferences prefs = Probe.getPreferences(context);

        long freq = Long.parseLong(prefs.getString(InstagramProbe.FREQUENCY, Probe.DEFAULT_FREQUENCY));
        map.put(Probe.PROBE_FREQUENCY, freq);

        boolean hash = prefs.getBoolean(InstagramProbe.ENCRYPT_DATA, InstagramProbe.DEFAULT_ENCRYPT);
        map.put(Probe.ENCRYPT_DATA, hash);

        return map;
    }

    @Override
    public void updateFromMap(Context context, Map<String, Object> params)
    {
        super.updateFromMap(context, params);

        if (params.containsKey(Probe.PROBE_FREQUENCY))
        {
            Object frequency = params.get(Probe.PROBE_FREQUENCY);

            if (frequency instanceof Double)
            {
                frequency = Long.valueOf(((Double) frequency).longValue());
            }

            if (frequency instanceof Long)
            {
                SharedPreferences prefs = Probe.getPreferences(context);
                Editor e = prefs.edit();

                e.putString(InstagramProbe.FREQUENCY, frequency.toString());
                e.commit();
            }
        }

        if (params.containsKey(Probe.ENCRYPT_DATA))
        {
            Object encrypt = params.get(Probe.ENCRYPT_DATA);

            if (encrypt instanceof Boolean)
            {
                Boolean encryptBoolean = (Boolean) encrypt;

                SharedPreferences prefs = Probe.getPreferences(context);
                Editor e = prefs.edit();

                e.putBoolean(InstagramProbe.ENCRYPT_DATA, encryptBoolean.booleanValue());
                e.commit();
            }
        }
    }

    @Override
    public String summary(Context context)
    {
        return context.getString(R.string.summary_instagram_probe_desc);
    }

    @Override
    @SuppressWarnings("deprecation")
    public PreferenceScreen preferenceScreen(final Context context, PreferenceManager manager)
    {
        final PreferenceScreen screen = manager.createPreferenceScreen(context);
        screen.setTitle(this.title(context));
        screen.setSummary(R.string.summary_instagram_probe_desc);

        final SharedPreferences prefs = Probe.getPreferences(context);

        String token = prefs.getString("oauth_instagram_token", null);
        String secret = prefs.getString("oauth_instagram_secret", null);

        CheckBoxPreference enabled = new CheckBoxPreference(context);
        enabled.setTitle(R.string.title_enable_probe);
        enabled.setKey(InstagramProbe.ENABLED);
        enabled.setDefaultValue(InstagramProbe.DEFAULT_ENABLED);

        screen.addPreference(enabled);

        FlexibleListPreference duration = new FlexibleListPreference(context);
        duration.setKey(InstagramProbe.FREQUENCY);
        duration.setEntryValues(R.array.probe_low_frequency_values);
        duration.setEntries(R.array.probe_low_frequency_labels);
        duration.setTitle(R.string.probe_frequency_label);
        duration.setDefaultValue(Probe.DEFAULT_FREQUENCY);

        screen.addPreference(duration);

        CheckBoxPreference encrypt = new CheckBoxPreference(context);
        encrypt.setKey(InstagramProbe.ENCRYPT_DATA);
        encrypt.setDefaultValue(InstagramProbe.DEFAULT_ENCRYPT);
        encrypt.setTitle(R.string.config_probe_instagram_encrypt_title);
        encrypt.setSummary(R.string.config_probe_instagram_encrypt_summary);

        screen.addPreference(encrypt);

        final Preference authPreference = new Preference(context);
        authPreference.setTitle(R.string.title_authenticate_instagram_probe);
        authPreference.setSummary(R.string.summary_authenticate_instagram_probe);

        final InstagramProbe me = this;

        final Preference logoutPreference = new Preference(context);
        logoutPreference.setTitle(R.string.title_logout_instagram_probe);
        logoutPreference.setSummary(R.string.summary_logout_instagram_probe);

        authPreference.setOnPreferenceClickListener(new OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                me.fetchAuth(context);

                screen.addPreference(logoutPreference);
                screen.removePreference(authPreference);

                return true;
            }
        });

        logoutPreference.setOnPreferenceClickListener(new OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Editor e = prefs.edit();
                e.remove("oauth_instagram_token");
                e.remove("oauth_instagram_secret");
                e.commit();

                screen.addPreference(authPreference);
                screen.removePreference(logoutPreference);

                if (context instanceof Activity)
                {
                    Activity activity = (Activity) context;

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(context, context.getString(R.string.toast_instagram_logout), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                return true;
            }
        });

        if (token == null || secret == null)
            screen.addPreference(authPreference);
        else
            screen.addPreference(logoutPreference);

        return screen;
    }

    @Override
    public JSONObject fetchSettings(Context context)
    {
        JSONObject settings = new JSONObject();

        try
        {
            JSONArray values = new JSONArray();
            values.put(true);
            values.put(false);

            JSONObject enabled = new JSONObject();
            enabled.put(Probe.PROBE_TYPE, Probe.PROBE_TYPE_BOOLEAN);
            enabled.put(Probe.PROBE_VALUES, values);
            settings.put(Probe.PROBE_ENABLED, enabled);

            JSONObject encrypt = new JSONObject();
            encrypt.put(Probe.PROBE_TYPE, Probe.PROBE_TYPE_BOOLEAN);
            encrypt.put(Probe.PROBE_VALUES, values);
            settings.put(Probe.ENCRYPT_DATA, encrypt);

            JSONObject frequency = new JSONObject();
            frequency.put(Probe.PROBE_TYPE, Probe.PROBE_TYPE_LONG);
            values = new JSONArray();

            String[] options = context.getResources().getStringArray(R.array.probe_low_frequency_values);

            for (String option : options)
            {
                values.put(Long.parseLong(option));
            }

            frequency.put(Probe.PROBE_VALUES, values);
            settings.put(Probe.PROBE_FREQUENCY, frequency);
        }
        catch (JSONException e)
        {
            LogManager.getInstance(context).logException(e);
        }

        return settings;
    }
}

package bg.spaceweather.spaceweatherforecast;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link SpaceWeatherWidgetConfigureActivity SpaceWeatherWidgetConfigureActivity}
 */
public class SpaceWeatherWidget extends AppWidgetProvider {

	/** Keep ids of the views for easier access. */
	final static int[][] IDS = new int[][]{
					{R.id.date_day_1, R.id.ap_day_1, R.id.state_day_1, R.id.kp_day_1},
					{R.id.date_day_2, R.id.ap_day_2, R.id.state_day_2, R.id.kp_day_2},
					{R.id.date_day_3, R.id.ap_day_3, R.id.state_day_3, R.id.kp_day_3},
	};

	static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
															final int appWidgetId) {
		CharSequence widgetText = SpaceWeatherWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

		/* Construct the remote views object. */
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.space_weather_widget);
		views.setTextViewText(R.id.appwidget_text, widgetText);

		/* Network communication should be done in separate thread. */
		(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {

				/* Create HTTP client object. */
				HttpClient client = new DefaultHttpClient();

				/* Set UTF-8 as charset. */
				client.getParams().setParameter(
								"http.protocol.content-charset", "UTF-8");

				//TODO Obtain host and script from setteings dialog.
				String host = "spaceweather.bg";
				String script = "forecast.php";
				HttpGet get = new HttpGet("http://" + host + "/" + script + "?limit=3");
				try {
					/* Execute HTTP get rquest. */
					HttpResponse response = client.execute(get);

					/* Obtain HTTP response. */
					JSONArray result = new JSONArray(EntityUtils.toString(response.getEntity(), "UTF-8"));

					/* Travers array with the days. */
					for (int i = 0; i < result.length(); i++) {
						JSONObject day = result.getJSONObject(i);
						views.setTextViewText(IDS[i][0], day.getString("date"));
						views.setInt(IDS[i][0], "setTextColor", Color.CYAN);
						views.setTextViewText(IDS[i][1], "" + day.getInt("ap"));
						views.setInt(IDS[i][1], "setTextColor", Color.GREEN);
						views.setTextViewText(IDS[i][2], day.getString("name"));
						views.setInt(IDS[i][2], "setTextColor", Color.RED);
						views.setTextViewText(IDS[i][3], "" + day.getInt("kp") + " " + day.getDouble("prob"));
						views.setInt(IDS[i][3], "setTextColor", Color.BLUE);
					}
				} catch (ClientProtocolException exception) {
				} catch (IOException exception) {
				} catch (JSONException exception) {
				}

				/* Instruct the widget manager to update the widget. */
				appWidgetManager.updateAppWidget(appWidgetId, views);
				return null;
			}
		}).execute();
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		/* There may be multiple widgets active, so update all of them. */
		for (int appWidgetId : appWidgetIds) {
			/* Load the information from the server. */
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		/* When the user deletes the widget, delete the preference associated with it. */
		for (int appWidgetId : appWidgetIds) {
			SpaceWeatherWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
		}
	}

	@Override
	public void onEnabled(Context context) {
	}

	@Override
	public void onDisabled(Context context) {
	}
}


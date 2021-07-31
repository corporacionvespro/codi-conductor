package codi.drive.conductor.chiclayo.compartido;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import androidx.annotation.Nullable;
import com.google.android.gms.location.LocationResult;
import java.util.List;

/**
 * By: el-bryant
 */

public class LocationUpdatesIntentService extends IntentService {
    private static final String ACTION_PROCESS_UPDATES = "com.google.android.gms.location.sample.locationupdatespendingintent.action" + ".PROCESS_UPDATES";
    private static final String TAG = LocationUpdatesIntentService.class.getSimpleName();

    public LocationUpdatesIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                List<Location> locations = result.getLocations();
                Utils.setLocationUpdatesResult(this, locations);
                Log.i(TAG, Utils.getLocationUpdatesResult(this));
            }
        }
    }
}

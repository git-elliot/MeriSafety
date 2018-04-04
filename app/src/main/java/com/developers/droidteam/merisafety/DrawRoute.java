package com.developers.droidteam.merisafety;

import android.os.AsyncTask;

import com.github.polok.routedrawer.RouteDrawer;
import com.github.polok.routedrawer.RouteRest;
import com.github.polok.routedrawer.model.Routes;
import com.github.polok.routedrawer.model.TravelMode;
import com.github.polok.routedrawer.parser.RouteJsonParser;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import rx.android.schedulers.AndroidSchedulers;

public class DrawRoute extends AsyncTask {

    GoogleMap map;
    LatLng start,end;
    int color;

    DrawRoute(GoogleMap googleMap, int color, LatLng first, LatLng last){
        map=googleMap;
        start=first;
        end=last;
        this.color=color;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Object doInBackground(Object[] objects) {
        final RouteDrawer routeDrawer = new RouteDrawer.RouteDrawerBuilder(map)
                .withColor(color)
                .withWidth(10)
                .withAlpha(0.5f)
                .withMarkerIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .build();
        RouteRest routeRest = new RouteRest();
        routeRest.getJsonDirections(start,end, TravelMode.WALKING)
                .observeOn(AndroidSchedulers.mainThread())
                .map(s -> new RouteJsonParser<Routes>().parse(s, Routes.class))
                .subscribe(routeDrawer::drawPath);

        return null;
    }
}


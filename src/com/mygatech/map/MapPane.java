package com.mygatech.map;

import java.util.Arrays;
import java.util.List;

import android.support.v4.app.NavUtils;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.MapFragment;
import com.mygatech.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class MapPane extends Activity {
	private GoogleMap mGoogleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.gMap)).getMap();

		if (getIntent() == null) {
			getActionBar().setTitle("GT Map");
			getActionBar().setDisplayHomeAsUpEnabled(true);
			
			LatLng culc = new LatLng(33.7744833, -84.3964000);
			PolygonOptions options = new PolygonOptions();
			options.addAll(getBoundaries());
			
			mGoogleMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
					.snippet("MyGaTech").position(culc))
					.showInfoWindow();
			mGoogleMap.addPolygon(options);
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.getUiSettings().setCompassEnabled(true);
			mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(culc, 15));
	
			onNewIntent(getIntent());
		} else { 
			String name = getIntent().getStringExtra("name");
			double latitude = getIntent().getDoubleExtra("latitude", 33.7744833);
			double longitude = getIntent().getDoubleExtra("longitude", -84.3964000);
			LatLng givenLatLng = new LatLng(latitude, longitude);
			mGoogleMap.addMarker(new MarkerOptions().title(name).position(givenLatLng)).showInfoWindow();
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.getUiSettings().setCompassEnabled(false);
			mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(givenLatLng, 17));
		}
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	    setIntent(intent);

		if(intent != null && intent.getAction() != null ){
//    		Log.e("check", "Intent: " + intent.getAction().toString());
    	}
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        	// when the word that matches clicked    		
        	Uri uri = getIntent().getData();
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
    		
    		if (cursor != null){
    			cursor.moveToFirst();
    			int iName = cursor.getColumnIndexOrThrow(MapDatabase.KEY_NAME);
    			int iLat = cursor.getColumnIndexOrThrow(MapDatabase.LATITUDE);
    			int iLong = cursor.getColumnIndexOrThrow(MapDatabase.LONGITUDE);
    			String name = cursor.getString(iName);
    			double latitude = cursor.getDouble(iLat);
    			double longitude = cursor.getDouble(iLong);
    			LatLng temp = new LatLng(latitude,longitude);
//    			Log.e("check",name + " at " + temp.toString());
    			mGoogleMap.clear();
    			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 17));
    			mGoogleMap.addMarker(new MarkerOptions().title(name).position(temp)).showInfoWindow();
    			mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

    		}
		}else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//			String query = intent.getStringExtra(SearchManager.QUERY);
//			Log.e("check", "Searching for " + query);
		}   
    }

    
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_menu, menu);
		// Add SearchWidget.
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(
				R.id.options_menu_main_search).getActionView();

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.options_menu_main_search:
            onSearchRequested();
            return true;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private List<LatLng> getBoundaries() {
//		LatLng southWest = new LatLng(33.7714167, -84.3961000);
//		LatLng northEast = new LatLng(33.7815000, -84.3916667);
		List<LatLng> points = Arrays.asList((new LatLng(33.78115726640807,
				-84.39166774973273)), (new LatLng(33.78096999957544,
				-84.39165702089667)), (new LatLng(33.7807738148354,
				-84.39159264788032)), (new LatLng(33.78044386675922,
				-84.39147463068366)), (new LatLng(33.78017634035896,
				-84.39137807115912)), (new LatLng(33.77973046116828,
				-84.39122786745429)), (new LatLng(33.77940050907284,
				-84.39123859629035)), (new LatLng(33.779025967316166,
				-84.39123859629035)), (new LatLng(33.77882086042205,
				-84.39126005396247)), (new LatLng(33.77850874029302,
				-84.39123859629035)), (new LatLng(33.778285796647396,
				-84.39123859629035)), (new LatLng(33.77816094795237,
				-84.39121713861823)), (new LatLng(33.778018263506674,
				-84.39121713861823)), (new LatLng(33.7778220720058,
				-84.39119568094611)), (new LatLng(33.77758129091326,
				-84.39116349443793)), (new LatLng(33.777269166267466,
				-84.3911205790937)), (new LatLng(33.77713539821397,
				-84.39108839258552)), (new LatLng(33.777001629951585,
				-84.3910669349134)), (new LatLng(33.776957040484376,
				-84.39105620607734)), (new LatLng(33.776939204690976,
				-84.39055195078254)), (new LatLng(33.77691516816669,
				-84.39036662690341)), (new LatLng(33.777204999430374,
				-84.39043099991977)), (new LatLng(33.77751712430998,
				-84.39050073735416)), (new LatLng(33.77781587134376,
				-84.39053292386234)), (new LatLng(33.77781141244047,
				-84.39027543179691)), (new LatLng(33.77781587134376,
				-84.38993747346103)), (new LatLng(33.77781587134376,
				-84.38965315930545)), (new LatLng(33.77781587134376,
				-84.38944931142032)), (new LatLng(33.77781141244047,
				-84.38925619237125)), (new LatLng(33.77781141244047,
				-84.3890684377402)), (new LatLng(33.77781141244047,
				-84.38892896287143)), (new LatLng(33.77781141244047,
				-84.38882167451084)), (new LatLng(33.77764371570592,
				-84.38878169283271)), (new LatLng(33.77743860550202,
				-84.38880315050483)), (new LatLng(33.77727808413026,
				-84.38879242166877)), (new LatLng(33.77719782333158,
				-84.38881387934089)), (new LatLng(33.77704621939559,
				-84.38881387934089)), (new LatLng(33.77691245099394,
				-84.38881387934089)), (new LatLng(33.7768767793849,
				-84.38872804865241)), (new LatLng(33.77686786148032,
				-84.3884920142591)), (new LatLng(33.7768767793849,
				-84.38821306452155)), (new LatLng(33.7768767793849,
				-84.38800921663642)), (new LatLng(33.7768767793849,
				-84.38776245340705)), (new LatLng(33.7768767793849,
				-84.3875907920301)), (new LatLng(33.776885697288556,
				-84.38739767298102)), (new LatLng(33.77676976446862,
				-84.38733329996467)), (new LatLng(33.77662707770561,
				-84.38734402880073)), (new LatLng(33.77650222659302,
				-84.38733329996467)), (new LatLng(33.77590472232071,
				-84.3873225711286)), (new LatLng(33.77569068992579,
				-84.3873225711286)), (new LatLng(33.775440984789164,
				-84.38734402880073)), (new LatLng(33.77528937774398,
				-84.38734402880073)), (new LatLng(33.77508426190281,
				-84.38734402880073)), (new LatLng(33.77512885234485,
				-84.38873877748847)), (new LatLng(33.77513777043046,
				-84.38969364389777)), (new LatLng(33.77517344276367,
				-84.38986530527472)), (new LatLng(33.77521803315928,
				-84.38997259363532)), (new LatLng(33.77586905029204,
				-84.39012279734015)), (new LatLng(33.77678760029728,
				-84.3903481028974)), (new LatLng(33.776841107760994,
				-84.39049830660224)), (new LatLng(33.776841107760994,
				-84.39078798517585)), (new LatLng(33.776850025668374,
				-84.39100256189704)), (new LatLng(33.775967148335184,
				-84.39087381586432)), (new LatLng(33.77557475548859,
				-84.39079871401191)), (new LatLng(33.77523586931101,
				-84.39070215448737)), (new LatLng(33.77490589990262,
				-84.3906163237989)), (new LatLng(33.77456701107917,
				-84.39054122194648)), (new LatLng(33.7745224203447,
				-84.3905626796186)), (new LatLng(33.77445107512129,
				-84.3905626796186)), (new LatLng(33.77437972983847,
				-84.39058413729072)), (new LatLng(33.7742727118028,
				-84.39058413729072)), (new LatLng(33.77306874969,
				-84.3905626796186)), (new LatLng(33.77225174585631,
				-84.39059000462294)), (new LatLng(33.77143125045404,
				-84.3906543776393)), (new LatLng(33.77113345661617,
				-84.39073434099555)), (new LatLng(33.768957314497406,
				-84.39082017168403)), (new LatLng(33.76889488337521,
				-84.39163556322455)), (new LatLng(33.76889488337521,
				-84.39205398783088)), (new LatLng(33.770018636615376,
				-84.39208617433906)), (new LatLng(33.77091941230113,
				-84.39206471666694)), (new LatLng(33.77123156008186,
				-84.39200034365058)), (new LatLng(33.77127615252912,
				-84.39598074182868)), (new LatLng(33.7717309941656,
				-84.39625969156623)), (new LatLng(33.7720431389896,
				-84.39648499712348)), (new LatLng(33.77249797655439,
				-84.39689269289374)), (new LatLng(33.772729853599365,
				-84.39726820215583)), (new LatLng(33.77290822012996,
				-84.39782610163093)), (new LatLng(33.77303307648045,
				-84.3985771201551)), (new LatLng(33.773220360665,
				-84.39977874979377)), (new LatLng(33.7734700722742,
				-84.40132370218635)), (new LatLng(33.773586009559644,
				-84.4018923304975)), (new LatLng(33.77367519198015,
				-84.40218200907111)), (new LatLng(33.77381788365986,
				-84.40258970484138)), (new LatLng(33.77396949330915,
				-84.40289011225104)), (new LatLng(33.77432622083735,
				-84.4034587405622)), (new LatLng(33.77527154160336,
				-84.40447797998786)), (new LatLng(33.77651114453568,
				-84.40593710169196)), (new LatLng(33.7774742768771,
				-84.40681686624885)), (new LatLng(33.77725133053907,
				-84.40668812021613)), (new LatLng(33.77713539821397,
				-84.40657010301948)), (new LatLng(33.77703730150864,
				-84.40649500116706)), (new LatLng(33.776885697288556,
				-84.40636625513434)), (new LatLng(33.7767608465529,
				-84.40624823793769)), (new LatLng(33.777830989811015,
				-84.40703144297004)), (new LatLng(33.778223372322635,
				-84.40718164667487)), (new LatLng(33.77859791758879,
				-84.40726747736335)), (new LatLng(33.77899921427086,
				-84.40734257921576)), (new LatLng(33.77998015380102,
				-84.40734257921576)), (new LatLng(33.78149612915968,
				-84.4073318503797)), (new LatLng(33.78146937688592,
				-84.40580835565925)), (new LatLng(33.781478294311434,
				-84.4050787948072)), (new LatLng(33.78148721173603,
				-84.40416684374213)), (new LatLng(33.78149612915968,
				-84.40355530008674)), (new LatLng(33.78148721173603,
				-84.40289011225104)), (new LatLng(33.78148721173603,
				-84.40233221277595)), (new LatLng(33.78150504658241,
				-84.40164556726813)), (new LatLng(33.78148721173603,
				-84.40106621012092)), (new LatLng(33.78150504658241,
				-84.40004697069526)), (new LatLng(33.78150504658241,
				-84.39933886751533)), (new LatLng(33.781513964004205,
				-84.39870586618781)), (new LatLng(33.78157986929589,
				-84.39782124012709)), (new LatLng(33.78161553894635,
				-84.39707022160292)), (new LatLng(33.78237003217387,
				-84.39710726961493)), (new LatLng(33.78251270937025,
				-84.39693560823798)), (new LatLng(33.78298532588549,
				-84.39692487940192)), (new LatLng(33.78307449852042,
				-84.39660301432014)), (new LatLng(33.783056664000846,
				-84.39630260691047)), (new LatLng(33.782976408616875,
				-84.39623823389411)), (new LatLng(33.78292290498578,
				-84.3961094878614)), (new LatLng(33.782896153157715,
				-84.39598074182868)), (new LatLng(33.782878318601014,
				-84.39578762277961)), (new LatLng(33.78290507043465,
				-84.39561596140265)), (new LatLng(33.78290507043465,
				-84.39540138468146)), (new LatLng(33.78290507043465,
				-84.39519753679633)), (new LatLng(33.78278022847279,
				-84.39502587541938)), (new LatLng(33.782735642013726,
				-84.39478984102607)), (new LatLng(33.78267322093203,
				-84.39455380663276)), (new LatLng(33.782307610825725,
				-84.39452162012458)), (new LatLng(33.7818974122632,
				-84.39452162012458)), (new LatLng(33.781656642626714,
				-84.39446797594428)), (new LatLng(33.781531798845016,
				-84.39429631456733)), (new LatLng(33.78154963368211,
				-84.39402809366584)), (new LatLng(33.78154963368211,
				-84.39370622858405)), (new LatLng(33.78155855109928,
				-84.39335217699409)), (new LatLng(33.78155855109928,
				-84.39299812540412)), (new LatLng(33.78158530334517,
				-84.39259042963386)), (new LatLng(33.78160313817112,
				-84.39225783571601)), (new LatLng(33.78159422075861,
				-84.39200034365058)), (new LatLng(33.781531798845016,
				-84.39185013994575)), (new LatLng(33.781478294311434,
				-84.39177503809333)));
		return points;
	}
	
}

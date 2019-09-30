package com.kloudsync.techexcel.contact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.rong.message.LocationMessage;


public class ContactMap extends Activity {
	
	private ArrayList<Customer> mList = new ArrayList<Customer>();
	private boolean isCustomer;
	
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	private Context context;

	// 定位相关
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private boolean isFirstIn = true;
	private boolean isDialog;
	private boolean isLocation;
	private double mLatitude;
	private double mLongtitude;
	// 自定义定位图标
	private BitmapDescriptor mIconLocation;
	private MyOrientationListener myOrientationListener;
	private float mCurrentX;
	private LocationMode mLocationMode;

	// 覆盖物相关
	private BitmapDescriptor mMarker;
	private BitmapDescriptor mMarker2;
//	private RelativeLayout mMarkerLy;s
	
	//之前的覆盖物相关
	private Marker markers = null;
	
	float density;
	
	LocationMessage Msg;	

    public ImageLoader imageLoader;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());
//		第一步，创建公交线路规划检索实例；
		mSearch = RoutePlanSearch.newInstance();
		setContentView(R.layout.activity_contactmap);
		
		this.context = this;
		
		isDialog = getIntent().getBooleanExtra("isDialog", false);
		isLocation = getIntent().getBooleanExtra("isLocation", false);
		mList = (ArrayList<Customer>) getIntent().getSerializableExtra("mList");
		isCustomer = getIntent().getBooleanExtra("isCustomer", true);
		initView();
		initLocation();
		initMarker();
		setMapListener();
		if(!isDialog){
			addOverlays(mList);
		}
		if(isLocation){
			Customer cus = (Customer) getIntent().getSerializableExtra("Customer");
			mList = new ArrayList<Customer>();
			mList.add(cus);
			addOverlays(mList);
		}
	}
	
	private void setMapListener() {
        imageLoader=new ImageLoader(getApplicationContext()); 
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@SuppressLint("NewApi") @Override
			public boolean onMarkerClick(Marker marker)
			{
				Bundle extraInfo = marker.getExtraInfo();
				final Customer customer = (Customer) extraInfo.getSerializable("info");

				/*InfoWindow infoWindow;
				TextView tv = new TextView(context);
				tv.setBackgroundResource(R.drawable.location_tips);
				tv.setPadding(30, 20, 30, 50);
				tv.setText(customer.getName());
				tv.setTextColor(Color.parseColor("#ffffff"));

				final LatLng latLng = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
				p.y -= 47;
				LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);
				
				infoWindow = new InfoWindow(tv, ll, 0);
				mBaiduMap.showInfoWindow(infoWindow);*/

				InfoWindow infoWindow;
				View view = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.map_customer, null);
//				view.setPadding(30, 20, 120 * density, 180 * density);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_gender = (TextView) view
						.findViewById(R.id.tv_gender);
				TextView tv_age = (TextView) view.findViewById(R.id.tv_age);
				TextView tv_serviceTimes = (TextView) view
						.findViewById(R.id.tv_serviceTimes);
				TextView tv_description = (TextView) view
						.findViewById(R.id.tv_description);
				TextView tv_address = (TextView) view
						.findViewById(R.id.tv_address);
				CircleImageView img_head = (CircleImageView) view
						.findViewById(R.id.img_head);
				ImageView img_crown = (ImageView) view
						.findViewById(R.id.img_crown);
				ImageView img_new = (ImageView) view
						.findViewById(R.id.img_new);	
				
				view.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(),
								isCustomer ? UserDetail.class
										: MemberDetail.class);
						intent.putExtra("Customer", customer);
						startActivity(intent);

					}
				});

				String url = customer.getUrl();
				if (null == url || url.length() < 1) {
					img_head.setImageResource(R.drawable.hello);			
				}else{
					imageLoader.DisplayImage(customer.getUrl(), img_head);
				}
				img_crown.setVisibility(customer.isCrown() ? View.VISIBLE
						: View.GONE);
				img_new.setVisibility(customer.isNew() ? View.VISIBLE
						: View.GONE);

				tv_name.setText(customer.getName());
				tv_gender.setText(customer.getSex().equals("2")? "女" : "男");
				if(customer.getSex().equals("0")){
					tv_gender.setText("");
				}
				tv_age.setText(customer.getAge() + "岁");
				tv_serviceTimes.setText(isCustomer ? ("服务次数：" + customer.getServiceCount() + "次") : customer.getTitle());
				tv_description.setText(isCustomer ? (customer.getSymptom()) : customer.getSummary());
				tv_address.setText(customer.getCurrentPosition());
				
				LinearLayout myLayout = (LinearLayout) view
						.findViewById(R.id.lin_problem);
				myLayout.removeAllViews();
				int size = 0;
				if(customer.getFocusPoints() != null){
					size = (customer.getFocusPoints().size() > 3 ? 3 : customer
						.getFocusPoints().size());
				}
				for (int i = 0; i < size; i++) {
					String problem = customer.getFocusPoints().get(i);
					TextView tv_problem = new TextView(context);
					tv_problem.setText(problem + "");
					tv_problem.setBackground(getResources().getDrawable(R.drawable.contact_tv_problem));
					tv_problem.setPadding(10, 0, 10, 0);
					tv_problem.setSingleLine(true);
					tv_problem.setEllipsize(TruncateAt.END);
					tv_problem.setTextColor(getResources().getColor(R.color.white));
//					tv_problem.setBackgroundColor(getResources().getColor(R.color.green));
					tv_problem.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(0, 0, 10, 0);
					                      
					tv_problem.setLayoutParams(lp);  
					myLayout.addView ( tv_problem ) ;
				}
				final LatLng latLng = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
				p.y -= 47;				
				LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);
				
				infoWindow = new InfoWindow(view, ll, -50);
				mBaiduMap.showInfoWindow(infoWindow);
				
				if (markers != null) {
					markers.setIcon(mMarker);
				}
				marker.setIcon(mMarker2);
				markers = marker;
//				mMarkerLy.setVisibility(View.VISIBLE);
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{

			@Override
			public boolean onMapPoiClick(MapPoi arg0)
			{
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0)
			{
				if(markers != null){
					markers.setIcon(mMarker);
				}
				markers = null;
//				mMarkerLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();
			}
		});
		
	}

	private void initView()
	{
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
	}
	
	private void initLocation()
	{

		mLocationMode = LocationMode.NORMAL;
		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);

		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		// 初始化图标
		mIconLocation = BitmapDescriptorFactory
				.fromResource(R.drawable.navi_map_gps_locked);
		myOrientationListener = new MyOrientationListener(context);

		myOrientationListener
				.setOnOrientationListener(new MyOrientationListener.OnOrientationListener()
				{
					@Override
					public void onOrientationChanged(float x)
					{
						mCurrentX = x;
					}
				});

	}
	
	private void initMarker()
	{
		mMarker = BitmapDescriptorFactory.fromResource(R.drawable.locationmap_d);
		mMarker2 = BitmapDescriptorFactory.fromResource(R.drawable.locationmap_a);
	}
	
	private class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{

			MyLocationData data = new MyLocationData.Builder()//
					.direction(mCurrentX)//
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			mBaiduMap.setMyLocationData(data);
			// 设置自定义图标
			MyLocationConfiguration config = new MyLocationConfiguration(
					mLocationMode, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);

			// 更新经纬度
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();
			
			mlocation = location;
			
			if (isFirstIn)
			{
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;
//				JustText();

				/*Log.e("location",
						location.getAddrStr() + ":" + location.getAltitude()
								+ ":" + location.getBuildingID() + ":"
								+ location.getBuildingName() + ":"
								+ location.getCity() + ":"
								+ location.getCityCode() + ":"
								+ location.getCoorType() + ":"
								+ location.getCountry() + ":"
								+ location.getCountryCode() + ":"
								+ location.getDerect() + ":"
								+ location.getDirection() + ":"
								+ location.getDistrict() + ":"
								+ location.getFloor() + ":"
								+ location.getLatitude() + ":"
								+ location.getLocationDescribe() + ":"
								+ location.getLocationWhere() + ":"
								+ location.getLocType() + ":"
								+ location.getLongitude() + ":"
								+ location.getNetworkLocationType() + ":"
								+ location.getOperators() + ":"
								+ location.getProvince() + ":"
								+ location.getRadius() + ":"
								+ location.getSatelliteNumber() + ":"
								+ location.getSemaAptag() + ":"
								+ location.getSpeed() + ":"
								+ location.getStreet() + ":"
								+ location.getStreetNumber() + ":"
								+ location.getTime() + ":"
								+ location.getAddress());*/
				Uri uri = Uri
						.parse("http://api.map.baidu.com/staticimage?center="
								+ mLongtitude + "," + mLatitude
								+ "&width=400&height=300&zoom=11&markers="
								+ mLongtitude + "," + mLatitude
								+ "&markerStyles=m,A");
				Msg = LocationMessage.obtain(mLatitude, mLongtitude,
						location.getAddrStr(), uri);
				if (isDialog) {
					if(!isLocation){
						LocationGoToDialog();
					}
				}else{
					Toast.makeText(context, location.getAddrStr(),
						Toast.LENGTH_SHORT).show();
				}
			}

		}


		
	}
	private void LocationGoToDialog() {
//		ConversationActivity.getInstance();
		/*ConversationActivity.getLastLocationCallback().onSuccess(Msg);
		ConversationActivity.getInstance().setLastLocationCallback(null);*/
		AppConfig.LOCATIONMESSAGE = Msg;
		AppConfig.ISLOCATIONS = true;
		finish();
	}
	
	/**
	 * 添加覆盖物
	 * 
	 * @param infos
	 */
	private void addOverlays(List<Customer> infos)
	{
		mBaiduMap.clear();
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;
		for (Customer info : infos)
		{
			// 经纬度
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			// 图标
			options = new MarkerOptions().position(latLng).icon(mMarker)
					.zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(options);
			Bundle arg0 = new Bundle();
			arg0.putSerializable("info", info);
			marker.setExtraInfo(arg0);
		}

		/*MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(msu);*/

	}
	
	
	RoutePlanSearch mSearch;
	BDLocation mlocation;
	public void JustText() {
		// TODO Auto-generated method stub
//		第二步，创建公交线路规划检索监听者；
		OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {  
		    public void onGetWalkingRouteResult(WalkingRouteResult result) {  
		        //    
		    	if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
		            Toast.makeText(ContactMap.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();  
		        }  
		        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {  
		            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息  
//		            result.getSuggestAddrInfo();
		            return;  
		        }  
		        if (result.error == SearchResult.ERRORNO.NO_ERROR) {  
		            WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);  
		            mBaiduMap.setOnMarkerClickListener(overlay);  
		            overlay.setData(result.getRouteLines().get(0));
		            overlay.addToMap();  
					overlay.zoomToSpan();
					
			    	Log.e("getSuggestAddrInfo", result.getSuggestAddrInfo()
							+ "");
		        }  
		    }  
		    public void onGetTransitRouteResult(TransitRouteResult result) {  
		        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
		            Toast.makeText(ContactMap.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();  
		        }  
		        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {  
		            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息  
//		            result.getSuggestAddrInfo();
		            return;  
		        }  
		        if (result.error == SearchResult.ERRORNO.NO_ERROR) {  
		            TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);  
		            mBaiduMap.setOnMarkerClickListener(overlay);  
		            overlay.setData(result.getRouteLines().get(0));  
		            overlay.addToMap();  
					overlay.zoomToSpan();
					
					Log.e("getRouteLines", result.getRouteLines().size()
							+ ":"
							+ result.getRouteLines().get(0).getTitle()
							+ ":"
							+ result.getRouteLines().get(0).getDistance()
							+ ":"
							+ result.getRouteLines().get(0).getDuration()
							+ ":"
							+ result.getRouteLines().get(0).getAllStep().get(0)
									.getDistance()
							+ ":"
							+ result.getRouteLines().get(0).getStarting()
									.getTitle()
							+ ":"
							+ result.getRouteLines().get(0).getTerminal()
									.getTitle());
		        }  
		    }  
		    public void onGetDrivingRouteResult(DrivingRouteResult result) {  
		        //   
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					Toast.makeText(ContactMap.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
				}
				if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
					// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
					// result.getSuggestAddrInfo();
					return;
				}
				if (result.error == SearchResult.ERRORNO.NO_ERROR) {
					DrivingRouteOverlay overlay = new DrivingRouteOverlay(
							mBaiduMap);
					mBaiduMap.setOnMarkerClickListener(overlay);
					overlay.setData(result.getRouteLines().get(0));
					overlay.addToMap();
					overlay.zoomToSpan();

//					Log.e("getTaxiInfo", (null == result.getTaxiInfo())+ "");
					Log.e("getTaxiInfo", result.getTaxiInfo()+ "");
					/*Log.e("getTaxiInfo", result.getTaxiInfo().getDesc() + ":"
							+ result.getTaxiInfo().getDistance() + ":"
							+ result.getTaxiInfo().getDuration() + ":"
							+ result.getTaxiInfo().getPerKMPrice() + ":"
							+ result.getTaxiInfo().getStartPrice() + ":"
							+ result.getTaxiInfo().getTotalPrice());*/
				}

			}
		};
		
//		第三步，设置公交线路规划检索监听者；
		mSearch.setOnGetRoutePlanResultListener(listener);
//		第四步，准备检索起、终点信息；
//		PlanNode stNode = PlanNode.withCityNameAndPlaceName("上海", "上海新天地");  
		PlanNode stNode = PlanNode.withLocation(new LatLng(mlocation.getLatitude(), mlocation.getLongitude()));
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("上海", "城隍庙老街");
//		第五步，发起公交线路规划检索；
		/*mSearch.transitSearch((new TransitRoutePlanOption())  
		    .from(stNode)  
		    .city(mlocation.getCity())  
		    .to(enNode));
		
		mSearch.drivingSearch((new DrivingRoutePlanOption())  
			    .from(stNode)  
			    .to(enNode));
		
		mSearch.walkingSearch((new WalkingRoutePlanOption())  
			    .from(stNode)
			    .to(enNode));*/
		
		
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		MobclickAgent.onPageStart("ContactMap"); 
	    MobclickAgent.onResume(this);  
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// 开启定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		// 开启方向传感器
		myOrientationListener.start();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//		mMapView.onDestroy();
		mMapView.onPause();
        MobclickAgent.onPageEnd("ContactMap");
	    MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		// 停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// 停止方向传感器
		myOrientationListener.stop();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mSearch.destroy();
	}

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.tran_in2, R.anim.tran_out2);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

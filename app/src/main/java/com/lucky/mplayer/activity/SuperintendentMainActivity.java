package com.lucky.mplayer.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lucky.mplayer.MyApplication;
import com.lucky.mplayer.R;
import com.lucky.mplayer.model.UserLocation;
import com.lucky.mplayer.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class SuperintendentMainActivity extends BaseActivity implements SensorEventListener {


    @BindView(R.id.left_btn)
    ImageView leftBtn;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.right_text)
    TextView rightText;
    @BindView(R.id.right1_text)
    TextView right1Text;
    @BindView(R.id.bmapView)
    MapView mMapView;

    // 定位相关
    private LocationClient mLocClient;
    //    private MyLocationListener myListener = new MyLocationListener();
    // 定位图层显示方式
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    // 初始化地图
    private BaiduMap mBaiduMap;
    // 是否首次定位
    private boolean isFirstLoc = true;
    // 是否开启定位图层
    private boolean isLocationLayerEnable = true;
    private MyLocationData myLocationData;

    BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.ditubiaozhu);
    BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.ditubiaozhu2);
    List<Marker> markers = new ArrayList<>();
    List<Marker> markers1 = new ArrayList<>();

    List<UserLocation> users = new ArrayList<>();

    long time;
    private InfoWindow mInfoWindow;

//    public String currentLocation;



    public int start_time;



    public int end_time;

    public int st_for_d1 = 9;
    public int et_for_d1 = 11;

    public int st_for_d2 = 14;
    public int et_for_d2 = 16;

    public boolean isD1Classing = false;
    public boolean isD2Classing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_minjing);
        ButterKnife.bind(this);
        time = System.currentTimeMillis();
        title.setText("map");
        rightText.setText("");
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setBuildingsEnabled(true);
        MapStatus mapStatus = mBaiduMap.getMapStatus();
        if (null != mapStatus) {
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            // 设置地图状态
            mBaiduMap.setMapStatus(mapStatusUpdate);
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        // 定位初始化
//        initLocation();

        LatLng ll = new LatLng(31.279986, 120.746497);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
//                Intent intent = new Intent(SuperintendentMainActivity.this, MinjingUserInfoActivity.class);
//                intent.putExtra("user", users.get(markers.indexOf(marker)));
//                startActivity(intent);


                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(baidumapsdk.demo.R.drawable.popup);
                InfoWindow.OnInfoWindowClickListener listener = null;
                button.setText(users.get(markers.indexOf(marker)).getName());
                button.setTextColor(Color.BLACK);
                button.setWidth(300);
                // InfoWindow点击事件监听接口
                listener = new InfoWindow.OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
//                        LatLng latLng = marker.getPosition();
//                        LatLng latLngNew = new LatLng(latLng.latitude + 0.005, latLng.longitude + 0.005);
//                        marker.setPosition(latLngNew);
//                        // 隐藏地图上的所有InfoWindow
//                        mBaiduMap.hideInfoWindow();

                        double d1=DistanceUtil. getDistance(marker.getPosition(), markers1.get(0).getPosition());
                        double d2=DistanceUtil. getDistance(marker.getPosition(), markers1.get(1).getPosition());
                    show((int)d1,(int)d2);
                    }
                };
                LatLng latLng = marker.getPosition();
                // 创建InfoWindow
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), latLng, -47, listener);
                // 显示 InfoWindow, 该接口会先隐藏其他已添加的InfoWindow, 再添加新的InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);

                return false;
            }
        });

        getUsers();
        //获得设置好的当前位置，时间
        Intent intent = getIntent();
//        currentLocation = intent.getStringExtra("location");
        start_time = intent.getIntExtra("start_time",0);
        end_time = intent.getIntExtra("end_time",0);
        //检查两个自习室是否在预定时间端内上课
        //第一个自习室判断
        if(start_time<=st_for_d1&&end_time>st_for_d1&&end_time<=et_for_d1){
           isD1Classing = true;
        }
        if (start_time<=st_for_d1&&end_time>=et_for_d1){
            isD1Classing = true;
        }

        if (start_time>=st_for_d1&&start_time<et_for_d1&&end_time>=et_for_d1){
            isD1Classing = true;
        }
        if(start_time>=st_for_d1&&end_time<=et_for_d1){
            isD1Classing = true;
        }
        //第二个
        if(start_time<=st_for_d2&&end_time>st_for_d2&&end_time<=et_for_d2){
            isD2Classing = true;
        }
        if (start_time<=st_for_d2&&end_time>=et_for_d2){
            isD2Classing = true;
        }

        if (start_time>=st_for_d2&&start_time<et_for_d2&&end_time>=et_for_d2){
            isD2Classing = true;
        }
        if(start_time>=st_for_d2&&end_time<=et_for_d2){
            isD2Classing = true;
        }
    }

    public void show(int d1,int d2) {
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(SuperintendentMainActivity.this);
        final View dialogView = LayoutInflater.from(SuperintendentMainActivity.this)
                .inflate(R.layout.dialog_add_user,null);
        final TextView edit_text =
                (TextView) dialogView.findViewById(R.id.edit_text);
        final  TextView edit_text1 =
                (TextView) dialogView.findViewById(R.id.edit_text1);
            if(isD1Classing==false && isD2Classing==false){
                edit_text.setText(d1+"m");
                edit_text1.setText(d2+"m");
                customizeDialog.setTitle("Distance");
            }

            if (isD2Classing==true&&isD1Classing==false){
                edit_text1.setText("room 2 is not available");
                edit_text.setText(d1+"m");
                customizeDialog.setTitle("Distance");

            }
            if (isD1Classing==true &&isD2Classing ==false){
                edit_text1.setText(d2+"m");
                edit_text.setText("room 1 is not available");
                customizeDialog.setTitle("Distance");

            }
        if(isD1Classing==true && isD2Classing==true){
            edit_text.setText("room 1 is not available");
            edit_text1.setText("room 2 is not available");
        }
            customizeDialog.setView(dialogView);
            customizeDialog.setPositiveButton("comfim",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            customizeDialog.show();
        }



    private void getUsers() {
        BmobQuery<UserLocation> query = new BmobQuery<UserLocation>();
        query.findObjects(new FindListener<UserLocation>() {

            @Override
            public void done(List<UserLocation> paramList, BmobException paramBmobException) {
                hideLoading();
                if (paramList != null && paramList.size() > 0) {
                    users.clear();
                    users.addAll(paramList);
                    for (UserLocation user : users) {
                        if ("1".equals(user.getType())) {
                            LatLng llA = new LatLng(user.getLatitude(), user.getLongitude());
                            MarkerOptions ooA = new MarkerOptions().position(llA).icon(bd).
                                    zIndex(9).draggable(true);
                            // 掉下动画 
//                            ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                            Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));
                            markers.add(mMarker);
                        } else {
                            LatLng llA = new LatLng(user.getLatitude(), user.getLongitude());
                            MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA).
                                    zIndex(9).draggable(true);
                            // 掉下动画 
//                            ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
                            Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));
                            markers.add(mMarker);
                            markers1.add(mMarker);
                        }


                    }

                } else {
                    ToastUtil.showToast(SuperintendentMainActivity.this, "not data currently");
                }
            }
        });
    }


    /**
     * 定位初始化
     */
    public void initLocation() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
//        mLocClient = new LocationClient(this);
//        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    /**
     * //     * 定位SDK监听函数
     * //
     */
//    public class MyLocationListener implements BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            // MapView 销毁后不在处理新接收的位置
//            if (location == null || mMapView == null) {
//                return;
//            }
//            mCurrentLat = location.getLatitude();
//            mCurrentLon = location.getLongitude();
//
//            mCurrentAccracy = location.getRadius();
//            myLocationData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
//                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
//                    .latitude(location.getLatitude())
//                    .longitude(location.getLongitude())
//                    .build();
//            mBaiduMap.setMyLocationData(myLocationData);
//
//
//            if (System.currentTimeMillis() - time > 60 * 1000) {
//                MyApplication.user.setLatitude(mCurrentLat);
//                MyApplication.user.setLongitude(mCurrentLon);
//                MyApplication.user.update(MyApplication.user.getObjectId(), new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//
//                    }
//                });
//
//                UserLocation mUserLocation = new UserLocation();
//                mUserLocation.setUserTel(MyApplication.user.getUserTel());
//                mUserLocation.setLatitude(mCurrentLat);
//                mUserLocation.setLongitude(mCurrentLon);
//                mUserLocation.save(new SaveListener<String>() {
//                    @Override
//                    public void done(String s, BmobException e) {
//
//                    }
//                });
//                time = System.currentTimeMillis();
//            }
//
//
//        }
//    }
    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }


    @OnClick({R.id.left_btn, R.id.right_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                Intent intent = new Intent();
                intent.setClass(SuperintendentMainActivity.this, UserCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.right_text:
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

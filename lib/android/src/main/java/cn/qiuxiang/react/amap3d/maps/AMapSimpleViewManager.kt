package cn.qiuxiang.react.amap3d.maps

import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp

/**
 * Created by lee on 2019/1/23.
 */
@Suppress("unused")
internal class AMapSimpleViewManager : ViewGroupManager<AMapSimpleView>() {

    companion object {
        val ANIMATE_TO = 1
        val MAPTYPE_TO = 2
        val CHANGEUI_TO = 3
        val CHANGE_FOLLOW = 4//改变地图跟随状态
        val ADD_CELLS = 10//未使用，不通过js调用
        val CHANGE_CELLS_VISIBLE = 11
        val REMOVE_CELLS = 12
        val ADD_TESTPOINT = 16
        val ADD_TESTPOINTS = 17
        val CHANGE_TESTPOINT_VISIBLE = 18
        val CHANGE_RENDER_FIELD = 19
    }

    override fun createViewInstance(reactContext: ThemedReactContext): AMapSimpleView {
        return AMapSimpleView(reactContext)
    }

    override fun getName(): String {
        return "AMapSimpleView"
    }

    override fun getCommandsMap(): Map<String, Int> {
        return mapOf("animateTo" to ANIMATE_TO, "maptypeTo" to MAPTYPE_TO, "changeUI" to CHANGEUI_TO,
                "changeFollow" to CHANGE_FOLLOW,
                "addCells" to ADD_CELLS, "changeCellsVisible" to CHANGE_CELLS_VISIBLE, "removeCells" to REMOVE_CELLS,
                "addTestPoint" to ADD_TESTPOINT, "addTestPoints" to ADD_TESTPOINTS,
                "changeTestPointVisible" to CHANGE_TESTPOINT_VISIBLE, "changeRenderField" to CHANGE_RENDER_FIELD)
    }

    override fun receiveCommand(root: AMapSimpleView?, commandId: Int, args: ReadableArray?) {
        when (commandId) {
            ANIMATE_TO -> root?.animateTo(args)
            MAPTYPE_TO -> root?.maptypeTo(args)
            CHANGE_FOLLOW -> root?.following = true
            ADD_TESTPOINT -> root?.addTestPoint(args)
            ADD_TESTPOINTS -> root?.addTestPoints(args)
            CHANGE_TESTPOINT_VISIBLE -> root?.changeTestPointVisible(args)
            CHANGE_RENDER_FIELD -> root?.changeRenderField(args)
            ADD_CELLS -> root?.addCells(args)
            CHANGE_CELLS_VISIBLE -> root?.changeCellsVisible(args)
            REMOVE_CELLS -> root?.removeAllCells()
        }
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        return MapBuilder.of(
                "onPress", MapBuilder.of("registrationName", "onPress"),
                "onLongPress", MapBuilder.of("registrationName", "onLongPress"),
                "onMarkerPress", MapBuilder.of("registrationName", "onMarkerPress"),
//                "onAnimateCancel", MapBuilder.of("registrationName", "onAnimateCancel"),
//                "onAnimateFinish", MapBuilder.of("registrationName", "onAnimateFinish"),
                "onStatusChange", MapBuilder.of("registrationName", "onStatusChange"),
                "onStatusChangeComplete", MapBuilder.of("registrationName", "onStatusChangeComplete"),
                "onLocation", MapBuilder.of("registrationName", "onLocation"),
                "onFollowStateChanged", MapBuilder.of("registrationName", "onFollowStateChanged")//跟随状态发生改变

        )
    }

    @ReactProp(name = "locationEnabled")
    fun setMyLocationEnabled(view: AMapSimpleView, enabled: Boolean) {
        view.setLocationEnabled(enabled)
    }

    @ReactProp(name = "showsCompass")
    fun setCompassEnabled(view: AMapSimpleView, show: Boolean) {
        view.map.uiSettings.isCompassEnabled = show
    }

    @ReactProp(name = "showsLocationButton")
    fun setMyLocationButtonEnabled(view: AMapSimpleView, enabled: Boolean) {
        view.map.uiSettings.isMyLocationButtonEnabled = enabled
    }

    @ReactProp(name = "maxZoomLevel")
    fun setMaxZoomLevel(view: AMapSimpleView, zoomLevel: Float) {
        view.map.maxZoomLevel = zoomLevel
    }

    @ReactProp(name = "minZoomLevel")
    fun setMinZoomLevel(view: AMapSimpleView, zoomLevel: Float) {
        view.map.minZoomLevel = zoomLevel
    }

    @ReactProp(name = "zoomLevel")
    fun setZoomLevel(view: AMapSimpleView, zoomLevel: Float) {
        view.map.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
    }

    @ReactProp(name = "zoomEnabled")
    fun setZoomGesturesEnabled(view: AMapSimpleView, enabled: Boolean) {
        view.map.uiSettings.isZoomGesturesEnabled = enabled
    }


    @ReactProp(name = "rotateEnabled")
    fun setRotateGesturesEnabled(view: AMapSimpleView, enabled: Boolean) {
        view.map.uiSettings.isRotateGesturesEnabled = enabled
    }

    @ReactProp(name = "tiltEnabled")
    fun setTiltGesturesEnabled(view: AMapSimpleView, enabled: Boolean) {
        view.map.uiSettings.isTiltGesturesEnabled = enabled
    }

    @ReactProp(name = "coordinate")
    fun moveToCoordinate(view: AMapSimpleView, coordinate: ReadableMap) {
        view.map.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(
                coordinate.getDouble("latitude"),
                coordinate.getDouble("longitude"))))
    }

    @ReactProp(name = "region")
    fun setRegion(view: AMapSimpleView, region: ReadableMap) {
        view.setRegion(region)
    }


    @ReactProp(name = "tilt")
    fun changeTilt(view: AMapSimpleView, tilt: Float) {
        view.map.moveCamera(CameraUpdateFactory.changeTilt(tilt))
    }

    @ReactProp(name = "rotation")
    fun changeRotation(view: AMapSimpleView, rotation: Float) {
        view.map.moveCamera(CameraUpdateFactory.changeBearing(rotation))
    }

    @ReactProp(name = "mapType")
    fun setMapType(view: AMapSimpleView, mapType: String) {
        when (mapType) {
            "standard" -> view.map.mapType = AMap.MAP_TYPE_NORMAL
            "satellite" -> view.map.mapType = AMap.MAP_TYPE_SATELLITE
        }
    }

//    @ReactProp(name = "showsLabels")
//    fun showMapText(view: AMapSimpleView, show: Boolean) {
//        view.map.showMapText(show)
//    }
//
//    @ReactProp(name = "showsIndoorMap")
//    fun showIndoorMap(view: AMapSimpleView, show: Boolean) {
//        view.map.showIndoorMap(show)
//    }
//
//    @ReactProp(name = "showsIndoorSwitch")
//    fun setIndoorSwitchEnabled(view: AMapSimpleView, show: Boolean) {
//        view.map.uiSettings.isIndoorSwitchEnabled = show
//    }
//
//    @ReactProp(name = "showsBuildings")
//    fun showBuildings(view: AMapSimpleView, show: Boolean) {
//        view.map.showBuildings(show)
//    }

//    @ReactProp(name = "showsZoomControls")
//    fun setZoomControlsEnabled(view: AMapSimpleView, enabled: Boolean) {
//        view.map.uiSettings.isZoomControlsEnabled = enabled
//    }
//
//    @ReactProp(name = "showsScale")
//    fun setScaleControlsEnabled(view: AMapSimpleView, enabled: Boolean) {
//        view.map.uiSettings.isScaleControlsEnabled = enabled
//    }
//    @ReactProp(name = "scrollEnabled")
//    fun setScrollGesturesEnabled(view: AMapSimpleView, enabled: Boolean) {
//        view.map.uiSettings.isScrollGesturesEnabled = enabled
//    }
//    @ReactProp(name = "limitRegion")
//    fun setLimitRegion(view: AMapSimpleView, limitRegion: ReadableMap) {
//        view.setLimitRegion(limitRegion)
//    }


}
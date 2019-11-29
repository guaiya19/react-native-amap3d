package cn.qiuxiang.react.amap3d.maps

import android.content.Context
import cn.qiuxiang.react.amap3d.dt.CellObj
import cn.qiuxiang.react.amap3d.dt.MapElementType
import cn.qiuxiang.react.amap3d.toLatLng
import cn.qiuxiang.react.amap3d.toWritableMap
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.TextureMapView
import com.amap.api.maps.model.*
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.google.gson.JsonArray

class AMapCellView(context: Context) : TextureMapView(context) {

    private val eventEmitter: RCTEventEmitter = (context as ThemedReactContext).getJSModule(RCTEventEmitter::class.java)
    private val locationStyle by lazy {
        val locationStyle = MyLocationStyle()
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        locationStyle
    }

    private val fixedCells = mutableListOf<Marker>()
    private val normalCells = mutableListOf<Marker>()

    private var selectMarker: Marker? = null //选中标记
    //private var selectMarkerList = HashMap<String, Marker>()

    var cellVisibleLevel: Float = 17F //小区可见的最小显示级别，未使用

    fun addCells(args: ReadableArray?) {
        val fixed: Boolean = args?.getBoolean(0)!!
        val targets = args?.getArray(1)!!
        val size: Int = args?.getInt(3)!!
        if (fixed) {
            val ranges: JsonArray = args?.getArray(2)!! as JsonArray
            addFixedCells(targets, ranges, size)
        } else {
            val visible = args?.getBoolean(4)!!
            addNormalCells(targets,size,visible)
        }
    }

    private fun addFixedCells(targets: ReadableArray, ranges: JsonArray, size: Int) {
        clearMarkerList(fixedCells)
    }

    private fun addNormalCells(targets: ReadableArray, size: Int, visible: Boolean) {
        clearMarkerList(normalCells)
    }

    fun addCell(args: ReadableArray?) {

    }

    fun removeCells(args: ReadableArray?) {
        val isFixedCells = args?.getBoolean(0)!!
        when (isFixedCells) {
            true -> clearMarkerList(fixedCells)
            false -> clearMarkerList(normalCells)
        }
    }

    fun changeRenderField(args: ReadableArray?) {

    }

    fun changeCellsVisible(args: ReadableArray?) {
        val visible: Boolean = args?.getBoolean(1)!!
        val isFixedCells = args?.getBoolean(0)!!
        val cells = when (isFixedCells) {
            true -> fixedCells
            false -> normalCells
        }
        cells?.let {
            if (it.any()) {
                for (marker in it) {
                    marker.isVisible = visible
                }
            }
        }
    }

    fun changeCellsStyle(args: ReadableArray?) {

    }

    fun selectCell(args: ReadableArray?) {
        if (selectMarker != null)
            removeSelectMarker()
        val isFixedCells = args?.getBoolean(0)!!
        val id: String? = args?.getString(1)
        if (!id.isNullOrEmpty()) {
            //val marker = selectMarkerList[id]!!
            val extData = (selectMarker?.`object` as ExtraData)!!

            val bitmapDescriptor: BitmapDescriptor? = when (isFixedCells) {
                true -> {
                    val cellObject = extData.elementValue!!
                    CellObj.getSelectBitmapDescriptor(cellObject["COVER_TYPE"].asString, cellObject["NET_NAME"].asString, extData.elementSize)

                }
                else -> null
            }
            //selectMarker = marker
            selectMarker?.setIcon(bitmapDescriptor)
        } else {
            //removeSelectMarker()
        }

    }

    private fun removeSelectMarker() {
        selectMarker?.let {
            if (it.isInfoWindowShown)
                it.hideInfoWindow()
            val extData = (it.`object` as ExtraData)!!
            val cellObject = extData.elementValue!!
            val bitmapDescriptor =
                    when (extData.elementType) {
                        MapElementType.mark_Cell.value ->
                            CellObj.getOriginalBitmapDescriptor(cellObject["COVER_TYPE"].asString, cellObject["NET_NAME"].asString, extData.elementSize)
                        else -> null
                    }

            it.setIcon(bitmapDescriptor)
            selectMarker = null
        }
    }

    private fun clearMarkerList(markerList: MutableList<Marker>) {
        if (markerList.any()) {
            for (marker in markerList) {
                marker?.remove()
            }
            markerList.clear()
        }
    }

    fun setLocationEnabled(enabled: Boolean) {
        map.isMyLocationEnabled = enabled
        map.myLocationStyle = locationStyle
    }


    fun animateTo(args: ReadableArray?) {
        val currentCameraPosition = map.cameraPosition
        val target = args?.getMap(0)!!
        val duration = args.getInt(1)

        var coordinate = currentCameraPosition.target
        var zoomLevel = currentCameraPosition.zoom
        var tilt = currentCameraPosition.tilt
        var rotation = currentCameraPosition.bearing

        if (target.hasKey("coordinate")) {
            coordinate = target.getMap("coordinate").toLatLng()
        }

        if (target.hasKey("zoomLevel")) {
            zoomLevel = target.getDouble("zoomLevel").toFloat()
        }

        if (target.hasKey("tilt")) {
            tilt = target.getDouble("tilt").toFloat()
        }

        if (target.hasKey("rotation")) {
            rotation = target.getDouble("rotation").toFloat()
        }

        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                CameraPosition(coordinate, zoomLevel, tilt, rotation))
        map.animateCamera(cameraUpdate, duration.toLong(), null)//animateCallback)
    }


    fun emitCameraChangeEvent(event: String, position: CameraPosition?) {
        position?.let {
            val data = it.target.toWritableMap()
            data.putDouble("zoomLevel", it.zoom.toDouble())
            data.putDouble("tilt", it.tilt.toDouble())
            data.putDouble("rotation", it.bearing.toDouble())
            if (event == "onStatusChangeComplete") {
                val southwest = map.projection.visibleRegion.latLngBounds.southwest
                val northeast = map.projection.visibleRegion.latLngBounds.northeast
                data.putDouble("latitudeDelta", Math.abs(southwest.latitude - northeast.latitude))
                data.putDouble("longitudeDelta", Math.abs(southwest.longitude - northeast.longitude))
            }
            emit(id, event, data)
        }
    }

    fun emit(id: Int?, name: String, data: WritableMap = Arguments.createMap()) {
        id?.let { eventEmitter.receiveEvent(it, name, data) }
    }

    init {
        super.onCreate(null)
        //project = map.projection

        map.mapType = AMap.MAP_TYPE_NORMAL
        map.uiSettings.isRotateGesturesEnabled = false
        map.uiSettings.isTiltGesturesEnabled = false
        map.uiSettings.isIndoorSwitchEnabled = false
        map.uiSettings.isScaleControlsEnabled = false
        map.uiSettings.isCompassEnabled = false
        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isZoomControlsEnabled = false

        map.setOnMapClickListener { latLng ->
            removeSelectMarker()
            emit(id, "onPress", latLng.toWritableMap())
        }

        map.setOnMapLongClickListener { latLng ->
            emit(id, "onLongPress", latLng.toWritableMap())
        }

        map.setOnMyLocationChangeListener { location ->
            val event = Arguments.createMap()
            event.putDouble("latitude", location.latitude)
            event.putDouble("longitude", location.longitude)
            event.putDouble("accuracy", location.accuracy.toDouble())
            event.putDouble("altitude", location.altitude)
            event.putDouble("speed", location.speed.toDouble())
            event.putInt("timestamp", location.time.toInt())
            emit(id, "onLocation", event)
        }

        map.setOnMarkerClickListener { marker ->
            true
        }

        map.setOnMapTouchListener { event ->

        }

        map.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChangeFinish(position: CameraPosition?) {
                emitCameraChangeEvent("onStatusChangeComplete", position)

            }

            override fun onCameraChange(position: CameraPosition?) {
                emitCameraChangeEvent("onStatusChange", position)
            }
        })

        map.setOnMapLoadedListener {
            val location = map.myLocation
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(LatLng(location.latitude, location.longitude), 17f, 0f, 0f))
            map.animateCamera(cameraUpdate, 500, null)//animateCallback)
            //map.moveCamera(CameraUpdateFactory.zoomTo(17f))
        }


    }
}
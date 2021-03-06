// @flow
import React from "react";
import PropTypes from "prop-types";
import {
  processColor,
  requireNativeComponent,
  ViewPropTypes
} from "react-native";
import { LatLng, Region } from "../PropTypes";
import Component from "../Component";
import { MapStatus, LocationStyle } from "../maps/MapView";

export default class SimpleMapView extends Component<any> {
  static propTypes = {
    ...ViewPropTypes,

    /**
     * 地图类型
     *
     * - standard: 标准地图
     * - satellite: 卫星地图
     * - navigation: 导航地图
     * - night: 夜间地图
     * - bus: 公交地图
     */
    mapType: PropTypes.oneOf([
      "standard",
      "satellite",
      "navigation",
      "night",
      "bus"
    ]),

    /**
     * 设置定位图标的样式
     */
    locationStyle: LocationStyle,

    /**
     * 设置定位模式 默认 LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
     *
     * @platform android
     */
    locationType: PropTypes.oneOf([
      "show",
      "locate",
      "follow",
      "map_rotate",
      "location_rotate",
      "location_rotate_no_center",
      "follow_no_center",
      "map_rotate_no_center"
    ]),

    /**
     * 是否启用定位
     */
    locationEnabled: PropTypes.bool,

    /**
     * 定位间隔(ms)，默认 2000
     *
     * @platform android
     */
    locationInterval: PropTypes.number,

    /**
     * 定位的最小更新距离
     *
     * @platform ios
     */
    distanceFilter: PropTypes.number,

    /**
     * 是否显示室内地图
     */
    showsIndoorMap: PropTypes.bool,

    /**
     * 是否显示室内地图楼层切换控件
     *
     * TODO: 似乎并不能正常显示
     */
    showsIndoorSwitch: PropTypes.bool,

    /**
     * 是否显示3D建筑
     */
    showsBuildings: PropTypes.bool,

    /**
     * 是否显示文本标签
     */
    showsLabels: PropTypes.bool,

    /**
     * 是否显示指南针
     */
    showsCompass: PropTypes.bool,

    /**
     * 是否显示放大缩小按钮
     *
     * @platform android
     */
    showsZoomControls: PropTypes.bool,

    /**
     * 是否显示比例尺
     */
    showsScale: PropTypes.bool,

    /**
     * 是否显示定位按钮
     *
     * @platform android
     */
    showsLocationButton: PropTypes.bool,

    /**
     * 是否显示路况
     */
    showsTraffic: PropTypes.bool,

    /**
     * 最大缩放级别
     */
    maxZoomLevel: PropTypes.number,

    /**
     * 最小缩放级别
     */
    minZoomLevel: PropTypes.number,

    /**
     * 当前缩放级别，取值范围 [3, 20]
     */
    zoomLevel: PropTypes.number,

    /**
     * 中心坐标
     */
    coordinate: LatLng,

    /**
     * 显示区域
     */
    region: Region,

    /**
     * 限制地图只能显示某个矩形区域
     */
    limitRegion: Region,

    /**
     * 倾斜角度，取值范围 [0, 60]
     */
    tilt: PropTypes.number,

    /**
     * 旋转角度
     */
    rotation: PropTypes.number,

    /**
     * 是否启用缩放手势，用于放大缩小
     */
    zoomEnabled: PropTypes.bool,

    /**
     * 是否启用滑动手势，用于平移
     */
    scrollEnabled: PropTypes.bool,

    /**
     * 是否启用旋转手势，用于调整方向
     */
    rotateEnabled: PropTypes.bool,

    /**
     * 是否启用倾斜手势，用于改变视角
     */
    tiltEnabled: PropTypes.bool,

    /**
     * 点击事件
     *
     * @param {{ nativeEvent: LatLng }}
     */
    onPress: PropTypes.func,

    /**
     * 长按事件
     *
     * @param {{ nativeEvent: LatLng }}
     */
    onLongPress: PropTypes.func,

    /**
     * 标记物点击事件
     * @param{object}
     */
    onMarkerPress: PropTypes.func,

    /**
     * 跟随状态改变事件
     * @param {{following:boolen}}
     */
    onFollowStateChanged: PropTypes.func,

    /**
     * 获取框选经纬度范围
     */
    onMapRectSelected: PropTypes.func,

    /**
     * 关键字查询结果，目前只针对工参查询，返回结果ids：array<string>
     */
    onKeywordSearched: PropTypes.func,

    /**
     * 地图倾斜角度发生变化
     */
    onMapTiltChanged: PropTypes.func,

    /**
     * 定位事件
     *
     * @param {{
     *   nativeEvent: {
     *     timestamp: number,
     *     speed: number,
     *     accuracy: number,
     *     altitude: number,
     *     longitude: number,
     *     latitude: number,
     *   }
     * }}
     */
    onLocation: PropTypes.func,

    /**
     * 动画完成事件
     */
    onAnimateFinish: PropTypes.func,

    /**
     * 动画取消事件
     */
    onAnimateCancel: PropTypes.func,

    /**
     * 地图状态变化事件
     *
     * @param {{
     *   nativeEvent: {
     *     longitude: number,
     *     latitude: number,
     *     rotation: number,
     *     zoomLevel: number,
     *     tilt: number,
     *   }
     * }}
     */
    onStatusChange: PropTypes.func,

    /**
     * 地图状态变化完成事件
     *
     * @param {{
     *   nativeEvent: {
     *     longitude: number,
     *     latitude: number,
     *     longitudeDelta: number,
     *     latitudeDelta: number,
     *     rotation: number,
     *     zoomLevel: number,
     *     tilt: number,
     *   }
     * }}
     */
    onStatusChangeComplete: PropTypes.func
  };

  name = "AMapSimpleView";

  /**
   * 动画过渡到某个状态（坐标、缩放级别、倾斜度、旋转角度）
   */
  animateTo(target: MapStatus, duration?: number = 500) {
    this.sendCommand("animateTo", [target, duration]);
  }

  /**
   *
   * @param {object:'standard'} target
   */
  maptypeTo(target: object) {
    this.sendCommand("maptypeTo", [target]);
  }

  changeFollow(follow: boolean = true) {
    this.sendCommand("changeFollow", [follow]);
  }

  changeMeasure(measuring: boolean = false) {
    this.sendCommand("changeMeasure", [measuring]);
  }
  /******************通用命令部分 */

  /**
   * 地图上添加一组对象
   * @param {*} elementType
   * @param {*} target
   * @param {*} size
   * @param {*} testStatus
   * @param {boolean} selected 是否处于列表选中状态
   */
  addElements(
    elementType: number,
    target: Array,
    size: number = 72,
    testStatus: string = "RUNNING",
    selected: boolen = false,
    visible: boolen = true
  ) {
    if (target && target.length > 0)
      this.sendCommand("addElements", [
        elementType,
        target,
        size,
        testStatus,
        selected,
        visible
      ]);
  }

  /**
   * 地图上添加单个对象
   * @param {*} elementType
   * @param {*} target
   * @param {*} size
   * @param {*} testStatus
   */
  addElement(
    elementType: number,
    target: object,
    size: number = 72,
    testStatus: string = "STOPPED"
  ) {
    this.sendCommand("addElement", [elementType, target, size, testStatus]);
  }

  /**
   * 设置对象选中
   * @param {*} elementType
   * @param {*} id
   */
  selectElement(elementType: number, id: string = "") {
    this.sendCommand("selectElement", [elementType, id]);
  }

  /**
   * 清楚地图选中状态
   */
  removeSelection(){
    console.log("IdleMap removeSelection");
    this.sendCommand("removeSelection",[]);
  }

  /**
   * 设置一类对象的是否可见
   * @param {*} elementType
   * @param {*} visible
   * @param {*} data
   */
  changeElementsVisible(
    elementType: number,
    visible: boolean,
    data: Array = null
  ) {
    this.sendCommand("changeElementsVisible", [elementType, visible, data]);
  }

  changeElementsStyle(elementType: number, style: object) {
    this.sendCommand("changeElementsStyle", [elementType, style]);
  }

  removeElements(elementType: number) {
    this.sendCommand("removeElements", [elementType]);
  }

  changeRenderField(field: string, ranges: Array, size: number = 48) {
    this.sendCommand("changeRenderField", [field, size, ranges]);
  }

  fromScreenRect(elementType: number, rect: object) {
    this.sendCommand("fromScreenRect", [elementType, rect]);
  }

  render() {
    const props = { ...this.props };
    if (props.locationStyle) {
      if (props.locationStyle.strokeColor) {
        props.locationStyle.strokeColor = processColor(
          props.locationStyle.strokeColor
        );
      }
      if (props.locationStyle.fillColor) {
        props.locationStyle.fillColor = processColor(
          props.locationStyle.fillColor
        );
      }
    }
    return <AMapSimpleView {...props} />;
  }
}

const AMapSimpleView = requireNativeComponent("AMapSimpleView", SimpleMapView);

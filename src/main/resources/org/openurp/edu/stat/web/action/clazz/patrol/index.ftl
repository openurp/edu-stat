[@b.head/]
[@base.semester_bar value=semester name="semester.id"]
  <div style="float:right">
  [@b.a href="!exportMonitor?semester.id="+semester.id target="_blank"]<i class="fa-solid fa-file-excel"></i>导出全部可视化考勤数据[/@]
  &nbsp;
  [@b.a href="!allDevices?semester.id="+semester.id target="_blank"]<i class="fa-solid fa-file-zipper"></i>导出监控端巡课任务[/@]
  </div>
[/@]
<div class="search-container">
    <div class="search-panel">
    [@b.form name="campusSearchForm" action="!search" target="campuslist" title="ui.searchForm" theme="search"]
      [@b.select name="campus.id" items=campuses  label="校区"/]
      [@b.select name="building.id" items=buildings  label="教学楼"/]
      [@b.select name="weekday" items={"1":"周一","2":'周二',"3":'周三',"4":'周四',"5":'周五',"6":'周六',"7":'周日'} value="1" label="周几" required="true"/]
      [@b.select name="beginUnit" items=units value=units?first option="indexno,name" label="开始节次" required="true"/]
      [@b.select name="monitor" items={"1":"有摄像头","0":'无'} value="" label="监控摄像"/]
      <input type="hidden" name="orderBy" value="campus.code"/>
      <input type="hidden" name="semester.id" value="${semester.id}"/>
      <input type="hidden" name="" value="1"/>
    [/@]
    </div>
    <div class="search-list">
      [@b.div id="campuslist" href="!search?semester.id="+semester.id +"&weekday=1&beginUnit=1"/]
    </div>
  </div>
[@b.foot/]

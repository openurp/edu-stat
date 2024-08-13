[@b.head/]
<div class="container">
<p style="text-align:center;margin:0px">${semester.schoolYear}学期${semester.name}学期 教师生日上课情况
[#if (request.getHeader('x-requested-with')??) || Parameters['x-requested-with']??]
  [@b.a href="!report?semesterId="+semester.id target="_blank" class="notprint"]<i class="fas fa-print"></i>打印[/@]&nbsp;&nbsp;
  [@b.a href="!excel?semesterId="+semester.id target="_blank" class="notprint"]<i class="fas fa-file-excel"></i>导出[/@]
[/#if]
</p>
[@b.grid items=lessons var="lesson" sortable="false" class="border-1px border-blue"]
    [@b.row]
        [@b.col title="序号" width="5%"]${lesson_index+1}[/@]
        [@b.col property="day" title="日期" width="10%"]${lesson.day}[/@]
        [@b.col property="activity.time.beginAt" title="开始时间" width="9%"]
           ${lesson.activity.time.beginAt}~${lesson.activity.time.endAt}
        [/@]
        [@b.col property="teacher.code" title="教师工号" width="8%"/]
        [@b.col property="teacher.name" title="教师姓名" width="9%"/]
        [@b.col property="activity.clazz.crn" title="课程序号" width="6%"/]
        [@b.col property="activity.clazz.course.name" title="课程名称" width="20%"/]
        [@b.col property="activity.clazz.enrollment.stdCount" title="人数" width="7%"/]
        [@b.col title="校区" width="9%"]
           [#list lesson.activity.rooms as r]${(r.campus.name)!}[#if r_has_next],[/#if][/#list]
        [/@]
        [@b.col title="上课教室" width="17%"]
          [#list lesson.activity.rooms as r]${r.name}[#if r_has_next],[/#if][/#list]
        [/@]
    [/@]
[/@]
</div>
[@b.foot/]

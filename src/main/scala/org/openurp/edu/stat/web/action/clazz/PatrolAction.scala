/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.edu.stat.web.action.clazz

import org.beangle.commons.collection.{Collections, Order}
import org.beangle.commons.file.zip.Zipper
import org.beangle.commons.io.Files
import org.beangle.commons.lang.time.{WeekDay, WeekTime, Weeks}
import org.beangle.commons.lang.{Strings, SystemInfo}
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.doc.transfer.exporter.ExportContext
import org.beangle.template.freemarker.Configurator
import org.beangle.webmvc.support.ActionSupport
import org.beangle.webmvc.view.{Stream, View}
import org.beangle.web.servlet.util.RequestUtils
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.edu.model.CourseUnit
import org.openurp.base.edu.service.TimeSettingService
import org.openurp.base.model.{Campus, Project, Semester}
import org.openurp.base.resource.model.{Building, Device}
import org.openurp.edu.clazz.model.{Clazz, ClazzActivity}
import org.openurp.starter.web.support.ProjectSupport

import java.io.File

/** 巡课检查
 */
class PatrolAction extends ActionSupport, EntityAction[Clazz], ProjectSupport {

  var entityDao: EntityDao = _

  var timeSettingService: TimeSettingService = _

  var freemarkerConfigurer: Configurator = _

  def index(): View = {
    given project: Project = getProject

    val semester = getSemester
    put("project", project)
    put("semester", semester)
    put("campuses", findInSchool(classOf[Campus]))
    put("buildings", findInSchool(classOf[Building]))
    val setting = timeSettingService.get(project, semester, None)
    put("units", setting.units.sortBy(_.indexno))
    forward()
  }

  def search(): View = {
    given project: Project = getProject

    val weekday = WeekDay.of(getInt("weekday").get)
    val beginUnit = getInt("beginUnit").get
    val query = buildQuery(weekday, beginUnit, getBoolean("monitor"))
    put("activities", entityDao.search(query))
    put("semester", getSemester)
    forward()
  }

  private def buildQuery(weekday: WeekDay, beginUnit: Int, monitor: Option[Boolean])(using project: Project): OqlBuilder[ClazzActivity] = {
    val semester = getSemester
    val years = Collections.newSet[Int]
    years.add(semester.beginOn.getYear)
    years.add(semester.endOn.getYear)

    val query = OqlBuilder.from(classOf[ClazzActivity], "activity")
    query.where("activity.clazz.project=:project", project)
    query.where("activity.clazz.semester=:semester", semester)
    query.where("activity.beginUnit=:beginUnit", beginUnit)
    query.where("activity.time.startOn in(:startOns)", years.map(y => WeekTime.getStartOn(y, weekday)))

    monitor foreach { b =>
      val prefix = if (b) " " else " not "
      query.where(prefix + "exists(from activity.rooms as r join r.devices as d " +
        "where :beginOn between d.beginOn and coalesce(d.endOn,current_date) and d.deviceType.name like :monitor)",
        semester.beginOn, "%摄像%")
    }
    getInt("building.id") match
      case Some(id) =>
        query.where("exists(from activity.rooms as r where r.building.id=:buildingId)", id)
      case None =>
        getInt("campus.id") foreach { campusId =>
          query.where("exists(from activity.rooms as r where r.campus.id=:campusId)", campusId)
        }
    query
  }

  def devices(): View = {
    given project: Project = getProject

    val semester = getSemester
    val weekday = WeekDay.of(getInt("weekday").get)
    val beginUnit = getInt("beginUnit").get
    val activities = entityDao.search(buildQuery(weekday, beginUnit, Some(true)))
    val devices = activities.flatten(x => x.rooms.flatten(r => r.activeDevices(semester)))
      .distinct.sortBy(_.room.map(_.name).getOrElse(""))
    put("weekday", weekday)
    put("beginUnit", beginUnit)
    put("devices", devices)
    forward()
  }

  def allDevices(): View = {
    given project: Project = getProject

    val semester = getSemester
    val setting = timeSettingService.get(project, semester, None)
    val template = freemarkerConfigurer.config.getTemplate("org/openurp/edu/stat/web/action/clazz/patrol/devices.ftl")

    val dir = new File(SystemInfo.tmpDir + "/kedacom")
    val files = Collections.newBuffer[File]
    val mata = new StringBuilder("[TimedTask]\n")
    dir.mkdir()
    val weekdayNames = Array("", "周一", "周二", "周三", "周四", "周五", "周六", "周日")
    for (weekday <- WeekDay.values; unit <- setting.units) {
      val activities = entityDao.search(buildQuery(weekday, unit.indexno, Some(true)))
      val devices = activities.flatten(x => x.rooms.flatten(r => r.activeDevices(semester)))
        .distinct.sortBy(_.room.map(_.name).getOrElse(""))
      val fileName = weekday.id + "_" + unit.indexno + ".iniDataFile"
      val file = new File(dir.getAbsolutePath + "/" + fileName)
      if (devices.nonEmpty) {
        files.addOne(file)
        //确保任务的名称和xml中的任务名是一致的
        mata.append(s"${weekdayNames(weekday.id)} 第${Strings.leftPad(unit.indexno.toString, 2, '0')}节=${fileName}\n")
        dump2File(weekday, unit, devices, file)
      }
    }
    val metaFile = new File(SystemInfo.tmpDir + "/kedacom/cu.txt")
    Files.writeString(metaFile, mata.toString)
    files.addOne(metaFile)
    val zipfile = new File(SystemInfo.tmpDir + "/kedacom/devices.zip")
    Zipper.zip(dir, files, zipfile, "utf-8")
    Stream(zipfile).cleanup(() => Files.remove(dir))
  }

  private def dump2File(weekday: WeekDay, unit: CourseUnit, devices: Iterable[Device], file: File): Unit = {
    val model = Collections.newMap[String, Any]
    model.put("devices", devices)
    model.put("weekday", weekday)
    model.put("beginUnit", unit.indexno)
    val result = freemarkerConfigurer.render("org/openurp/edu/stat/web/action/clazz/patrol/devices.ftl", model)
    Files.writeString(file, result)
  }

  /** 导出可视化考勤系统监控所需格式
   */
  def exportMonitor(): View = {
    val semesterId = getIntId("semester")
    val semester = entityDao.get(classOf[Semester], semesterId)
    val query = OqlBuilder.from(classOf[ClazzActivity], "ca")
    query.where("ca.clazz.semester =:semester", semester)
    query.orderBy(Order.parse("ca.clazz.crn"))
    val activities = entityDao.search(query)
    val datas = Collections.newBuffer[Array[Any]]
    val monitorActivities = activities.filter(x => x.rooms.exists(_.devices.exists(_.deviceType.name.contains("摄像"))))
    monitorActivities foreach { activity =>
      val data = Array.ofDim[Any](15)
      val clazz = activity.clazz
      data(0) = semester.year.startYear
      data(1) = semester.name
      data(2) = clazz.teachDepart.name
      data(3) = clazz.course.code
      data(4) = clazz.course.name
      data(5) = clazz.crn
      data(6) = clazz.courseType.name

      val teacher = activity.teachers.headOption.orElse(clazz.teachers.headOption)
      teacher foreach { t =>
        data(7) = t.staff.code
        data(8) = t.name
        data(9) = t.department.name
      }
      activity.rooms.headOption foreach { r =>
        data(10) = r.name
      }
      data(11) = activity.time.weekday.id
      data(12) = s"${activity.beginUnit}-${activity.endUnit}"

      var s = semester.beginOn
      val activityStartOn = activity.time.startOn
      while (s.getDayOfWeek != activityStartOn.getDayOfWeek) {
        s = s.plusDays(1)
      }
      val yearWeeks = Math.abs(Weeks.between(s, activityStartOn))

      var a = activity.time.weekstate.toString.reverse
      if (activity.time.startOn.getYear != semester.beginOn.getYear) {
        a = Strings.repeat('0', yearWeeks) + a.substring(1) //忽略第0周
      } else {
        a = a.substring(yearWeeks + 1) //忽略第0周
      }
      data(13) = Strings.rightPad(a, 31, '0').substring(0, 31)
      data(14) = clazz.enrollment.stdCount
      datas.addOne(data)
    }
    RequestUtils.setContentDisposition(response, "activity.xlsx")
    val context = ExportContext.excel(Some("可视化考勤数据"), Strings.split("学年度,学期,开课院系,课程代码,课程名称,课程序号,课程类型,教师工号,教师姓名,教师所在院系,教室,周几,小节,周状态,人数").toSeq)
    context.setItems(datas).writeTo(response.getOutputStream)
    null
  }
}

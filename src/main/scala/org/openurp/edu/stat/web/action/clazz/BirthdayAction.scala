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

import org.beangle.commons.lang.time.WeekTime
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.transfer.excel.ExcelItemWriter
import org.beangle.data.transfer.exporter.ExportContext
import org.beangle.web.action.annotation.{mapping, param}
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.edu.model.Teacher
import org.openurp.base.model.{Project, Semester}
import org.openurp.edu.clazz.model.{Clazz, Session}
import org.openurp.edu.stat.web.helper.BirthdayLesson
import org.openurp.starter.edu.helper.ProjectSupport

import java.time.LocalDate
import scala.collection.mutable

class BirthdayAction extends EntityAction[Clazz] with ProjectSupport {

  def index(): View = {
    put("project", getProject)
    val semester = getId("semester") match {
      case Some(sid) => entityDao.get(classOf[Semester], sid.toInt)
      case None => getCurrentSemester
    }
    put("currentSemester", semester)
    forward()
  }

  @mapping("report/{semesterId}")
  def report(@param("semesterId") semesterId: String): View = {
    val semester = entityDao.get(classOf[Semester], semesterId.toInt)
    val p = getProject
    val lessons = stat(p, semester)
    put("semester", semester)
    put("lessons", lessons)
    forward()
  }

  @mapping("excel/{semesterId}")
  def excel(@param("semesterId") semesterId: String): View = {
    val semester = entityDao.get(classOf[Semester], semesterId.toInt)
    val p = getProject
    val lessons = stat(p, semester)
    val cxt = new ExportContext
    val res = response
    res.setContentType("application/vnd.ms-excel;charset=GBK")
    res.setHeader("Content-Disposition", s"attachment;filename=${semester.code}_birthday_lesson.xls")
    val writer = new ExcelItemWriter(cxt, response.getOutputStream)
    writer.writeTitle("教师生日上课情况", Array("序号", "日期", "开始时间", "教师工号", "教师姓名", "课程序号", "课程名称", "人数", "校区", "上课教室"))
    var idx = 1
    lessons foreach { l =>
      val time = l.session.time.beginAt.toString() + "~" + l.session.time.endAt.toString()
      val campuses = l.session.rooms.map(_.campus.name).toSet.mkString(",")
      val rooms = l.session.rooms.map(_.name).toSet.mkString(",")
      val clazz = l.session.clazz
      val data = Array(idx, l.day, time, l.teacher.user.code, l.teacher.user.name, clazz.crn, clazz.course.name, clazz.enrollment.actual, campuses, rooms)
      writer.write(data)
      idx += 1
    }
    writer.close()
    null
  }

  private def stat(p: Project, semester: Semester): collection.Seq[BirthdayLesson] = {
    val query = OqlBuilder.from[Teacher](classOf[Clazz].getName, "cl")
    query.join("cl.teachers", "t")
    query.where("cl.project=:project", p)
    query.where("cl.semester=:semester", semester)
    query.where("t.person is not null")
    query.select("distinct t")
    val teachers = entityDao.search(query).toSet

    val lessons = new mutable.ArrayBuffer[BirthdayLesson]
    val sessionQuery = OqlBuilder.from(classOf[Session], "s")
    sessionQuery.where("s.clazz.project=:project and s.clazz.semester=:semester", p, semester)
    val sessions = entityDao.search(sessionQuery)
    val teacherSessions = sessions.map(x => x.teachers.map(t => t -> x)).flatten.groupMap(_._1)(_._2)
    teachers foreach { t =>
      teacherSessions.get(t) match {
        case Some(sessionList) =>
          val birthday = t.person.get.birthday
          val days = new mutable.ArrayBuffer[LocalDate]
          val day1st = birthday.withYear(semester.beginOn.getYear)
          val day2nd = birthday.withYear(semester.endOn.getYear)

          if (semester.within(day1st)) days += day1st
          if (semester.within(day2nd) && day1st != day2nd) days += day2nd

          days foreach { day =>
            val wt = WeekTime.of(day)
            val daySessions = sessionList.filter { s =>
              s.time.startOn == wt.startOn && s.time.weekstate.isOverlap(wt.weekstate)
            }
            daySessions foreach { session =>
              lessons += BirthdayLesson(day, t, session)
            }
          }
        case None =>
      }
    }
    lessons.sorted
  }

}

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

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.data.model.Entity
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.hr.model.Teacher
import org.openurp.base.model.{Project, Semester}
import org.openurp.code.job.model.ProfessionalTitle
import org.openurp.edu.clazz.model.{Clazz, CourseTaker}
import org.openurp.starter.web.support.ProjectSupport

import scala.collection.mutable.Map

class IndexAction extends ActionSupport, EntityAction[Clazz], ProjectSupport {

  var entityDao: EntityDao = _

  def index(): View = {
    given project: Project = getProject

    put("project", project)
    put("semester", getSemester)
    forward()
  }

  private def addCondition[T <: Entity[_]](query: OqlBuilder[T]): Unit = {
    if (query.alias == "c") query.where("c.enrollment.stdCount>0")
    val semesterId = getId("semester", classOf[Int])

    semesterId foreach { s =>
      query.where(s"${query.alias}.semester.id = :semesterId", s)
    }
  }

  def semester(): View = {
    val query = OqlBuilder.from(classOf[Clazz].getName, "c")
    addCondition(query)
    query.groupBy("c.semester.id,c.semester.schoolYear,c.semester.name")
    query.orderBy("c.semester.schoolYear desc,c.semester.name desc")
    query.select("c.semester.schoolYear,c.semester.name,count(*),count(distinct c.course.id)")
    put("semesters", entityDao.search(query))

    if (getId("semester", classOf[Int]).nonEmpty) {
      var query = OqlBuilder.from(classOf[CourseTaker].getName, "ct")
      addCondition(query)
      query.groupBy("ct.takeType.id,ct.takeType.name")
      query.orderBy("ct.takeType.name")
      query.select("ct.takeType.name, count(*),sum(case when ct.freeListening = true then 1 else 0 end) as free_listen_cnt," +
        "sum(case when ct.alternative = true then 1 else 0 end) as alternative_cnt")
      put("takeTypes", entityDao.search(query))
    }
    forward()
  }

  def department(): View = {
    val query = OqlBuilder.from(classOf[Clazz].getName, "c")
    addCondition(query)
    query.groupBy("c.teachDepart.id,c.teachDepart.name")
    query.orderBy("c.teachDepart.name desc")
    query.select("c.teachDepart.name,count(*)")
    var rs = entityDao.search(query).asInstanceOf[Seq[Array[Any]]]
    rs = rs.sortBy(x => 0 - x(1).asInstanceOf[Long])
    put("departments", rs)
    forward()
  }

  def courseType(): View = {
    val query = OqlBuilder.from(classOf[Clazz].getName, "c")
    addCondition(query)
    query.groupBy("c.courseType.id,c.courseType.name")
    query.orderBy("c.courseType.name desc")
    query.select("c.courseType.name,count(*)")
    var rs = entityDao.search(query).asInstanceOf[Seq[Array[Any]]]
    rs = rs.sortBy(x => 0 - x(1).asInstanceOf[Long])
    put("courseTypes", rs)
    forward()
  }

  def enrollment(): View = {
    val ranges = List((1, 30), (31, 60), (61, 100), (101, 150), (151, 5000))
    val semesterId = getIntId("semester")
    val datas = ranges.map { r =>
      val query = OqlBuilder.from[Long](classOf[Clazz].getName, "c")
      query.where("c.semester.id=:semesterId", semesterId)
      query.where("c.enrollment.stdCount between :from and :to", r._1, r._2)
      query.select("count(*)")
      Array(r._1 + "~" + r._2, entityDao.search(query).head)
    }
    put("datas", datas)
    val query2 = OqlBuilder.from(classOf[Clazz].getName, "c")
    addCondition(query2)
    query2.select("max(c.enrollment.stdCount),min(c.enrollment.stdCount)")
    val rs = entityDao.search(query2)
    put("items", rs)
    forward()
  }

  def electionMode(): View = {
    val query = OqlBuilder.from(classOf[CourseTaker].getName, "ct")
    addCondition(query)
    query.groupBy("ct.electionMode.id,ct.electionMode.name")
    query.orderBy("ct.electionMode.name")
    query.select("ct.electionMode.name, count(*)")
    put("electionModes", entityDao.search(query))
    forward()
  }

  def takeType(): View = {

    forward()
  }

  def teacher(): View = {
    val query = OqlBuilder.from(classOf[Teacher].getName, "t")
    val semesterId = getInt("semester.id")
    semesterId match {
      case Some(value) => {
        val semester = entityDao.get(classOf[Semester], value)
        query.where(s"exists (from ${classOf[Clazz].getName} c where c.semester = :semester" +
          s" and c.enrollment.stdCount >0 and exists (from c.teachers ct where ct = t))", semester)
        put("semester", semester)
      }
      case None => query.where(s"exists (from ${classOf[Clazz].getName} c where exists (from c.teachers ct where ct = t))")
    }
    query.where("t.staff.title is not null")
    query.select("count(*)")
    put("sum", entityDao.search(query))
    query.groupBy("t.staff.title.id, t.staff.title.name")
    query.orderBy("t.staff.title.name")
    query.select("t.staff.title.name,count(*)")
    var rs = entityDao.search(query).asInstanceOf[Seq[Array[Any]]]
    rs = rs.sortBy(x => 0 - x(1).asInstanceOf[Long])
    put("titles", rs)
    forward()
  }

  def teacherTitle(): View = {
    val query = OqlBuilder.from(classOf[Clazz].getName, "c")
    addCondition(query)
    query.join("c.teachers", "t")
    query.groupBy("t.staff.title.id, t.staff.title.name")
    query.orderBy("t.staff.title.name")
    query.select(s"t.staff.title.name,count(c.id),avg(c.course.creditHours)")
    var rs = entityDao.search(query).asInstanceOf[Seq[Array[Any]]]
    rs = rs.sortBy(x => 0 - x(1).asInstanceOf[Long])
    put("titles", rs)
    forward()
  }

  def topN(): View = {
    val query1 = OqlBuilder.from[ProfessionalTitle](classOf[ProfessionalTitle].getName, "pt")
    query1.where(s"exists (from ${classOf[Teacher].getName} t where t.staff.title = pt and " +
      s"exists (from ${classOf[Clazz].getName} c where c.enrollment.stdCount>0 and exists (from c.teachers ct where ct = t)))")
    val titles = entityDao.search(query1)
    val titlesMap = Map[Int, ProfessionalTitle]()
    titles.foreach(title => titlesMap(title.id) = title)
    put("titlesMap", titlesMap)

    val itemData = "title.name,name,department.name,count(*)".split(",")
    itemData(0) = s"t.staff.${itemData(0)}"
    itemData(1) = s"t.${itemData(1)}"
    itemData(2) = s"t.${itemData(2)}"
    val query2 = OqlBuilder.from(classOf[Clazz].getName, "c")
    addCondition(query2)
    query2.join("c.teachers", "t")
    query2.groupBy("t.staff.title.id, t.staff.title.name, t.id, t.name, t.department.id, t.department.name")
    query2.orderBy("t.staff.title.name, count(*) desc")
    query2.select(itemData.mkString(","))
    put("titles", entityDao.search(query2))
    forward()
  }
}

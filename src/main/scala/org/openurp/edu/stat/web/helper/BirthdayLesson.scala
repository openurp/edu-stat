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

package org.openurp.edu.stat.web.helper

import org.openurp.base.edu.model.Teacher
import org.openurp.edu.clazz.model.Session

import java.time.LocalDate

case class BirthdayLesson(day: LocalDate, teacher: Teacher, session: Session) extends Ordered[BirthdayLesson] {
  override def compare(that: BirthdayLesson): Int = {
    var r = day.compareTo(that.day)
    if (r == 0) {
      r = session.time.beginAt.compare(that.session.time.beginAt)
      if (r == 0) {
        r = teacher.name.compareTo(that.teacher.name)
      }
    }
    r
  }
}

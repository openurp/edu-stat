[#if semesters?size gt 1]
  <div class="container" style="display:flex;justify-content:space-around;flex-wrap:wrap">
    [@b.card style="width:100%" class="card-info card-primary card-outline"]
      [@b.card_header]
        <h3>每学期开课的门次数/门数</h3>
      [/@]
      [@b.card_body]
        <table class="table table-hover table-sm" style="width: 100%">
          <thead>
             <th>学年度</th>
             <th>学期</th>
             <th>门次数</th>
             <th>门数</th>
          </thead>
          <tbody>
            [#list semesters as semester]
            <tr>
              <td>${semester[0]}</td>
              <td>${semester[1]}</td>
              <td>${semester[2]}</td>
              <td>${semester[3]}</td>
            </tr>
            [/#list]
          </tbody>
        </table>
      [/@]
    [/@]
  </div>
[#else]
    [@b.card style="width:100%" class="card-info card-primary card-outline"]
      [@b.card_header]
        <h3>总体预览</h3>
      [/@]
      [@b.card_body]
        [#if semesters?size>0]
          [#assign total_course_taker=0]
            [#list takeTypes as takeType][#assign total_course_taker= total_course_taker+ takeType[1]/][/#list]
          开课门次数为<span class="badge badge-primary" style="margin-left: 3px; margin-right: 3px">${semesters?first[2]}</span>，门数为<span class="badge badge-primary" style="margin-left: 3px; margin-right: 3px">${semesters?first[3]}</span>。
          选课总数<span class="badge badge-primary" style="margin-left: 3px; margin-right: 3px">${total_course_taker}</span>

        <table class="table table-hover table-sm" style="width: 100%">
          <thead>
          <th>修读类别</th>
          <th>人数</th>
          <th>免听</th>
          <th>替代</th>
          </thead>
          <tbody>
          [#list takeTypes as takeType]
            <tr>
              <td>${takeType[0]}</td>
              <td>${takeType[1]}</td>
              <td>${takeType[2]}</td>
              <td>${takeType[3]}</td>
            </tr>
          [/#list]
          </tbody>
        </table>
        [#else]
            本学期没有课程
        [/#if]
      [/@]
    [/@]
[/#if]

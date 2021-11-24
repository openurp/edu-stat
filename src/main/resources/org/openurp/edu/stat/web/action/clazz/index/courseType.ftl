  [@b.card style="width:100%" class="card-info card-primary card-outline"]
    [@b.card_header]
      <h3>各课程类别统计</h3>
    [/@]
    [@b.card_body]
      <table class="table table-hover table-sm" style="width: 100%">
        <thead>
           <th>课程类别</th>
           <th>门次数</th>
        </thead>
        <tbody>
          [#list courseTypes as courseType]
          <tr>
            <td>${courseType[0]}</td>
            <td>${courseType[1]}</td>
          </tr>
          [/#list]
        </tbody>
      </table>
    [/@]
  [/@]

  [@b.card style="width:100%" class="card-info card-primary card-outline"]
    [@b.card_header]
      <h3 style="float: left">授课老师的职称分布</h3>
      <span style="position: relative;top: 13px;margin-left: 3px;margin-right: 3px;">总共<span class="badge badge-primary" style="margin-left: 3px; margin-right: 3px">${sum?first}</span>人</span>
    [/@]
    [@b.card_body]
      <table class="table table-hover table-sm" style="width: 100%">
        <thead>
           <th>授课教师职称</th>
           <th>人数</th>
        </thead>
        <tbody>
          [#list titles as title]
          <tr>
            <td>${title[0]}</td>
            <td>${title[1]}</td>
          </tr>
          [/#list]
        </tbody>
      </table>
    [/@]
  [/@]

  [@b.card style="width:100%" class="card-info card-primary card-outline"]
    [@b.card_header]
      <h3>开课最多的前${Parameters.count}位</h3>
    [/@]
    [@b.card_body]
      [#assign index = 0/]
      [#assign iTitle = ""/]
      [#assign count = Parameters.count?number]
      [#list titles as title]
        [#if iTitle != title[0]]
          [#if index gt 0 && index lt count]
                </tbody>
              </table>
          [/#if]
          [#assign index = 0/]
          [#assign iTitle = title[0]/]
              <table class="table table-hover table-sm" style="width: 100%">
                <thead>
                   <th colspan="2">${title[0]}</th>
                   <th>门次数</th>
                </thead>
                <tbody>
        [/#if]
        [#if index lt count]
                  <tr>
                    <td>${title[1]}</td>
                    <td>${title[2]}</td>
                    <td>${title[3]}</td>
                  </tr>
        [/#if]
        [#assign index = index + 1/]
        [#if index == count || index lt count && !title_has_next]
                </tbody>
              </table>
        [/#if]
      [/#list]
    [/@]
  [/@]

    [@b.card style="width:100%; padding-left: 10px" class="card-info card-primary card-outline"]
        [@b.card_header]
            <h3>教学班规模统计</h3>
        [/@]
        <table class="table table-hover table-sm">
            <thead>
            <tr>
                <th>教学班规模</th>
                <th>门次数</th>
            </tr>
            </thead>
            <tbody>
            [#list datas as data]
                <tr>
                    <td>${data[0]}</td>
                    <td>${data[1]}</td>
                </tr>
            [/#list]
            </tbody>
        </table>
        [#if items?size>0 && items?first[0]??]
        <div>
            <span>其中，最大教学班人数<span class="badge badge-primary" style="margin-left: 3px; margin-right: 3px">${items?first[0]}</span>，最小教学班人数<span class="badge badge-primary" style="margin-left: 3px; margin-right: 3px">${items?first[1]}</span>。</span>
        </div>
        [/#if]
    [/@]
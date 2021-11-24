[#if (Parameters.display!"") == "pie"]
  <div id="container_take_type" style="float:left;width:50%;height:400px"></div>
  <script type="text/javascript">
    require(["echarts"],function(echarts) {
     console.log({ "take_type-echarts": echarts });
        var dom = document.getElementById("container_take_type");
        var myChart = echarts.init(dom);
        var app = {};
        option = null;
        option = {
            title: {
                text: '修读类别的分布',
                subtext: '[#if semester?exists]${semester.schoolYear}学年度${semester.name}学期[/#if]',
                left: 'center'
            },
            tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b} : {c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: [[#list takeTypes as takeType]'${takeType[0]?js_string}'[#if takeType_has_next], [/#if][/#list]]
            },
            series: [
                {
                    name: '修读类别',
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '50%'],
                    data: [
                      [#list takeTypes as takeType]
                        {value: ${takeType[1]}, name: '${takeType[0]?js_string}'}[#if takeType_has_next],[/#if]
                      [/#list]
                    ],
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };
        ;
        if (option && typeof option === "object") {
            myChart.setOption(option, true);
        }
      });
  </script>
[#else]
    [@b.card style="width:100%" class="card-info card-primary card-outline"]
      [@b.card_header]
        <h3>修读类别的分布</h3>
      [/@]
      [@b.card_body]
        <table class="table table-hover table-sm" style="width: 100%">
          <thead>
             <th>修读类别</th>
             <th>人数</th>
          </thead>
          <tbody>
            [#list takeTypes as takeType]
            <tr>
              <td>${takeType[0]}</td>
              <td>${takeType[1]}</td>
            </tr>
            [/#list]
          </tbody>
        </table>
      [/@]
    [/@]
[/#if]

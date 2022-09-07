    [@b.card style="width:100%" class="card-info card-primary card-outline"]
      [@b.card_header]
        <h3>选课方式的分布</h3>
      [/@]
      [@b.card_body]
[#if (Parameters.display!"") == "pie"]
  <div id="container_election_mode" style="float:left;width:100%;height:300px"></div>
  <script type="text/javascript">
    require(["echarts"],function(echarts) {
     console.log({ "election_mode-echarts": echarts });
        var dom = document.getElementById("container_election_mode");
        var myChart = echarts.init(dom);
        var app = {};
        option = null;
        option = {
            title: {
                text: '选课方式的分布',
                subtext: '',
                left: 'center'
            },
            tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b} : {c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: [[#list electionModes as electionMode]'${electionMode[0]?js_string}'[#if electionMode_has_next], [/#if][/#list]]
            },
            series: [
                {
                    name: '选课方式',
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '50%'],
                    data: [
                      [#list electionModes as electionMode]
                        {value: ${electionMode[1]}, name: '${electionMode[0]?js_string}'}[#if electionMode_has_next],[/#if]
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
        <table class="table table-hover table-sm" style="width: 100%">
          <thead>
             <th>选课方式</th>
             <th>人数</th>
          </thead>
          <tbody>
            [#list electionModes as electionMode]
            <tr>
              <td>${electionMode[0]}</td>
              <td>${electionMode[1]}</td>
            </tr>
            [/#list]
          </tbody>
        </table>
[/#if]
      [/@]
    [/@]

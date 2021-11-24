[@b.head/]
  <div style="height: 5px"></div>
  <div>
    [@urp_base.semester  name="semester.id" label="学年学期" value=currentSemester/]
    <button type="submit" class="btn btn-outline-primary btn-sm"><i class="fa fa-search fa-sm"></i>查询</button>
  </div>
    [@b.div id="display" /]
  <script>
    $(document).ready(function() {
      var pickerObj = $(".semester-picker");
      var submitObj = $(":submit");
      submitObj.click(function() {
        var dataMap = {};
        dataMap["semester.id"] = pickerObj.find("[name='semester.id']").val();
        $.ajax({
          "type": "POST",
          "url": "${b.url("!display")}",
          "async": true,
          "dataType": "html",
          "data": dataMap,
          "success": function(data) {
            $("#display").html(data);
          }
        });
      });
      submitObj.click();
    });
  </script>
[@b.foot/]

[@b.head/]
[@base.semester_bar value=semester name="semester.id"]
[/@]
<div  class="container-fluid" style="display:flex;justify-content:space-around;flex-wrap:wrap">
    <div class="" style="display:flex;flex-wrap:nowrap ;flex-direction:column;width:400px">
        [@b.div href="!semester?semester.id="+semester.id /]
        [@b.div href="!enrollment?semester.id="+semester.id /]
        [@b.div href="!electionMode?display=pie&semester.id="+semester.id /]
    </div>
    [@b.div href="!department?semester.id="+semester.id style="width:400px"/]

[@b.div href="!teacher?semester.id="+semester.id style="width:400px" /]
[@b.div href="!teacherTitle?semester.id="+semester.id style="width:400px"/]
[@b.div href="!courseType?semester.id="+semester.id style="width:400px"/]
[@b.div href="!topN?count=5&semester.id="+semester.id style="width:400px"/]
</div>

[@b.foot/]

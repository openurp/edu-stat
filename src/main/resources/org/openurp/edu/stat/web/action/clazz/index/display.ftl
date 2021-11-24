<div  class="container-fluid" style="display:flex;justify-content:space-around;flex-wrap:wrap">
    <div class="" style="display:flex;flex-wrap:nowrap ;flex-direction:column;width:400px">
        [@b.div href="!semester?semester.id="+Parameters['semester.id'] /]
        [@b.div href="!enrollment?semester.id="+Parameters['semester.id'] /]
        [@b.div href="!electionMode?display=pie&semester.id="+Parameters['semester.id'] /]
    </div>
    [@b.div href="!department?semester.id="+Parameters['semester.id'] style="width:400px"/]

[@b.div href="!teacher?semester.id="+Parameters['semester.id'] style="width:400px" /]
[@b.div href="!teacherTitle?semester.id="+Parameters['semester.id'] style="width:400px"/]
[@b.div href="!courseType?semester.id="+Parameters['semester.id'] style="width:400px"/]
[@b.div href="!topN?count=5&semester.id="+Parameters['semester.id'] style="width:400px"/]
</div>
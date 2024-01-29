[#ftl attributes={"content_type":"application/xml"}]
[#assign weekdays={"1":"周一","2":'周二',"3":'周三',"4":'周四',"5":'周五',"6":'周六',"7":'周日'}/]
<?xml version="1.0" encoding="utf-8" ?>
<UserCustomData>
    <Name>${weekdays[weekday.id?string]} 第${beginUnit?left_pad(2, "0")}节</Name>
    [#list devices as d]
    <PlayWndPoll>
        <Staytime>5</Staytime>
        <VideoSrcKey>
            <DeviceId>${d.uuid}@kedacom</DeviceId>
            <Id>0</Id>
            <VideoSrcId>-1</VideoSrcId>
        </VideoSrcKey>
    </PlayWndPoll>
    [/#list]
    <TaskEnable>0</TaskEnable>
    <TaskEndTime>0</TaskEndTime>
    <TaskStartTime>0</TaskStartTime>
</UserCustomData>

Create table process_relation (
table_name       string,
store_procedure     string,
comment           string,
source_tables     string,
after_tables      string,
map_json           string
)


set hive.strict.checks.cartesian.product=false;
set hive.mapred.mode = nonstrict;
--精确查询获得关系图的查询使用.
select table_name,source_name,after_name
from process_relation
where table_name = #{tableName}
--插入数据demo
insert into process_relation values
("CW","CW_1","demo1","AA,BB","CC","{title: {text: ''},tooltip: {},animationDurationUpdate: 1500,animationEasingUpdate: 'quinticInOut',label: {normal: {show: true,textStyle: {fontSize: 12}}},series: [{type: 'graph',layout: 'force',symbolSize: 45,focusNodeAdjacency: false,draggable : false,categories: [{name: '父级节点',itemStyle: { normal: {   color: '#009800' }}}, {name: '子集节点',itemStyle: { normal: {   color: '#4592FF' }}}],label: {normal: { show: true, textStyle: {   fontSize: 12 }}},force: {repulsion: 1000},edgeSymbolSize: [4, 50],edgeLabel: {normal: { show: true, textStyle: {   fontSize: 10 }, formatter: '{c}'}},data: [{name: 'CW',draggable: true}, {name: 'AA',category: 1,draggable: true}, {name: 'BB',category: 1,draggable: true}, {name: 'CC',category: 0,draggable: true}],links: [{source: 0target: 1,value: '子集节点'}, {source: 0,target: 2,value: '子集节点'}, {source: 0,target: 3,value: '父级节点'}],lineStyle: {normal: { opacity: 0.9, width: 1, curveness: 0}}}]}")
,
("HACHI","CW_2","demo2","AA,CC","DD","{title:{text:''},tooltip:{},animationDurationUpdate:1500,animationEasingUpdate:'quinticInOut',label:{normal:{show:true,textStyle:{fontSize:12}}},series:[{type:'graph',layout:'force',symbolSize:45,focusNodeAdjacency:false,draggable:false,categories:[{name:'父级节点',itemStyle:{normal:{color:'#009800'}}},{name:'子集节点',itemStyle:{normal:{color:'#4592FF'}}}],label:{normal:{show:true,textStyle:{fontSize:12}}},force:{repulsion:1000},edgeSymbolSize:[4,50],edgeLabel:{normal:{show:true,textStyle:{fontSize:10},formatter:'{c}'}},data:[{name:'HACHI',draggable:true},{name:'AA',category:1,draggable:true},{name:'CC',category:1,draggable:true},{name:'DD',category:0,draggable:true}],links:[{source:0,target:1,value:'子集节点'},{source:0,target:2,value:'子集节点'},{source:0,target:3,value:'父级节点'}],lineStyle:{normal:{opacity:0.9,width:1,curveness:0}}}]}")
;
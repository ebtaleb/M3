var m = [20, 120, 20, 120],
    w = 900 - m[1] - m[3],
    h = 500 - m[0] - m[2],
    i = 0,
    root;

var getDirection = function(data){
    if(!data){
        return 'root';
    }
    if(data.position){
        return data.position;
    }
    return getDirection(data.parent);
};

var selectNode = function(target){
    if(target){
        var sel = d3.selectAll('#body svg .node').filter(function(d){return d.id==target.id})[0][0];
        if(sel){
            select(sel);
        }
    }
};

var insertNode = function(name) {
    var selection = d3.select(".node.selected")[0][0];
    if (selection){
        var data = selection.__data__;
        var dir = getDirection(data);

        if (name){
            if (dir==='root'){
                dir = data.right.length>data.left.length?'left':'right';
            }
            var cl = data[dir] || data.children || data._children;
            if (!cl){
                cl = data.children = [];
            }
            cl.push({name: name, position: dir});
            update(root);
        }
    }
};

var deleteNode = function() {
    var selection = d3.select(".node.selected")[0][0];
    if (selection){
        var data = selection.__data__;
        var dir = getDirection(data);
        if(dir==='root'){
            //alert('Can\'t delete root');
            return;
        }
        var cl = data.parent[dir] || data.parent.children;
        if(!cl){
            //alert('Could not locate children');
            return;
        }
        var i = 0, l = cl.length;
        for(; i<l; i++){
            if(cl[i].id === data.id){
                //if(confirm('Sure you want to delete '+data.name+'?') === true){
                    cl.splice(i, 1);
                //}
                break;
            }
        }
        selectNode(root);
        update(root);
    }
};

var renameNode = function(){
    var selection = d3.select(".node.selected")[0][0];
    if(selection){
        var data = selection.__data__;
        data.name = prompt('New text:', data.name) || data.name;
        update(root);
    }
};

var addNodes = function(dir){
    root[dir].push({name: 'bar', position: dir}, {name: 'none', position: dir}, {name: 'some', position: dir}, {name: 'value', position: dir});
    update(root);
};

var moveNodes = function(from, to){
    var tmp = root[from].shift();
    tmp.position = to;
    root[to].push(tmp);
    update(root);
};

var setConnector = function(type){
    //alert(type);
    connector = window[type];
    update(root);
};

var select = function(node){
    // Find previously selected, unselect
    d3.select(".selected").classed("selected", false);

    // Select current item
    d3.select(node).classed("selected", true);
};

var createNew = function(){
    root = {name: 'Root', children: [], left: [], right: []};
    update(root, true);
    selectNode(root);
};

var handleClick = function(d, index){
    select(this);
    update(d);
};

var tree = d3.layout.tree()
    .size([h, w]);

var calcLeft = function(d){
    var l = d.y;
    if(d.position==='left'){
        l = (d.y)-w/2;
        l = (w/2) + l;
    }
    return {x : d.x, y : l};
};

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

var elbow = function (d, i){
    var source = calcLeft(d.source);
    var target = calcLeft(d.target);
    var hy = (target.y-source.y)/2;
    return "M" + source.y + "," + source.x
        + "H" + (source.y+hy)
        + "V" + target.x + "H" + target.y;
};

var connector = elbow;

var vis = d3.select("#body")
.append("svg:svg")
.attr("width", w
        + m[1] + m[3])
.attr("height", h + m[0] + m[2])
.append("svg:g")
.attr("transform", "translate(" + (w/2+m[3]) + "," + m[0] + ")")
;

var loadJSONObj = function(jsonstr) {
        var json = JSON.parse(jsonstr);
        var i=0, l=json.children.length;
        window.data = root = json;
        root.x0 = h / 2;
        root.y0 = 0;

        json.left = [];
        json.right = [];

        for(; i<l; i++){
            if(i%2){
                json.left.push(json.children[i]);
                json.children[i].position = 'left';
            }else{
                json.right.push(json.children[i]);
                json.children[i].position = 'right';
            }
        }

        update(root, true);
        selectNode(root);
};

var loadJSON = function(fileName){
    d3.json(fileName, function(json) {
        var i=0, l=json.children.length;
        window.data = root = json;
        root.x0 = h / 2;
        root.y0 = 0;

        json.left = [];
        json.right = [];

        for(; i<l; i++){
            if(i%2){
                json.left.push(json.children[i]);
                json.children[i].position = 'left';
            }else{
                json.right.push(json.children[i]);
                json.children[i].position = 'right';
            }
        }

        update(root, true);
        selectNode(root);
    });
};

var loadFreeMind = function(fileName){
    d3.xml(fileName, 'application/xml', function(err, xml){
        // Changes XML to JSON
        function xmlToJson(xml) {

            // Create the return object
            var obj = {};

            if (xml.nodeType == 1) { // element
                // do attributes
                if (xml.attributes.length > 0) {
                    obj["@attributes"] = {};
                    for (var j = 0; j < xml.attributes.length; j++) {
                        var attribute = xml.attributes.item(j);
                        obj["@attributes"][attribute.nodeName] = attribute.nodeValue;
                    }
                }
            } else if (xml.nodeType == 3) { // text
                obj = xml.nodeValue;
            }

            // do children
            if (xml.hasChildNodes()) {
                for(var i = 0; i < xml.childNodes.length; i++) {
                    var item = xml.childNodes.item(i);
                    var nodeName = item.nodeName;
                    if (typeof(obj[nodeName]) == "undefined") {
                        obj[nodeName] = xmlToJson(item);
                    } else {
                        if (typeof(obj[nodeName].push) == "undefined") {
                            var old = obj[nodeName];
                            obj[nodeName] = [];
                            obj[nodeName].push(old);
                        }
                        obj[nodeName].push(xmlToJson(item));
                    }
                }
            }
            return obj;
        };
        var js = xmlToJson(xml);
        var data = js.map.node;
        var parseData = function(data, direction){
            var key, i, l, dir = direction, node = {}, child;
            for(key in data['@attributes']){
                node[key.toLowerCase()] = data['@attributes'][key];
            }
            node.direction = node.direction || dir;
            l = (data.node || []).length;
            if(l){
                node.children = [];
                for(i=0; i<l; i++){
                    dir = data.node[i]['@attributes'].POSITION || dir;
                    child = parseData(data.node[i], {}, dir);
                    (node[dir] = node[dir] || []).push(child);
                    node.children.push(child);
                }
            }
            return node;
        };
        root = parseData(data, 'right');
        root.x0 = h / 2;
        root.y0 = w / 2;
        update(root, true);
        selectNode(root);
    });
};

var toArray = function(item, arr, d){
    arr = arr || [];
    var dr = d || 1;
    var i = 0, l = item.children?item.children.length:0;
    arr.push(item);
    if(item.position && item.position==='left'){
        dr = -1;
    }
    item.y = dr * item.y;
    for(; i < l; i++){
        toArray(item.children[i], arr, dr);
    }
    return arr;
};

function update(source, slow) {
    var duration = (d3.event && d3.event.altKey) || slow ? 1000 : 100;

    // Compute the new tree layout.
    var nodesLeft = tree
        .size([h, (w/2)-20])
        .children(function(d){
            return (d.depth===0)?d.left:d.children;
        })
    .nodes(root).reverse();

    var nodesRight = tree
        .size([h, w/2])
        .children(function(d){
            return (d.depth===0)?d.right:d.children;
        })
    .nodes(root).reverse();

    root.children = root.left.concat(root.right);
    root._children = null;
    var nodes = toArray(root);

    // Update the nodes…
    var node = vis.selectAll("g.node")
        .data(nodes, function(d) { return d.id || (d.id = ++i); });

    // Enter any new nodes at the parent's previous position.
    var nodeEnter = node.enter().append("svg:g")
        .attr("class", function(d){ return d.selected?"node selected":"node"; })
        .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
        .on("click", handleClick);

    nodeEnter.append("svg:circle")
        .attr("r", 1e-6);

    nodeEnter.append("svg:text")
        .attr("x", function(d) { return d.children || d._children ? -10 : 10; })
        .attr("dy", 14)
        .attr("text-anchor", "middle")
        .text(function(d) { return (d.name || d.text); })
        .style("fill-opacity", 1);

    // Transition nodes to their new position.
    var nodeUpdate = node.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

    nodeUpdate.select("text")
        .text(function(d) { return (d.name || d.text); });

    nodeUpdate.select("circle")
        .attr("r", 4.5);

    // Transition exiting nodes to the parent's new position.
    var nodeExit = node.exit().transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
        .remove();

    nodeExit.select("circle").attr("r", 1e-6);

    nodeExit.select("text").style("fill-opacity", 1e-6);

    // Update the links…
    var link = vis.selectAll("path.link")
        .data(tree.links(nodes), function(d) { return d.target.id; });

    // Enter any new links at the parent's previous position.
    link.enter().insert("svg:path", "g")
        .attr("class", "link")
        .attr("d", function(d) {
            var o = {x: source.x0, y: source.y0};
            return connector({source: o, target: o});
        })
    .transition()
        .duration(duration)
        .attr("d", connector);

    // Transition links to their new position.
    link.transition()
        .duration(duration)
        .attr("d", connector);

    // Transition exiting nodes to the parent's new position.
    link.exit().transition()
        .duration(duration)
        .attr("d", function(d) {
            var o = {x: source.x, y: source.y};
            return connector({source: o, target: o});
        })
    .remove();

    // Stash the old positions for transition.
    nodes.forEach(function(d) {
        d.x0 = d.x;
        d.y0 = d.y;
    });
}

// Toggle children.
function toggle(d) {
    if (d.children) {
        d._children = d.children;
        d.children = null;
    } else {
        d.children = d._children;
        d._children = null;
    }
}

var censor = function (key, value) {
    if (key != 'parent' && key != 'depth' && key != 'x' && key != 'y' && key != 'x0' && key != 'y0' && key != 'id' && key != 'position' && key != '_children' && key != 'left' && key != 'right')
        return value;
}

function save(fileName) {
    Android.saveData(JSON.stringify(root, censor));
}

window.onload = function () {
    loadJSONObj(Android.loadData());
};

